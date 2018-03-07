/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.ozone.web;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import org.apache.hadoop.ozone.web.handlers.StorageHandlerBuilder;
import org.apache.hadoop.ozone.web.interfaces.StorageHandler;

import org.apache.commons.io.IOUtils;

/**
 * Global filter to save storageHandler to a thread local variable.
 */
public class StorageHandlerFilter implements Filter {

  private StorageHandler storageHandler;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    storageHandler = (StorageHandler) filterConfig.getServletContext()
        .getAttribute(StorageHandler.class.toString());
  }

  @Override
  public void doFilter(ServletRequest servletRequest,
      ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
    StorageHandlerBuilder.setStorageHandler(storageHandler);
    try {
      filterChain.doFilter(servletRequest, servletResponse);
    } finally {
      StorageHandlerBuilder.removeStorageHandler();
    }

  }

  @Override
  public void destroy() {
  }
}
