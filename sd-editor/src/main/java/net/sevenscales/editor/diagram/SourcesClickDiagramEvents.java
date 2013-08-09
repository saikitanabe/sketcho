package net.sevenscales.editor.diagram;

public interface SourcesClickDiagramEvents {
	public void registerClickHandler(ClickDiagramHandler listener);
	public void unregisterClickHandler(ClickDiagramHandler listener);
}
