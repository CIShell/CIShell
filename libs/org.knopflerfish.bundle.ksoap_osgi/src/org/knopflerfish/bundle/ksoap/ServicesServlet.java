/*
 * Copyright (c) 2003-2004, KNOPFLERFISH project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 *
 * - Neither the name of the KNOPFLERFISH project nor the names of its
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.knopflerfish.bundle.ksoap;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.knopflerfish.util.servlet.WebApp;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.MarshalHashtable;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.servlet.SoapServlet;

/**
 * The <code>ServiceServlet</code> extends the SoapServlet to enable it to
 * work in an OSGi environment.
 * 
 * @author Lasse Helander (lars-erik.helander@home.se)
 */
public class ServicesServlet extends SoapServlet {
    private static final long serialVersionUID = 1L;

    protected String getWebappBase(HttpServletRequest request) {
        StringBuffer baseURL = new StringBuffer(128);

        baseURL.append(request.getScheme());
        baseURL.append("://");
        baseURL.append(request.getServerName());
        if (request.getServerPort() != 80) {
            baseURL.append(":");
            baseURL.append(request.getServerPort());
        }
        baseURL.append(request.getContextPath());
        baseURL.append(WebApp.webAppDescriptor.context);
        return baseURL.toString();
    }
    
    public synchronized void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER12);
        new MarshalHashtable().register(envelope);
        new MarshalBase64().register(envelope);
        
        this.setEnvelope(envelope);
        super.doPost(req, res);
    }
    
    
    protected Object getInstance(HttpServletRequest request) {
        System.out.println(request.getPathInfo());
        return super.getInstance(request);
    }
}
