package net.sevenscales.editor.content;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends ClientBundle {
  Resources INSTANCE = GWT.create(Resources.class);
  
  @Source("down.png")
  ImageResource down();
  
  @Source("up.png")
  ImageResource up();
}
