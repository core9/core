package io.core9.core.proxy;

import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import net.xeoh.plugins.base.Plugin;

public class InvocationHandlerProvider {

	private static InvocationHandlerProvider instance;

	private Map<String, SortedMap<Integer, Class<? extends InvocationHandler>>> invocationHandlers;

	private InvocationHandlerProvider() {
		invocationHandlers = new HashMap<String, SortedMap<Integer,Class<? extends InvocationHandler>>>();
	}
	
	public void addInvocationHandler(Class<? extends Plugin> plugin, Integer priority, Class<? extends InvocationHandler> handler){
		this.invocationHandlers.get(plugin).put(priority, handler);
	}

	public Class<? extends InvocationHandler> getInvocationHandler(Class<? extends Plugin> plugin) {
		
		String tmp = plugin.getName();
		SortedMap<Integer, Class<? extends InvocationHandler>> ihp = invocationHandlers.get(tmp);
		Class<? extends InvocationHandler> handler = null;
		if(ihp != null) {
			int lastkey = invocationHandlers.get(plugin.getName()).lastKey();
			handler = ihp.get(lastkey);
		}
		if(handler == null) handler = DefaultInvocationHandler.class; 
		return handler;
	}

	public static InvocationHandlerProvider getInstance() {
		if (instance == null) {
			instance = new InvocationHandlerProvider();
		}
		return instance;
	}



	public void setDefaultInvocationHandler(Class<?> plugin, Class<? extends InvocationHandler> handler) {
		if(!invocationHandlers.containsKey(plugin.getName())) {
			invocationHandlers.put(plugin.getName(), new TreeMap<Integer,Class<? extends InvocationHandler>>());
		}
		invocationHandlers.get(plugin.getName()).put(2, handler);
	}

}
