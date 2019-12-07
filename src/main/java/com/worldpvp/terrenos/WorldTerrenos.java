package com.worldpvp.terrenos;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.worldnetwork.spawner.SpawnerPlugin;
import com.worldpvp.terrenos.commands.TerrenoCommand;
import com.worldpvp.terrenos.listeners.TerrenoListener;
import com.worldpvp.terrenos.listeners.TerrenoSpawnerListener;
import com.worldpvp.terrenos.managers.TerrenoManager;

import net.milkbowl.vault.economy.Economy;

public class WorldTerrenos extends JavaPlugin {

	private static WorldTerrenos plugin;
	private static TerrenoManager manager;
	
	private static SpawnerPlugin worldSpawner;
//	private static WorldSuper worldSuper;
//	private static WorldMaquinas worldMaquina;
	
	private static Economy economy;
	private static WorldGuardPlugin worldGuard;
	
	public void onEnable() {
		plugin = this;
		
		saveDefaultConfig();
		
		setupEconomy();
		
		worldSpawner = (SpawnerPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldSpawnerMaven");
		
//		worldSuper = (WorldSuper) Bukkit.getServer().getPluginManager().getPlugin("WorldSuperSpawner");
//		worldMaquina = (WorldMaquinas) Bukkit.getServer().getPluginManager().getPlugin("WorldMaquinas");
		
		worldGuard = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		
		manager = new TerrenoManager();
		manager.enable();
		
		getCommand("t").setExecutor(new TerrenoCommand());
		getCommand("p").setExecutor(new TerrenoCommand());
		
		if(worldSpawner != null) {
			getServer().getPluginManager().registerEvents(new TerrenoSpawnerListener(), this);
		}
		
//		if(worldSuper != null) {
//			getServer().getPluginManager().registerEvents(new TerrenoSuperSpawnerListener(), this);
//		}
		
//		if(worldMaquina != null) {
//			getServer().getPluginManager().registerEvents(new TerrenoMaquinaListener(), this);
//		}
		
		getServer().getPluginManager().registerEvents(new TerrenoListener(), this);
	}
	
	public static WorldTerrenos getInstance() {
		return plugin;
	}
	
	public static SpawnerPlugin getWorldSpawner() {
		return worldSpawner;
	}
	
//	public static WorldMaquinas getWorldMaquina() {
//		return worldMaquina;
//	}
	
//	public static WorldSuper getWorldSuper() {
//		return worldSuper;
//	}
	
	public static TerrenoManager getManager() {
		return manager;
	}
	
	public static Economy getEconomy() {
		return economy;
	}
	
	public static WorldGuardPlugin getWorldGuard() {
		return worldGuard;
	}
	
	private boolean setupEconomy() {
	    if (getServer().getPluginManager().getPlugin("Vault") == null) {
	        return false;
	    }
	    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
	    if (rsp == null) {
	        return false;
	    }
	    
	    economy = rsp.getProvider();
	    return economy != null;
	}
}
