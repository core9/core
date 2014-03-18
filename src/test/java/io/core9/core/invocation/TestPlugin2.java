package io.core9.core.invocation;

import io.core9.core.hooks.ConsumesHookHandlers;
import io.core9.core.hooks.ProvidesHooks;
import io.core9.core.plugin.Core9Plugin;
import io.core9.core.proxy.HasInvocationHandler;

public interface TestPlugin2 extends Core9Plugin, ConsumesHookHandlers, ProvidesHooks, HasInvocationHandler  {

	String dummyTest();

}
