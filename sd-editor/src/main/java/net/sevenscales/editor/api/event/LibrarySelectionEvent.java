package net.sevenscales.editor.api.event;

import net.sevenscales.editor.api.LibrarySelections;
import net.sevenscales.editor.api.LibrarySelections.Library;

import com.google.gwt.event.shared.GwtEvent;

public class LibrarySelectionEvent extends GwtEvent<LibrarySelectionEventHandler> {
  public static Type<LibrarySelectionEventHandler> TYPE = new Type<LibrarySelectionEventHandler>();
	private Library library;

  public LibrarySelectionEvent(LibrarySelections.Library library) {
  	this.library = library;
	}

	@Override
  protected void dispatch(LibrarySelectionEventHandler handler) {
      handler.onSelection(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<LibrarySelectionEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public Library getLibrary() {
		return library;
	}
}
