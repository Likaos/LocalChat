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
	public String whispSign, shoutSign, hrpSign, globalSign, worldSign;
	public int whispRadius, localRadius, shoutRadius, hrpRadius;
	public ArrayList<String> spies = new ArrayList<String>();
	
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
		globalSign = getConfig().getString("global-sign");
		worldSign = getConfig().getString("world-sign");
		
		// Recuperation des radius de configuration
		whispRadius = getConfig().getInt("whisp-radius");
		localRadius = getConfig().getInt("local-radius");
		shoutRadius = getConfig().getInt("shout-radius");
		hrpRadius = getConfig().getInt("hrp-radius");
		
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
	
	public Boolean permissionSafeCheck(Player player, String permission) {
		if (this.vault!=null) {
			return vault.perms.has(player, permission);
		}
		return false;
	}
	
}
