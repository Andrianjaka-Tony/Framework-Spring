package run;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import etu1765.framework.Mapping;
import use.Package;

public class Main {
  
  public static void main(String[] args) {
    try {
      String absolutePath = "/opt/tomcat/webapps/framework/WEB-INF/classes";
      HashMap<String, Mapping> map = Package.scanPackages(absolutePath);
      Set<String> set = map.keySet();
      for (String s : set) {
        System.out.println(s);
        System.out.println(map.get(s).getClassName());
        System.out.println(map.get(s).getMethod());
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }

}
