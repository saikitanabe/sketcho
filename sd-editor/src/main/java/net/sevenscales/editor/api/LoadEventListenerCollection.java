package net.sevenscales.editor.api;

import java.util.ArrayList;

public class LoadEventListenerCollection extends ArrayList {
	public void fireLoadedEvent() {
		for (int i = 0; i < size(); ++i) {
			SurfaceLoadedEventListener l = (SurfaceLoadedEventListener) get(i);
			l.onLoaded();
		}
	}
}
