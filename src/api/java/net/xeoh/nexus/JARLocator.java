/*
 * JARLocator.java
 * 
 * Copyright (c) 2011, Ralf Biedert, DFKI. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the author nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package net.xeoh.nexus;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.xeoh.nexus.options.Option;

/**
 * A JAR locator locates classes from a given file.
 * 
 * @author Ralf Biedert
 * @since 1.0
 */
public class JARLocator extends URILoader {
    /**
     * Locates classes in the given JAR.
     * 
     * @param uri Scan the given URI.
     * @param options The options this locator supports.
     */
    public JARLocator(URI uri, Option... options) {
        super(uri, options);
    }

    /**
     * Locates classes in the given JAR.
     * 
     * @param file Scan the given file.
     */
    public JARLocator(File file) {
        this(file.toURI());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.xeoh.nexus.Abstract2StageLocator#candidates()
     */
    @Override
    public Collection<Candidate> candidates() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.xeoh.nexus.Abstract2StageLocator#locate(java.util.Collection)
     */
    @Override
    public Collection<Service> locate(Collection<Candidate> candidates) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.xeoh.nexus.AbstractLocator#locate()
     */
    @Override
    public Collection<Service> locate() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Lists all top level class entries for the given URL pointing to a
     * JAR file.
     * 
     * @param uri The URI to load the JAR from.
     * @throws IOException When the JAR could not be accessed.
     * @return A list with all class entries.
     */
    protected Collection<String> listAll(URI uri) throws IOException {
        final Collection<String> rval = new ArrayList<String>();

        // Use the native JAR access mechanism to get our classes.
        try {
            final JarURLConnection connection = (JarURLConnection) new URL("jar:" + uri + "!/").openConnection();
            final JarFile jarFile = connection.getJarFile();
            final Enumeration<JarEntry> entries = jarFile.entries();

            // Get all avaliable entreis
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();

                // We only search for class file entries
                if (entry.isDirectory()) continue;
                if (!entry.getName().endsWith(".class")) continue;

                // Convert the name
                String name = entry.getName();
                name = name.replaceAll("/", ".");

                // Remove trailing .class
                if (name.endsWith("class")) {
                    name = name.substring(0, name.length() - 6);
                }

                rval.add(name);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return rval;
    }
}
