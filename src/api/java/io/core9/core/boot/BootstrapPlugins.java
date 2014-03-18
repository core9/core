package io.core9.core.boot;

import io.core9.core.PluginRegistry;

public interface BootstrapPlugins {
	
	void bootstrap();
	
	void setRegistry(PluginRegistry registry);

}
