package com.worldpvp.terrenos.objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.worldpvp.terrenos.WorldTerrenos;
import com.worldpvp.utils.utils.DatabaseUtils;

public class Terreno {

	private String player;
	
	private Location center;
	private Location spawn;

	private TerrenoSize terrenoSize;
	private TerrenoVenda terrenoVenda;
	
	private int terrenoID;
	
	private List<String> friends;
	private List<String> impedidos;

	private Map<EntityType, Integer> spawners;
	private Map<String, Integer> superSpawners;
	private Map<String, Integer> maquinas;
	
	public Terreno(String player, Location center, TerrenoSize terrenoSize, int terrenoID, TerrenoVenda terrenoVenda, Location spawn, List<String> impedidos, Map<EntityType, Integer> spawners, Map<String, Integer> superSpawners, Map<String, Integer> maquinas) {
		this.player = player;
		this.center = center;
		this.spawn = spawn;
		this.terrenoSize = terrenoSize;
		this.terrenoID = terrenoID;
		this.terrenoVenda = terrenoVenda;
		this.impedidos = impedidos;
		this.spawners = spawners;
		this.superSpawners = superSpawners;
		this.maquinas = maquinas;
		this.friends = new ArrayList<>();

		for(String friend : getRegion().getMembers().getPlayers()) {
			friends.add(friend);
		}
	}
	
	public Terreno(String player, Location center, TerrenoSize terrenoSize, int terrenoID) {
		this(player, center, terrenoSize, terrenoID, null, center, new ArrayList<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
	}
	
	public String getPlayer() {
		return player;
	}
	
	public Location getCenter() {
		return center;
	}
	
	public TerrenoSize getTerrenoSize() {
		return terrenoSize;
	}
	
	public ProtectedCuboidRegion getRegion() {
		return (ProtectedCuboidRegion) WorldTerrenos.getWorldGuard().getRegionManager(center.getWorld()).getRegion(player + "-" + terrenoID);
	}
	
	public int getId() {
		return terrenoID;
	}
	
	public Location getSpawn() {
		return spawn;
	}
	
	public void setSpawn(Location spawn) {
		this.spawn = spawn;
		
		updateMySQL(); 
	}
	
	public TerrenoVenda getTerrenoVenda() {
		return terrenoVenda;
	}
	
	public void setTerrenoVenda(TerrenoVenda terrenoVenda) {
		this.terrenoVenda = terrenoVenda;
		
		updateMySQL();
	}
	
	public boolean isPvP() {
		if(getRegion() == null || getRegion().getFlag(DefaultFlag.PVP) == null) {
			return false;
		}
		
		return getRegion().getFlag(DefaultFlag.PVP) == StateFlag.State.ALLOW;
	}
	
	public void setPvP(StateFlag.State stateFlag) {
		getRegion().setFlag(DefaultFlag.PVP, stateFlag);
	}
	
	public boolean isMobSpawning() {
		if(getRegion() == null || getRegion().getFlag(DefaultFlag.MOB_DAMAGE) == null) {
			return false;
		}
		
		return getRegion().getFlag(DefaultFlag.MOB_SPAWNING) == StateFlag.State.ALLOW;
	}
	
	public void setMobSpawning(StateFlag.State stateFlag) {
		getRegion().setFlag(DefaultFlag.MOB_SPAWNING, stateFlag);
	}
	
	public Map<String, Integer> getMaquinas() {
		return maquinas;
	}
	
	public Map<String, Integer> getSuperSpawners2() {
		return superSpawners;
	}
	
	public int getSpawnerAmount() {
		int amount = 0;
		
		for(Entry<EntityType, Integer> e : spawners.entrySet()) {
			amount += e.getValue();
		}
		
		return amount;
	}
	
	public int getSpawnerAmount(EntityType entityType) {
		if(!spawners.containsKey(entityType)) {
			return 0;
		}
		
		return spawners.get(entityType);
	}
	
	public int getSuperSpawnerAmount() {
		int amount = 0;
		
		for(Entry<String, Integer> e : superSpawners.entrySet()) {
			amount += e.getValue();
		}
		
		return amount;
	}
	
	public int getSuperSpawnerAmount(String superSpawner) {
		if(!superSpawners.containsKey(superSpawner)) {
			return 0;
		}
		
		return superSpawners.get(superSpawner);
	}
	
	public int getMachineAmount() {
		int amount = 0;
		
		for(Entry<String, Integer> e : maquinas.entrySet()) {
			amount += e.getValue();
		}
		
		return amount;
	}
	
	public int getMachineAmount(String machine) {
		if(!maquinas.containsKey(machine)) {
			return 0;
		}
		
		return maquinas.get(machine);
	}

	
//	public Map<Maquina, Integer> getSuperSpawners() {
//		Map<Maquina, Integer> spwns = new HashMap<>();
//		for(Entry<String, Integer> e : superSpawners.entrySet()) {
//			spwns.put(WorldSuper.getManager().getMaquina(e.getKey()), e.getValue());
//		}
//		
//		return spwns;
//	}
	
	public int getMaquinasAmount() {
		int amount = 0;
		
		for(Entry<String, Integer> e : maquinas.entrySet()) {
			amount += e.getValue();
		}
		
		return amount;
	}
	
	public int getSuperSpawnersAmount() {
		int amount = 0;
		
//		for(Entry<Maquina, Integer> e : getSuperSpawners().entrySet()) {
//			amount += e.getValue();
//		}
		
		return amount;
	}
	
	public Map<EntityType, Integer> getSpawners() {
		return spawners;
	}
	
	public int getSpawner(EntityType entityType) {
		if(!spawners.containsKey(entityType)) {
			return 0;
		}
		
		return spawners.get(entityType);
	}
	
	public int getSpawnersAmount() {
		int amount = 0;

		for(int amt : spawners.values()) {
			amount += amt;
		}
		
		return amount;
	}
	
	public List<String> getFriends() {
		return friends;
	}
	
	public List<String> getImpedidos() {
		List<String> copy = new ArrayList<>();
		
		if(impedidos.contains("*")) {
			return Arrays.asList("Todos");
		}
		
		for(String impedido : impedidos) {
			if(!impedido.equalsIgnoreCase("*")) {
				copy.add(impedido);
			}
		}
		
		return copy;
	}
	
	public void addImpedido(String player) {
		if(isImpedido(player)) {
			return;
		}
		
		if(player.equalsIgnoreCase("*")) {
			impedidos.clear();
		}
		
		this.impedidos.add(player);
		
		updateMySQL();
	}
	
	public void removeImpedido(String player) {
		if(player.equalsIgnoreCase("*")) {
			impedidos.clear();
		}else{
			this.impedidos.remove(player);
		}
		
		updateMySQL();
	}
	
	public boolean isImpedido(String player) {
		if(isFriend(player) || this.player.equalsIgnoreCase(player)) {
			return false;
		}
		
		if(impedidos.contains("*")) {
			return true;
		}
		
		for(String impedido : impedidos) {
			if(impedido.equalsIgnoreCase(player)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void addFriend(String player) {
		if(isFriend(player)) {
			return;
		}
		
		this.friends.add(player);
		this.getRegion().getMembers().addPlayer(player);
		
		updateMySQL();
	}
	
	public void removeFriend(String player) {
		this.friends.remove(player);
		this.getRegion().getMembers().removePlayer(player);
		
		
		updateMySQL();
	}
	
	public boolean isFriend(String player) {
		for(String friend : friends) {
			if(friend.equalsIgnoreCase(player)) {
				return true;
			}
		}
		
		return false;
	}
	
//	public void countMaquina(com.worldpvp.maquinas.maquina.Maquina maquina) {
//		if(!maquinas.containsKey(maquina.getName())) {
//			maquinas.put(maquina.getName(), 0);
//		}
//		
//		maquinas.put(maquina.getName(), maquinas.get(maquina.getName()) + 1);
//		
//		updateMySQL();
//	}
//	
//	public void removeMaquina(com.worldpvp.maquinas.maquina.Maquina maquina) {
//		if(maquinas.containsKey(maquina.getName())) {
//			int amount = maquinas.get(maquina.getName());
//			
//			if(amount - 1 == 0) {
//				maquinas.remove(maquina.getName());
//			}else{
//				maquinas.put(maquina.getName(), amount - 1);
//			}
//			
//			updateMySQL();
//		}
//	}
	
//	public void countSuper(Maquina maquina) {
//		if(!superSpawners.containsKey(maquina.getName())) {
//			superSpawners.put(maquina.getName(), 0);
//		}
//		
//		superSpawners.put(maquina.getName(), superSpawners.get(maquina.getName()) + 1);
//		
//		updateMySQL();
//	}
//	
//	public void removeSpawner(Maquina maquina) {
//		if(superSpawners.containsKey(maquina.getName())) {
//			int amount = superSpawners.get(maquina.getName());
//			
//			if(amount - 1 == 0) {
//				superSpawners.remove(maquina.getName());
//			}else{
//				superSpawners.put(maquina.getName(), amount - 1);
//			}
//			
//			updateMySQL();
//		}
//	}
	
	public void countSpawner(EntityType entityType) {
		if(!spawners.containsKey(entityType)) {
			spawners.put(entityType, 0);
		}

		spawners.put(entityType, spawners.get(entityType) + 1);
		
		updateMySQL();
	}
	
	public void removeSpawner(EntityType entityType) {
		if(spawners.containsKey(entityType)) {
			int amount = spawners.get(entityType);
			if(amount - 1 == 0) {
				spawners.remove(entityType);
			}else{
				spawners.put(entityType, amount - 1);
			}
			
			updateMySQL();
		}
	}
	
	private String serializeLocation(Location location) {
		return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
	}
	
	@SuppressWarnings("unchecked")
	public void updateMySQL() {
		JSONObject json = new JSONObject();
		
		json.put("Center", serializeLocation(center));
		json.put("Spawn", serializeLocation(spawn));
		json.put("TerrenoSize", terrenoSize.getName());
		json.put("Impedidos", impedidos);
		
		if(WorldTerrenos.getWorldSpawner() != null) {
			json.put("Spawners", spawners);
		}
		
//		if(WorldTerrenos.getWorldSuper() != null) {
//			Map<String, Integer> ss = new HashMap<>();
//			for(Entry<String, Integer> e : superSpawners.entrySet()) {
//				ss.put(e.getKey(), e.getValue());
//			}
//			
//			json.put("SuperSpawners", ss);
//		}
//		
//		if(WorldTerrenos.getWorldMaquina() != null) {
//			Map<String, Integer> ss = new HashMap<>();
//			for(Entry<String, Integer> e : maquinas.entrySet()) {
//				ss.put(e.getKey(), e.getValue());
//			}
//			
//			json.put("Maquinas", ss);
//		}
		
		if(terrenoVenda != null) {
			json.put("VendaSign", serializeLocation(terrenoVenda.getSign().getLocation()));
			json.put("VendaPrice", terrenoVenda.getPrice());
		}
		
		try {
			ResultSet rs = DatabaseUtils.getStatement().executeQuery("SELECT * FROM terrenos WHERE nome='" + player + "' AND terrenoID='" + terrenoID + "'");
			if(rs.next()) {
				do{
					new BukkitRunnable() {
						public void run() {
							try {
								DatabaseUtils.getStatement().execute("UPDATE terrenos SET coisas='" + json.toString() + "' WHERE nome='" + player + "' AND terrenoID='" + terrenoID + "'");
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
					}.runTaskLater(WorldTerrenos.getInstance(), 20L);
				}while(rs.next());
			}else{
				new BukkitRunnable() {
					public void run() {
						try {
							DatabaseUtils.getStatement().execute("INSERT INTO terrenos (`nome`, `terrenoID`, `coisas`) VALUES ('" + player + "', '" + terrenoID + "', '" + json.toString() + "')");
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}.runTaskLater(WorldTerrenos.getInstance(), 20L);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
