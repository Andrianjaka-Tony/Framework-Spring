package etu1765.framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

import use.Package;
import use.Utility;
import use.JSON;
import use.ModelView;
import etu1765.framework.Mapping;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import annotation.Auth;

@MultipartConfig(fileSizeThreshold = 2240 * 2240, maxFileSize = 2240 * 2240, maxRequestSize = 2240 * 2240 * 5 * 5)
public class FrontServlet extends HttpServlet {

  HashMap<String, Mapping> mappingUrls;
  HashMap<String, Object> objects;
  String path;

  public void updateSession(HttpServletRequest request, ModelView modelView) {
    HttpSession session = request.getSession();
    Set<String> keys = modelView.getSessions().keySet();
    for (String key : keys) {
      session.setAttribute(key, modelView.getSessions().get(key));
    }
  }

  public ModelView loadView(String url, HttpServletRequest req) {
    try {
      Method method = ModelView.getMethod(url, mappingUrls);
      if (ModelView.isAuth(method)) {
        String session = (String) method.getDeclaredAnnotation(Auth.class).getClass().getDeclaredMethod("session")
            .invoke(method.getDeclaredAnnotation(Auth.class));
        String value = (String) method.getDeclaredAnnotation(Auth.class).getClass().getDeclaredMethod("value")
            .invoke(method.getDeclaredAnnotation(Auth.class));
        HttpSession session2 = req.getSession();
        String sessValue = (String) session2.getAttribute(session);
        if (value.equals(sessValue) == false) {
          ModelView response = new ModelView("/error.jsp");
          return response;
        }
      }
    } catch (Exception e) {
    }

    String className = mappingUrls.get(url).getClassName();
    Object o = new Object();
    if (this.objects.get(className) != null) {
      o = this.objects.get(className);
    } else {
      try {
        Class<?> classe = Class.forName(className);
        Constructor<?> constructor = classe.getDeclaredConstructor();
        o = constructor.newInstance();
      } catch (Exception e) {
      }
    }
    ModelView modelView = new ModelView();
    try {
      modelView = ModelView.loadView(url, this.mappingUrls, req, o);
    } catch (Exception e) {
    }
    updateSession(req, modelView);
    return modelView;
  }

  public void executeSingleton(HttpServletRequest req, ModelView modelView, String className) throws Exception {
    Utility.resetObject(this.objects.get(className));
    Utility.save(req, mappingUrls, this.objects.get(className));
    modelView.addItem("form", this.objects.get(className));
  }

  public void executeNonSingleton(HttpServletRequest req, ModelView modelView) throws Exception {
    Object object = Utility.save(req, mappingUrls);
    modelView.addItem("form", object);
  }

  public void save(HttpServletRequest req, ModelView modelView) {
    try {
      String className = Utility.classToSave(req, mappingUrls);
      if (this.objects.get(className) != null) {
        this.executeSingleton(req, modelView, className);
      } else {
        this.executeNonSingleton(req, modelView);
      }
    } catch (Exception e) {
    }
  }

  public void dispatch(HttpServletRequest req, HttpServletResponse resp, ModelView modelView) throws Exception {
    req.setAttribute("data", modelView.getDatas());
    RequestDispatcher dispatcher = req.getRequestDispatcher(modelView.getViewName());
    dispatcher.forward(req, resp);
  }

  public boolean isSave(HttpServletRequest req) {
    boolean save = false;
    try {
      save = Utility.isSave(req, mappingUrls);
    } catch (Exception e) {
    }
    return save;
  }

  public void doSave(HttpServletRequest req, ModelView modelView) {
    if (isSave(req)) {
      save(req, modelView);
    }
  }

  public void init() {
    ServletContext context = getServletContext();
    this.path = context.getRealPath("");
    this.path += "WEB-INF\\classes";
    this.path = this.path.replace('\\', '/');
    try {
      this.mappingUrls = Package.scanPackages(this.path);
      this.objects = Package.singletons(this.path);
    } catch (Exception e) {
    }
  }

  public void normalTreatment(ModelView modelView, HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException, Exception {
    if (modelView.getViewName().equals("/error.jsp")) {
      this.dispatch(req, resp, modelView);
      return;
    }
    doSave(req, modelView);
    this.dispatch(req, resp, modelView);
  }

  public void JSONTreatment(ModelView modelView, HttpServletResponse response) throws Exception {
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    String json = JSON.stringify(modelView.getDatas());
    out.println(json);
  }

  protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException, Exception {
    String url = String.valueOf(req.getRequestURL());
    url = Utility.getUrl(url);

    // * Checker si on va save
    ModelView modelView = this.loadView(url, req);
    if (!modelView.isJson()) {
      this.normalTreatment(modelView, req, resp);
    } else {
      this.JSONTreatment(modelView, resp);
    }
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
