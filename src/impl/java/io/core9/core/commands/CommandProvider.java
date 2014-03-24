package io.core9.core.commands;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class CommandProvider {
	private static CommandProvider instance;
	private Map<String, HashMap<String, TreeMap<Integer, Command>>> commands;
	
	private CommandProvider() {
		commands = new HashMap<String, HashMap<String, TreeMap<Integer, Command>>>();
	}
	
	public static CommandProvider getInstance() {
		if(instance == null) {
			instance = new CommandProvider();
		}
		return instance;
		
	}

	public void registerCommands(Collection<Command> commands) {
		for(Command command : commands) {
			if(!this.commands.containsKey(command.getClassName())) {
				this.commands.put(command.getClassName(), new HashMap<String,TreeMap<Integer, Command>>());
			}
			if(!this.commands.get(command.getClassName()).containsKey(command.getMethod())) {
				this.commands.get(command.getClassName()).put(command.getMethod(), new TreeMap<Integer, Command>());
			}
			this.commands.get(command.getClassName()).get(command.getMethod()).put(command.getPriority(), command);
		}
	}
	
	public Collection<Command> getCommands(String plugin, String method) {
		try {
			return this.commands.get(plugin).get(method).values();
		} catch (NullPointerException e) {
			return null;
		}
	}
}
