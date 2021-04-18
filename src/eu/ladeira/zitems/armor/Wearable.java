package eu.ladeira.zitems.armor;

import org.bukkit.Material;

import eu.ladeira.zitems.Rarity;
import eu.ladeira.zitems.Texture;
import eu.ladeira.zitems.ZItem;

public class Wearable extends ZItem {

	public static Texture getTexture(ArmorSlot slot, int textureId) {
		switch (slot) {
		case HEAD:
			return new Texture(Material.LEATHER_HELMET, textureId);
		case CHEST:
			return new Texture(Material.LEATHER_CHESTPLATE, textureId);
		case LEGS:
			return new Texture(Material.LEATHER_LEGGINGS, textureId);
		case FEET:
			return new Texture(Material.LEATHER_BOOTS, textureId);
		}
		return null;
	}
	
	private float protection;
	
	public Wearable(String name, String desc, Rarity rarity, ArmorSlot slot, int textureId, int id, float protection) {
		super(name, desc, rarity, "WEARABLE", getTexture(slot, textureId), id);
		
		this.protection = protection;
	}
	
	public float getProtection() { // Leather armor on average gives 7 protection
		return this.protection - 7 > 0 ? this.protection - 7 : 0;
	}
}
