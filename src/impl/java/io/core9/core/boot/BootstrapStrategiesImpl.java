package io.core9.core.boot;

import io.core9.core.PluginRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import net.xeoh.plugins.base.Plugin;

import org.apache.commons.lang3.ClassUtils;
import org.apache.log4j.Logger;

public class BootstrapStrategiesImpl implements BootstrapStrategies {
	private static Logger log = Logger.getLogger(BootstrapStrategiesImpl.class);

	private SortedMap<Integer, ArrayList<BootStrategy>> strategies;
	private PluginRegistry registry;

	@Override
	public SortedMap<Integer, ArrayList<BootStrategy>> getStrategies() {
		return strategies;
	}
	
	@Override
	public void bootstrap() {
		strategies = new TreeMap<Integer, ArrayList<BootStrategy>>();
		for(Plugin plugin: registry.getPlugins()) {
			List<Class<?>> interfaces = ClassUtils.getAllInterfaces(plugin.getClass());
			if(interfaces.contains(BootStrategy.class)) {
				log.info("Found BootStrategy in plugin: " + plugin.getClass().getName());
				BootStrategy strategy = (BootStrategy) plugin;
				Integer priority = strategy.getPriority();
				if(!strategies.containsKey(priority)) {
					strategies.put(priority, new ArrayList<BootStrategy>());
				}else{
					log.info("Priority confict with plugin : " + plugin.getClass().getName());
					log.info("Priority taken by plugin : " + strategies.get(priority).getClass().getName());
				}
				strategies.get(priority).add(strategy);
			}
		}
	}
	
	@Override
	public void setRegistry(PluginRegistry registry) {
		this.registry = registry;
	}
}
