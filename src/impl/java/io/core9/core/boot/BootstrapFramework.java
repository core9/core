package io.core9.core.boot;

import java.util.List;

import io.core9.core.PluginRegistry;
import io.core9.core.PluginRegistryImpl;

import org.apache.log4j.Logger;

/**
 * Framework bootstrap
 * 
 * Starts the framework by loading the plugins and setting a bootstrategy
 * 
 * @author mark
 *
 */
public class BootstrapFramework {
	private static Logger log = Logger.getLogger(BootstrapFramework.class);
	private static PluginRegistry registry = PluginRegistryImpl.getInstance();
	
	
	public void runInstance(){
		log.info("Starting Core9 Framework");
		bootstrapPlugins();
		bootstrapStrategies();
		log.info("Core9 Framework started");
	}
	
	public static void run(){
		log.info("Starting Core9 Framework");
		bootstrapPlugins();
		bootstrapStrategies();
		log.info("Core9 Framework started");
	}
	
	private static void bootstrapPlugins() {
		BootstrapPlugins plugins = new BootstrapPluginsImpl();
		plugins.setRegistry(registry);
		plugins.bootstrap();
	}
	
	private static void bootstrapStrategies() {
		BootstrapStrategies strategies = new BootstrapStrategiesImpl();
		strategies.setRegistry(registry);
		strategies.bootstrap();
		int numberOfStrategies = 0;
		for(List<BootStrategy> list : strategies.getStrategies().values()) {
			for(BootStrategy strategy : list) {
				log.info("Executing strategy: " + strategy.getClass().getName());
				strategy.setRegistry(registry);
				strategy.processPlugins();
				numberOfStrategies++;
			}
		}
		if(numberOfStrategies == 0) {
			log.error("No BootStrategies found, please use a BootStrategy");
		}
	}

	public static void main(String[] args) throws Exception {
		BootstrapFramework.run();
		System.in.read();
	}
}
