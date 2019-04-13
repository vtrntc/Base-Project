package com.vtr.habilidades.managers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import com.vtr.api.shared.utils.DatabaseUtils;
import com.vtr.api.spigot.inventory.loader.ItemLoader;
import com.vtr.api.spigot.misc.PotionInfo;
import com.vtr.api.spigot.misc.YamlConfig;
import com.vtr.api.spigot.utils.PotionUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.habilidades.acrobatics.Acrobatics;
import com.vtr.habilidades.habilidades.acrobatics.AcrobaticsFallExperience;
import com.vtr.habilidades.habilidades.acrobatics.AcrobaticsInfo;
import com.vtr.habilidades.habilidades.archery.Archery;
import com.vtr.habilidades.habilidades.archery.ArrowRetrieval;
import com.vtr.habilidades.habilidades.archery.Daze;
import com.vtr.habilidades.habilidades.archery.SkillShot;
import com.vtr.habilidades.habilidades.axes.Axes;
import com.vtr.habilidades.habilidades.axes.extras.TreeCut;
import com.vtr.habilidades.habilidades.excavation.Excavation;
import com.vtr.habilidades.habilidades.fishing.FishType;
import com.vtr.habilidades.habilidades.fishing.Fishing;
import com.vtr.habilidades.habilidades.herbalism.DoubleDrop;
import com.vtr.habilidades.habilidades.herbalism.Herbalism;
import com.vtr.habilidades.habilidades.mining.Mining;
import com.vtr.habilidades.habilidades.swords.Swords;
import com.vtr.habilidades.habilidades.swords.SwordsInfo;
import com.vtr.habilidades.habilidades.swords.extras.CounterAttack;
import com.vtr.habilidades.habilidades.swords.extras.Dodge;
import com.vtr.habilidades.habilidades.swords.extras.bleed.Bleed;
import com.vtr.habilidades.habilidades.swords.extras.bleed.BleedLevel;
import com.vtr.habilidades.habilidades.unarmed.Disarmor;
import com.vtr.habilidades.habilidades.unarmed.Unarmed;
import com.vtr.habilidades.objects.HabilidadeBlock;
import com.vtr.habilidades.objects.HabilidadeDrop;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadePlayer;
import com.vtr.habilidades.objects.HabilidadeType;

public class HabilidadeManager {

	private List<Habilidade> habilidades;
	
	private Map<String, HabilidadePlayer> players;
	
	public HabilidadeManager() {
		this.habilidades = new ArrayList<>();
		this.players = new HashMap<>();
	}
	
	public void enable() {
		YamlConfig config = HabilidadePlugin.getYamlConfig();
		
		if(config.isSet("Habilidades")) {
			for(String e : config.getConfigurationSection("Habilidades").getKeys(false)) {
				HabilidadeType type = HabilidadeType.getType(e);
				if(type != null) {
					String name = config.getString("Habilidades." + e + ".Name");
					
					List<Material> tools = new ArrayList<>();
					if(config.isSet("Habilidades." + e + ".Tools")) {
						for(String m : config.getStringList("Habilidades." + e + ".Tools")) {
							Material material = Material.matchMaterial(m);
							if(material != null) {
								tools.add(material);
							}
						}
					}
					
					List<HabilidadeDrop> drops = new ArrayList<>();
					if(config.isSet("Habilidades." + e + ".Drops")) {
						for(String d : config.getConfigurationSection("Habilidades." + e + ".Drops").getKeys(false)) {
							drops.add(new HabilidadeDrop(d, ItemLoader.loadItemFromPath(config, "Habilidades." + e + ".Drops." + d + ".Item"), config.getDouble("Habilidades." + e + ".Drops." + d + ".Chance"), config.getDouble("Habilidades." + e + ".Drops." + d + ".MaxChance"), config.getInt("Habilidades." + e + ".Drops." + d + ".MinLevel")));
						}
					}
					
					switch(type) {
						case UNARMED:
							Disarmor disarmor = null;
							if(config.isSet("Habilidades." + e + ".Disarmor")) {
								disarmor = new Disarmor(config.getDouble("Habilidades." + e + ".Disarmor.Chance"), config.getInt("Habilidades." + e + ".Disarmor.PerLevel"), config.getInt("Habilidades." + e + ".Disarmor.MaxIncrease"));
							}
							
							habilidades.add(new Unarmed(name, drops, tools, loadEntitiesExperience(config, e), disarmor));
							break;
						case SWORDS:
							Swords swords = new Swords(name, drops, tools, loadEntitiesExperience(config, e));
							habilidades.add(swords);
							
							if(config.isSet("Habilidades." + e + ".Extras")) {
								for(String x : config.getConfigurationSection("Habilidades." + e + ".Extras").getKeys(false)) {
									switch(x) {
										case "Bleed":
											List<BleedLevel> bleed = new ArrayList<>();
											
											for(String b : config.getConfigurationSection("Habilidades." + e + ".Extras." + x + ".Levels").getKeys(false)) {
												bleed.add(new BleedLevel(config.getInt("Habilidades." + e + ".Extras." + x + ".Levels." + b + ".MinLevel"), config.getInt("Habilidades." + e + ".Extras." + x + ".Levels." + b + ".Amount"), config.getInt("Habilidades." + e + ".Extras." + x + ".Levels." + b + ".Time"), config.getDouble("Habilidades." + e + "." + x + ".Levels." + b + ".Damage")));
											}
											
											swords.registerHabilidadeExtra(new Bleed(config.getDouble("Habilidades." + e + ".Extras." + x + ".PerLevel"), config.getDouble("Habilidades." + e + ".Extras." + x + ".MaxChance"), bleed));
											break;
										case "Dodge":
											swords.registerHabilidadeExtra(new Dodge(config.getDouble("Habilidades." + e + ".Extras." + x + ".PerLevel"), config.getDouble("Habilidades." + e + ".Extras." + x + ".MaxChance")));
											break;
										case "Counter":
											swords.registerHabilidadeExtra(new CounterAttack(config.getDouble("Habilidades." + e + ".Extras." + x + ".PerLevel"), config.getDouble("Habilidades." + e + ".Extras." + x + ".MaxChance")));
											break;
										default:
											break;
									}
								}
							}
							
							break;
						case FISHING:
							Map<FishType, Double> fishs = new HashMap<>();
							if(config.isSet("Habilidades." + e + ".Fishs")) {
								for(String f : config.getConfigurationSection("Habilidades." + e + ".Fishs").getKeys(false)) {
									FishType fishType = FishType.getFish(f);
									if(fishType != null) {
										fishs.put(fishType, config.getDouble("Habilidades." + e + ".Fishs." + f + ".XP"));
									}
								}
							}
							
							habilidades.add(new Fishing(name, drops, tools, fishs));
							break;
						case AXES:
							Axes axes = new Axes(name, drops, tools, loadEntitiesExperience(config, e));
							habilidades.add(axes);
							
							if(config.isSet("Habilidades." + e + ".Extras")) {
								for(String x : config.getConfigurationSection("Habilidades." + e + ".Extras").getKeys(false)) {
									switch(x) {
										case "TreeCut":
											axes.registerHabilidadeExtra(new TreeCut(config.getDouble("Habilidades." + e + ".Extras." + x + ".PerLevel"), config.getDouble("Habilidades." + e + ".Extras." + x + ".MaxChance")));
											break;
										default:
											break;
									}
								}
							}
							
							break;
						case ARCHERY:
							Daze daze = null;
							if(config.isSet("Habilidades." + e + ".Daze")) {
								PotionInfo potionInfo = PotionUtils.loadPotion(config, "Habilidades." + e + ".Daze.Potion");
								if(potionInfo != null) {
									daze = new Daze(potionInfo);
								}
							}
							
							SkillShot skillShot = null;
							if(config.isSet("Habilidades." + e + ".SkillShot")) {
								skillShot = new SkillShot(config.getInt("Habilidades." + e + ".SkillShot.PerLevel"), config.getInt("Habilidades." + e + ".SkillShot.MaxIncrease"), config.getDouble("Habilidades." + e + ".SkillShot.Chance"));
							}
							
							ArrowRetrieval arrowRetrieval = null;
							if(config.isSet("Habilidades." + e + ".ArrowRetrieval")) {
								arrowRetrieval = new ArrowRetrieval(config.getInt("Habilidades." + e + ".ArrowRetrieval.PerLevel"), config.getInt("Habilidades." + e + ".ArrowRetrieval.MaxIncrease"), config.getDouble("Habilidades." + e + ".ArrowRetrieval.Chance"));
							}
							
							habilidades.add(new Archery(name, drops, tools));
							break;
						case ACROBATICS:
							List<AcrobaticsFallExperience> fallExperience = new ArrayList<>();
							if(config.isSet("Habilidades." + e + ".FallExperience")) {
								for(String fe : config.getConfigurationSection("Habilidades." + e + ".FallExperience").getKeys(false)) {
									fallExperience.add(new AcrobaticsFallExperience(config.getInt("Habilidades." + e + ".FallExperience." + fe + ".MinDistance"), config.getDouble("Habilidades." + e + ".FallExperience." + fe + ".XP")));
								}
							}
							
							habilidades.add(new Acrobatics(name, drops, tools, fallExperience));
							break;
						case HERBALISM:
							List<Material> doubleDrops = new ArrayList<>();
							if(config.isSet("Habilidades." + e + ".DoubleDrop.Blocks")) {
								for(String m : config.getStringList("Habilidades." + e + ".DoubleDrop.Blocks")) {
									Material material = Material.matchMaterial(m);
									if(material != null) {
										doubleDrops.add(material);
									}
								}
							}
							
							habilidades.add(new Herbalism(name, tools, drops, loadBlockExperience(config, e, drops), new DoubleDrop(config.getDouble("Habilidades." + e + ".DoubleDrop.Chance"), doubleDrops)));
							break;
						case EXCAVATION:
							habilidades.add(new Excavation(name, tools, drops, loadBlockExperience(config, e, drops)));
							break;
						case MINING:
							Map<Material, HabilidadeBlock> miningBlocks = new HashMap<>();
							if(config.isSet("Habilidades." + e + ".Blocks")) {
								for(String m : config.getConfigurationSection("Habilidades." + e + ".Blocks").getKeys(false)) {
									Material material = Material.matchMaterial(m);
									if(material != null) {
										List<HabilidadeDrop> blockDrops = new ArrayList<>();
										if(config.isSet("Habilidades." + e + ".Blocks." + m + ".Drops")) {
											for(String d : config.getStringList("Habilidades." + e + ".Blocks." + m + ".Drops")) {
												HabilidadeDrop drop = drops.stream().filter(dr -> dr.getName().equalsIgnoreCase(d)).findFirst().orElse(null);
												if(drop != null) {
													blockDrops.add(drop);
												}
											}
										}
										
										miningBlocks.put(material, new HabilidadeBlock(material, config.getDouble("Habilidades." + e + ".Blocks." + m + ".XP"), blockDrops));
									}
								}
							}
							
							Mining mining = new Mining(name, tools, drops, miningBlocks);
							habilidades.add(mining);
							
							if(config.isSet("Habilidades." + e + ".Extras")) {
								for(String x : config.getConfigurationSection("Habilidades." + e + ".Extras").getKeys(false)) {
									switch(x) {
										case "DoubleDrop":
											List<Material> allowed = new ArrayList<>();
											for(String m : config.getStringList("Habilidades." + e + ".Extras." + x + ".Blocks")) {
												Material material = Material.getMaterial(m);
												if(material != null) {
													allowed.add(material);
												}
											}
											
											mining.registerHabilidadeExtra(new com.vtr.habilidades.habilidades.mining.DoubleDrop(config.getDouble("Habilidades." + e + ".Extras." + x + ".PerLevel"), config.getInt("Habilidades." + e + ".Extras." + x + ".MaxLevel"), allowed));
											break;
										default:
											break;
									}
								}
							}
							
							List<Material> doubleDrop = new ArrayList<>();
							if(config.isSet("Habilidades." + e + ".DoubleDrop")) {
								if(config.isSet("Habilidades." + e + ".DoubleDrop.Blocks")) {
									for(String m : config.getStringList("Habilidades." + e + ".DoubleDrop.Blocks")) {
										Material material = Material.matchMaterial(m);
										if(material != null) {
											doubleDrop.add(material);
										}
									}
								}
							}
							
							habilidades.add(new Mining(name, tools, drops, miningBlocks));
							break;
						default:
							break;
					}
				}
			}
		}
		
		Map<String, String> sql = new LinkedHashMap<>();
		sql.put("name", "VARCHAR(32)");
		sql.put("habilidade", "VARCHAR(64)");
		sql.put("level", "INTEGER");
		sql.put("xp", "DECIMAL(16, 2)");
		
		DatabaseUtils.createTable("player_habilidades", sql);
		
		sql.clear();
		
		DatabaseUtils.getInfo("player_habilidades", sql).whenComplete((rs, error) -> {
			try {
				Map<String, Map<HabilidadeType, HabilidadeInfo>> infos = new HashMap<>();
				while(rs.next()) {
					Habilidade habilidade = getHabilidadeByTypeName(rs.getString("habilidade"));
					if(habilidade != null) {
						String name = rs.getString("name");
						if(!infos.containsKey(name.toLowerCase())) {
							infos.put(name.toLowerCase(), new HashMap<>());
						}
						
						int level = rs.getInt("level");
						
						double xp = rs.getDouble("xp");
						
						switch(habilidade.getType()) {
							case SWORDS:
								infos.get(name.toLowerCase()).put(habilidade.getType(), new SwordsInfo(name, habilidade, level, xp));
								break;
							case ACROBATICS:
								infos.get(name.toLowerCase()).put(habilidade.getType(), new AcrobaticsInfo(name, habilidade, level, xp));
								break;
							default:
								infos.get(name.toLowerCase()).put(habilidade.getType(), new HabilidadeInfo(name, habilidade, level, xp));
								break;
						}
					}
				}
				
				for(Entry<String, Map<HabilidadeType, HabilidadeInfo>> e : infos.entrySet()) {
					players.put(e.getKey().toLowerCase(), new HabilidadePlayer(e.getKey(), e.getValue()));
				}
			}catch(SQLException e) {
				e.printStackTrace();
			}
		});
		
		new BukkitRunnable() {
			public void run() {
				for(HabilidadePlayer habilidadePlayer : players.values()) {
					if(habilidadePlayer.isNeedUpdate()) {
						for(HabilidadeInfo habilidadeInfo : habilidadePlayer.getHabilidades().values()) {
							if(habilidadeInfo.isNeedUpdate()) {
								habilidadeInfo.setNeedUpdate(false);
								habilidadeInfo.save();
							}
						}
						
						habilidadePlayer.setNeedUpdate(false);
					}
				}
			}
		}.runTaskTimer(HabilidadePlugin.getInstance(), 60 * 20, 60 * 20);
	}
	
	public List<Habilidade> getHabilidades() {
		return habilidades;
	}
	
	public Habilidade getHabilidade(String habilidade) {
		return habilidades.stream().filter(h -> h.getName().equalsIgnoreCase(habilidade)).findFirst().orElse(null);
	}
	
	public Habilidade getHabilidadeByTypeName(String habilidade) {
		return habilidades.stream().filter(h -> h.getType().name().equalsIgnoreCase(habilidade)).findFirst().orElse(null);
	}
	
	public boolean isPlayer(String player) {
		return players.containsKey(player.toLowerCase());
	}
	
	public HabilidadePlayer getPlayer(String player) {
		if(!players.containsKey(player.toLowerCase())) {
			players.put(player.toLowerCase(), new HabilidadePlayer(player));
		}
		
		return players.get(player.toLowerCase());
	}
	
	private Map<Material, HabilidadeBlock> loadBlockExperience(YamlConfig config, String skill, List<HabilidadeDrop> drops) {
		Map<Material, HabilidadeBlock> blocks = new HashMap<>();
		if(config.isSet("Habilidades." + skill + ".Blocks")) {
			for(String m : config.getConfigurationSection("Habilidades." + skill + ".Blocks").getKeys(false)) {
				Material material = Material.matchMaterial(m);
				if(material != null) {
					List<HabilidadeDrop> blockDrops = new ArrayList<>();
					if(config.isSet("Habilidades." + skill + ".Blocks." + m + ".Drops")) {
						for(String d : config.getStringList("Habilidades." + skill + ".Blocks." + m + ".Drops")) {
							HabilidadeDrop drop = drops.stream().filter(dr -> dr.getName().equalsIgnoreCase(d)).findFirst().orElse(null);
							if(drop != null) {
								blockDrops.add(drop);
							}
						}
					}
					
					blocks.put(material, new HabilidadeBlock(material, config.getDouble("Habilidades." + skill + ".Blocks." + m + ".XP"), blockDrops));
				}
			}
		}
		
		return blocks;
	}
	
	private Map<EntityType, Double> loadEntitiesExperience(YamlConfig config, String skill) {
		Map<EntityType, Double> entitiesXp = new HashMap<>();
		if(config.isSet("Habilidades." + skill + ".Entities")) {
			for(String b : config.getConfigurationSection("Habilidades." + skill + ".Entities").getKeys(false)) {
				EntityType entityType = EntityType.valueOf(b);
				if(entityType != null) {
					entitiesXp.put(entityType, config.getDouble("Habilidades." + skill + ".Entities." + b + ".XP"));
				}
			}
		}
		
		return entitiesXp;
	}
}

