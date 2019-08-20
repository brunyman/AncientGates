package org.mcteam.ancientgates.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class BlockUtil {

	public static HashSet<Material> standableGateMaterials;
	public static Map<Material, Boolean> standableMaterials;

	static {
		standableGateMaterials = new HashSet<>();
		standableGateMaterials.add(Material.PISTON_HEAD);
		standableGateMaterials.add(Material.END_PORTAL);
		standableGateMaterials.add(Material.END_GATEWAY);
		standableGateMaterials.add(Material.LAVA);
		standableGateMaterials.add(Material.NETHER_PORTAL);
		standableGateMaterials.add(Material.LAVA);
		standableGateMaterials.add(Material.WATER);
		standableGateMaterials.add(Material.SUGAR_CANE);
		standableGateMaterials.add(Material.WATER);
		standableGateMaterials.add(Material.COBWEB);
	}

	static {
		standableMaterials = new HashMap<>();
		try {
			standableMaterials.put(Material.AIR, true); // 0 Air
			standableMaterials.put(Material.OAK_SAPLING, true); // 6 Saplings
			standableMaterials.put(Material.WATER, true); // 8 Water
			standableMaterials.put(Material.WATER, true); // 9 Stationary water
			standableMaterials.put(Material.LAVA, true); // 10 Lava
			standableMaterials.put(Material.LAVA, true); // 11 Stationary lava
			standableMaterials.put(Material.POWERED_RAIL, true); // 27 Powered Rail
			standableMaterials.put(Material.DETECTOR_RAIL, true); // 28 Detector Rail
			standableMaterials.put(Material.COBWEB, true); // 30 Cobweb
			standableMaterials.put(Material.TALL_GRASS, true); // 31 Tall Grass
			standableMaterials.put(Material.DEAD_BUSH, true); // 32 Dead Bush
			standableMaterials.put(Material.PISTON_HEAD, true); // 36 Piston (Moving)
			//standableMaterials.put(Material.DANDELION_YELLOW, true); // 37 Dandelion
			//standableMaterials.put(Material.ROSE_RED, true); // 38 Rose
			standableMaterials.put(Material.BROWN_MUSHROOM, true); // 39 Brown Mushroom
			standableMaterials.put(Material.RED_MUSHROOM, true); // 40 Red Mushroom
			standableMaterials.put(Material.STONE_SLAB, false); // 44 Stone Slab
			standableMaterials.put(Material.TORCH, true); // 50 Torch
			standableMaterials.put(Material.FIRE, true); // 51 Fire
			standableMaterials.put(Material.REDSTONE_WIRE, true); // 55 Redstone Wire
			standableMaterials.put(Material.WHEAT_SEEDS, true); // 59 Wheat Seeds
			//standableMaterials.put(Material.SIGN, true); // 63 Sign Post
			standableMaterials.put(Material.LADDER, true); // 65 Ladders
			standableMaterials.put(Material.RAIL, true); // 66 Rails
			//standableMaterials.put(Material.WALL_SIGN, true); // 68 Wall Sign
			standableMaterials.put(Material.LEVER, true); // 69 Lever
			standableMaterials.put(Material.STONE_PRESSURE_PLATE, true); // 70 Stone Pressure Plate
			standableMaterials.put(Material.OAK_PRESSURE_PLATE, true); // 72 Wooden Pressure Plate
			standableMaterials.put(Material.REDSTONE_TORCH, true); // 75 Redstone Torch (Off)
			standableMaterials.put(Material.REDSTONE_TORCH, true); // 76 Redstone Torch (On)
			standableMaterials.put(Material.STONE_BUTTON, true); // 77 Stone Button
			standableMaterials.put(Material.SNOW, true); // 78 Snow
			standableMaterials.put(Material.SUGAR_CANE, true); // 83 Sugar Cane
			standableMaterials.put(Material.NETHER_PORTAL, true); // 90 Portal
			standableMaterials.put(Material.REPEATER, true); // 93 Redstone Repeater (Off)
			standableMaterials.put(Material.REPEATER, true); // 94 Redstone Repeater (On)
			standableMaterials.put(Material.VINE, true); // 106 Vines
			standableMaterials.put(Material.LILY_PAD, true); // 111 Lily Pad
			standableMaterials.put(Material.END_PORTAL, true); // 119 End Portal
			standableMaterials.put(Material.OAK_SLAB, false); // 126 Wooden Slab
			standableMaterials.put(Material.TRIPWIRE_HOOK, true); // 131 Tripwire Hook
			standableMaterials.put(Material.TRIPWIRE, true); // 132 Tripwire
			standableMaterials.put(Material.FLOWER_POT, true); // 140 Flower Pot
			standableMaterials.put(Material.CARROT, true); // 141 Carrot
			standableMaterials.put(Material.POTATO, true); // 142 Potatoes
			standableMaterials.put(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, true); // 147 Gold Pressure Plate
			standableMaterials.put(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, true); // 148 Iron Pressure Plate
			standableMaterials.put(Material.COMPARATOR, true); // 149 Redstone
			// Comparator (Off)
			standableMaterials.put(Material.COMPARATOR, true); // 150 Redstone
			// Comparator (On)
			standableMaterials.put(Material.DAYLIGHT_DETECTOR, false); // 151 Daylight Sensor
			standableMaterials.put(Material.ACTIVATOR_RAIL, true); // 157 Activator Rail
			standableMaterials.put(Material.WHITE_CARPET, true); // 171 Carpet
			standableMaterials.put(Material.END_GATEWAY, true); // 209 End Gateway
			//standableMaterials.put(Material.DOUBLE_PLANT, true); // double_plant Double Plants
		} catch (final NoSuchFieldError e) {
		} // Support previous MC versions
	}

	public static boolean isPortalGateMaterial(final Material material) {
		return material.equals(Material.NETHER_PORTAL) || material.equals(Material.END_PORTAL) || material.equals(Material.END_GATEWAY);
	}

	public static boolean isStandableGateMaterial(final Material material) {
		return standableGateMaterials.contains(material);
	}

	public static boolean canPlayerStandInGateBlock(final Block block, final Boolean fullHeight) {
		if (fullHeight) {
			return isStandableGateMaterial(block.getType()) && isStandableGateMaterial(block.getRelative(BlockFace.UP).getType());
		}
		return isStandableGateMaterial(block.getType());
	}

	public static boolean isStandableMaterial(final Material material) {
		return standableMaterials.containsKey(material);
	}

	public static boolean canPassThroughMaterial(final Material material) {
		return standableMaterials.get(material) == null ? false : standableMaterials.get(material);
	}

}
