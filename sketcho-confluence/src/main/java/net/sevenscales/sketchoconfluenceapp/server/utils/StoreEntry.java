package net.sevenscales.sketchoconfluenceapp.server.utils;


public class StoreEntry {
  private byte[] image;
  private String diagramContent;
  private String svg;
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

  public void setSvg(String svg) {
    this.svg = svg;
  }
  public String getSvg() {
    return svg;
  }
  
  public int getVersion() {
    return version;
  }
  public void setVersion(int version) {
    this.version = version;
  }
  
}
