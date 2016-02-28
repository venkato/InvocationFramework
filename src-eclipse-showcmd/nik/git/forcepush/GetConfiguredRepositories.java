package nik.git.forcepush;


import java.util.List;

import nik.git.forcepush.jrr.GetConfiguredRepositoriesImpl;

public abstract class GetConfiguredRepositories {

	public static GetConfiguredRepositories getConfiguredRepositories = new GetConfiguredRepositoriesImpl();

	public abstract List<String> getConfiguredRepositories();

}
