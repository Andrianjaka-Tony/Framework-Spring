package use;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import etu1765.framework.FileUpload;
import etu1765.framework.Mapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

public class Utility {

  public static String getUrl(String url) {
    String[] decompose = url.split("/");
    String reponse = "";
    int itterator = 0;
    for (int i = decompose.length - 1; i > 3; i--) {
      if (itterator != 0) {
        reponse = "/" + reponse;
      }
      decompose[i] = decompose[i].replace('?', '=');
      String[] tableau = decompose[i].split("=");
      reponse = tableau[0] + reponse;
      itterator += 1;
    }

    return "/" + reponse;
  }

  public static Vector<String> parameters(HttpServletRequest request) {
    Vector<String> reponse = new Vector<String>();
    Map<String, String[]> parameterMap = request.getParameterMap();
    for (String paramName : parameterMap.keySet()) {
      reponse.add(paramName);
    }
    return reponse;
  }

  public static int countParameters(HttpServletRequest request) {
    int count = 0;
    Enumeration<String> parameters = request.getParameterNames();
    while (parameters.hasMoreElements()) {
      count++;
      parameters.nextElement();
    }
    return count;
  }

  public static Vector<String> fields(String className) throws Exception {
    Vector<String> reponse = new Vector<String>();
    Field[] fields = Class.forName(className).getDeclaredFields();
    for (Field field : fields) {
      reponse.add(field.getName());
    }

    return reponse;
  }

  public static String classToSave(HttpServletRequest request, HashMap<String, Mapping> mappingUrls, String url)
      throws Exception {
    String reponse = "";
    reponse = mappingUrls.get(url).getClassName();
    return reponse;
  }

  public static String currentPath() {
    String reponse = new File("").getAbsolutePath();
    return reponse;
  }

  public static void uploadFile(Object reponse, HttpServletRequest request, String parameter, Field field,
      String destination)
      throws Exception {
    Part filePart;
    try {
      filePart = request.getPart(parameter);
    } catch (Exception e) {
      throw e;
    }
    InputStream inputStream = filePart.getInputStream();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[4096];
    int bytesRead;
    while ((bytesRead = inputStream.read(buffer)) != -1) {
      byteArrayOutputStream.write(buffer, 0, bytesRead);
    }

    byte[] fileBytes = byteArrayOutputStream.toByteArray();
    byteArrayOutputStream.close();
    inputStream.close();
    String filename = filePart.getSubmittedFileName();
    String path = destination + "Files\\" + filename;
    System.out.println(path);
    filePart.write(path);
    FileUpload fileUpload = new FileUpload(filename, path, fileBytes);
    field.set(reponse, fileUpload);
  }

  public static void setValue(Object object, HttpServletRequest request, String parameter, Field field)
      throws Exception {
    String value = request.getParameter(parameter);
    if (value == null) {
      return;
    }
    if (field.getGenericType().getTypeName().compareTo("java.lang.Integer") == 0) {
      field.set(object, Integer.valueOf(value));
    } else if (field.getGenericType().getTypeName().compareTo("java.lang.Double") == 0) {
      field.set(object, Double.valueOf(value));
    } else {
      field.set(object, value);
    }
  }

  public static void runObject(Object object, HttpServletRequest request, Vector<String> parameters, String destination)
      throws Exception {
    for (String parameter : parameters) {
      Field field = object.getClass().getDeclaredField(parameter);
      field.setAccessible(true);

      if (object.getClass().getDeclaredField(parameter).getType().getSimpleName()
          .compareToIgnoreCase("FileUpload") == 0) {
        System.out.println("Upload");
        Utility.uploadFile(object, request, parameter, field, destination);
      } else {
        System.out.println("Non upload");
        Utility.setValue(object, request, parameter, field);
      }
    }
  }

  public static Object save(HttpServletRequest request, HashMap<String, Mapping> mappingUrls, String url,
      String destination)
      throws Exception {
    String className = Utility.classToSave(request, mappingUrls, url);
    Vector<String> parameters = Utility.fields(className);
    Class<?> clazz = Class.forName(className);
    Constructor<?> constructor = clazz.getConstructor();

    Object reponse = constructor.newInstance();
    Utility.runObject(reponse, request, parameters, destination);

    return reponse;
  }

  public static void save(HttpServletRequest request, HashMap<String, Mapping> mappingUrls, Object object,
      String destination)
      throws Exception {
    Vector<String> parameters = Utility.fields(object.getClass().getName());
    Utility.runObject(object, request, parameters, destination);
  }

  public static void resetObject(Object object) throws Exception {
    Field[] fields = object.getClass().getDeclaredFields();
    for (Field field : fields) {
      field.setAccessible(true);
      field.set(object, null);
    }
  }

}