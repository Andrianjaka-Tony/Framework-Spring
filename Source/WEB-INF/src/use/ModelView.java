package use;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

import etu1765.framework.Mapping;

public class ModelView {
  private String viewName;
  private HashMap<String, Object> datas;

  public ModelView() {
    this.datas = new HashMap<String, Object>();
  }

  public void addItem(String key, Object object) {
    this.datas.put(key, object);
  }

  public static ModelView loadView(String url, HashMap<String, Mapping> mappingUrls) throws Exception {

    Set<String> set = mappingUrls.keySet();
    if (!set.contains(url)) {
      throw new Exception("404 not found!");
    }
    
    String className = mappingUrls.get(url).getClassName();
    String methodName = mappingUrls.get(url).getMethod();
    Class<?> classe = Class.forName(className);
    Method method = classe.getDeclaredMethod(methodName);

    Constructor<?> constructor = classe.getDeclaredConstructor();
    Object object = constructor.newInstance();

    ModelView modelView = new ModelView();
    modelView = (ModelView) method.invoke(object);
    return modelView;
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
