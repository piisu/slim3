/*
 * Copyright 2004-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.slim3.controller;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

/**
 * This class handles the request.
 * 
 * @author higa
 * @since 3.0
 * 
 */
public class RequestHandler {

    /**
     * The request.
     */
    protected HttpServletRequest request;

    /**
     * Constructor.
     * 
     * @param request
     *            the request
     */
    public RequestHandler(HttpServletRequest request) {
        if (request == null) {
            throw new NullPointerException("The request parameter is null.");
        }
        this.request = request;
    }

    /**
     * Handles the request.
     * 
     * @return the handled request
     */
    @SuppressWarnings("unchecked")
    public HttpServletRequest handle() {
        for (Enumeration<String> e = request.getParameterNames(); e
            .hasMoreElements();) {
            String name = e.nextElement();
            if (name.endsWith("Array")) {
                request.setAttribute(name, request.getParameterValues(name));
            } else {
                request.setAttribute(name, request.getParameter(name));
            }
        }
        return request;
    }
}