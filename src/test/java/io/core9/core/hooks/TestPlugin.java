package io.core9.core.hooks;

import io.core9.core.hooks.ConsumesHookHandlers;
import io.core9.core.plugin.Core9Plugin;

public interface TestPlugin extends Core9Plugin, ConsumesHookHandlers {
	String realTest(String body);
}
