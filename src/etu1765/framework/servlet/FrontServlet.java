package etu1765.framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
// import java.util.Set;

import use.Package;
import use.Utility;
import use.ModelView;
import etu1765.framework.Mapping;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@MultipartConfig(fileSizeThreshold = 2240 * 2240, maxFileSize = 2240 * 2240, maxRequestSize = 2240 * 2240 * 5 * 5)
public class FrontServlet extends HttpServlet {

  HashMap<String, Mapping> mappingUrls;
  HashMap<String, Object> objects;
  String path;

  public void init() {
    ServletContext context = getServletContext();
    this.path = context.getRealPath("");
    this.path += "WEB-INF\\classes";
    this.path = this.path.replace('\\', '/');
    try {
      this.mappingUrls = Package.scanPackages(this.path);
      this.objects = Package.singletons(this.path);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException, Exception {
    PrintWriter out = resp.getWriter();
    String url = String.valueOf(req.getRequestURL());
    url = Utility.getUrl(url);

    // Checker si l'URL est l'URL de base
    if (url.compareToIgnoreCase("/") == 0) {
      RequestDispatcher dispatcher = req.getRequestDispatcher("index.jsp");
      dispatcher.forward(req, resp);
    }

    // Checker si on va save
    ModelView modelView = ModelView.loadView(url, this.mappingUrls, req);
    boolean save = false;
    try {
      save = Utility.isSave(req, mappingUrls);
    } catch (Exception e) {
      System.out.println(e);
    }
    // si on save
    if (save) {
      try {
        String className = Utility.classToSave(req, mappingUrls);
        if (this.objects.get(className) != null) {
          Utility.resetObject(this.objects.get(className));
          Utility.save(req, mappingUrls, this.objects.get(className));
          modelView.addItem("form", this.objects.get(className));
          System.out.println(this.objects.get(className));
        } else {
          Object object = Utility.save(req, mappingUrls);
          modelView.addItem("form", object);
        }
      } catch (Exception e) {
        out.println(e.getMessage());
      }
    }

    req.setAttribute("data", modelView.getDatas());
    RequestDispatcher dispatcher = req.getRequestDispatcher(modelView.getViewName());
    dispatcher.forward(req, resp);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    PrintWriter out = resp.getWriter();
    try {
      this.processRequest(req, resp);
    } catch (Exception e) {
      out.println(e.getMessage());
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    PrintWriter out = resp.getWriter();
    try {
      this.processRequest(req, resp);
    } catch (Exception e) {
      out.println(e.getMessage());
    }
  }

}
