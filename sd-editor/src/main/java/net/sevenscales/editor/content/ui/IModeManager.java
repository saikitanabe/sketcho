package net.sevenscales.editor.content.ui;


public interface IModeManager {
  boolean isConnectMode();
  void setConnectionMode(boolean enable);
	void setForceConnectionPoint(int x, int y);
	int getConnectionPointX();
	int getConnectionPointY();
	void clearConnectionPoint();
	boolean isForceConnectionPoint();
}
