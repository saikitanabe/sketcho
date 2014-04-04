package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

import net.sevenscales.domain.js.ImageInfo;


public class ImageAddedEvent extends GwtEvent<ImageAddedEventHandler> {
  public static Type<ImageAddedEventHandler> TYPE = new Type<ImageAddedEventHandler>();

  private ImageInfo imageInfo;

  public ImageAddedEvent(ImageInfo imageInfo) {
  	this.imageInfo = imageInfo;
  }

  public ImageInfo getImageInfo() {
  	return imageInfo;
  }

  @Override
  protected void dispatch(ImageAddedEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ImageAddedEventHandler> getAssociatedType() {
		return TYPE;
	}
}
