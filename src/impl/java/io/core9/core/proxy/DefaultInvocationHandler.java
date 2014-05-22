package io.core9.core.proxy;

import io.core9.core.hooks.Hook;
import io.core9.core.hooks.HookProvider;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;

public class DefaultInvocationHandler implements InvocationHandler {
	private Object target;
	private HookProvider hookprovider = HookProvider.getInstance();

	public DefaultInvocationHandler(Object target) {
		this.target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String name = proxy.getClass().getInterfaces()[0].getName();
		Collection<Hook> pres = hookprovider.getHooks(name, method.getName() + ":pre");
		Collection<Hook> posts = hookprovider.getHooks(name, method.getName() + ":post");
		
		Object[] newargs = null;
		if(pres != null) {
			for(Hook hook : pres) {
				newargs = hook.execute(args);
			}
			if(newargs != null) {
				args = newargs;
			}
		}
		Object ret = execute(proxy, method, args);
		if(posts != null) {
			for(Hook hook : posts) {
				if(ret == null) {
					ret = hook.execute(args);
				} else {
					ret = hook.execute(ret)[0];
				}
			}
		}
		return ret;
	}
	
	/**
	 * Executes the real called method (can be overridden)
	 * @param proxy
	 * @param method
	 * @param args
	 * @return Object
	 * @throws Throwable
	 */
	protected Object execute(Object proxy, Method method, Object[] args) throws Throwable {
		return method.invoke(target, args);
	}

	public Object getOriginalObject() {
		return target;
	}
}
