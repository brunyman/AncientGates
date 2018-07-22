package org.mcteam.ancientgates.util.types;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

public enum GateMaterial {
	// AIR   -  Not working in 1.13
	//AIR("psudeo air blocks", Material.LEGACY_PISTON_MOVING_PIECE),

	// LAVA
	LAVA("stationary lava blocks", Material.LAVA),

	// NETHER/ENDER PORTAL
	PORTAL("nether/ender portal blocks", Material.LEGACY_PORTAL),

	// SUGARCANE
	SUGARCANE("sugarcane blocks", Material.LEGACY_SUGAR_CANE_BLOCK),

	// WATER
	WATER("stationary water blocks", Material.WATER),

	// WEB
	WEB("spiders web blocks", Material.COBWEB);

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

	static {
		final GateMaterial[] values = values();
		for (int i = 0; i < values.length; i++)
			names[i] = values[i].name();
	}

	protected final String desc;

	public String getDesc() {
		return this.desc;
	}

	protected final Material material;

	public Material getMaterial() {
		return this.material;
	}

	private GateMaterial(final String desc, final Material material) {
		this.desc = desc;
		this.material = material;
	}

}
