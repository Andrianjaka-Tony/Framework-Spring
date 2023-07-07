package use;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import etu1765.framework.Mapping;
import javax.servlet.http.HttpServletRequest;

public class ModelView {
  private String viewName;
  private HashMap<String, Object> datas;

  public ModelView() {
    this.datas = new HashMap<String, Object>();
  }

  public void addItem(String key, Object object) {
    this.datas.put(key, object);
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

  public static boolean hasParameters(String url, HashMap<String, Mapping> mappingUrls) throws Exception {
    return ModelView.getMethod(url, mappingUrls).getParameterTypes().length > 0;
  }

  public static Object[] loadParameters(String url, HashMap<String, Mapping> mappingUrls, HttpServletRequest request) throws Exception {
    Object[] reponse = new Object[Utility.countParameters(request)];
    Vector<String> paramValues = Utility.parameters(request);
    Method method = ModelView.getMethod(url, mappingUrls);
    Class<?>[] types = method.getParameterTypes();
    int index = 0;
    for (Class<?> type: types) {
      if (type.getName().equals("java.lang.Integer")) {
        reponse[index] = Integer.valueOf(request.getParameter(paramValues.get(index)));
      } else if (type.getName().equals("java.lang.String")) {
        reponse[index] = String.valueOf(request.getParameter(paramValues.get(index)));
      }
      index ++;
    }

    return reponse;
  }

  public static ModelView loadView(String url, HashMap<String, Mapping> mappingUrls, HttpServletRequest request) throws Exception {

    Set<String> set = mappingUrls.keySet();
    if (!set.contains(url)) {
      throw new Exception("404 not found!");
    }
    
    String className = mappingUrls.get(url).getClassName();
    String methodName = mappingUrls.get(url).getMethod();
    Class<?> classe = Class.forName(className);
    Constructor<?> constructor = classe.getDeclaredConstructor();
    Object object = constructor.newInstance();

    if (!ModelView.hasParameters(url, mappingUrls)) {
      Method method = classe.getDeclaredMethod(methodName);
      ModelView modelView = new ModelView();
      modelView = (ModelView) method.invoke(object);
      return modelView;
    } else {
      Method method = ModelView.getMethod(url, mappingUrls);
      Method invoke = classe.getDeclaredMethod(methodName, method.getParameterTypes());
      Object[] paramValues = ModelView.loadParameters(url, mappingUrls, request);
      ModelView modelView = new ModelView();
      modelView = (ModelView) invoke.invoke(object, paramValues);
      return modelView;
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
}
