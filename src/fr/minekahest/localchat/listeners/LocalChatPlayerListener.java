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
		String whispsign = plugin.getConfig().getString("whisp-sign");
		String shoutsign = plugin.getConfig().getString("shout-sign");
		String hrpsign = plugin.getConfig().getString("hrp-sign");
		String globalsign = plugin.getConfig().getString("global-sign");
		
		//Recuperation des radius de configuration
		int whispradius = plugin.getConfig().getInt("whisp-radius");
		int localradius = plugin.getConfig().getInt("local-radius");
		int shoutradius = plugin.getConfig().getInt("shout-radius");
		int hrpradius = plugin.getConfig().getInt("hrp-radius");
		
		//Le joueur qui parle
		Player talkingPlayer = event.getPlayer();
		//Le message
		String msg = event.getMessage();
							
		//On casse le chat classique pour pouvoir gérer nous mêle l'evènement
		//Et éviter les double phrases
		event.setCancelled(true);
		
		if (event.getMessage().startsWith(globalsign) && globalsign != "false") {
			//Suppresion du signe
			msg = msg.substring(1).trim();
			//Global on envois le message à tout le monde
			plugin.getServer().broadcastMessage(ChatColor.DARK_GREEN+ talkingPlayer.getName()+": "+msg);
		}
	
		//Whisp
		else if (event.getMessage().startsWith(whispsign) && whispsign != "false") {
			//Suppresion du signe
			msg = msg.substring(1).trim();
			//Verifie la distance des joueurs par rapport à la conf et leur envois le message
			checkDistanceAndSendMessage(talkingPlayer, whispradius, msg, "Chuchotement");
		}
		
		//Shout
		else if (event.getMessage().startsWith(shoutsign) && shoutsign != "false") {
			//Suppresion du signe
			msg = msg.substring(1).trim();
			//Verifie la distance des joueurs par rapport à la conf et leur envois le message
			checkDistanceAndSendMessage(talkingPlayer, shoutradius, msg, "Cri");			
		}
		
		//Hors-roleplay
		else if (event.getMessage().startsWith(hrpsign) && hrpsign != "false") {
			//Suppresion du signe
			msg = msg.substring(1).trim();
			//Verifie la distance des joueurs par rapport à la conf et leur envois le message
			checkDistanceAndSendMessage(talkingPlayer, hrpradius, msg, "Hrp");			
		}
				
		//Local si aucun signe
		else {
			//Verifie la distance des joueurs par rapport à la conf et leur envois le message
			checkDistanceAndSendMessage(talkingPlayer, localradius, msg, "false");			
		}				
	}
	
	
	//Un peu de math
	public void checkDistanceAndSendMessage(Player player, Integer radius, String message, String prefix) {
		
		for (Player listeningPlayer : plugin.getServer().getOnlinePlayers()) {		
			//Calcul des distances
			org.bukkit.Location pLoc = listeningPlayer.getLocation();
			org.bukkit.Location sLoc = player.getLocation();
	        int dx = sLoc.getBlockX() - pLoc.getBlockX();
	        int dz = sLoc.getBlockZ() - pLoc.getBlockZ();
	        dx = dx * dx;
	        dz = dz * dz;
	        int d = (int) Math.sqrt(dx + dz);
	        //Si distance = ok envois, sinon pas de messages :'(
        	if (d <= radius)
        	
        	//Petit fix pour le local
        		if (prefix != "false") {
        	listeningPlayer.sendMessage("("+prefix+") "+player.getDisplayName()+": "+message);
        		} else {
        	listeningPlayer.sendMessage(player.getDisplayName()+": "+message);
        		}
		}
		//On log tout de même sur le serveur histoire de pas laisser l'admin dans les choux :)
		plugin.getLogger().info(player.getName()+": "+message);
	}

}
