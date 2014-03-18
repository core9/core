package io.core9.core.hooks;

import io.core9.core.hooks.Hook;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class HookProvider {
	private static HookProvider instance;
	private Map<String, HashMap<String, TreeMap<Integer, Hook>>> hooks;
	
	private HookProvider() {
		hooks = new HashMap<String, HashMap<String, TreeMap<Integer, Hook>>>();
	}
	
	public static HookProvider getInstance() {
		if(instance == null) {
			instance = new HookProvider();
		}
		return instance;
		
	}

	public void registerHooks(Collection<Hook> hooks) {
		for(Hook hook : hooks) {
			if(!this.hooks.containsKey(hook.getClassName())) {
				this.hooks.put(hook.getClassName(), new HashMap<String,TreeMap<Integer, Hook>>());
			}
			if(!this.hooks.get(hook.getClassName()).containsKey(hook.getMethod())) {
				this.hooks.get(hook.getClassName()).put(hook.getMethod(), new TreeMap<Integer, Hook>());
			}
			this.hooks.get(hook.getClassName()).get(hook.getMethod()).put(hook.getPriority(), hook);
		}
	}
	
	public Collection<Hook> getHooks(String plugin, String method) {
		try {
			return this.hooks.get(plugin).get(method).values();
		} catch (NullPointerException e) {
			return null;
		}
	}
}
