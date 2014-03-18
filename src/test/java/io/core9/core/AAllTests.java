package io.core9.core;

import io.core9.core.hooks.HooksTest;
import io.core9.core.invocation.InvocationHandlersTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({InvocationHandlersTest.class, HooksTest.class})
public class AAllTests {
	
}
