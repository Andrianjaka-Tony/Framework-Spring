package use;


public class Utility {
  
  public static String getUrl(String url) {
    String[] decompose = url.split("/");
    String reponse = "";
    int itterator = 0;
    for(int i = decompose.length - 1; i > 3; i --) {
      if(itterator != 0) {
        reponse = "/" + reponse;
      }
      decompose[i] = decompose[i].replace('?', '=');
      String[] tableau = decompose[i].split("=");
      reponse = tableau[0] + reponse;
      itterator += 1;
    }
    
    return "/" + reponse;
  }

}