package io.core9.core.proxy;

import java.lang.reflect.InvocationHandler;

public interface HasInvocationHandler {
	Class<? extends InvocationHandler> getInvocationHandler();
}
