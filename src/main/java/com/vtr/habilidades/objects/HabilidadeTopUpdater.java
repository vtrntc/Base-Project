package com.vtr.habilidades.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.scheduler.BukkitRunnable;

import com.vtr.api.shared.API;
import com.vtr.api.shared.sql.SQL;
import com.vtr.api.shared.sql.SQLAction;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.user.HabilidadeUser;

public class HabilidadeTopUpdater {
	
	private Map<HabilidadeType, Map<Integer, HabilidadeUser>> top;

	public HabilidadeTopUpdater() {
		this.top = new HashMap<>();
		start();
	}
	
	private void start() {
		new BukkitRunnable() {
			public void run() {
				for(Habilidade habilidade : HabilidadePlugin.getManager().getHabilidades()) {
					
					AtomicInteger position = new AtomicInteger(1);
					
					SQLAction action = (rs) -> {
						if(!top.containsKey(habilidade.getType())) {
							top.put(habilidade.getType(), new HashMap<>());
						}
						
						top.get(habilidade.getType()).put(position.get(), HabilidadePlugin.getModuleFactory().getUserModule(rs.getInt("user_id")));
						
						position.set(position.get() + 1);
					};
					
					//TODO maybe sync?
					API.Mysql.getServerConnection().runAction(new SQL("SELECT * FROM skills ORDER BY " + habilidade.getType().name().toLowerCase() + "_lvl DESC LIMIT 0,10"), action, false);
				}
			}
		}.runTaskTimerAsynchronously(HabilidadePlugin.getInstance(), 0, 10 * 20);
	}
}
