package io.core9.core.executor;

import io.core9.core.plugin.Core9Plugin;

public interface TestPlugin extends Core9Plugin, Executor {
	boolean getState();
}
