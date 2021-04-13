package eu.ladeira.zitems.firearm;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import eu.ladeira.zitems.ZItems;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ClipReload implements ReloadingArchitecture {

	private int clipSize;
	private Ammo ammoType;
	private int reloadTime;
	
	public HashMap<Player, Integer> reloadingTasks;
	public HashMap<Integer, Entry<Integer, Integer>> reloadingTimes; // <totalTicks, ticksLeft>
	
	public ClipReload(int clipSize, Ammo ammoType, int reloadTime) {
		this.clipSize = clipSize;
		this.ammoType = ammoType;
		this.reloadTime = reloadTime;
		
		this.reloadingTasks = new HashMap<>();
		this.reloadingTimes = new HashMap<>();
	}

	public int getClipSize() {
		return this.clipSize;
	}
	
	public Ammo getAmmoType() {
		return this.ammoType;
	}
	
	public int getReloadTime() {
		return this.reloadTime;
	}
	
	@Override
	public void reload(Player p, ItemStack is) {
		if (ammoType.countAmmo(p) < 1) return; // No point of reloading as player doesn't have any ammo
		
		p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, getReloadTime(), 2));
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, getReloadTime(), -4));
		final int amountReloading;
		final int amountLoaded = Firearm.getAmmo(is);
		final int amountNeededToReload = clipSize - Firearm.getAmmo(is);
		
		if (ammoType.countAmmo(p) < amountNeededToReload) { // Not enough but still has some
			amountReloading = ammoType.countAmmo(p);
		} else { // Player has enough, fill up the entire mag
			amountReloading = amountNeededToReload;
		}
		p.updateInventory();
		
		int id = new BukkitRunnable() {
			public void run() {
				if (isReloading(p)) {
					Entry<Integer, Integer> reloadingData = reloadingTimes.get(this.getTaskId());
					int totalTicks = reloadingData.getKey();
					int ticksLeft = reloadingData.getValue();
					
					if (!(p.getInventory().getItemInMainHand().equals(is))) {
						cancelReload(p);
						return;
					}
					if (ticksLeft < 0) { // Done
						ammoType.removeAmmo(p, amountReloading);
						Firearm.setAmmo(is, amountLoaded + amountReloading);
						reloadingTimes.remove(this.getTaskId());
						reloadingTasks.remove(p);
						this.cancel();
					} else {
						float percentage = (100 / ((float)totalTicks)) * Math.abs(ticksLeft - totalTicks);
						if ((percentage % 50) == 0) {
							p.playSound(p.getLocation(), Sound.BLOCK_PISTON_EXTEND, 1, 4);
						}
						reloadingTimes.put(this.getTaskId(), new AbstractMap.SimpleEntry<Integer, Integer>(totalTicks, ticksLeft - 1));
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GRAY + "" + "Reloading: " + getProgressBar(percentage)));
					}
				}
			}
		}.runTaskTimer(ZItems.getPlugin(), 1, 1).getTaskId();
		
		reloadingTasks.put(p, id);
		reloadingTimes.put(id, new AbstractMap.SimpleEntry<Integer, Integer>(reloadTime, reloadTime));
	}
	
	@Override
	public void cancelReload(Player p) {
		p.removePotionEffect(PotionEffectType.SLOW);
		p.removePotionEffect(PotionEffectType.JUMP);
		Bukkit.getScheduler().cancelTask(reloadingTasks.get(p));
		reloadingTimes.remove(reloadingTasks.get(p));
		reloadingTasks.remove(p);
	}

	@Override
	public boolean isReloading(Player p) {
		return reloadingTasks.containsKey(p);
	}
	
	private String getProgressBar(float percentage) {
		String msg = "";
		for (float i = 0; i <= 19; i++) {
			if (i <= (percentage / 10) * 2) {
				msg += ChatColor.GREEN + "" + ChatColor.BOLD + "|";
			} else {
				msg += ChatColor.RED + "" + ChatColor.BOLD + "|";
			}
		}
		return ChatColor.WHITE + "" + ChatColor.BOLD + "[" + msg + ChatColor.WHITE + "" + ChatColor.BOLD + "]";
	}
}
