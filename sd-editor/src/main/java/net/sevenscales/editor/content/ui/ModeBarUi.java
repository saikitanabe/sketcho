package net.sevenscales.editor.content.ui;

public class ModeBarUi implements IModeManager {

	private int x;
	private int y;
	private boolean forceConnectionPoint;
  private boolean connectionMode;

  public ModeBarUi() {
  }
  
  @Override
  public boolean isConnectMode() {
    return connectionMode;
  }
  
  @Override
  public void setConnectionMode(boolean enable) {
    connectionMode = enable;
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
