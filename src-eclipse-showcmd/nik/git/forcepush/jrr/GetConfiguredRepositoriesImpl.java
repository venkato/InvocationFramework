package nik.git.forcepush.jrr;


import java.util.List;

import org.eclipse.egit.core.Activator;

import nik.git.forcepush.GetConfiguredRepositories;

public class GetConfiguredRepositoriesImpl extends GetConfiguredRepositories{

	@Override
	public List<String> getConfiguredRepositories() {
		return Activator.getDefault().getRepositoryUtil().getConfiguredRepositories();
	}

}
