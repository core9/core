package io.core9.core.hooks;

import java.util.Collection;

/**
 * A plugin that provides hooks gives functionality to other plugins.
 * @author mark
 *
 */
public interface ProvidesHooks {
	Collection<Hook> getHooks();
}
