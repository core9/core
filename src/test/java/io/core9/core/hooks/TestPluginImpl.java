package io.core9.core.hooks;

import io.core9.core.hooks.Hook;

import java.util.List;

import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
public class TestPluginImpl implements TestPlugin {

	@Override
	public void setHooks(List<? extends Hook> hooks) {
		
	}

	@Override
	public String realTest(String body) {
		return body;
	}
}
