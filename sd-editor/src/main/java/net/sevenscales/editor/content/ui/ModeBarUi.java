package net.sevenscales.editor.content.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class ModeBarUi extends Composite implements IModeManager {

  private static ModeBarUiUiBinder uiBinder = GWT.create(ModeBarUiUiBinder.class);

  interface ModeBarUiUiBinder extends UiBinder<Widget, ModeBarUi> {
  }
  
  @UiField ToggleButton relationshipMode;
  @UiField ImageResource relationship;
//  @UiField VerticalPanel mainpanel;
	private int x;
	private int y;
	private boolean forceConnectionPoint;

  public ModeBarUi() {
    initWidget(uiBinder.createAndBindUi(this));
    // for some reason IE doesn't accept background-color in ui.xml
//    mainpanel.getElement().getStyle().setBackgroundColor("#d0e4f6");  
    relationshipMode.getUpFace().setHTML("<img src='"+relationship.getURL()+"'></img>");
  }
  
  @Override
  public boolean isConnectMode() {
    return relationshipMode.isDown();
  }
  
  @Override
  public void setConnectionMode(boolean enable) {
    relationshipMode.setDown(enable);
  }
  
  @Override
  public void setForceConnectionPoint(int x, int y) {
  	forceConnectionPoint = true;
  	this.x = x;
  	this.y = y;
  }
  @Override
  public void clearConnectionPoint() {
  	forceConnectionPoint = false;
  	this.x = -1;
  	this.y = -1;
  }
  
  @Override
  public int getConnectionPointX() {
  	return x;
  }
  @Override
  public int getConnectionPointY() {
  	return y;
  }
  
  public boolean isForceConnectionPoint() {
		return forceConnectionPoint;
	}

}
