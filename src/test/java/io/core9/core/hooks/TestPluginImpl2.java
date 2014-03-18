package io.core9.core.hooks;

import io.core9.core.hooks.Hook;

import java.util.ArrayList;
import java.util.Collection;

import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
public class TestPluginImpl2 implements TestPlugin2 {

	@Override
	public Collection<Hook> getHooks() {
		Collection<Hook> hooks = new ArrayList<Hook>();
		hooks.add(new Hook(io.core9.core.hooks.TestPlugin.class.getCanonicalName() + ":realTest:pre", 1) {
			
			@Override
			public Object[] execute(Object... args) {
				args[0] = "false";
				System.out.println(this);
				return args;
			}
		});
		hooks.add(new Hook(io.core9.core.hooks.TestPlugin.class.getCanonicalName() + ":realTest:pre", 2) {
			
			@Override
			public Object[] execute(Object... args) {
				args[0] = "success";
				return args;
			}
		});
		return hooks;
	}

}
