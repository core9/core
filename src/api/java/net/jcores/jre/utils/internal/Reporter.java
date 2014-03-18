/*
 * Reporter.java
 * 
 * Copyright (c) 2010, Ralf Biedert All rights reserved.
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
package net.jcores.jre.utils.internal;

import static net.jcores.jre.CoreKeeper.$;

import java.util.concurrent.ConcurrentLinkedQueue;

import net.jcores.jre.CommonCore;
import net.jcores.jre.cores.CoreNumber;
import net.jcores.jre.extensions.GlobalExtension;
import net.jcores.jre.interfaces.functions.F1V;
import net.xeoh.nexus.Service;

/**
 * Manages and keeps internal trouble records. You do not need this.
 * 
 * @author Ralf Biedert
 */
public class Reporter {
    /** All known records */
    ConcurrentLinkedQueue<String> allRecords = new ConcurrentLinkedQueue<String>();
    
    /** The common core */
    CommonCore commonCore;

    public Reporter(CommonCore common) {
        this.commonCore = common;
    }
    
    /**
     * @param record
     */
    public void record(String record) {
        this.allRecords.add(record);
    }

    /**
     * Prints all records.
     */
    public void printRecords() {
        // Print what went wrong
        System.out.println(">>> jCores trouble log (" + this.commonCore.version() + ")");
        for (String r : this.allRecords) {
            System.out.println(">>> " + r);
        }
        
        // Check if we are current or not
        this.commonCore.net.get("http://api.jcores.net/versioncheck/", null).onNext(new F1V<String>() {
            @Override
            public void fV(String x) {
                String revisionOnline = $(x).split("-").trim().get(-1, "UNDEFINED");
                String revisionOffline = $($.version()).split("-").get(-1, "UNDEFINED");

                if(revisionOffline.contains("@")) {
                    System.out.println(">>> You are probably running from source. Cannot determine your exact version.");
                    return;
                }
                
                if(revisionOnline.contains("UNDEFINED")) {
                    System.out.println(">>> Latest online version unknown. Probably you are not on the internet.");
                    return;
                }

                final CoreNumber n = $(revisionOffline, revisionOnline).number(Long.class).as(CoreNumber.class);
                final long diff = n.get(0).longValue() - n.get(1).longValue();
                
                if(diff == 0) {
                    System.out.println(">>> Your version is up to date.");
                }
                
                if(diff > 0) {
                    System.out.println(">>> Your version is newer than the online version. Early bird ;-)");
                }
                
                if(diff < 0) {
                    System.out.println(">>> Your version is outdated. Get the latest version at http://jcores.net");
                } 
                
            }
        });
        
        // Check which extensions are loaded
        for(Service service : this.commonCore.nexus().list()) {
            final Object object = service.getService();
            if(object instanceof GlobalExtension) {
                System.out.println(">>> Loaded global extension '" + object.getClass().getCanonicalName() + "'");
            }
        }
        
        // Check if we are in a multiple-classloader scenario and emit a warning if we are
        try {
            final String ourID = this.commonCore.coreID();
            final String knownIDs = System.getProperty("jcores.net.knownIDs");
            
            if(knownIDs == null || knownIDs.equals(ourID)) {
                // We shouldn't say something if there's no need to
                // System.out.println(">>> Up to now only one jCores instance appears to be running, everything looks fine. But check again in the future.");
                
                // TODO: Not really thread safe ...
                if(knownIDs == null) {
                    System.setProperty("jcores.net.knownIDs", ourID);
                }
            } else {
                System.out.println(">>> Multiple jCores instances detected. Be careful with methods that have a @AttentionWithClassloaders annotation!");
                
                // TODO: Not really thread safe ...
                if(!knownIDs.contains(ourID)) {
                    System.setProperty("jcores.net.knownIDs", knownIDs + ";" + ourID);
                }
            }
        } catch (Exception e) {
            System.out.println(">>> Unable to check if we are in a multiple-classloader scenario. Probably you should be a bit careful.");
        }
    }
}
