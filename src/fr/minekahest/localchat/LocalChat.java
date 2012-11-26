package fr.minekahest.localchat;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.minekahest.localchat.lib.VaultSupport;
import fr.minekahest.localchat.listeners.LocalChatCommandExecutor;
import fr.minekahest.localchat.listeners.LocalChatPlayerListener;

public class LocalChat extends JavaPlugin {
	
	// Init
	public static LocalChat instance;
	
	// Variables
	public static final Logger log = Logger.getLogger("Minecraft");
	public String whispSign, shoutSign, hrpSign, serverSign, worldSign, realLostChar;
	public int whispRadius, localRadius, shoutRadius, hrpRadius, realRadius, serverFlood, worldFlood, realLostLetters;
	public boolean realAnonym;
	public ArrayList<String> spies = new ArrayList<String>();
	public ArrayList<String> worldFloodTimer = new ArrayList<String>();
	public ArrayList<String> serverFloodTimer = new ArrayList<String>();
	
	// VaultSupport
	public VaultSupport vault;
	
	public LocalChat() {
		instance = this;
	}
	
	@Override
	public void onEnable() {
		
		// Cree un fichier de config si inexistant
		saveDefaultConfig();
		
		// Recuperation des signs
		whispSign = getConfig().getString("whisp-sign");
		shoutSign = getConfig().getString("shout-sign");
		hrpSign = getConfig().getString("hrp-sign");
		serverSign = getConfig().getString("server-sign");
		worldSign = getConfig().getString("world-sign");		
		// Recuperation des radius de configuration
		whispRadius = getConfig().getInt("whisp-radius");
		localRadius = getConfig().getInt("local-radius");
		shoutRadius = getConfig().getInt("shout-radius");
		hrpRadius = getConfig().getInt("hrp-radius");
		// Recuperation des radius du mode realiste
		realRadius = getConfig().getInt("real-radius");		
		realAnonym = getConfig().getBoolean("real-anonym");
		realLostLetters = getConfig().getInt("real-lost-letters");
		realLostChar = getConfig().getString(("real-lost-char"));
		// Recuperation des temps de flood et conversion en secondes
		serverFlood = getConfig().getInt("server-flood") * 20;
		worldFlood = getConfig().getInt("world-flood") * 20;
		
		// Enregistrement des listeners
		PluginManager pm = getServer().getPluginManager();
		
		// Support de Vault si présent sur le serveur
		if (pm.getPlugin("Vault") != null) {
			vault = new VaultSupport(this);
		}
		
		// Joueurs listeners
		pm.registerEvents(new LocalChatPlayerListener(this), this);
		
		// Commandes
		getCommand("localchat").setExecutor(new LocalChatCommandExecutor(this));
		
	}
	
	// ARRET DU PLUGIN
	@Override
	public void onDisable() {
		
	}
	
	// Check si vault est actif, assure la compatabilité sans permissions
	public Boolean permissionSafeCheck(Player player, String permission) {
		if (this.vault != null) {
			return vault.perms.has(player, permission);
		}
		return false;
	}
	
}
