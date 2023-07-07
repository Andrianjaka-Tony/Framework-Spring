package use;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import annotation.Auth;
import etu1765.framework.Mapping;

public class ModelView {
  private String viewName;
  private HashMap<String, Object> datas;
  private HashMap<String, Object> sessions;
  private boolean isJson;
  private boolean invalidateSession;
  private Vector<String> removeSession;

  public ModelView() {
    this.datas = new HashMap<String, Object>();
    this.sessions = new HashMap<String, Object>();
    this.removeSession = new Vector<String>();
  }

  public ModelView(String view) {
    this.viewName = view;
  }

  public void addItem(String key, Object object) {
    this.datas.put(key, object);
  }

  public void addSessions(String key, Object value) {
    this.sessions.put(key, value);
  }

  public static Method getMethod(String url, HashMap<String, Mapping> mappingUrls) throws Exception {
    Method method = null;
    String className = mappingUrls.get(url).getClassName();
    String methodName = mappingUrls.get(url).getMethod();
    Class<?> classe = Class.forName(className);
    for (Method meth : classe.getDeclaredMethods()) {
      if (meth.getName().equals(methodName)) {
        method = meth;
      }
    }

    return method;
  }

  public static boolean isAuth(Method method) {
    return method.isAnnotationPresent(Auth.class);
  }

  public static boolean hasParameters(String url, HashMap<String, Mapping> mappingUrls) throws Exception {
    return ModelView.getMethod(url, mappingUrls).getParameterTypes().length > 0;
  }

  public static Object[] loadParameters(String url, HashMap<String, Mapping> mappingUrls, HttpServletRequest request)
      throws Exception {
    Object[] reponse = new Object[Utility.countParameters(request)];
    Vector<String> paramValues = Utility.parameters(request);
    Method method = ModelView.getMethod(url, mappingUrls);
    Class<?>[] types = method.getParameterTypes();
    int index = 0;
    for (Class<?> type : types) {
      if (type.getName().equals("java.lang.Integer")) {
        reponse[index] = Integer.valueOf(request.getParameter(paramValues.get(index)));
      } else if (type.getName().equals("java.lang.String")) {
        reponse[index] = String.valueOf(request.getParameter(paramValues.get(index)));
      }
      index++;
    }

    return reponse;
  }

  public static ModelView invokeWithoutParameters(Class<?> classe, String methodName, Object object) throws Exception {
    Method method = classe.getDeclaredMethod(methodName);
    ModelView modelView = new ModelView();
    modelView = (ModelView) method.invoke(object);
    return modelView;
  }

  public static ModelView invokeWithParameters(String url, HashMap<String, Mapping> mappingUrls, Class<?> classe,
      String methodName, HttpServletRequest request, Object object) throws Exception {
    Method method = ModelView.getMethod(url, mappingUrls);
    Method invoke = classe.getDeclaredMethod(methodName, method.getParameterTypes());
    Object[] paramValues = ModelView.loadParameters(url, mappingUrls, request);
    ModelView modelView = new ModelView();
    modelView = (ModelView) invoke.invoke(object, paramValues);
    return modelView;
  }

  public static ModelView loadView(String url, HashMap<String, Mapping> mappingUrls, HttpServletRequest request,
      Object o)
      throws Exception {

    Set<String> set = mappingUrls.keySet();
    if (!set.contains(url)) {
      throw new Exception("404 not found!");
    }

    String className = mappingUrls.get(url).getClassName();
    String methodName = mappingUrls.get(url).getMethod();
    Class<?> classe = Class.forName(className);

    if (!ModelView.hasParameters(url, mappingUrls)) {
      return ModelView.invokeWithoutParameters(classe, methodName, o);
    } else {
      return ModelView.invokeWithParameters(url, mappingUrls, classe, methodName, request, o);
    }
  }

  public String getViewName() {
    return viewName;
  }

  public void setViewName(String viewName) {
    this.viewName = viewName;
  }

  public HashMap<String, Object> getDatas() {
    return datas;
  }

  public void setDatas(HashMap<String, Object> datas) {
    this.datas = datas;
  }

  public HashMap<String, Object> getSessions() {
    return sessions;
  }

  public void setSessions(HashMap<String, Object> sessions) {
    this.sessions = sessions;
  }

  public boolean isJson() {
    return isJson;
  }

  public void setJson(boolean isJson) {
    this.isJson = isJson;
  }

  public boolean isInvalidateSession() {
    return invalidateSession;
  }

  public void setInvalidateSession(boolean invalidateSession) {
    this.invalidateSession = invalidateSession;
  }

  public Vector<String> getRemoveSession() {
    return removeSession;
  }

  public void setRemoveSession(Vector<String> removeSession) {
    this.removeSession = removeSession;
  }
}
