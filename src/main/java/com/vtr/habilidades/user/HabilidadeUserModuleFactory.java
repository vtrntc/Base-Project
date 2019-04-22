package com.vtr.habilidades.user;

import com.vtr.api.shared.user.NetworkUser;
import com.vtr.api.spigot.APISpigot;
import com.vtr.api.spigot.user.User;
import com.vtr.api.spigot.user.module.SpigotUserModuleFactory;
import java.util.UUID;

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
            System.out.println("user is instanceof");
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
        return new HabilidadeUser(user);
    }
    
    @Override
    public void exportUserModule(HabilidadeUser user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
