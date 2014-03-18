package io.core9.core;

import java.util.Collection;

import net.xeoh.plugins.base.Plugin;
import net.xeoh.plugins.base.impl.PluginManagerImpl;

public interface PluginRegistry {
	void registerPlugin(Class<? extends Plugin> clazz, Plugin plugin);
	Plugin getPlugin(Class<? extends Plugin> className);
	
	void registerPlugins(Collection<Plugin> plugins);
	Collection<Plugin> getPlugins();

	PluginManagerImpl getManager();
}
