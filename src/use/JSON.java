package use;

import org.codehaus.jackson.map.ObjectMapper;

public class JSON {
  public static String stringify(Object object) {
    String response = "";
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      response = objectMapper.writeValueAsString(object);
    } catch (Exception e) {
    }
    return response;
  }
}
