package fr.minekahest.localchat;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.minekahest.localchat.listeners.LocalChatPlayerListener;


public class LocalChat extends JavaPlugin {
	
	//Init
    public static LocalChat instance;
    
    public LocalChat() {
    	instance = this;
    }
	   
    //Variables
    public static final Logger log = Logger.getLogger("Minecraft");
  
    @Override
    public void onEnable(){
    	    	
    	//Cree un fichier de config si inexistant
    	this.saveDefaultConfig();
    	  	    	
    	//Enregistrement des listeners
    	PluginManager pm = getServer().getPluginManager();
    	//Joueurs listeners
        pm.registerEvents(new LocalChatPlayerListener(this), this);
    
    }
    
	//ARRET DU PLUGIN
    @Override
    public void onDisable() {
    	
    }
  
}
