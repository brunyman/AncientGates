package org.mcteam.ancientgates.listeners;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.Plugin;
import org.mcteam.ancientgates.queue.BungeeQueue;
import org.mcteam.ancientgates.queue.types.BungeeQueueType;
import org.mcteam.ancientgates.tasks.BungeeMessage;
import org.mcteam.ancientgates.tasks.BungeeServerList;
import org.mcteam.ancientgates.tasks.BungeeServerName;
import org.mcteam.ancientgates.util.EntityUtil;
import org.mcteam.ancientgates.util.ExecuteUtil;
import org.mcteam.ancientgates.util.TeleportUtil;
import org.mcteam.ancientgates.util.types.CommandType;
import org.mcteam.ancientgates.util.types.InvBoolean;
import org.mcteam.ancientgates.util.types.WorldCoord;

public class PluginPlayerListener implements Listener {

	public Plugin plugin;

	private final HashMap<Player, Location> playerLocationAtEvent = new HashMap<>();
	private final Set<Player> teleportingPlayers = new HashSet<>();

	public PluginPlayerListener(final Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		if (!Conf.bungeeCordSupport) {
			return;
		}

		final Player player = event.getPlayer();
		final String playerName = player.getName();

		this.teleportingPlayers.remove(event.getPlayer());

		// Ok so a player joins the server
		// Find if they're in the BungeeCord in-bound teleport queue
		final BungeeQueue queue = Plugin.bungeeCordInQueue.remove(playerName.toLowerCase());
		if (queue != null) {
			// Display custom join message
			String msg = null;
			if (Conf.useBungeeMessages) {
				final String server = queue.getServer();
				msg = ChatColor.translateAlternateColorCodes('&', Conf.bungeeJoinMessage.replace("%p", playerName).replace("%s", server));
			}
			event.setJoinMessage(msg);

			// Display teleport message
			final String message = queue.getMessage();
			if (!message.equals("null"))
				player.sendMessage(message);

			if (queue.getDestination() != null) {
				// Teleport incoming BungeeCord player
				final BungeeQueueType queueType = queue.getQueueType();
				if (queueType == BungeeQueueType.PLAYER) {
					final Location location = queue.getDestination();

					// Handle player riding entity
					Entity entity = null;
					if (queue.getEntityType() != null) {
						final World world = location.getWorld();
						if (queue.getEntityType().isSpawnable()) {
							// Spawn incoming BungeeCord player's entity
							entity = world.spawnEntity(location, queue.getEntityType());
							EntityUtil.setEntityTypeData(entity, queue.getEntityTypeData());
						}
					}

					TeleportUtil.teleportPlayer(player, location, false, InvBoolean.TRUE);
					if (entity != null)
						entity.setPassenger(player);

					return;
					// Teleport incoming BungeeCord passenger
				} else if (queueType == BungeeQueueType.PASSENGER) {
					TeleportUtil.teleportVehicle(player, queue.getVehicleTypeName(), queue.getVelocity(), queue.getDestination());
					return;
				}
			}

			// Execute teleport command
			final String command = queue.getCommand();
			final CommandType commandType = queue.getCommandType();
			if (!command.equals("null"))
				ExecuteUtil.execCommand(player, command, commandType);

			// Activate cooldown period
			final Long now = Calendar.getInstance().getTimeInMillis();
			Plugin.lastTeleportTime.put(player.getName(), now);
		}

		// Process BungeeCord message queue
		if (Plugin.bungeeMsgQueue.size() > 0)
			new BungeeMessage(plugin).runTaskLater(plugin, 20L);

		// Schedule task to check bungeeServerName & bungeeServerList is set
		if (Plugin.bungeeServerName == null)
			new BungeeServerName(plugin).runTaskLater(plugin, 20L);
		if (Plugin.bungeeServerList == null)
			new BungeeServerList(plugin).runTaskLater(plugin, 20L);

	}

	@EventHandler
	public void onPlayerQuit(final PlayerQuitEvent event) {
		final String playerName = event.getPlayer().getName();

		// Clear player hashmaps
		this.playerLocationAtEvent.remove(event.getPlayer());
		this.teleportingPlayers.remove(event.getPlayer());
		Plugin.lastMessageTime.remove(playerName);
		Plugin.lastTeleportTime.remove(playerName);

		if (!Conf.bungeeCordSupport) {
			return;
		}

		// Ok so a player quits the server
		// If it's a BungeeCord teleport, display a custom quit message
		final String server = Plugin.bungeeCordOutQueue.remove(playerName.toLowerCase());
		if (server != null) {
			String msg = null;
			if (Conf.useBungeeMessages)
				msg = ChatColor.translateAlternateColorCodes('&', Conf.bungeeQuitMessage.replace("%p", playerName).replace("%s", server));
			event.setQuitMessage(msg);
		}
	}

	/*
	 * Unlike Nether Portals and End Portals, using an End Gateway triggers a
	 * PlayerTeleportEvent:
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerTeleport(final PlayerTeleportEvent event) {
		if (event.isCancelled()) {
			return;
		}

		// If a teleportation is in progress for this player, the triggering of
		// a plugin-caused PlayerTeleportEvent means it has completed - there's
		// no need to do anything else
		if (event.getCause() == TeleportCause.PLUGIN) {
			this.teleportingPlayers.remove(event.getPlayer());
			return;
		}

		// End Gateways are the only cause of teleportation we care about here
		if (event.getCause() != TeleportCause.END_GATEWAY) {
			return;
		}

		// Coming into contact with an End Gateway that teleports to itself will
		// often trigger multiple PlayerTeleportEvents - ignore any after the
		// first one
		if (this.teleportingPlayers.contains(event.getPlayer())) {
			return;
		}

		// From this point onwards, handling a gate-related PlayerTeleportEvent
		// is identical to handling a PlayerPortalEvent - pretend that the
		// player's location is the End Gateway's teleportation destination,
		// which we use as a proxy for the End Gateway block itself
		handleTeleportEvents(event, event.getTo());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPortal(final PlayerPortalEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (event.getCause() != TeleportCause.NETHER_PORTAL && event.getCause() != TeleportCause.END_PORTAL) {
			return;
		}

		// From this point onwards, handling a gate-related PlayerPortalEvent
		// is identical to handling a PlayerTeleportEvent
		handleTeleportEvents(event, this.playerLocationAtEvent.remove(event.getPlayer()));
	}

	private void handleTeleportEvents(final PlayerTeleportEvent event, Location playerLocation) {
		final Player player = event.getPlayer();

		// Find the nearest gate
		final WorldCoord playerCoord = new WorldCoord(playerLocation);
		final Gate nearestGate = Gates.gateFromPortal(playerCoord);

		if (nearestGate != null) {
			event.setCancelled(true);

			// Check teleportation method
			if (!Conf.useVanillaPortals) {
				return;
			}

			// Check player is not carrying a passenger
			if (player.getPassenger() != null) {
				return;
			}

			// Get current time
			final Long now = Calendar.getInstance().getTimeInMillis();

			// Check player has passed cooldown period
			if (Plugin.lastTeleportTime.containsKey(player.getName()) && Plugin.lastTeleportTime.get(player.getName()) > now - Conf.getGateCooldownMillis()) {
				return;
			}

			// Check player has permission to enter the gate.
			if (!Plugin.hasPermManage(player, "ancientgates.use." + nearestGate.getId()) && !Plugin.hasPermManage(player, "ancientgates.use.*") && Conf.enforceAccess) {
				player.sendMessage("You lack the permissions to enter this gate.");
				return;
			}

			// Handle economy (check player has funds to use gate)
			if (!Plugin.handleEconManage(player, nearestGate.getCost())) {
				player.sendMessage("This gate costs: " + nearestGate.getCost() + ". You have insufficient funds.");
				return;
			}

			// Handle BungeeCord gates (BungeeCord support disabled)
			if (nearestGate.getBungeeTo() != null && Conf.bungeeCordSupport == false) {
				player.sendMessage(String.format("BungeeCord support not enabled."));
				return;
			}

			if (nearestGate.getTo() == null && nearestGate.getBungeeTo() == null && nearestGate.getCommand() == null) {
				player.sendMessage(String.format("This gate does not point anywhere :P"));
				return;
			}

			// Teleport the player (Nether method)
			this.teleportingPlayers.add(player);
			if (nearestGate.getTo() != null) {
				TeleportUtil.teleportPlayer(player, nearestGate.getTo(), nearestGate.getTeleportEntities(), nearestGate.getTeleportInventory());

				if (nearestGate.getCommand() != null)
					ExecuteUtil.execCommand(player, nearestGate.getCommand(), nearestGate.getCommandType());
				if (nearestGate.getMessage() != null)
					player.sendMessage(nearestGate.getMessage());

				Plugin.lastTeleportTime.put(player.getName(), now);
			} else if (nearestGate.getBungeeTo() != null) {
				TeleportUtil.teleportPlayer(player, nearestGate.getBungeeTo(), nearestGate.getBungeeType(), nearestGate.getTeleportEntities(), nearestGate.getTeleportInventory(), false, nearestGate.getCommand(), nearestGate.getCommandType(),
						nearestGate.getMessage());
			} else {
				ExecuteUtil.execCommand(player, nearestGate.getCommand(), nearestGate.getCommandType(), true);
				Plugin.lastTeleportTime.put(player.getName(), now);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityPortalEnterEvent(final EntityPortalEnterEvent event) {
		if (event.getEntity() instanceof Player) {
			final Player player = (Player) event.getEntity();

			// Ok so a player enters a portal
			// Immediately record their location
			final Location playerLocation = event.getLocation();
			this.playerLocationAtEvent.put(player, playerLocation);
		}
	}

}
