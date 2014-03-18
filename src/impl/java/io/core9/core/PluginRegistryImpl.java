package io.core9.core;

import io.core9.core.PluginRegistry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.xeoh.plugins.base.Plugin;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.impl.PluginManagerImpl;
import net.xeoh.plugins.base.util.JSPFProperties;

public class PluginRegistryImpl implements PluginRegistry {
	
	private static PluginRegistry singleton;
	private Map<Class<? extends Plugin>,Plugin> registry = new HashMap<Class<? extends Plugin>, Plugin>();
	private PluginManagerImpl pm; 
	
	@Override
	public void registerPlugin(Class<? extends Plugin> clazz, Plugin plugin) {
		this.registry.put(clazz, plugin);
	}
	
	@Override
	public Plugin getPlugin(Class<? extends Plugin> className) {
		return registry.get(className);
	}
	
	@Override
	public void registerPlugins(Collection<Plugin> plugins) {
	}
	
	@Override
	public Collection<Plugin> getPlugins() {
		return registry.values();
	}

	public static PluginRegistry getInstance() {
		if(singleton == null) {
			singleton = (PluginRegistry) new PluginRegistryImpl();
		}
		return singleton;
	}

	private PluginRegistryImpl() {
		final JSPFProperties props = new JSPFProperties();
	    props.setProperty(PluginManager.class, "cache.enabled", "true");
	    props.setProperty(PluginManager.class, "cache.mode", "weak");
	    props.setProperty(PluginManager.class, "cache.file", "jspf.cache");
	    //props.setProperty(PluginManager.class, "logging.level", "FINE");
	    //props.setProperty(BootImpl.class, "plugin.disabled", "true");
	    pm = (PluginManagerImpl) PluginManagerFactory.createPluginManager(props);
	}

	@Override
	public PluginManagerImpl getManager() {
		return pm;
	}

}
