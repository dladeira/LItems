package eu.ladeira.zitems;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import eu.ladeira.zitems.events.FirearmFire;
import eu.ladeira.zitems.events.ProjectileLand;
import eu.ladeira.zitems.firearm.Ammo;
import eu.ladeira.zitems.firearm.Firearm;

public class ZItems extends JavaPlugin implements CommandExecutor {
	
	private static Plugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		plugin.saveConfig(); // Generate config file
		
		this.getCommand("zitems").setExecutor(this);
		registerEvent(new FirearmFire(), new ProjectileLand());
		Ammo.loadAmmoFromConfig();
		Firearm.loadFirearmFromConfig();
		
	}
	
	@Override
	public void onDisable() {
		plugin = null;
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}
	
	public static FileConfiguration getConf() {
		return getPlugin().getConfig();
	}
	
	public void registerEvent(Listener... listeners) {
		for (Listener listener : listeners) {
			Bukkit.getServer().getPluginManager().registerEvents(listener, this);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		if (args.length == 1) {
			int id = Integer.parseInt(args[0]);
			p.getInventory().setItemInMainHand(ZItem.getItem(id).generateItemStack());
		} else if (args.length == 2) {
			p.getInventory().addItem(Ammo.getAmmo(args[0]).getAmmo(64));
		}
		return true;
	}
}