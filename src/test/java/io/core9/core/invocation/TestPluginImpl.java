package io.core9.core.invocation;

import java.lang.reflect.InvocationHandler;
import java.util.Collection;
import java.util.List;

import io.core9.core.hooks.Hook;
import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
public class TestPluginImpl implements TestPlugin {
	@Override
	public String dummyTest() {
		return "standardplugin";
	}

	@Override
	public Class<? extends InvocationHandler> getInvocationHandler() {
		return TestPluginInvocationHandler.class;
	}

	@Override
	public Collection<Hook> getHooks() {
		return null;
	}



	@Override
	public void setHooks(List<? extends Hook> hooks) {
		
	}

}
