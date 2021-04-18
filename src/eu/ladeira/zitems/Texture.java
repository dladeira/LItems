package eu.ladeira.zitems;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_16_R3.ItemArmor;

public class Texture { // Custom textures based on item durability

	private int damage;
	private Material item;
	
	public Texture(Material damageable, int damage) {
		this.item = damageable;
		this.damage = damage;
	}
	
	public Texture(Material material) {
		this.item = material;
		this.damage = 0;
	}
	
	public ItemStack getItem() {
		ItemStack is = new ItemStack(item);
		ItemMeta im = is.getItemMeta();
		
		if (im instanceof Damageable) ((Damageable)im).setDamage(damage); // If damageable set damage
		im.setUnbreakable(true); // Hides durability bar
		
		is.setItemMeta(im);
		return is;
	}
	
	public boolean isWearable() {
		return (CraftItemStack.asNMSCopy(getItem()).getItem() instanceof ItemArmor);
	}
}
