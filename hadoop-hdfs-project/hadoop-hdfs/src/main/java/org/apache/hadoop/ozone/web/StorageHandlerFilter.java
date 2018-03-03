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
