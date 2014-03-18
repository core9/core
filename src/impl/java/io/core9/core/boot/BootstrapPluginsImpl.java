package io.core9.core.boot;

import io.core9.core.Config;
import io.core9.core.PluginRegistry;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import net.xeoh.plugins.base.Plugin;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.spawning.handler.InjectHandler;
import net.xeoh.plugins.base.options.addpluginsfrom.OptionReportAfter;

/**
 * Bootstraps the plugins in the /plugins folder
 * 
 * @author mark
 * 
 */
public class BootstrapPluginsImpl implements BootstrapPlugins {

	private Collection<Plugin> plugins;
	private PluginRegistry registry;

	@Override
	public void bootstrap() {

		Config config = Config.getInstance();
		
		String[] pluginDirs = config.getPluginDirs();
		for (int i = 0; i < pluginDirs.length; i++) {
			registry.getManager().addPluginsFrom(new File(pluginDirs[i]).toURI(), new OptionReportAfter()); 	
		}
		try {
			registry.getManager().addPluginsFrom(new URI("classpath://*"), new OptionReportAfter());
		} catch (Exception e) {	
			e.printStackTrace();
		}

		
		plugins = registry.getManager().getPluginRegistry().getAllPlugins();
				
		for (Plugin plugin : plugins) {
			registry.registerPlugin(plugin.getClass(), plugin);
			PluginManager pluginManager = registry.getManager();
			try {
				// this is only possible when all plugins are loaded
	            new InjectHandler(pluginManager).init(plugin);
            } catch (Exception e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
		}
	}

	@Override
	public void setRegistry(PluginRegistry registry) {
		this.registry = registry;
	}
	
	public static void addPath(String s) throws Exception {
	    File f = new File(s);
	    URL u = f.toURI().toURL();
	    System.out.println("Adding files from: " + u.toString());
	    URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
	    Class<URLClassLoader> urlClass = URLClassLoader.class;
	    Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
	    method.setAccessible(true);
	    method.invoke(urlClassLoader, new Object[]{u});
	}
}
