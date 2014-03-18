package io.core9.core.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class TestPluginInvocationHandler implements InvocationHandler {
	
	Object plugin;
	
	public TestPluginInvocationHandler(Object plugin) {
		this.plugin = plugin;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		if(method.getName().equals("dummyTest")){
			return "invocationhandler";
		}
		return method.invoke(plugin, args);
		/*switch(method.getName()) {
		case "dummyTest":
			return "invocationhandler";
		default:
			return method.invoke(plugin, args);
		}*/
	}

}
