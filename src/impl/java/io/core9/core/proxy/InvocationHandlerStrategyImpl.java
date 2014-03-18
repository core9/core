package io.core9.core.proxy;

import io.core9.core.boot.CoreBootStrategy;

import java.lang.reflect.InvocationHandler;
import java.util.List;

import net.xeoh.plugins.base.Plugin;
import net.xeoh.plugins.base.annotations.PluginImplementation;

import org.apache.commons.lang3.ClassUtils;

@PluginImplementation
public class InvocationHandlerStrategyImpl extends CoreBootStrategy implements InvocationHandlerStrategy {
	InvocationHandlerProvider provider = InvocationHandlerProvider.getInstance(); 

	@Override
	public void processPlugins() {
		for(Plugin plugin : this.registry.getPlugins()) {
			List<Class<?>> interfaces = ClassUtils.getAllInterfaces(plugin.getClass());
			if(interfaces.contains(HasInvocationHandler.class)) {
				Class<? extends InvocationHandler> handler = ((HasInvocationHandler) plugin).getInvocationHandler();
				//FIXME What if the plugin implements 2 interfaces?
				provider.setDefaultInvocationHandler(plugin.getClass().getInterfaces()[0], handler);
			}
		}
		
		for(Plugin plugin : this.registry.getPlugins()) {
			//FIXME What if the plugin implements 2 interfaces?
			@SuppressWarnings("unchecked")
			Class<? extends Plugin> inf = (Class<? extends Plugin>) plugin.getClass().getInterfaces()[0];
			Plugin wrapped = (Plugin) this.registry.getManager().getPlugin(inf, provider.getInvocationHandler(inf));
			registry.registerPlugin(plugin.getClass(), wrapped);
		}
	}

	@Override
	public Integer getPriority() {
		return 10;
	}
}
