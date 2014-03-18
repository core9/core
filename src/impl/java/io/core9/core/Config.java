package io.core9.core;

import java.util.Arrays;

public final class Config {

	private String[] pluginDirs;

	private static Config instance;

	public void setPluginDirs(String[] addedPluginDirs) {
		if (addedPluginDirs == null) {
			this.pluginDirs = new String[0];
		} else {
			this.pluginDirs = Arrays.copyOf(addedPluginDirs, addedPluginDirs.length);
		}

	}

	public String[] getPluginDirs() {
		return pluginDirs;
	}

	private Config() {
		pluginDirs = new String[] { "plugins/" };
	}

	public static Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}

}
