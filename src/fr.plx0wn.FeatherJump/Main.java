package fr.plx0wn.featherjump;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	static ArrayList<Player> jumpers = new ArrayList<Player>();
	static ConsoleCommandSender console = Bukkit.getConsoleSender();
	static ArrayList<Player> cooldown = new ArrayList<Player>();
	static Plugin instance;
	static int cooldownmessage;

	static void cancelTask() {
		instance.getServer().getScheduler().cancelTask(cooldownmessage);
	}

	static void consoleCM(String msg) {
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}

	public void onEnable() {
		instance = this;
		
		// CONFIG
		getConfig().options().copyDefaults();
		saveDefaultConfig();
		
		// EVENTS
		Bukkit.getServer().getPluginManager().registerEvents(new JumpEvents(), this);
		
		console.sendMessage(ChatColor.GREEN + "Thank's to have downloaded FeatherJump!");
		
		// COMPATIBILITY
		if(Bukkit.getVersion().contains("1.7")){
			getConfig().set("delay-settings.enable-delay-message", false);
		}

	}

	public static void swColor(Player player, String msg) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("fjump")) {

			if ((args.length == 0) && (sender.hasPermission("fjump.info"))) {
				sender.sendMessage(ChatColor.GREEN + "---------- [FeatherJump] ----------");
				sender.sendMessage(ChatColor.GREEN + "/fjump reload");
				sender.sendMessage(ChatColor.GREEN + "/fjump tuto");
				sender.sendMessage(ChatColor.GREEN + "---------------------------------");
			}

			// TUTO
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("tuto")) {
					if (sender.hasPermission("fjump.tuto")) {
						sender.sendMessage(ChatColor.GREEN + "[FeatherJump] " + ChatColor.WHITE
								+ "Just right click on feather for have double-jump! (If you're on the ground)");
					} else {
						sender.sendMessage(ChatColor.RED + "You don't have the permissions.");
					}
				}

				// RELOAD
				if (args[0].equalsIgnoreCase("reload")) {
					if (sender.hasPermission("fjump.reload")) {
						sender.sendMessage(ChatColor.GREEN + "[FeatherJump] " + ChatColor.WHITE
								+ "The configuration file has been reloaded successfully.");
						console.sendMessage(ChatColor.GREEN + "[FeatherJump] " + ChatColor.WHITE
								+ "The configuration file has been reloaded by " + sender.getName());
						reloadConfig();
					} else {
						sender.sendMessage(ChatColor.RED + "You don't have the permissions.");
					}
				}
			}
		}
		return false;
	}


}
