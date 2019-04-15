package com.vtr.habilidades.user;

import com.vtr.api.shared.user.NetworkUser;
import com.vtr.api.shared.user.module.UserModuleFactory;
import com.vtr.api.spigot.APISpigot;

public class HabilidadeUserModuleFactory extends UserModuleFactory<HabilidadeUser> {

	@Override
	public HabilidadeUser getUser(NetworkUser user) {
		// TODO Auto-generated method stub
		APISpigot.getInstance().getUserFactory().getUser(user.getId());
		return null;
	}

	@Override
	public HabilidadeUser getUser(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HabilidadeUser getUser(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HabilidadeUser downloadUser(NetworkUser user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HabilidadeUser downloadUser(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HabilidadeUser downloadUser(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void exportUser(HabilidadeUser user) {
		// TODO Auto-generated method stub
		
	}
}
