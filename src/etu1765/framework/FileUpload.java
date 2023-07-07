package etu1765.framework;

public class FileUpload {
  private String nom;
  private String path;
  private byte[] bytes;

  public FileUpload(String nom, String path, byte[] bytes) {
    this.nom = nom;
    this.path = path;
    this.bytes = bytes;
  }

  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public byte[] getBytes() {
    return bytes;
  }

  public void setBytes(byte[] bytes) {
    this.bytes = bytes;
  }
}
