package io.core9.core.hooks;

public abstract class Hook {
	
	private String className;
	private String method;
	private String type;
	private Integer priority;
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	public abstract Object[] execute(Object... args);
	
	public Hook(String name, Integer priority) {
		this.className = name.substring(0, name.indexOf(':'));
		this.method = name.substring(name.indexOf(':') + 1);
		this.priority = priority;
	}

}
