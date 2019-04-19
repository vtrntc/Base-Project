package com.vtr.habilidades;

import org.bukkit.plugin.java.JavaPlugin;

import com.vtr.api.spigot.APISpigot;
import com.vtr.api.spigot.misc.YamlConfig;
import com.vtr.habilidades.commands.HabilidadeCommand;
import com.vtr.habilidades.listeners.HabilidadeListener;
import com.vtr.habilidades.managers.HabilidadeManager;
import com.vtr.habilidades.user.HabilidadeUserModuleFactory;

public class HabilidadePlugin extends JavaPlugin {

	private static HabilidadePlugin plugin;
	private static HabilidadeManager manager;
	
	private static YamlConfig yamlConfig;
	
	private static HabilidadeUserModuleFactory moduleFactory;
	
	public void onEnable() {
		plugin = this;
		
		saveDefaultConfig();
		
		yamlConfig = new YamlConfig("config", plugin);
		
		moduleFactory = new HabilidadeUserModuleFactory();
		
		manager = new HabilidadeManager();
		manager.enable();
		
		APISpigot.getInstance().registerListener(plugin, new HabilidadeListener());
		
		APISpigot.getInstance().registerCommand(new HabilidadeCommand());
	}
	
	public static HabilidadePlugin getInstance() {
		return plugin;
	}
	
	public static HabilidadeManager getManager() {
		return manager;
	}
	
	public static YamlConfig getYamlConfig() {
		return yamlConfig;
	}
	
	public static HabilidadeUserModuleFactory getModuleFactory() {
		return moduleFactory;
	}
}
