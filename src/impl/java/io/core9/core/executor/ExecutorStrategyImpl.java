package io.core9.core.executor;

import io.core9.core.boot.CoreBootStrategy;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;

import net.xeoh.plugins.base.Plugin;
import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
public class ExecutorStrategyImpl extends CoreBootStrategy implements ExecutorStrategy {

	@Override
	public void processPlugins() {
		for(Plugin plugin : this.registry.getPlugins()) {
			List<Class<?>> interfaces = ClassUtils.getAllInterfaces(plugin.getClass());
			if(interfaces.contains(Executor.class)) {
				((Executor) plugin).execute();
			}
		}
	}

	@Override
	public Integer getPriority() {
		return Integer.MAX_VALUE;
	}
}
