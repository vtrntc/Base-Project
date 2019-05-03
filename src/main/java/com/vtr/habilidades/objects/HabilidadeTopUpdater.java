package com.vtr.habilidades.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.scheduler.BukkitRunnable;

import com.vtr.api.shared.API;
import com.vtr.api.shared.sql.SQL;
import com.vtr.api.shared.sql.SQLAction;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.inventories.HabilidadeTopInventory;
import com.vtr.habilidades.user.HabilidadeUser;

public class HabilidadeTopUpdater {
	
	private Map<HabilidadeType, List<HabilidadeUser>> top;

	public HabilidadeTopUpdater() {
		this.top = new HashMap<>();
		start();
	}
	
	public List<HabilidadeUser> getTopFromHabilidade(HabilidadeType type) {
		if(top.containsKey(type)) {
			return top.get(type);
		}
		
		return new ArrayList<>();
	}
	
	public int getUserPosition(HabilidadeType type, HabilidadeUser user) {
		if(top.containsKey(type)) {
			List<HabilidadeUser> topType = top.get(type);
			if(topType.contains(user)) {
				return topType.indexOf(user) + 1;
			}
		}
		
		return 0;
	}
	
	private void start() {
		new BukkitRunnable() {
			public void run() {
				for(Habilidade habilidade : HabilidadePlugin.getManager().getHabilidades()) {
					SQLAction action = (rs) -> {
						while(rs.next()) {
							if(!top.containsKey(habilidade.getType())) {
								top.put(habilidade.getType(), new ArrayList<>());
							}
							
							top.get(habilidade.getType()).add(HabilidadePlugin.getModuleFactory().getUserModule(rs.getInt("user_id")));
						}
						
						HabilidadeTopInventory.updateInventory(habilidade.getType());
					};
					
					//TODO maybe sync?
					API.Mysql.getServerConnection().runAction(new SQL("SELECT * FROM skills ORDER BY " + habilidade.getType().name().toLowerCase() + "_lvl DESC LIMIT 0,10"), action, false);
				}
			}
		}.runTaskTimerAsynchronously(HabilidadePlugin.getInstance(), 0, 10 * 20);
	}
}
