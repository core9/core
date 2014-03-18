package io.core9.core.hooks;

import static org.junit.Assert.assertTrue;
import io.core9.core.PluginRegistry;
import io.core9.core.PluginRegistryImpl;
import io.core9.core.boot.BootstrapFramework;

import org.junit.Before;
import org.junit.Test;

public class HooksTest {

	PluginRegistry registry;
		
	@Before
	public void setUp() {
		BootstrapFramework.run();
		registry = PluginRegistryImpl.getInstance();
	}
	

	@Test
	public void testPluginWithHook() {
		TestPlugin plugin = (TestPlugin) registry.getPlugin(TestPluginImpl.class);
		assertTrue(plugin.realTest("Blaat").equals("success"));
	}

}
