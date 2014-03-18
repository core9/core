package io.core9.core.invocation;

import static org.junit.Assert.assertTrue;
import io.core9.core.PluginRegistry;
import io.core9.core.PluginRegistryImpl;
import io.core9.core.boot.BootstrapFramework;


import org.junit.Before;
import org.junit.Test;

public class InvocationHandlersTest {

	PluginRegistry registry;
	
	@Before
	public void setUp() {
		BootstrapFramework.run();
		registry = PluginRegistryImpl.getInstance();
	}
	
	@Test
	public void testPluginWithDefaultInvocationHandler(){
		TestPlugin testPlugin = (TestPlugin) registry.getPlugin(TestPluginImpl.class);
		assertTrue(testPlugin.dummyTest().equals("invocationhandler"));
	}
	
	@Test
	public void testPluginWithOverride() {
		TestPlugin testPlugin = (TestPlugin) registry.getPlugin(TestPluginImpl.class);
		assertTrue(testPlugin.dummyTest().equals("invocationhandler"));
		
		TestPlugin2 testPlugin2 = (TestPlugin2) registry.getPlugin(TestPluginImpl2.class);
		assertTrue(testPlugin2.dummyTest().equals("fromtestplugin2invocationhandler"));
		
	}
}
