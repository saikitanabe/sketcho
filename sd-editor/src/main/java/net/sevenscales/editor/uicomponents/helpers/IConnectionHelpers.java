package net.sevenscales.editor.uicomponents.helpers;

import java.util.List;
import net.sevenscales.editor.diagram.Diagram;

public interface IConnectionHelpers extends IGlobalElement {
	public interface IExtraConnectionHandler {
		void showExtraConnectionHandles();
		boolean disableBottom();
	}
	
	boolean isShownFor(Diagram diagram);
	void show(Diagram parent);
	void toggle(Diagram parent);
	boolean isShown();
	void hide(Diagram diagram);
	void setVisibility(boolean visibility);
	void setShape(int left, int top, int width, int height, Integer rotateDegrees);
	void removeExtraConnectionHandles();
	List<ConnectionHandle> getExtraConnectionHandles();
	void addExtraConnectionHandle(Diagram parent, int x, int y, int radius);
	void hideForce();

}
