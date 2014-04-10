package net.sevenscales.editor.content.ui;

public interface DiagramSelectionHandler {
	public interface WhenScrolledHandler {
		void whenScrolled();
	}

	void hidePopup();
	void addScrollHandler(WhenScrolledHandler scrollHandler);
}
