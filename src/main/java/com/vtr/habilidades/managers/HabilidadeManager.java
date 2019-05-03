package com.vtr.habilidades.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import com.vtr.api.shared.API;
import com.vtr.api.shared.utils.SQLUtils;
import com.vtr.api.spigot.inventory.loader.ItemLoader;
import com.vtr.api.spigot.misc.YamlConfig;
import com.vtr.api.spigot.utils.PotionUtils;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.habilidades.acrobatics.Acrobatics;
import com.vtr.habilidades.habilidades.acrobatics.AcrobaticsFallExperience;
import com.vtr.habilidades.habilidades.acrobatics.extras.Dodge;
import com.vtr.habilidades.habilidades.acrobatics.extras.PerfectRoll;
import com.vtr.habilidades.habilidades.acrobatics.extras.Roll;
import com.vtr.habilidades.habilidades.archery.Archery;
import com.vtr.habilidades.habilidades.archery.ArcheryDamageExperience;
import com.vtr.habilidades.habilidades.archery.extras.Daze;
import com.vtr.habilidades.habilidades.archery.extras.Impact;
import com.vtr.habilidades.habilidades.axes.Axes;
import com.vtr.habilidades.habilidades.axes.extras.TreeCut;
import com.vtr.habilidades.habilidades.excavation.Excavation;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtra;
import com.vtr.habilidades.habilidades.extra.HabilidadeExtraType;
import com.vtr.habilidades.habilidades.fishing.FishType;
import com.vtr.habilidades.habilidades.fishing.Fishing;
import com.vtr.habilidades.habilidades.herbalism.Herbalism;
import com.vtr.habilidades.habilidades.herbalism.extras.DoubleDropHerbalism;
import com.vtr.habilidades.habilidades.mining.Mining;
import com.vtr.habilidades.habilidades.mining.extras.DoubleDropMining;
import com.vtr.habilidades.habilidades.swords.Swords;
import com.vtr.habilidades.habilidades.swords.extras.CounterAttack;
import com.vtr.habilidades.habilidades.swords.extras.bleed.Bleed;
import com.vtr.habilidades.habilidades.swords.extras.bleed.BleedLevel;
import com.vtr.habilidades.objects.HabilidadeBlock;
import com.vtr.habilidades.objects.HabilidadeDrop;
import com.vtr.habilidades.objects.HabilidadeTopUpdater;
import com.vtr.habilidades.objects.HabilidadeType;

public class HabilidadeManager {

	private HabilidadeTopUpdater topUpdater;
	
    private List<Habilidade> habilidades;

    public HabilidadeManager() {
        this.habilidades = new ArrayList<>();
    }

    public void enable() {
        YamlConfig config = HabilidadePlugin.getYamlConfig();

        if (config.isSet("Habilidades")) {
            for (String e : config.getConfigurationSection("Habilidades").getKeys(false)) {
                HabilidadeType type = HabilidadeType.getType(e);
                if (type != null) {
                    String name = config.getString("Habilidades." + e + ".Name");

                    List<Material> tools = new ArrayList<>();
                    if (config.isSet("Habilidades." + e + ".Tools")) {
                        for (String m : config.getStringList("Habilidades." + e + ".Tools")) {
                            Material material = Material.matchMaterial(m);
                            if (material != null) {
                                tools.add(material);
                            }
                        }
                    }

                    List<HabilidadeDrop> drops = new ArrayList<>();
                    if (config.isSet("Habilidades." + e + ".Drops")) {
                        for (String d : config.getConfigurationSection("Habilidades." + e + ".Drops").getKeys(false)) {
                            drops.add(new HabilidadeDrop(d, ItemLoader.loadItemFromPath(config, "Habilidades." + e + ".Drops." + d + ".Item"), config.getDouble("Habilidades." + e + ".Drops." + d + ".Chance"), config.getDouble("Habilidades." + e + ".Drops." + d + ".MaxChance"), config.getInt("Habilidades." + e + ".Drops." + d + ".MinLevel")));
                        }
                    }

                    List<HabilidadeExtra> extras = new ArrayList<>();
                    if (config.isSet("Habilidades." + e + ".Extras")) {
                        for (String x : config.getConfigurationSection("Habilidades." + e + ".Extras").getKeys(false)) {
                            HabilidadeExtraType extraType = HabilidadeExtraType.getType(x);
                            if (extraType != null) {
                                switch (extraType) {
                                    //swords
                                    case BLEED:
                                        List<BleedLevel> bleedLevel = new ArrayList<>();
                                        for (String b : config.getConfigurationSection("Habilidades." + e + ".Extras." + x + ".Levels").getKeys(false)) {
                                            bleedLevel.add(new BleedLevel(config.getInt("Habilidades." + e + ".Extras." + x + ".Levels." + b + ".MinLevel"), config.getInt("Habilidades." + e + ".Extras." + x + ".Levels." + b + ".Amount"), config.getInt("Habilidades." + e + ".Extras." + x + ".Levels." + b + ".Time"), config.getDouble("Habilidades." + e + ".Extras." + x + ".Levels." + b + ".Damage")));
                                        }

                                        extras.add(new Bleed(config.getDouble("Habilidades." + e + ".Extras." + x + ".PerLevel"), config.getDouble("Habilidades." + e + ".Extras." + x + ".MaxChance"), bleedLevel));
                                        break;
                                    case COUNTER_ATTACK:
                                        extras.add(new CounterAttack(config.getDouble("Habilidades." + e + ".Extras." + x + ".PerLevel"), config.getDouble("Habilidades." + e + ".Extras." + x + ".MaxChance")));
                                        break;
                                    //fishing
                                    //axes
                                    case TREE_CUT:
                                        extras.add(new TreeCut(config.getDouble("Habilidades." + e + ".Extras." + x + ".PerLevel"), config.getDouble("Habilidades." + e + ".Extras." + x + ".MaxChance")));
                                        break;
                                    //archery
                                    case IMPACT:
                                        extras.add(new Impact(config.getDouble("Habilidades." + e + ".Extras." + x + ".PerLevel"), config.getInt("Habilidades." + e + ".Extras." + x + ".LevelBase"), config.getInt("Habilidades." + e + ".Extras." + x + ".MaxLevel"), config.getDouble("Habilidades." + e + ".Extras." + x + ".Damage")));
                                        break;
                                    case DAZE:
                                        extras.add(new Daze(config.getDouble("Habilidades." + e + ".Extras." + x + ".PerLevel"), config.getDouble("Habilidades." + e + ".Extras." + x + ".MaxChance"), PotionUtils.loadPotion(config, "Habilidades." + e + ".Extras." + x + ".Potion")));
                                        break;
                                    //acrobatics
                                    case ROLL:
                                        extras.add(new Roll(config.getDouble("Habilidades." + e + ".Extras." + x + ".PerLevel"), config.getInt("Habilidades." + e + ".Extras." + x + ".MaxChance")));
                                        break;
                                    case PERFECT_ROLL:
                                        extras.add(new PerfectRoll(config.getDouble("Habilidades." + e + ".Extras." + x + ".PerLevel"), config.getDouble("Habilidades." + e + ".Extras." + x + ".MaxChance")));
                                        break;
                                    case DODGE:
                                        extras.add(new Dodge(config.getDouble("Habilidades." + e + ".Extras." + x + ".PerLevel"), config.getDouble("Habilidades." + e + ".Extras." + x + ".MaxChance")));
                                        break;
                                    //herbalism

                                    //excavation
                                    //mining
                                    case DOUBLE_DROP:
                                        List<Material> allowed = new ArrayList<>();
                                        for (String m : config.getStringList("Habilidades." + e + ".Extras." + x + ".Blocks")) {
                                            Material material = Material.getMaterial(m);
                                            if (material != null) {
                                                allowed.add(material);
                                            }
                                        }

                                        switch (type) {
                                            case MINING:
                                                extras.add(new DoubleDropMining(config.getDouble("Habilidades." + e + ".Extras." + x + ".PerLevel"), config.getDouble("Habilidades." + e + ".Extras." + x + ".MaxChance"), allowed));
                                                break;
                                            case HERBALISM:
                                                extras.add(new DoubleDropHerbalism(config.getDouble("Habilidades." + e + ".Extras." + x + ".PerLevel"), config.getDouble("Habilidades." + e + ".Extras." + x + ".MaxChance"), allowed));
                                                break;
                                            default:
                                                break;
                                        }

                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }

                    switch (type) {
                        case SWORDS:
                            habilidades.add(new Swords(name, drops, tools, extras, loadEntitiesExperience(config, e)));
                            break;
                        case FISHING:
                            Map<FishType, Double> fishs = new HashMap<>();
                            if (config.isSet("Habilidades." + e + ".Fishs")) {
                                for (String f : config.getConfigurationSection("Habilidades." + e + ".Fishs").getKeys(false)) {
                                    FishType fishType = FishType.getFish(f);
                                    if (fishType != null) {
                                        fishs.put(fishType, config.getDouble("Habilidades." + e + ".Fishs." + f + ".XP"));
                                    }
                                }
                            }

                            habilidades.add(new Fishing(name, drops, tools, extras, fishs));
                            break;
                        case AXES:
                            habilidades.add(new Axes(name, drops, tools, extras, loadBlockExperience(config, e, drops)));
                            break;
                        case ARCHERY:
                            List<ArcheryDamageExperience> damageExperiences = new ArrayList<>();
                            System.out.println("1: " + "Habilidades." + e + ".DamageExperience");
                            if (config.isSet("Habilidades." + e + ".DamageExperience")) {
                                for (String x : config.getConfigurationSection("Habilidades." + e + ".DamageExperience").getKeys(false)) {
                                    System.out.println("2: " + "Habilidades." + e + ".DamageExperience." + x);
                                    damageExperiences.add(new ArcheryDamageExperience(config.getInt("Habilidades." + e + ".DamageExperience." + x + ".Distance"), config.getDouble("Habilidades." + e + ".DamageExperience." + x + ".XP")));
                                }
                            }

                            habilidades.add(new Archery(name, drops, tools, extras, damageExperiences));
                            break;
                        case ACROBATICS:
                            List<AcrobaticsFallExperience> fallExperience = new ArrayList<>();
                            if (config.isSet("Habilidades." + e + ".FallExperience")) {
                                for (String fe : config.getConfigurationSection("Habilidades." + e + ".FallExperience").getKeys(false)) {
                                    fallExperience.add(new AcrobaticsFallExperience(config.getInt("Habilidades." + e + ".FallExperience." + fe + ".MinDistance"), config.getDouble("Habilidades." + e + ".FallExperience." + fe + ".XP")));
                                }
                            }

                            habilidades.add(new Acrobatics(name, drops, tools, extras, fallExperience));
                            break;
                        case HERBALISM:
                            habilidades.add(new Herbalism(name, tools, drops, extras, loadBlockExperience(config, e, drops), null));
                            break;
                        case EXCAVATION:
                            habilidades.add(new Excavation(name, tools, drops, extras, loadBlockExperience(config, e, drops)));
                            break;
                        case MINING:
                            Map<Material, HabilidadeBlock> miningBlocks = new HashMap<>();
                            if (config.isSet("Habilidades." + e + ".Blocks")) {
                                for (String m : config.getConfigurationSection("Habilidades." + e + ".Blocks").getKeys(false)) {
                                    Material material = Material.matchMaterial(m);
                                    if (material != null) {
                                        List<HabilidadeDrop> blockDrops = new ArrayList<>();
                                        if (config.isSet("Habilidades." + e + ".Blocks." + m + ".Drops")) {
                                            for (String d : config.getStringList("Habilidades." + e + ".Blocks." + m + ".Drops")) {
                                                HabilidadeDrop drop = drops.stream().filter(dr -> dr.getName().equalsIgnoreCase(d)).findFirst().orElse(null);
                                                if (drop != null) {
                                                    blockDrops.add(drop);
                                                }
                                            }
                                        }

                                        miningBlocks.put(material, new HabilidadeBlock(material, config.getDouble("Habilidades." + e + ".Blocks." + m + ".XP"), blockDrops));
                                    }
                                }
                            }

                            habilidades.add(new Mining(name, tools, drops, extras, miningBlocks));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        
        topUpdater = new HabilidadeTopUpdater();
    }
    
    public void setupTables() {
    	LinkedHashMap<String, String> map = new LinkedHashMap<>();
    	map.put("user_id", "INTEGER");
    	
    	for(HabilidadeType habilidadeType : HabilidadeType.values()) {
    		map.put(habilidadeType.name().toLowerCase() + "_level", "INTEGER");
    		map.put(habilidadeType.name().toLowerCase() + "_xp", "DECIMAL(64, 2)");
    	}
    	
    	SQLUtils.createTable(API.Mysql.getServerConnection(), "skills", false, map);
    }
    
    public HabilidadeTopUpdater getTopUpdater() {
		return topUpdater;
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

    private Map<Material, HabilidadeBlock> loadBlockExperience(YamlConfig config, String skill, List<HabilidadeDrop> drops) {
        Map<Material, HabilidadeBlock> blocks = new HashMap<>();
        if (config.isSet("Habilidades." + skill + ".Blocks")) {
            for (String m : config.getConfigurationSection("Habilidades." + skill + ".Blocks").getKeys(false)) {
                Material material = Material.matchMaterial(m);
                if (material != null) {
                    List<HabilidadeDrop> blockDrops = new ArrayList<>();
                    if (config.isSet("Habilidades." + skill + ".Blocks." + m + ".Drops")) {
                        for (String d : config.getStringList("Habilidades." + skill + ".Blocks." + m + ".Drops")) {
                            HabilidadeDrop drop = drops.stream().filter(dr -> dr.getName().equalsIgnoreCase(d)).findFirst().orElse(null);
                            if (drop != null) {
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
        if (config.isSet("Habilidades." + skill + ".Entities")) {
            for (String b : config.getConfigurationSection("Habilidades." + skill + ".Entities").getKeys(false)) {
                EntityType entityType = EntityType.valueOf(b);
                if (entityType != null) {
                    entitiesXp.put(entityType, config.getDouble("Habilidades." + skill + ".Entities." + b + ".XP"));
                }
            }
        }

        return entitiesXp;
    }
}
