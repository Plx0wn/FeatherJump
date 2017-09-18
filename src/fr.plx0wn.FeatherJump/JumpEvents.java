package fr.lucluc.featherjump;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class JumpEvents implements Listener {

	Plugin plugin = Main.instance;

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		final Player player = e.getPlayer();
		Material mat = player.getItemInHand().getType();
		int jump = plugin.getConfig().getInt("multiplier");
		int heightjump = plugin.getConfig().getInt("height-jump");
		Material item = Material.getMaterial(plugin.getConfig().getString("item"));
		Effect effect = Effect.getByName(plugin.getConfig().getString("effect"));
		String noperm = plugin.getConfig().getString("no-perm");

		final int delay = plugin.getConfig().getInt("delay-settings.delay");
		final String delaymsg = plugin.getConfig().getString("delay-settings.delay-message");
		final String delayend = plugin.getConfig().getString("delay-settings.delay-message-end");

		if (e.getPlayer().hasPermission("fjump.jump")) {
			if ((!Main.cooldown.contains(player))
					&& ((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK))
					&& (mat == item)) {
				if (plugin.getConfig().getBoolean("sound")) {
					player.playSound(player.getLocation(), Sound.valueOf(plugin.getConfig().getString("sound-type")),
							0.5F, 3.0F);
				}
				if (plugin.getConfig().getBoolean("particles")) {
					player.getWorld().playEffect(player.getLocation(),
							Effect.valueOf(plugin.getConfig().getString("particles-type")), 10);
				}
				e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().multiply(jump));
				e.getPlayer().setVelocity(
						new Vector(e.getPlayer().getVelocity().getX(), heightjump, e.getPlayer().getVelocity().getZ()));

				Main.cooldown.add(player);
				Main.jumpers.add(player);
				if (plugin.getConfig().getBoolean("delay-settings.enable-delay-message")) {
					Main.cooldownmessage = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
						int count = delay;

						public void run() {
							if (player.getInventory().contains(Material.FEATHER)) {
								Tools.sendActionBar(player, delaymsg.replace("{COUNT}", "" + count));
							}
							count -= 1;
							if (count < 0) {
								Main.cancelTask();
								if (player.getInventory().contains(Material.FEATHER)) {
									Tools.sendActionBar(player, delayend);
								}
							}
						}
					}, 0L, 20L);
				}

				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						Main.cooldown.remove(player);
					}
				}, (delay * 20) + 5);
			}
		} else {
			player.sendMessage(ChatColor.RED + "You don't have the permissions.");
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if ((e.getEntity() instanceof Player)) {
			Player p = (Player) e.getEntity();
			if ((e.getCause() == EntityDamageEvent.DamageCause.FALL) && (Main.jumpers.contains(p))) {
				e.setCancelled(true);
				Main.jumpers.remove(p);
			}
		}
	}

}
