package io.core9.core.commands;

import io.core9.core.boot.CoreBootStrategy;


import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;

import net.xeoh.plugins.base.Plugin;
import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
public class CommandStrategyImpl extends CoreBootStrategy implements CommandStrategy {
	CommandProvider provider = CommandProvider.getInstance(); 

	@Override
	public void processPlugins() {
		for(Plugin plugin : this.registry.getPlugins()) {
			List<Class<?>> interfaces = ClassUtils.getAllInterfaces(plugin.getClass());
			if(interfaces.contains(ProvidesCommands.class)) {
				Collection<Command> commands = ((ProvidesCommands) plugin).getCommands();
				if(commands != null) {
					provider.registerCommands(commands);
				}
			}
		}
	}

	@Override
	public Integer getPriority() {
		return 20;
	}
}
