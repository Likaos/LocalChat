package fr.minekahest.localchat.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
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
	
	// Le joueur parle
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		
		// On stop tout si l'event est annule
		if (event.isCancelled())
			return;
		
		// Le joueur qui parle
		Player talkingPlayer = event.getPlayer();
		// Le message
		String msg = event.getMessage();
		
		// On casse le chat classique pour pouvoir gerer l'evenement
		event.setCancelled(true);
		
		// Message server
		if (event.getMessage().startsWith(plugin.serverSign) && plugin.serverSign != "false") {
			if (isOnFloodCooldown(talkingPlayer, "server")) {
				talkingPlayer.sendMessage(ChatColor.RED+"Merci d'éviter le flood sur le chan serveur ! :) ");
				return;
			}
			// Suppression du signe
			msg = msg.substring(1).trim();
			String finalColoredPrefix = preFormatMessage("server");
			sendAllMessage(talkingPlayer, finalColoredPrefix, msg);
			schedulePutAndRemoveFromFloodList(talkingPlayer, plugin.serverFlood, "server");
		}
		// Message world actuel
		else if (event.getMessage().startsWith(plugin.worldSign) && plugin.worldSign != "false") {
			if (isOnFloodCooldown(talkingPlayer, "world")) {
				talkingPlayer.sendMessage(ChatColor.RED+"Merci d'éviter le flood sur le chan monde ! :) ");
				return;
			}
			msg = msg.substring(1).trim();
			String finalColoredPrefix = preFormatMessage("world");
			sendWorldMessage(talkingPlayer, finalColoredPrefix, msg);
			schedulePutAndRemoveFromFloodList(talkingPlayer, plugin.serverFlood, "world");
		}
		
		// Message definit pour une zone
		else {
			
			String finalColoredPrefix;
			int radius = 0;
			
			// Whisp
			if (event.getMessage().startsWith(plugin.whispSign) && plugin.whispSign != "false") {
				msg = msg.substring(1).trim();
				finalColoredPrefix = preFormatMessage("whisp");
				radius = plugin.whispRadius;
			}
			
			// Shout
			else if (event.getMessage().startsWith(plugin.shoutSign) && plugin.shoutSign != "false") {
				msg = msg.substring(1).trim();
				finalColoredPrefix = preFormatMessage("shout");
				radius = plugin.shoutRadius;
			}
			
			// Hors-roleplay
			else if (event.getMessage().startsWith(plugin.hrpSign) && plugin.hrpSign != "false") {
				msg = msg.substring(1).trim();
				finalColoredPrefix = preFormatMessage("hrp");
				radius =  plugin.hrpRadius;
			}
			
			// Local si aucun sign
			else {
				finalColoredPrefix = preFormatMessage("local");
				radius = plugin.localRadius;
			}
			
			checkDistanceAndSendMessage(talkingPlayer, radius, finalColoredPrefix, msg);
		}
		
	}
	
	// Remplacement du broadcast serveur par un maison
	private void sendAllMessage(Player talkingPlayer, String finalColoredPrefix, String msg) {
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			p.sendMessage(finalColoredPrefix + talkingPlayer.getName() + ": " + msg);
		}
	}
	
	// Broadcast par World (pull PunKeel modifie :p)
	protected void sendWorldMessage(Player talkingPlayer, String finalColoredPrefix, String msg) {
		// Boucle de verification de joueurs sur le meme monde
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if (p.getWorld() == talkingPlayer.getWorld() || plugin.spies.contains(p.getName())) {
				p.sendMessage(finalColoredPrefix + talkingPlayer.getName() + ": " + msg);
			}
		}
	}
	
	// Un peu de formattage
	public String preFormatMessage(String chatType) {
		String prefix = plugin.getConfig().getString(chatType + "-prefix");
		String color = plugin.getConfig().getString(chatType + "-color");
		String coloredPrefix;
		// Si pas de prefix on va faire une petite exeption
		if (prefix != "false") {
			coloredPrefix = ChatColor.translateAlternateColorCodes('&', color) + prefix + " ";
		} else {
			coloredPrefix = ChatColor.translateAlternateColorCodes('&', color);
		}
		return coloredPrefix;
	}
	
	// Calcul des distances et envois du message
	public void checkDistanceAndSendMessage(Player player, Integer radius, String prefix, String message) {
		
		//Petit calcul
		int realRadius = radius + Math.round(radius*plugin.realRadius/100);
		
		for (Player listeningPlayer : plugin.getServer().getOnlinePlayers()) {
			// Positions des 2 joueurs test�s
			Location pLoc = listeningPlayer.getLocation();
			Location sLoc = player.getLocation();
			// Si distance = ok ou qu'un op est en ecoute, envois, sinon pas de messages 
			if (sLoc.distance(pLoc) <= radius || plugin.spies.contains(listeningPlayer.getName())){
				listeningPlayer.sendMessage(prefix + player.getDisplayName() + ": " + message);				
			}
			// Mode realiste, le joueur est-il assez proche
			else if (sLoc.distance(pLoc) <= realRadius) {
				//Mode anonyme
				if (plugin.realAnonym) {
					listeningPlayer.sendMessage(prefix + eatLetters(message));
				}
				else {
					listeningPlayer.sendMessage(prefix + player.getDisplayName() + ": " + eatLetters(message));	
				}

			}

		}
		// On log tout de meme sur le serveur
		plugin.getLogger().info((prefix + player.getName() + ": " + message).substring(2));
	}
	
	//Le joueur est-t'il toujours dans la liste de flood ?
	public boolean isOnFloodCooldown(Player player, String channel) {	
		if (channel == "world" && plugin.worldFloodTimer.contains(player.getName())) {
			return true;
		}
		else if (channel == "server" && plugin.serverFloodTimer.contains(player.getName())) {
			return true;
		}
		return false;
	}
	
	//Ajoute le joueur et le retire plus tard
	public void schedulePutAndRemoveFromFloodList(final Player player, int delay, String channel) {
		
		ArrayList<String> floodTimerList = null;
		final String playerName = player.getName();
		
		if (channel == "world") {
		floodTimerList = plugin.worldFloodTimer; 
		plugin.worldFloodTimer.add(playerName);
		}
		else if (channel == "server") {
		floodTimerList = plugin.serverFloodTimer; 
		plugin.serverFloodTimer.add(playerName);
		}
		
		final ArrayList<String> finalFloodTimerList = floodTimerList;		
		
		Runnable removeTask = new Runnable() {
			@Override
			public void run() {
				finalFloodTimerList.remove(playerName);
			}
		};
		//Lancement de tâche
		plugin.getServer().getScheduler().runTaskLater(plugin, removeTask, delay);
	}
	
	//Fonction pour manger les lettres
	public String eatLetters(String string) {
		List<Character> letters = new ArrayList<Character>();
		Random randPicker = new Random();
		//Transfert de la String dans la list
		for (char c:string.toCharArray()) {
			//Random 100
			int r = randPicker.nextInt(100);
			//Si dans le % définit la lettre est perdue (exeption des espaces)
			if (r < plugin.realLostLetters && c != ' ') {				
				letters.add(plugin.realLostChar.charAt(0));
			//Lettre conservée
			} else {
				letters.add(c);	
			}
		}
		
		//Reconstruction de la phrase
		StringBuilder output = new StringBuilder();
		for (Character c : letters) {
			output.append(c);
		}
		return output.toString();
	}
	

	//Fonction melanger les lettres :p
	/*
	public String shake(String string) {
		
		 List<Character> letters = new ArrayList<Character>();
	        for(char c:string.toCharArray()){
	            letters.add(c);
	        }
	        StringBuilder output = new StringBuilder(string.length());
	        while(letters.size()!=0){
	            int randPicker = (int)(Math.random()*letters.size());
	            output.append(letters.remove(randPicker));
	        }
	        return output.toString();
	}
	*/
}