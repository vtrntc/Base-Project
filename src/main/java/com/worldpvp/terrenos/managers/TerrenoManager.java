package com.worldpvp.terrenos.managers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.worldnetwork.spawner.SpawnerPlugin;
import com.worldpvp.terrenos.WorldTerrenos;
import com.worldpvp.terrenos.objects.Cuboid;
import com.worldpvp.terrenos.objects.Square;
import com.worldpvp.terrenos.objects.Terreno;
import com.worldpvp.terrenos.objects.TerrenoSize;
import com.worldpvp.terrenos.objects.TerrenoVenda;
import com.worldpvp.utils.builders.ItemBuilder;
import com.worldpvp.utils.utils.DatabaseUtils;
import com.worldpvp.utils.utils.LocationUtils;
import com.worldpvp.utils.utils.StringUtils;

public class TerrenoManager {

	private Inventory buyInventory;

	private Map<Player, BukkitTask> confirm;
	private Map<String, List<Terreno>> terrenos;
	
	public TerrenoManager() {
		this.confirm = new HashMap<>();
		this.terrenos = new HashMap<>();
	}
	
	@SuppressWarnings("unchecked")
	public void enable() {
		TerrenoSize.load();
		
		this.buyInventory = Bukkit.createInventory(null, WorldTerrenos.getInstance().getConfig().getInt("Menus.Comprar.Rows") * 9, StringUtils.convert(WorldTerrenos.getInstance().getConfig().getString("Menus.Comprar.Nome")));

		for(TerrenoSize terrenoSize : TerrenoSize.getSizes()) {
			buyInventory.setItem(terrenoSize.getSlot(), terrenoSize.getIcon());
		}
		
		try {
			DatabaseUtils.getStatement().executeUpdate("CREATE TABLE IF NOT EXISTS terrenos (id INTEGER NOT NULL AUTO_INCREMENT, nome VARCHAR(32), terrenoID INTEGER, coisas MEDIUMTEXT, PRIMARY KEY (id))");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			ResultSet rs = DatabaseUtils.getStatement().executeQuery("SELECT * FROM terrenos");
			if(rs.next()) {
				do{
					if(!terrenos.containsKey(rs.getString("nome").toLowerCase())) {
						terrenos.put(rs.getString("nome").toLowerCase(), new ArrayList<>());
					}
					
					JSONObject json = getJSONObjectFromString(rs.getString("coisas"));
					
					TerrenoVenda terrenoVenda = null;
					if(json != null && json.containsKey("VendaSign") && json.containsKey("VendaPrice")) {
						String loc = (String) json.get("VendaSign");
						if(loc != null) {
							Location location = LocationUtils.deserializeLocation(loc);
							if(location != null && location.getBlock().getState() instanceof Sign) {
								terrenoVenda = new TerrenoVenda((Sign) location.getBlock().getState(), (int) (long) json.get("VendaPrice"));
							}
						}
					}
					
					
					Map<EntityType, Integer> spawners = new HashMap<>();
					if(WorldTerrenos.getWorldSpawner() != null) {
						if(json.containsKey("Spawners")) {
							
							Map<String, Long> fromJSON = (Map<String, Long>) json.get("Spawners");
							for(Entry<String, Long> e : fromJSON.entrySet()) {
								spawners.put(EntityType.valueOf(e.getKey()), (int) (long) e.getValue());
							}
						}
					}
					
					Map<String, Integer> superSpawners = new HashMap<>();
//					if(WorldTerrenos.getWorldSuper() != null) {
//						if(json.containsKey("SuperSpawners")) {
//						
//							Map<String, Long> fromJSON = (Map<String, Long>) json.get("SuperSpawners");
//							for(Entry<String, Long> e : fromJSON.entrySet()) {
//								superSpawners.put(e.getKey(), (int) (long) e.getValue());
//							}
//						}
//					}
					
					Map<String, Integer> maquinas = new HashMap<>();
//					if(WorldTerrenos.getWorldMaquina() != null) {
//						if(json.containsKey("Maquinas")) {
//						
//							Map<String, Long> fromJSON = (Map<String, Long>) json.get("Maquinas");
//							for(Entry<String, Long> e : fromJSON.entrySet()) {
//								maquinas.put(e.getKey(), (int) (long) e.getValue());
//							}
//						}
//					}
					
					terrenos.get(rs.getString("nome").toLowerCase()).add(new Terreno(rs.getString("nome"), LocationUtils.deserializeLocation((String) json.get("Center")), TerrenoSize.getByName((String) json.get("TerrenoSize")), rs.getInt("terrenoID"), terrenoVenda, LocationUtils.deserializeLocation((String) json.get("Spawn")), (List<String>) json.get("Impedidos"), spawners, superSpawners, maquinas));
					
				}while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static JSONObject getJSONObjectFromString(String s) {
		try {
			return (JSONObject) new JSONParser().parse(s);
		} catch(ParseException e) {
		}
		
		return null;
	}
	
	public boolean isAllowedWorld(World world) {
		return WorldTerrenos.getInstance().getConfig().getString("Config.Mundo").equalsIgnoreCase(world.getName());
	}
	
	@SuppressWarnings("deprecation")
	public long getSpawnerPrice(EntityType entityType) {
		if(!WorldTerrenos.getInstance().getConfig().isSet("Spawners." + SpawnerPlugin.getManager().getOldEntity(entityType.getName()))) {
			return 0;
		}
 		
		return WorldTerrenos.getInstance().getConfig().getLong("Spawners." + SpawnerPlugin.getManager().getOldEntity(entityType.getName()));
	}
	
	public Inventory getBuyInventory() {
		return buyInventory;
	}
	
	public int getCustoPvP() {
		return WorldTerrenos.getInstance().getConfig().getInt("Config.CustoPvP");
	}
	
	public int getCustoMob() {
		return WorldTerrenos.getInstance().getConfig().getInt("Config.CustoMob");
	}
	
	public void setConfirm(Player player) {
		confirm.put(player, new BukkitRunnable() {
			public void run() {
				confirm.remove(player);
			}
		}.runTaskLater(WorldTerrenos.getInstance(), 20 * 3));
	}
	
	public boolean hasConfirm(Player player) {
		return confirm.containsKey(player);
	}
	
	private int getNextTerrenoID(Player player) {
		if(!terrenos.containsKey(player.getName().toLowerCase()) || terrenos.get(player.getName().toLowerCase()).isEmpty()) {
			return 1;
		}else{
			int id = 1;
			
			List<Integer> ids = new ArrayList<>();
			for(Terreno terreno : getTerrenos(player.getName())) {
				ids.add(terreno.getId());
			}
			
			while(ids.contains(id)) {
				id++;
			}
			
			return id;
		}
	}
	
	public String formatList(List<String> list) {
		String formated = "";
		
		for(String s : list) {
			formated += s + ", "; 
		}
		
		return formated.isEmpty() ? "" : formated.substring (0, formated.length() - 2);
	}
	
	public Terreno getTerreno(String player, int id) {
		for(Terreno terreno : getTerrenos(player)) {
			if(terreno.getId() == id) {
				return terreno;
			}
		}
		
		return null;
	}
	
	public Terreno getTerreno(Location location) {
		ApplicableRegionSet set = WorldTerrenos.getWorldGuard().getRegionManager(location.getWorld()).getApplicableRegions(new Location(location.getWorld(), location.getX(), 1, location.getZ()));
		for(ProtectedRegion pr : set.getRegions()) {
			if(pr != null && pr.getId().contains("-")) {
				String[] all = pr.getId().split("-");
				for(Terreno terreno : getTerrenos(all[0])) {
					if(terreno.getRegion() != null && terreno.getRegion().equals(pr)) {
						return terreno;
					}
				}
			}
		}
		
		return null;
	}
	
	public void abandonaTerreno(Terreno terreno) {
		if(terrenos.get(terreno.getPlayer().toLowerCase()).contains(terreno)) {
			terrenos.get(terreno.getPlayer().toLowerCase()).remove(terreno);
		}
		
		try {
			DatabaseUtils.getStatement().execute("DELETE FROM `terrenos` WHERE nome='" + terreno.getPlayer() + "' AND terrenoID='" + terreno.getId() + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		ProtectedRegion region = WorldTerrenos.getWorldGuard().getRegionManager(terreno.getCenter().getWorld()).getRegion(terreno.getRegion().getId());
		if(region != null) {
			WorldTerrenos.getWorldGuard().getRegionManager(terreno.getCenter().getWorld()).removeRegion(region.getId());
		}
	}
	
	public Inventory getVisitarInventory(String target) {
		Inventory inventory = Bukkit.createInventory(null, WorldTerrenos.getInstance().getConfig().getInt("Menus.Visitar.Rows") * 9, StringUtils.convert(WorldTerrenos.getInstance().getConfig().getString("Menus.Visitar.Nome")));
		
		for(Terreno terreno : getTerrenos()) {
			if(!terreno.isFriend(target)) {
				continue;
			}
			
			ItemBuilder itemBuilder = new ItemBuilder(WorldTerrenos.getInstance().getConfig().getString("Menus.Visitar.Item.ID"));
			itemBuilder.setName(WorldTerrenos.getInstance().getConfig().getString("Menus.Visitar.Item.Nome").replaceAll("%id%", Integer.toString(terreno.getId())));
			
			List<String> lore = new ArrayList<>();
			for(String s : WorldTerrenos.getInstance().getConfig().getStringList("Menus.Visitar.Item.Lore")) {
				lore.add(s.replaceAll("%maquinas%", Integer.toString(terreno.getMaquinasAmount())).replaceAll("%super%", Integer.toString(terreno.getSuperSpawnersAmount())).replaceAll("%spawners%", Integer.toString(terreno.getSpawnersAmount())).replaceAll("%id%", Integer.toString(terreno.getId())).replaceAll("%player%", terreno.getPlayer()));
			}
			
			itemBuilder.setLore(lore);
			
			itemBuilder.setNBT("TerrenoID", Integer.toString(terreno.getId()));
			itemBuilder.setNBT("Dono", terreno.getPlayer());
			
			inventory.setItem(inventory.firstEmpty(), itemBuilder.build());
		}
		
		return inventory;
	}
	
	public Inventory getIrInventory(String target) {
		Inventory inventory = Bukkit.createInventory(null, WorldTerrenos.getInstance().getConfig().getInt("Menus.Ir.Rows") * 9, StringUtils.convert(WorldTerrenos.getInstance().getConfig().getString("Menus.Ir.Nome")));
		
		List<Terreno> copy = new ArrayList<>(getTerrenos(target));
		
		copy.sort((t1, t2) -> {
			return Integer.compare(getTerrenoSortID(t1), getTerrenoSortID(t2));
		});
		
		for(Terreno terreno : copy) {
			int terrenoID = getTerrenoSortID(terreno);
			
			ItemBuilder item = new ItemBuilder(Material.WOOL);
			if(terrenoID == 3) {
				item.setDurability((short) 5);
			}else if(terrenoID == 1) {
				item.setDurability((short) 14);
			}else if(terrenoID == 2) {
				item.setDurability((short) 4);
			}
			
			item.setName(WorldTerrenos.getInstance().getConfig().getString("Menus.Ir.La.Nome").replaceAll("%id%", Integer.toString(terreno.getId())));
		
			List<String> lore = new ArrayList<>();
			for(String s : WorldTerrenos.getInstance().getConfig().getStringList("Menus.Ir.La.Lore")) {
				lore.add(StringUtils.convert(s).replaceAll("%maquinas%", Integer.toString(terreno.getMaquinasAmount())).replaceAll("%super%", Integer.toString(terreno.getSuperSpawnersAmount())).replaceAll("%spawners%", Integer.toString(terreno.getSpawnersAmount())).replaceAll("%mob%", terreno.isMobSpawning() ? "ligado" : "desligado").replaceAll("%pvp%", terreno.isPvP() ? "ligado" : "desligado"));
			}
			
			item.setLore(lore);
			
			item.setNBT("TerrenoID", Integer.toString(terreno.getId()));
			item.setNBT("Dono", target);
			
			inventory.setItem(inventory.firstEmpty(), item.build());
		}
		
		return inventory;
	}
	
	private int getTerrenoSortID(Terreno terreno) {
		if(terreno.isPvP()) {
			return 1;
		}else if(terreno.isMobSpawning()) {
			return 2;
		}
		
		return 3;
	}
	
	private void setupRegion(String player, Location center, TerrenoSize terrenoSize, int id, boolean isPvP, boolean isMobSpawning) {
		Square square = new Square(center, terrenoSize.getSize());
		
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(player + "-" + id, new BlockVector(square.getMaxLocation().getBlockX(), 256, square.getMaxLocation().getBlockZ()), new BlockVector(square.getMinLocation().getBlockX(), 0, square.getMinLocation().getBlockZ()));
		DefaultDomain domain = new DefaultDomain();
		
		domain.addPlayer(player);
		
		region.setPriority(100);
		region.setOwners(domain);
		
		region.setFlag(DefaultFlag.MOB_DAMAGE, StateFlag.State.ALLOW);
		region.setFlag(DefaultFlag.DAMAGE_ANIMALS, StateFlag.State.ALLOW);
		
		WorldTerrenos.getWorldGuard().getRegionManager(center.getWorld()).addRegion(region);

		region.setFlag(DefaultFlag.PVP, isPvP ? StateFlag.State.ALLOW : StateFlag.State.DENY);
		region.setFlag(DefaultFlag.MOB_SPAWNING, isMobSpawning ? StateFlag.State.ALLOW : StateFlag.State.DENY);
	}
	
	public void transfereTerreno(Player player, Terreno terreno) {
		boolean isPvP = terreno.isPvP();
		boolean isMobSpawning = terreno.isMobSpawning();
		
		abandonaTerreno(terreno);

		int id = getNextTerrenoID(player);
		
		setupRegion(player.getName(), terreno.getCenter(), terreno.getTerrenoSize(), id, isPvP, isMobSpawning);

		Terreno novo = new Terreno(player.getName(), terreno.getCenter(), terreno.getTerrenoSize(), id, null, terreno.getSpawn(), new ArrayList<>(), terreno.getSpawners(), terreno.getSuperSpawners2(), terreno.getMaquinas());
		
		if(!terrenos.containsKey(player.getName().toLowerCase())) {
			terrenos.put(player.getName().toLowerCase(), new ArrayList<>());
		}
		
		terrenos.get(player.getName().toLowerCase()).add(novo);
		
		novo.updateMySQL();
	}
	
	public void compraTerreno(Player player, TerrenoSize terrenoSize) {
		Location center = player.getLocation();
		center.setY(center.getWorld().getHighestBlockYAt(center));
		
		Square square = new Square(center, terrenoSize.getSize());
		
		for(Location loc : square.getSquareLocations(center.getBlockY())) {
			loc.getBlock().setType(Material.FENCE);
		}
		
		int id = getNextTerrenoID(player);
		
		setupRegion(player.getName(), center, terrenoSize, id, false, false);

		Terreno terreno = new Terreno(player.getName(), center, terrenoSize, id);
		
		if(!terrenos.containsKey(player.getName().toLowerCase())) {
			terrenos.put(player.getName().toLowerCase(), new ArrayList<>());
		}
		
		terrenos.get(player.getName().toLowerCase()).add(terreno);

		WorldTerrenos.getEconomy().withdrawPlayer(player, terrenoSize.getCost());

		terreno.updateMySQL();
	}
	
	public boolean isRegion(Location location, TerrenoSize terrenoSize) {
		Cuboid cuboid = new Cuboid(location, terrenoSize.getSize());
		for(Block b : cuboid.getBlocks()) {
			 ApplicableRegionSet set = WorldTerrenos.getWorldGuard().getRegionManager(location.getWorld()).getApplicableRegions(new Location(location.getWorld(), b.getLocation().getX(), 5, b.getLocation().getZ()));
			 for(ProtectedRegion pr : set.getRegions()) {
				 if(pr != null) {
					 return true;
				 }
			 }
		}
		
		return false;
	}
	
	public List<Terreno> getTerrenos() {
		List<Terreno> list = new ArrayList<>();
		
		for(List<Terreno> t : terrenos.values()) {
			list.addAll(t);
		}
		
		return list;
	}
	
	public List<Terreno> getTerrenos(String player) {
		if(!terrenos.containsKey(player.toLowerCase())) {
			return new ArrayList<>();
		}
		
		return terrenos.get(player.toLowerCase());
	}
}
