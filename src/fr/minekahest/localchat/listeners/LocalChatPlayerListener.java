package fr.minekahest.localchat.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.minekahest.localchat.LocalChat;

public class LocalChatPlayerListener implements Listener {

	private LocalChat plugin;

	public LocalChatPlayerListener(LocalChat instance) {
		plugin = instance;
	}
	
 	//Le joueur parle
	@EventHandler(priority = EventPriority.LOW  )	
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		
		//On stop tout si l'event est annulé
		if (event.isCancelled()) return;
		
		//Recupération des signes définit dans la configuration
		String whispSign = plugin.getConfig().getString("whisp-sign");
		String shoutSign = plugin.getConfig().getString("shout-sign");
		String hrpSign = plugin.getConfig().getString("hrp-sign");
		String globalSign = plugin.getConfig().getString("global-sign");
		String worldSign = plugin.getConfig().getString("world-sign");
		
		//Recuperation des radius de configuration
		int whispRadius = plugin.getConfig().getInt("whisp-radius");
		int localRadius = plugin.getConfig().getInt("local-radius");
		int shoutRadius = plugin.getConfig().getInt("shout-radius");
		int hrpRadius = plugin.getConfig().getInt("hrp-radius");
		
		//Le joueur qui parle
		Player talkingPlayer = event.getPlayer();
		//Le message
		String msg = event.getMessage();
							
		//On casse le chat classique pour pouvoir gérer nous mêle l'evènement
		//Et éviter les double phrases
		event.setCancelled(true);
		
		//Message global
		if (event.getMessage().startsWith(globalSign) && globalSign != "false") {		
			//Suppresion du signe
			msg = msg.substring(1).trim();
			//Format
			String finalColoredPrefix = preFormatMessage("global");
			//Colorisation et envois serveur entier
			sendAllMessage(talkingPlayer, finalColoredPrefix, msg);
		}
		//Message world actuel
		else if (event.getMessage().startsWith(worldSign) && worldSign != "false") {		
				//Suppresion du signe
				msg = msg.substring(1).trim();
				//Format
				String finalColoredPrefix = preFormatMessage("world");
				//Colorisation et envois serveur entier
				sendWorldMessage(talkingPlayer, finalColoredPrefix, msg);			
		}
	
		//Whisp
		else if (event.getMessage().startsWith(whispSign) && whispSign != "false") {
			//Suppresion du signe
			msg = msg.substring(1).trim();
			//Format
			String finalColoredPrefix = preFormatMessage("whisp");
			//Verifie la distance des joueurs par rapport à la conf et leur envois le message
			checkDistanceAndSendMessage(talkingPlayer, whispRadius, finalColoredPrefix, msg);
		}
		
		//Shout
		else if (event.getMessage().startsWith(shoutSign) && shoutSign != "false") {
			//Suppresion du signe
			msg = msg.substring(1).trim();
			//Format
			String finalColoredPrefix = preFormatMessage("shout");
			//Verifie la distance des joueurs par rapport à la conf et leur envois le message
			checkDistanceAndSendMessage(talkingPlayer, shoutRadius, finalColoredPrefix, msg);			
		}
		
		//Hors-roleplay
		else if (event.getMessage().startsWith(hrpSign) && hrpSign != "false") {
			//Suppresion du signe
			msg = msg.substring(1).trim();
			//Format
			String finalColoredPrefix = preFormatMessage("hrp");
			//Verifie la distance des joueurs par rapport à la conf et leur envois le message
			checkDistanceAndSendMessage(talkingPlayer, hrpRadius, finalColoredPrefix, msg);		
		}
		
		//Local si aucun sign
		else {
			String finalColoredPrefix = preFormatMessage("local");
			//Verifie la distance des joueurs par rapport à la conf et leur envois le message
			checkDistanceAndSendMessage(talkingPlayer, localRadius, finalColoredPrefix, msg);			
		}				
	}
	
	//Remplacement du broadcast serveur par un maison
	private void sendAllMessage(Player talkingPlayer, String finalColoredPrefix, String msg) {
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			p.sendMessage(finalColoredPrefix+talkingPlayer.getName()+": "+msg);
			}
		}
	
	//Broadcast par World (pull PunKeel modifié :p)
	protected void sendWorldMessage(Player talkingPlayer, String finalColoredPrefix, String msg) {
		//Boucle de vérification de joueurs sur le même monde
		for (Player p : plugin.getServer().getOnlinePlayers()) {
				if (p.getWorld() == talkingPlayer.getWorld()) {
					p.sendMessage(finalColoredPrefix+talkingPlayer.getName()+": "+msg);
				}
		}
}
		
	//Un peu de formattage
	public String preFormatMessage (String chatType) {
		String prefix = plugin.getConfig().getString(chatType+"-prefix");
		String color = plugin.getConfig().getString(chatType+"-color");
		String coloredPrefix;
		//Si pas de prefix on va faire une petite exeption
		if (prefix != "false") {
			coloredPrefix = ChatColor.translateAlternateColorCodes('&', color) + prefix + " ";
		} else {
			coloredPrefix = ChatColor.translateAlternateColorCodes('&', color);
		}
		return coloredPrefix;
	}
		
	//Calcul des distances et envois
	public void checkDistanceAndSendMessage(Player player, Integer radius, String prefix, String message) {
		
		for (Player listeningPlayer : plugin.getServer().getOnlinePlayers()) {
			//Positions des 2 joueurs testés
			org.bukkit.Location pLoc = listeningPlayer.getLocation();
			org.bukkit.Location sLoc = player.getLocation();
	        //Si distance = ok envois, sinon pas de messages	        
	        if(sLoc.distance(pLoc) <= radius)     
	        //Envois du message
        	listeningPlayer.sendMessage(prefix+player.getDisplayName()+": "+message);
		}
		//On log tout de même sur le serveur histoire de pas laisser l'admin dans les choux :)
		plugin.getLogger().info((prefix+player.getName()+": "+message).substring(2));
	}
}
