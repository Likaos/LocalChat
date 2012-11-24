package fr.minekahest.localchat.listeners;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.minekahest.localchat.LocalChat;

public class LocalChatCommandExecutor implements CommandExecutor {
	
	private LocalChat plugin;
	
	public LocalChatCommandExecutor(LocalChat plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Ne fonctionne pas en console
		if (!(sender instanceof Player)) {
			plugin.getLogger().info("Cette commande est uniquement utilisable en jeu !");
			return true;
		}
		
		// On estime que le sender est un joueur
		Player player = (Player) sender;
		// Verification de la commande localchat + un argument
		if (command.getLabel().equals("localchat")) {
			// Verif des droits ou OP
			if (player.isOp() || plugin.permissionSafeCheck(player, "localchat.spy")) {
				// Un seul argument
				if (args.length == 1) {
					if (args[0].equals("show")) {
						plugin.spies.add(player.getName());
						player.sendMessage(ChatColor.YELLOW + "Mode espion: ON");
						return true;
					} else if (args[0].equals("hide")) {
						plugin.spies.remove(player.getName());
						player.sendMessage(ChatColor.YELLOW + "Mode espion: OFF");
						return true;
					}
				}
				
			} else {
				player.sendMessage(ChatColor.RED + "Vous n'avez pas les droits !");
				return true;
			}
		}
		return false;
	}
	
}
