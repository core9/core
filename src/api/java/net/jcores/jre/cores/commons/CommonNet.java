/*
 * CommonFile.java
 * 
 * Copyright (c) 2011, Ralf Biedert All rights reserved.
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
package net.jcores.jre.cores.commons;

import static net.jcores.jre.CoreKeeper.$;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;

import net.jcores.jre.CommonCore;
import net.jcores.jre.annotations.SupportsOption;
import net.jcores.jre.interfaces.functions.F0R;
import net.jcores.jre.interfaces.functions.F1;
import net.jcores.jre.options.KillSwitch;
import net.jcores.jre.options.OnFailure;
import net.jcores.jre.options.Option;
import net.jcores.jre.utils.Async;
import net.jcores.jre.utils.map.MapEntry;

/**
 * Networking functions, e.g., HTTP-GET requests or finding free ports.
 * 
 * @author Ralf Biedert
 * @since 1.0
 * 
 */
public class CommonNet extends CommonNamespace {

    /**
     * Creates a common file object.
     * 
     * @param commonCore
     */
    public CommonNet(CommonCore commonCore) {
        super(commonCore);
    }

    /**
     * Finds a free TCP port.
     * 
     * @return A free TCP port or -1 of none was found (which is likely a bug). 
     */
    public int findFreeTCP() {
        return findFreeTCP(20000 + $.random().nextInt(30000));
    }
    
    /**
     * Finds a free TCP port starting from a given base port.
     * 
     * @param base The base port to star tthe search from. 
     * 
     * @return A free TCP port or -1 of none was found (which is likely a bug). 
     */
    public int findFreeTCP(int base) {
        for (int i = 0; i < 5000; i++) {
            if (TCPavailable(base + i)) return base + i;
        }

        return -1;
    }


    /**
     * Checks if a given port is available or not on the "default" interface.
     * 
     * @param port The port to check.
     * 
     * @return True if the port is available, false if not.
     */
    public boolean TCPavailable(int port) {
        // Code shamelessly stolen from
        // (http://stackoverflow.com/questions/434718/sockets-discover-port-availability-using-java)
        ServerSocket ss = null;

        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            return true;
        } catch (IOException e) {} finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }
    
    
    /**
     * Performs a HTTP GET operation on the given URL and passes the 
     * specified data. 
     * 
     * @since 1.0
     * @param url The URL to contact.
     * @param data The parameters to send (can be null).
     * @param options Optional arguments, especially {@link KillSwitch} and {@link OnFailure}.
     * @return An {@link Async} object which will contain the result (content) the server gave.  
     */
    @SupportsOption(options = { KillSwitch.class, OnFailure.class })
    public Async<String> get(final String url, final Map<String, String> data, final Option... options) {
        
        return this.commonCore.async(new F0R<String>() {
            @Override
            public String f() {
                // Assemble call (encode & join parameters)
                final String param = $(data).map(new F1<MapEntry<String,String>, String>() {
                    @Override
                    public String f(MapEntry<String, String> x) {
                        return x.key() + "=" + $(x.value()).encode().get(0);
                    }
                }).string().join("&");
                
                // Perform call.
                return $(url + "?" + param).uri().input(options).text().get(0);
            }
        }, options);
    }
}
