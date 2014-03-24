package io.core9.core.commands;

import java.util.Collection;

/**
 * A plugin that provides hooks gives functionality to other plugins.
 * @author mark
 *
 */
public interface ProvidesCommands {
	Collection<Command> getCommands();
}
