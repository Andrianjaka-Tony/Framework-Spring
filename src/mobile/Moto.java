package mobile;

import annotation.Url;

public class Moto {
  
  @Url(value = "/moto/rouler")
  public void rouler() {}

  @Url(value = "/moto/plein")
  public void plein() {}

}
