package com.vtr.habilidades.user;

import com.vtr.api.shared.user.NetworkUser;
import com.vtr.api.shared.user.module.UserModuleFactory;
import com.vtr.api.spigot.APISpigot;
import com.vtr.api.spigot.user.User;

/**
 *
 * @author Matheus Santos (Matgsan)
 */
public class HabilidadeUserModuleFactory extends UserModuleFactory<HabilidadeUser> {

    @Override
    public HabilidadeUser getUserModule(NetworkUser networkUser) {
        User user = APISpigot.getInstance().getUserFactory().getUser(networkUser.getId());
        if (user instanceof HabilidadeUserImpl) {
            return ((HabilidadeUserImpl) user).getHabilidadeUserModule();
        }
        return null;
    }

    @Override
    public HabilidadeUser getUserModule(int id) {
        User user = APISpigot.getInstance().getUserFactory().getUser(id);
        if (user instanceof HabilidadeUserImpl) {
            return ((HabilidadeUserImpl) user).getHabilidadeUserModule();
        }
        return null;
    }

    @Override
    public HabilidadeUser getUserModule(String name) {
        User user = APISpigot.getInstance().getUserFactory().getUser(name);
        if (user instanceof HabilidadeUserImpl) {
            return ((HabilidadeUserImpl) user).getHabilidadeUserModule();
        }
        return null;
    }

    @Override
    public HabilidadeUser downloadUserModule(NetworkUser user) {
        //Baixar
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HabilidadeUser downloadUserModule(int id) {
        //Baixar
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HabilidadeUser downloadUserModule(String name) {
        //Baixar
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exportUserModule(HabilidadeUser user) {
        //SALVAR
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
