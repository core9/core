package io.core9.core.executor;

import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
public class TestPluginImpl implements TestPlugin {
	
	private boolean state = false;

	@Override
	public void execute() {
		state = true;
	}

	@Override
	public boolean getState() {
		return state;
	}
}
