package org.apache.hadoop.hdds.server;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.WriterAppender;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogStreamServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    String PATTERN = "%d [%p|%c|%C{1}] %m%n";
    WriterAppender appender =
        new WriterAppender(new PatternLayout(PATTERN), resp.getWriter());
    appender.setThreshold(Level.TRACE);

    try {
      Logger.getRootLogger().addAppender(appender);
      try {
        Thread.sleep(Integer.MAX_VALUE);
      } catch (InterruptedException e) {
        //interrupted
      }
    } finally {
      Logger.getRootLogger().removeAppender(appender);
    }
  }

}
