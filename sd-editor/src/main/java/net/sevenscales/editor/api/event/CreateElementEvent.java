package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

import net.sevenscales.domain.js.ImageInfo;
import net.sevenscales.domain.js.JsShapeConfig;

public class CreateElementEvent extends GwtEvent<CreateElementEventHandler> {
  public static Type<CreateElementEventHandler> TYPE = new Type<CreateElementEventHandler>();
	private String elementType;
	// in case elementType is image, image info is needed
	private JsShapeConfig shapeConfig;
	private ImageInfo imageInfo;
	private int x;
	private int y;

  public CreateElementEvent(String elementType, ImageInfo imageInfo, int x, int y) {
  	this.elementType = elementType;
  	this.imageInfo = imageInfo;
  	this.x = x;
  	this.y = y;
	}

	@Override
  protected void dispatch(CreateElementEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<CreateElementEventHandler> getAssociatedType() {
		return TYPE;
	}

	public void setShapeConfig(JsShapeConfig shapeConfig) {
		this.shapeConfig = shapeConfig;
	}
	public JsShapeConfig getShapeConfig() {
		return shapeConfig;
	}

	public String getElementType() {
		return elementType;
	}

	public ImageInfo getImageInfo() {
		return imageInfo;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
