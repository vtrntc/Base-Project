package com.vtr.habilidades.user;

import java.util.LinkedHashMap;
import java.util.UUID;

import com.vtr.api.shared.API;
import com.vtr.api.shared.user.NetworkUser;
import com.vtr.api.shared.utils.SQLUtils;
import com.vtr.api.spigot.APISpigot;
import com.vtr.api.spigot.user.User;
import com.vtr.api.spigot.user.module.SpigotUserModuleFactory;
import com.vtr.habilidades.objects.HabilidadeInfo;

/**
 *
 * @author Matheus Santos (Matgsan)
 */
public class HabilidadeUserModuleFactory extends SpigotUserModuleFactory<HabilidadeUser> {

    @Override
    public HabilidadeUser getUserModule(int id) {
        User user = APISpigot.getInstance().getUserFactory().getUser(id);
        if (user instanceof HabilidadeUserImpl) {
            return ((HabilidadeUserImpl) user).getHabilidadeUser();
        }
        return null;
    }

    @Override
    public HabilidadeUser getUserModule(String name) {
        User user = APISpigot.getInstance().getUserFactory().getUser(name);
        if (user instanceof HabilidadeUserImpl) {
            return ((HabilidadeUserImpl) user).getHabilidadeUser();
        }
        return null;
    }

    @Override
    public HabilidadeUser getUserModule(UUID uuid) {
        User user = APISpigot.getInstance().getUserFactory().getUser(uuid);
        if (user instanceof HabilidadeUserImpl) {
            return ((HabilidadeUserImpl) user).getHabilidadeUser();
        }
        return null;
    }

    @Override
    public HabilidadeUser downloadUserModule(NetworkUser user) {
    	LinkedHashMap<String, Object> map = new LinkedHashMap<>();
    	map.put("user_id", user.getId());
    	
    	SQLUtils.get(API.Mysql.getServerConnection(), "skills", false, map, (rs) -> {
    		if(rs.next()) {
    		}
    	});
    	
        return new HabilidadeUser(user);
    }
    
    @Override
    public void exportUserModule(HabilidadeUser user) {
    	LinkedHashMap<String, Object> map = new LinkedHashMap<>();
    	map.put("user_id", user.getNetworkUser().getId());
    	
    	LinkedHashMap<String, Object> update = new LinkedHashMap<>();
    	for(HabilidadeInfo info : user.getHabilidades().values()) {
    		update.put(info.getHabilidade().getType().name().toLowerCase() + "_level", info.getLevel());
    		update.put(info.getHabilidade().getType().name().toLowerCase() + "_xp", info.getXp());
    	}
    	
    	SQLUtils.insertIfExistUpdate(API.Mysql.getServerConnection(), "skills", false, update, map);
    	
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
