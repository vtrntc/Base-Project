package com.vtr.habilidades.user;

import com.vtr.api.shared.user.NetworkUser;
import com.vtr.api.spigot.APISpigot;
import com.vtr.api.spigot.user.User;
import com.vtr.api.spigot.user.module.SpigotUserModuleFactory;

/**
 *
 * @author Matheus Santos (Matgsan)
 */
public class HabilidadeUserModuleFactory extends SpigotUserModuleFactory<HabilidadeUser> {
    
    @Override
    public HabilidadeUser downloadUserModule(NetworkUser networkUser) {
        HabilidadeUser user = new HabilidadeUser(networkUser.getId());
        user.setName(networkUser.getName());
        return user;
    }


}
