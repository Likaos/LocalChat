package fr.minekahest.localchat;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.minekahest.localchat.listeners.LocalChatPlayerListener;

public class LocalChat extends JavaPlugin {

	// Init
	public static LocalChat instance;

	public LocalChat() {
		instance = this;
	}

	// Variables
	public static final Logger log = Logger.getLogger("Minecraft");
	public String whispSign, shoutSign, hrpSign, globalSign, worldSign;
	public int whispRadius, localRadius, shoutRadius, hrpRadius;

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
		// Joueurs listeners
		pm.registerEvents(new LocalChatPlayerListener(this), this);

	}

	// ARRET DU PLUGIN
	@Override
	public void onDisable() {

	}

}
