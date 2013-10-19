package net.sevenscales.editor.api;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class LibrarySelections extends Composite {

	private static LibrarySelectionsUiBinder uiBinder = GWT
			.create(LibrarySelectionsUiBinder.class);

	interface LibrarySelectionsUiBinder extends
			UiBinder<Widget, LibrarySelections> {
	}

	public enum Library {
		SOFTWARE, MINDMAP, ROADMAP
	}

	public interface LibrarySelectedHandler {
		void onSelected(Library library);
	}
	
	private LibrarySelectedHandler librarySelectedHandler;

	@UiField AnchorElement software;
	@UiField AnchorElement mindmap;
	@UiField AnchorElement roadmap;
	@UiField DivElement btnGroup;
//	@UiField AnchorElement roadmap;

	public LibrarySelections(LibrarySelectedHandler handler, EditorContext editorContext) {
		this.librarySelectedHandler = handler;
		initWidget(uiBinder.createAndBindUi(this));
		
		DOM.sinkEvents((com.google.gwt.user.client.Element) mindmap.cast(),
				Event.ONCLICK);
		DOM.setEventListener(
				(com.google.gwt.user.client.Element) mindmap.cast(),
				new EventListener() {
					@Override
					public void onBrowserEvent(Event event) {
						switch (DOM.eventGetType(event)) {
						case Event.ONCLICK:
							LibrarySelections.this.librarySelectedHandler.onSelected(Library.MINDMAP);
							break;
						}
					}
				});
		
		DOM.sinkEvents((com.google.gwt.user.client.Element) software.cast(),
				Event.ONCLICK);
		DOM.setEventListener(
				(com.google.gwt.user.client.Element) software.cast(),
				new EventListener() {
					@Override
					public void onBrowserEvent(Event event) {
						switch (DOM.eventGetType(event)) {
						case Event.ONCLICK:
							LibrarySelections.this.librarySelectedHandler.onSelected(Library.SOFTWARE);
							break;
						}
					}
				});

		DOM.sinkEvents((com.google.gwt.user.client.Element) roadmap.cast(),
				Event.ONCLICK);
		DOM.setEventListener(
				(com.google.gwt.user.client.Element) roadmap.cast(),
				new EventListener() {
					@Override
					public void onBrowserEvent(Event event) {
						switch (DOM.eventGetType(event)) {
						case Event.ONCLICK:
							LibrarySelections.this.librarySelectedHandler.onSelected(Library.ROADMAP);
							break;
						}
					}
				});
		

//		DOM.sinkEvents((com.google.gwt.user.client.Element) roadmap.cast(),
//				Event.ONCLICK);
//		DOM.setEventListener(
//				(com.google.gwt.user.client.Element) roadmap.cast(),
//				new EventListener() {
//					@Override
//					public void onBrowserEvent(Event event) {
//						switch (DOM.eventGetType(event)) {
//						case Event.ONCLICK:
//							LibrarySelections.this.librarySelectedHandler.onSelected(Library.ROADMAP);
//							break;
//						}
//					}
//				});
	}

}
