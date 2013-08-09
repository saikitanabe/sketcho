package net.sevenscales.sketchoconfluenceapp.server.utils;


public class StoreEntry {
  private byte[] image;
  private String diagramContent;
  private int version;
  
  public byte[] getImage() {
    return image;
  }
  public void setImage(byte[] image) {
    this.image = image;
  }
  
  public String getDiagramContent() {
    return diagramContent;
  }
  public void setDiagramContent(String diagramContent) {
    this.diagramContent = diagramContent;
  }
  
  public int getVersion() {
    return version;
  }
  public void setVersion(int version) {
    this.version = version;
  }
  
}
