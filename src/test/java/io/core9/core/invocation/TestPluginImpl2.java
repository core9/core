package io.core9.core.invocation;

import java.lang.reflect.InvocationHandler;
import java.util.Collection;
import java.util.List;

import io.core9.core.hooks.Hook;
import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
public class TestPluginImpl2 implements TestPlugin2  {

	

	@Override
	public Class<? extends InvocationHandler> getInvocationHandler() {
		return TestPlugin2InvocationHandler.class;
	}

	@Override
	public void setHooks(List<? extends Hook> hooks) {
		
	}

	@Override
	public Collection<Hook> getHooks() {
		return null;
	}

	@Override
	public String dummyTest() {
		return "hellofromtest";
	}
}
