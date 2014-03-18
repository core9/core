package io.core9.core.boot;

import io.core9.core.PluginRegistry;

public abstract class CoreBootStrategy implements BootStrategy {
	protected PluginRegistry registry;

	@Override
	public void setRegistry(PluginRegistry registry) {
		this.registry = registry;
	}

}
