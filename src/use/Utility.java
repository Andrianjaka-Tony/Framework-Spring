package use;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.text.Utilities;

import etu1765.framework.FileUpload;
import etu1765.framework.Mapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

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
    // Enumeration<String> parameters = request.getParameterNames();

    // while (parameters.hasMoreElements()) {
    // reponse.add(parameters.nextElement());
    // }
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

  public static int countRequestFields(HttpServletRequest request, String className) throws Exception {
    int reponse = 0;
    Vector<String> fields = Utility.fields(className);
    for (String field : fields) {
      if (request.getPart(field) != null) {
        reponse++;
      }
    }

    return reponse;
  }

  public static boolean isSave(HttpServletRequest request, HashMap<String, Mapping> mappingUrls) throws Exception {
    Set<String> keys = mappingUrls.keySet();
    for (String key : keys) {
      String className = mappingUrls.get(key).getClassName();
      if (Utility.countRequestFields(request, className) == Utility.fields(className).size()) {
        return true;
      }
    }
    return false;
  }

  public static String classToSave(HttpServletRequest request, HashMap<String, Mapping> mappingUrls) throws Exception {
    String reponse = "";
    Set<String> keys = mappingUrls.keySet();
    for (String key : keys) {
      String className = mappingUrls.get(key).getClassName();
      if (Utility.countRequestFields(request, className) == Utility.fields(className).size()) {
        return className;
      }
    }
    return reponse;
  }

  public static Object save(HttpServletRequest request, HashMap<String, Mapping> mappingUrls) throws Exception {
    String className = Utility.classToSave(request, mappingUrls);
    Vector<String> parameters = Utility.fields(className);
    Class<?> clazz = Class.forName(className);
    Constructor<?> constructor = clazz.getConstructor();

    Object reponse = constructor.newInstance();
    for (String parameter : parameters) {
      Field field = reponse.getClass().getDeclaredField(parameter);
      field.setAccessible(true);

      if (reponse.getClass().getDeclaredField(parameter).getType().getSimpleName()
          .compareToIgnoreCase("FileUpload") == 0) {
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
        String path = "D:/Apache/Tomcat/webapps/framework/Files/" + filename;
        filePart.write(path);
        FileUpload fileUpload = new FileUpload(filename, path, fileBytes);
        field.set(reponse, fileUpload);
      } else {
        String value = request.getParameter(parameter);
        if (field.getGenericType().getTypeName().compareTo("int") == 0) {
          field.set(reponse, Integer.valueOf(value));
        } else if (field.getGenericType().getTypeName().compareTo("double") == 0) {
          field.set(reponse, Double.valueOf(value));
        } else {
          field.set(reponse, value);
        }
      }
    }

    return reponse;
  }

}