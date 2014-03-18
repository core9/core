package io.core9.core.boot;

import io.core9.core.PluginRegistry;

public interface BootStrategy {


	/**
	 * Processes all plugins
	 */
	public void processPlugins();

	/**
	 * Set the plugin registry
	 */
	void setRegistry(PluginRegistry registry);
	
	/**
	 * Return the Plugin priority
	 * @return Integer
	 */
	Integer getPriority();

}
