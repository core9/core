package io.core9.core.hooks;

import io.core9.core.boot.CoreBootStrategy;
import io.core9.core.hooks.Hook;
import io.core9.core.hooks.ProvidesHooks;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;

import net.xeoh.plugins.base.Plugin;
import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
public class HookStrategyImpl extends CoreBootStrategy implements HookStrategy {
	HookProvider provider = HookProvider.getInstance(); 

	@Override
	public void processPlugins() {
		for(Plugin plugin : this.registry.getPlugins()) {
			List<Class<?>> interfaces = ClassUtils.getAllInterfaces(plugin.getClass());
			if(interfaces.contains(ProvidesHooks.class)) {
				Collection<Hook> hooks = ((ProvidesHooks) plugin).getHooks();
				if(hooks != null) {
					provider.registerHooks(hooks);
				}
			}
		}
	}

	@Override
	public Integer getPriority() {
		return 20;
	}
}
