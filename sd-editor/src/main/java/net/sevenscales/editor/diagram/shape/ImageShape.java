package net.sevenscales.editor.diagram.shape;

import net.sevenscales.domain.ElementType;


public class ImageShape extends HasRectShape {
  private String url;

  public ImageShape(int left, int top, int width, int height, String url) {
    super(left, top, width, height);
    this.url = url;
  }

  public String getElementType() {
    return ElementType.IMAGE.getValue();
  }

  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
}
