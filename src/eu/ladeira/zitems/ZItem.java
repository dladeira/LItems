package eu.ladeira.zitems;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ZItem { // Do not create instance of directly
	
	private static ArrayList<ZItem> items = new ArrayList<>();
	
	public static boolean addItem(ZItem item) {
		if (getItem(item.getId()) == null) { // Check for duplicate id
			items.add(item);
			System.out.println("Added item " + item.getId());
			return true;
		}
		return false;
	}
	
	public static ZItem getItem(int id) {
		for (ZItem item : items) {
			if (item.getId() == id) return item;
		}
		return null;
	}
	
	public static ArrayList<ZItem> getItems() {
		return items;
	}
	
	public static int getId(ItemStack is) {
		if (is == null) return 0;
		if (is.hasItemMeta() && is.getItemMeta().hasLore()) {
			try {
				return Integer.valueOf(decodeMessage(is, 1)); // Always will be the 1st line
			} catch (Exception e) {
				System.out.println("ERROR: Thinking " + decodeMessage(is, 1) + " is a item id");
			}
		}
		return 0;
	}
	
	public static ZItem getItem(ItemStack is) {
		if (is == null) return null;
		int id = ZItem.getId(is); // Get item id hidden in ItemStack
		if (id != 0) { // Item exists
			return ZItem.getItem(id);
		}
		return null;
	}
	
	private String name;
	private String description;
	private ArrayList<String> statistics;
	
	private Rarity rarity;
	private String category;
	private Texture texture;
	
	private int id;
	
	public ZItem(String name, String desc, ArrayList<String> stats, Rarity rarity, String category, Texture texture, int id) {
		this.name = name;
		this.description = desc;
		this.statistics = stats;
		this.rarity = rarity;
		this.category = category;
		this.texture = texture;
		
		this.id = id;
	}
	
	public ZItem(String name, String desc, Rarity rarity, String category,  Texture texture, int id) {
		this(name, desc, new ArrayList<>(), rarity, category, texture, id);
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	private ArrayList<String> getStatistics() {
		return this.statistics;
	}
	
	public void addStatistics(ArrayList<String> stats) {
		this.statistics.addAll(stats);
	}
	
	public Rarity getRarity() {
		return this.rarity;
	}
	
	public String getCategory() {
		return this.category;
	}
	
	public int getId() {
		return this.id;
	}
	
	public Texture getTexture() {
		return this.texture;
	}
	
	public ItemStack generateItemStack() {
		ItemStack is = this.getTexture().getItem();
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName(this.getRarityColor() + this.getName());
		
		ArrayList<String> lore = new ArrayList<>();
		lore.add(ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC + this.getDescription());
		lore.add(encodeMessage(this.getId() + ""));
		lore.addAll(this.getStatistics());
		lore.add("");
		lore.add(this.getRarityColor() + "" + ChatColor.BOLD + this.getRarity() + " " + this.getCategory());
		im.setLore(lore);
		
		is.setItemMeta(im);
		return is;
	}
	
	private static String encodeMessage(String message) { // Credit to chasechocolate (Bukkit Forums)
		StringBuilder builder = new StringBuilder();

		for (char c : message.toCharArray()){
		  builder.append(ChatColor.COLOR_CHAR).append(c + " ");
		}

		return builder.toString();
	}
	
	private static String decodeMessage(ItemStack is, int loreLine) {
		return (is.getItemMeta().getLore().get(loreLine)).toString().replaceAll(String.valueOf(ChatColor.COLOR_CHAR), "").replaceAll(" ", "");
	}
	
	public ChatColor getRarityColor() {
		switch (this.getRarity()) {
		case COMMON:
			return ChatColor.GRAY;
		case EPIC:
			return ChatColor.DARK_PURPLE;
		case LEGENDARY:
			return ChatColor.GOLD;
		case RARE:
			return ChatColor.AQUA;
		case UNCOMMON:
			return ChatColor.GREEN;
		}
		return null;
	}
}