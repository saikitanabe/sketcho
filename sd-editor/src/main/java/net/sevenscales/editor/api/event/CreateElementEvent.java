package net.sevenscales.editor.api.event;

import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.domain.js.ImageInfo;

import com.google.gwt.event.shared.GwtEvent;

public class CreateElementEvent extends GwtEvent<CreateElementEventHandler> {
  public static Type<CreateElementEventHandler> TYPE = new Type<CreateElementEventHandler>();
	private UMLDiagramType elementType;
	// in case elementType is image, image info is needed
	private ImageInfo imageInfo;
	private int x;
	private int y;

  public CreateElementEvent(UMLDiagramType elementType, ImageInfo imageInfo, int x, int y) {
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
	
	public UMLDiagramType getElementType() {
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
