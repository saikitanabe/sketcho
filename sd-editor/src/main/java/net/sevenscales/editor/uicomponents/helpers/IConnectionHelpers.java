package net.sevenscales.editor.uicomponents.helpers;

import java.util.List;

import net.sevenscales.editor.uicomponents.AbstractDiagramItem;

public interface IConnectionHelpers extends IGlobalElement {
	public interface IExtraConnectionHandler {
		void showExtraConnectionHandles();
		boolean disableBottom();
	}
	
	boolean isShownFor(AbstractDiagramItem diagram);
	void show(AbstractDiagramItem parent);
	void toggle(AbstractDiagramItem parent);
	boolean isShown();
	void hide(AbstractDiagramItem diagram);
	void setVisibility(boolean visibility);
	void setShape(int left, int top, int width, int height);
	void removeExtraConnectionHandles();
	List<ConnectionHandle> getExtraConnectionHandles();
	void addExtraConnectionHandle(AbstractDiagramItem parent, int x, int y, int radius);
	void hideForce();

}
