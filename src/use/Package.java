package use;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Vector;

import annotation.Scope;
import annotation.Url;
import etu1765.framework.Mapping;

public class Package {

  public static Vector<String> getFiles(String folder) {
    File file = new File(folder);
    Vector<String> reponse = new Vector<String>();
    File[] files = file.listFiles();
    if (files == null) {
      return reponse;
    }
    for (int i = 0; i < files.length; i++) {
      if (files[i].isDirectory()) {
        reponse.addAll(
            getFiles(files[i].getPath().replace('\\', '/')));
      } else {
        if (files[i].getName().endsWith(".class")) {
          reponse.add(file.getPath().replace('\\', '/') + "." + files[i].getName().replace(".class", ""));
        }
      }
    }
    for (int i = 0; i < reponse.size(); i++) {
      if (false == reponse.get(i).startsWith("./")) {
        reponse.set(i, reponse.get(i).replace('/', '.'));
      } else {
        reponse.set(i, reponse.get(i).replace('/', '.').substring(2));
      }
    }
    return reponse;
  }

  public static Vector<String> getClasses(String absolutePath) {
    Vector<String> files = getFiles(absolutePath);
    Vector<String> reponse = new Vector<String>();
    String remove = absolutePath.replace('/', '.') + ".";
    for (String string : files) {
      reponse.add(
          string.replaceAll(remove, ""));
    }
    return reponse;
  }

  public static HashMap<String, Mapping> scanPackages(String absolutePath) throws Exception {
    Vector<String> getClasses = Package.getClasses(absolutePath);
    HashMap<String, Mapping> reponse = new HashMap<String, Mapping>();
    for (String className : getClasses) {
      Class<?> classe = Class.forName(className);
      Method[] methods = classe.getDeclaredMethods();
      for (Method method : methods) {
        if (method.isAnnotationPresent(Url.class)) {
          Annotation annotation = method.getDeclaredAnnotation(Url.class);
          String value = (String) annotation.getClass().getDeclaredMethod("value").invoke(annotation);
          reponse.put(value, new Mapping(className, method.getName()));
        }
      }
    }
    return reponse;
  }

  public static HashMap<String, Object> singletons(String absolutePath) throws Exception {
    Vector<String> getClasses = Package.getClasses(absolutePath);
    HashMap<String, Object> reponse = new HashMap<String, Object>();
    for (String className : getClasses) {
      Class<?> classe = Class.forName(className);
      if (classe.isAnnotationPresent(Scope.class)) {
        Annotation annotation = classe.getDeclaredAnnotation(Scope.class);
        String value = (String) annotation.getClass().getDeclaredMethod("value").invoke(annotation);
        if (value.compareToIgnoreCase("singleton") == 0) {
          reponse.put(className, classe.getDeclaredConstructor().newInstance());
        }
      }
    }
    return reponse;
  }

}