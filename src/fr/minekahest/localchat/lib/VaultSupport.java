package fr.minekahest.localchat.lib;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.RegisteredServiceProvider;

import fr.minekahest.localchat.LocalChat;

public class VaultSupport {

	private LocalChat plugin;

	// Variables Vault
	public Economy econ = null;
	public Permission perms = null;
	public Chat chat = null;

	public VaultSupport(LocalChat plugin) {
		this.plugin = plugin;
		
		vaultSetupChat();
		vaultSetupEconomy();
		vaultSetupPermissions();
	}

	// Vault setups
	private boolean vaultSetupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = plugin
				.getServer()
				.getServicesManager()
				.getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			perms = permissionProvider.getProvider();
		}
		return (perms != null);
	}

	private boolean vaultSetupChat() {
		RegisteredServiceProvider<Chat> chatProvider = plugin.getServer()
				.getServicesManager()
				.getRegistration(net.milkbowl.vault.chat.Chat.class);
		if (chatProvider != null) {
			chat = chatProvider.getProvider();
		}

		return (chat != null);
	}

	private boolean vaultSetupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = plugin.getServer()
				.getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			econ = economyProvider.getProvider();
		}

		return (econ != null);
	}

}
