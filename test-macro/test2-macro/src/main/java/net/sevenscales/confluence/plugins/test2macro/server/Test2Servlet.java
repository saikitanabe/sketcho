package net.sevenscales.confluence.plugins.test2macro.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class Test2Servlet extends RemoteServiceServlet {
  private static final String CONTENT_TYPE = "text/plain";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse response)
      throws ServletException, IOException {
    String imageUrl = "testkuvaa";
    response.setContentLength(imageUrl.length());
    response.setContentType(CONTENT_TYPE);
    response.getOutputStream().write(imageUrl.getBytes());
    response.getOutputStream().flush();
  }
}
