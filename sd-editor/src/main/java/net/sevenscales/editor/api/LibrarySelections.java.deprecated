package net.sevenscales.editor.api;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
// import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.dom.client.Style.Display;

import net.sevenscales.editor.api.impl.TouchHelpers;


public class LibrarySelections extends Composite {

	private static LibrarySelectionsUiBinder uiBinder = GWT
			.create(LibrarySelectionsUiBinder.class);

	interface LibrarySelectionsUiBinder extends
			UiBinder<Widget, LibrarySelections> {
	}

	public interface LibrarySelectedHandler {
		void onSelected(Library library);
	}
	
	private LibrarySelectedHandler librarySelectedHandler;

	@UiField Element settings;
	@UiField Element software;
	@UiField Element mindmap;
	@UiField Element roadmap;
	@UiField Element general;
	@UiField Element images;
	@UiField Element team;
	@UiField Element aws;
	@UiField Element electronic;
	@UiField Element extra;
	@UiField DivElement btnGroup;
//	@UiField AnchorElement roadmap;

	private EditorContext editorContext;
	private Element active;

	public LibrarySelections(LibrarySelectedHandler handler, EditorContext editorContext) {
		this.editorContext = editorContext;
		this.librarySelectedHandler = handler;
		initWidget(uiBinder.createAndBindUi(this));

		if (TouchHelpers.isSupportsTouch()) {
			btnGroup.removeClassName("library-selection-no-touch");
		}

		applyActive(software);
		
		DOM.sinkEvents((com.google.gwt.user.client.Element) settings.cast(),
				Event.ONMOUSEDOWN);
		DOM.setEventListener(
				(com.google.gwt.user.client.Element) settings.cast(),
				new EventListener() {
					@Override
					public void onBrowserEvent(Event event) {
						switch (DOM.eventGetType(event)) {
						case Event.ONMOUSEDOWN:
							LibrarySelections.this.librarySelectedHandler.onSelected(Library.LIBRARY_SETTINGS);
							applyActive(settings);
							break;
						}
					}
				});

		DOM.sinkEvents((com.google.gwt.user.client.Element) mindmap.cast(),
				Event.ONMOUSEDOWN);
		DOM.setEventListener(
				(com.google.gwt.user.client.Element) mindmap.cast(),
				new EventListener() {
					@Override
					public void onBrowserEvent(Event event) {
						switch (DOM.eventGetType(event)) {
						case Event.ONMOUSEDOWN:
							LibrarySelections.this.librarySelectedHandler.onSelected(Library.MINDMAP);
							applyActive(mindmap);
							break;
						}
					}
				});
		
		DOM.sinkEvents((com.google.gwt.user.client.Element) software.cast(),
				Event.ONMOUSEDOWN);
		DOM.setEventListener(
				(com.google.gwt.user.client.Element) software.cast(),
				new EventListener() {
					@Override
					public void onBrowserEvent(Event event) {
						switch (DOM.eventGetType(event)) {
						case Event.ONMOUSEDOWN:
							LibrarySelections.this.librarySelectedHandler.onSelected(Library.SOFTWARE);
							applyActive(software);
							break;
						}
					}
				});

		DOM.sinkEvents((com.google.gwt.user.client.Element) roadmap.cast(),
				Event.ONMOUSEDOWN);
		DOM.setEventListener(
				(com.google.gwt.user.client.Element) roadmap.cast(),
				new EventListener() {
					@Override
					public void onBrowserEvent(Event event) {
						switch (DOM.eventGetType(event)) {
						case Event.ONMOUSEDOWN:
							LibrarySelections.this.librarySelectedHandler.onSelected(Library.ROADMAP);
							applyActive(roadmap);
							break;
						}
					}
				});

		DOM.sinkEvents((com.google.gwt.user.client.Element) general.cast(),
				Event.ONMOUSEDOWN);
		DOM.setEventListener(
				(com.google.gwt.user.client.Element) general.cast(),
				new EventListener() {
					@Override
					public void onBrowserEvent(Event event) {
						switch (DOM.eventGetType(event)) {
						case Event.ONMOUSEDOWN:
							LibrarySelections.this.librarySelectedHandler.onSelected(Library.GENERAL);
							applyActive(general);
							break;
						}
					}
				});



		DOM.sinkEvents((com.google.gwt.user.client.Element) images.cast(),
				Event.ONMOUSEDOWN);
		DOM.setEventListener(
				(com.google.gwt.user.client.Element) images.cast(),
				new EventListener() {
					@Override
					public void onBrowserEvent(Event event) {
						switch (DOM.eventGetType(event)) {
						case Event.ONMOUSEDOWN:
							LibrarySelections.this.librarySelectedHandler.onSelected(Library.IMAGES);
							applyActive(images);
							break;
						}
					}
				});

		DOM.sinkEvents((com.google.gwt.user.client.Element) team.cast(),
				Event.ONMOUSEDOWN);
		DOM.setEventListener(
				(com.google.gwt.user.client.Element) team.cast(),
				new EventListener() {
					@Override
					public void onBrowserEvent(Event event) {
						switch (DOM.eventGetType(event)) {
						case Event.ONMOUSEDOWN:
							LibrarySelections.this.librarySelectedHandler.onSelected(Library.TEAM);
							applyActive(team);
							break;
						}
					}
				});

		DOM.sinkEvents((com.google.gwt.user.client.Element) extra.cast(),
				Event.ONMOUSEDOWN);
		DOM.setEventListener(
				(com.google.gwt.user.client.Element) extra.cast(),
				new EventListener() {
					@Override
					public void onBrowserEvent(Event event) {
						switch (DOM.eventGetType(event)) {
						case Event.ONMOUSEDOWN:
							LibrarySelections.this.librarySelectedHandler.onSelected(Library.EXTRA);
							applyActive(extra);
							break;
						}
					}
				});

		DOM.sinkEvents((com.google.gwt.user.client.Element) aws.cast(),
				Event.ONMOUSEDOWN);
		DOM.setEventListener(
				(com.google.gwt.user.client.Element) aws.cast(),
				new EventListener() {
					@Override
					public void onBrowserEvent(Event event) {
						switch (DOM.eventGetType(event)) {
						case Event.ONMOUSEDOWN:
							LibrarySelections.this.librarySelectedHandler.onSelected(Library.AWS);
							applyActive(aws);
							break;
						}
					}
				});

		DOM.sinkEvents((com.google.gwt.user.client.Element) electronic.cast(),
				Event.ONMOUSEDOWN);
		DOM.setEventListener(
				(com.google.gwt.user.client.Element) electronic.cast(),
				new EventListener() {
					@Override
					public void onBrowserEvent(Event event) {
						switch (DOM.eventGetType(event)) {
						case Event.ONMOUSEDOWN:
							LibrarySelections.this.librarySelectedHandler.onSelected(Library.ELECTRONIC);
							applyActive(electronic);
							break;
						}
					}
				});
		
		showOrHideImages();
//		DOM.sinkEvents((com.google.gwt.user.client.Element) roadmap.cast(),
//				Event.ONMOUSEDOWN);
//		DOM.setEventListener(
//				(com.google.gwt.user.client.Element) roadmap.cast(),
//				new EventListener() {
//					@Override
//					public void onBrowserEvent(Event event) {
//						switch (DOM.eventGetType(event)) {
//						case Event.ONMOUSEDOWN:
//							LibrarySelections.this.librarySelectedHandler.onSelected(Library.ROADMAP);
//							break;
//						}
//					}
//				});
	}

	private void applyActive(Element active) {
		if (this.active != null) {
			this.active.removeClassName("library-selection-active");
		}

		this.active = active;
		this.active.addClassName("library-selection-active");
	}

	private void showOrHideImages() {
		if (isConfluence()) {
			images.getStyle().setDisplay(Display.NONE);
		}
	}

	private boolean isConfluence() {
		return editorContext.isTrue(EditorProperty.CONFLUENCE_MODE);
	}	

}
