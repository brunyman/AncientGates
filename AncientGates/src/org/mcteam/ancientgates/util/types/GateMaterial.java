package org.mcteam.ancientgates.util.types;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;

public enum GateMaterial {
	// AIR   -  Not working in 1.13
	//AIR("psudeo air blocks", Material.LEGACY_PISTON_MOVING_PIECE),

	// LAVA
	LAVA("stationary lava blocks", Material.LAVA, false),

	// NETHER/ENDER PORTAL
	PORTAL("nether/ender portal blocks", Material.LEGACY_PORTAL, true),

	// END GATEWAY
	ENDGATEWAY("End Gateway blocks", Material.END_GATEWAY, true),

	// SUGARCANE
	SUGARCANE("sugarcane blocks", Material.LEGACY_SUGAR_CANE_BLOCK, false),

	// WATER
	WATER("stationary water blocks", Material.WATER, false),

	// WEB
	WEB("spiders web blocks", Material.COBWEB, false);

	private static final Map<String, GateMaterial> nameToMaterial = new HashMap<>();

	static {
		for (final GateMaterial value : EnumSet.allOf(GateMaterial.class)) {
			nameToMaterial.put(value.name(), value);
		}
	}

	public static GateMaterial fromName(final String name) {
		return nameToMaterial.get(name);
	}

	public static final String[] names = new String[values().length];
	public static String[] vanillaNames;

	static {
		final GateMaterial[] values = values();
		final List<String> vanillaNamesList = new ArrayList<>();

		for (int i = 0; i < values.length; i++) {
			names[i] = values[i].name();
			if (values[i].isVanilla()) vanillaNamesList.add(values[i].name());
		}

		vanillaNames = vanillaNamesList.toArray(new String[0]);
	}

	protected final String desc;

	public String getDesc() {
		return this.desc;
	}

	protected final Material material;

	public Material getMaterial() {
		return this.material;
	}

	protected final boolean isVanilla;

	public boolean isVanilla() {
		return isVanilla;
	}

	private GateMaterial(final String desc, final Material material, final boolean isVanilla) {
		this.desc = desc;
		this.material = material;
		this.isVanilla = isVanilla;
	}

}
