package etu1765.framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Set;
import use.Package;
import etu1765.framework.Mapping;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontServlet extends HttpServlet {

  HashMap<String, Mapping> mappingUrls;

  public void init() {
    ServletContext context = getServletContext();
    String absolutePath = context.getRealPath("");
    absolutePath += "WEB-INF/classes";
    try {
      this.mappingUrls = Package.scanPackages(absolutePath);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    
    PrintWriter out = resp.getWriter();
    Set<String> set = this.mappingUrls.keySet();
    for (String key: set) {
      out.println("URL: " + key);
      out.println(" Classname: " + this.mappingUrls.get(key).getClassName());
      out.println(" Method: " + this.mappingUrls.get(key).getMethod());
      out.println("\n");
    }

  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      processRequest(req, resp);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      processRequest(req, resp);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
}
