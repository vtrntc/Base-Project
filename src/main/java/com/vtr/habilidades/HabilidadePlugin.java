package com.vtr.habilidades;

import org.bukkit.plugin.java.JavaPlugin;

import com.vtr.api.spigot.APISpigot;
import com.vtr.api.spigot.misc.YamlConfig;
import com.vtr.habilidades.commands.HabilidadeCommand;
import com.vtr.habilidades.listeners.HabilidadeListener;
import com.vtr.habilidades.managers.HabilidadeManager;

public class HabilidadePlugin extends JavaPlugin {

	private static HabilidadePlugin plugin;
	private static HabilidadeManager manager;
	
	private static YamlConfig yamlConfig;
	
	public void onEnable() {
		plugin = this;
		
		saveDefaultConfig();
		
		yamlConfig = new YamlConfig("config", plugin);
		
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
}