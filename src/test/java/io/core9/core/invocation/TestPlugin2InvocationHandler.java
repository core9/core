package io.core9.core.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class TestPlugin2InvocationHandler implements InvocationHandler {
	
	private Object target;
	
	public TestPlugin2InvocationHandler(Object target) {
		this.target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if(method.getName().equals("dummyTest")) {
			return "fromtestplugin2invocationhandler";
		}
		return method.invoke(target, args);
	}

}
