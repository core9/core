package io.core9.core.boot;

import io.core9.core.PluginRegistry;

import java.util.ArrayList;
import java.util.SortedMap;

public interface BootstrapStrategies {
	void bootstrap();
	SortedMap<Integer, ArrayList<BootStrategy>> getStrategies();
	void setRegistry(PluginRegistry registry);
}
