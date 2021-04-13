package eu.ladeira.zitems.firearm;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import eu.ladeira.zitems.ZItems;

public class Ammo {
	
	private static ArrayList<Ammo> ammoList = new ArrayList<Ammo>();
	
	public static Ammo getAmmo(String name) {
		for (Ammo ammo : ammoList) {
			if (ammo.getName().toLowerCase().equals(name)) return ammo;
		}
		return null;
	}
	
	public static void addAmmo(Ammo ammo) {
		if (getAmmo(ammo.getName()) == null) ammoList.add(ammo);
	}
	
	public static void loadAmmoFromConfig() {
		for (String name : ZItems.getConf().getConfigurationSection("ammo").getKeys(false)) {
			Material material;
			ChatColor color;
			try {
				material = Material.valueOf(ZItems.getConf().getString("ammo." + name + ".material"));
			} catch (Exception e) {
				System.out.println("Invalid ammo material: " + ZItems.getConf().getString("ammo." + name + ".material"));
				continue;
			}
			try {
				color = ChatColor.valueOf(ZItems.getConf().getString("ammo." + name + ".color"));
			} catch (Exception e) {
				System.out.println("Invalid ammo color: " + ZItems.getConf().getString("ammo." + name + ".color"));
				continue;
			}
			
			addAmmo(new Ammo(name, material, color));
		}
	}
	
	private String name;
	private Material material;
	private ChatColor color;
	
	public Ammo(String name, Material material, ChatColor color) {
		this.name = name;
		this.material = material;
		this.color = color;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Material getMaterial() {
		return this.material;
	}
	
	public ChatColor getColor() {
		return this.color;
	}
	
	public String getDisplayName() {
		return this.color + "" + ChatColor.BOLD + name.toUpperCase() + " AMMO";
	}
	
	public int countAmmo(Player p) {
		int amount = 0;
		for (ItemStack is : p.getInventory().getContents()) {
			if (is == null || !is.hasItemMeta()) continue;
			ItemMeta im = is.getItemMeta();
			if (im.hasDisplayName() && im.getDisplayName().equals(getDisplayName())) amount+=is.getAmount();
		}
		return amount;
	}
	
	public ItemStack getAmmo(int amount) {
		amount = Math.max(1, Math.min(amount, 64));
		ItemStack is = new ItemStack(getMaterial());
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName(getDisplayName());
		is.setItemMeta(im);
		is.setAmount(amount);
		return is;
	}
	
	public void removeAmmo(Player p, int amountToRemove) {
		int amountLeft = amountToRemove;
		PlayerInventory inv = p.getInventory();
		for (ItemStack is : inv.getContents()) {
			if (is == null || !is.hasItemMeta()) continue;
			ItemMeta im = is.getItemMeta();
			if (im.hasDisplayName() && im.getDisplayName().equals(getDisplayName())) {
				if (amountLeft > 0) {
					is.setAmount(amountLeft > is.getAmount() ? 0 : is.getAmount() - amountLeft);
					amountLeft-= is.getAmount();
				}
			}
		}
	}
}
