package com.vtr.habilidades.user;

import java.util.HashMap;
import java.util.Map;

import com.vtr.api.spigot.user.module.SpigotUserModule;
import com.vtr.habilidades.HabilidadePlugin;
import com.vtr.habilidades.habilidades.Habilidade;
import com.vtr.habilidades.habilidades.acrobatics.AcrobaticsInfo;
import com.vtr.habilidades.objects.HabilidadeInfo;
import com.vtr.habilidades.objects.HabilidadeType;

public class HabilidadeUser extends SpigotUserModule {
    
    private boolean needUpdate;

    private Map<HabilidadeType, HabilidadeInfo> habilidades;

    public HabilidadeUser(int user, Map<HabilidadeType, HabilidadeInfo> habilidades) {
        super(user);
        this.habilidades = habilidades;

//        User a = APISpigot.getInstance().getUserFactory().getUser(user);
//        
//        HabilidadePlugin.getModuleFactory().getUserModule(user);
        
        for (Habilidade habilidade : HabilidadePlugin.getManager().getHabilidades()) {
            if (!habilidades.containsKey(habilidade.getType())) {
                switch (habilidade.getType()) {
                    case ACROBATICS:
                        habilidades.put(habilidade.getType(), new AcrobaticsInfo(habilidade, 0, 0));
                        break;
                    default:
                        habilidades.put(habilidade.getType(), new HabilidadeInfo(habilidade, 0, 0));
                        break;
                }
            }
        }
    }

    public HabilidadeUser(int user) {
        this(user, new HashMap<>());
    }

    public Map<HabilidadeType, HabilidadeInfo> getHabilidades() {
        return habilidades;
    }

    public HabilidadeInfo getHabilidade(HabilidadeType type) {
        return habilidades.get(type);
    }

    public boolean isNeedUpdate() {
        return needUpdate;
    }

    public void setNeedUpdate(boolean needUpdate) {
        this.needUpdate = needUpdate;
    }
}
