package io.core9.core.commands;

import java.util.List;


public interface ConsumesCommandHandlers {
	
	void setCommands(List<? extends Command> commands);

}
