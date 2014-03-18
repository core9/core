/**
 * <b>START HERE</b>, contains all available cores and their functionalities. However, please note 
 * that you don't create classes of this package directly, but rather through the 
 * {@link net.jcores.jre.CoreKeeper} (accessible as <code>$</code>, also see the 
 * <a href="http://code.google.com/p/jcores/wiki/EclipseIntegration">Eclipse Integration Guide</a>). For 
 * example, to create a CoreObject for a number of objects, you could write one of these:<br/><br/>
 * 
 * <ul>
 * <li><code>$(o1, o2, o3).print()</code></li>
 * <li><code>$(myArray).print()</code></li>
 * <li><code>$(someList).print()</code></li>
 * </ul><br/>
 * 
 * which returns a {@link net.jcores.jre.cores.CoreObject}. The <code>$</code>-function tries to pick the most 
 * suitable {@link net.jcores.jre.cores.Core} (better said: subclass of Core) for the elements you passed. 
 * Similar to the example above, when you call<br/><br/>
 * 
 * <code>$("a", "b", "c").join("-")</code><br/><br/>
 * 
 * a {@link net.jcores.jre.cores.CoreString} will be returned. For arrays and individual objects this works 
 * automatically, for collections (due to Java's type erasure) you will receive a {@link net.jcores.jre.cores.CoreObject} 
 * which you can then simply cast to what you need, e.g.,<br/><br/>
 * 
 * <ul>
 * <li><code>$(stringList).get(-1)</code></li>
 * <li><code>$(stringList).as(CoreString.class).join("-")</code></li>
 * </ul></br/>
 * 
 * In addition to the the cores in this package
 * also have a look at the {@link net.jcores.jre.CommonCore} (accessible through <code>$</code> as well), 
 * which contains common helper functions that are not directly bound to a set of objects. Once you have a 
 * core, you can start working with it, like this sequence which starts with a {@link net.jcores.jre.cores.CoreString} 
 * and does some chaining of commands:<br/><br/>
 * 
 * <code>$("New York", "Rio", "Tokyo").random(2).print().get(-1)</code><br/><br/>
 * 
 * 
 * Other useful functions can be found in the {@link net.jcores.jre.CommonCore}, which is contains all 
 * functions that are not inherently bound to a set of objects to process, like reporting, the short-hand
 * generation of maps and lists, and many more:<br/><br/>
 * 
 * <code>$.report()</code><br/><br/>
 * 
 * From here on, just start exploring what's inside the {@link net.jcores.jre.CommonCore} and the individual subclasses 
 * of {@link net.jcores.jre.cores.CoreObject} to see what you can do. Also, have a 
 * <a target="_blank" href="http://code.google.com/p/jcores/wiki/Examples">look at the examples section</a>, which 
 * already contains many use cases with lots of explanations.</a> 
 *
 * @since 1.0
 */
package net.jcores.jre.cores;