package net.sevenscales.editor.content.ui;

import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.api.event.BoardEmptyAreaClickEventHandler;
import net.sevenscales.editor.api.event.BoardEmptyAreaClickedEvent;
import net.sevenscales.editor.api.event.CreateElementEvent;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent.FreehandModeType;
import net.sevenscales.editor.api.event.CommentModeEvent;
import net.sevenscales.editor.api.event.SaveButtonClickedEvent;
import net.sevenscales.editor.api.event.SaveButtonClickedEventHandler;
import net.sevenscales.editor.api.event.StartSelectToolEvent;
import net.sevenscales.editor.api.event.SurfaceMouseUpNoHandlingYetEvent;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.utils.EffectHelpers;
import net.sevenscales.editor.uicomponents.helpers.ElementHelpers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.DOM;


public class UiClickContextMenu extends Composite {

	private static UiClickContextMenuUiBinder uiBinder = GWT
			.create(UiClickContextMenuUiBinder.class);

	interface UiClickContextMenuUiBinder extends
			UiBinder<Widget, UiClickContextMenu> {
	}
	
	private PopupPanel popup;
	private ISurfaceHandler surface;

	// @UiField LIElement freehandMore;
	// @UiField LIElement freehandSmooth;
	// @UiField LIElement freehandLess;
	
//	@UiField AnchorElement newNote;
//	private int cancelTimer;

	public UiClickContextMenu(ISurfaceHandler surface) {
		this.surface = surface;
		
		initWidget(uiBinder.createAndBindUi(this));
		
//		new FastElementButton(newNote).addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				newNote(event.getClientX(), event.getClientY());
//			}
//		});

		popup = new PopupPanel();
		popup.setStyleName("UiContextMenu");
		popup.setAutoHideEnabled(true);
		popup.setWidget(this);
		
		RootPanel.get().addDomHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					hide();
				}
			}
		}, KeyDownEvent.getType());
		
		surface.addDomHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				if (popup.isShowing()) {
					hide();
				}
			}
		}, TouchStartEvent.getType());
		
		surface.addDomHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if (UiClickContextMenu.this.surface.getMouseDiagramManager().getBackgroundMoveHandler().backgroundMoveIsOn() ||
						UiClickContextMenu.this.surface.getMouseDiagramManager().getLassoSelectionHandler().isLassoing()) {
					hide();
				}
			}
		}, MouseMoveEvent.getType());

//		popup.addDomHandler(new MouseOverHandler() {
//			@Override
//			public void onMouseOver(MouseOverEvent event) {
//				clearTimeout(cancelTimer);
//			}
//		}, MouseOverEvent.getType());
		
//		popup.addDomHandler(new MouseOutHandler() {
//			@Override
//			public void onMouseOut(MouseOutEvent event) {
//				cancelTimer = setTimeout(5000);
//			}
//		}, MouseOutEvent.getType());

		surface.getEditorContext().getEventBus().addHandler(BoardEmptyAreaClickedEvent.TYPE, new BoardEmptyAreaClickEventHandler() {
			@Override
			public void on(final BoardEmptyAreaClickedEvent event) {
				if (UiClickContextMenu.this.surface.getSelectionHandler().getSelectedItems().size() == 0 &&
					 !UiClickContextMenu.this.surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE)) {
//					clearTimeout(cancelTimer);
					popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
						@Override
						public void setPosition(int offsetWidth, int offsetHeight) {
							int x = event.getX();
							int y = event.getY();
		//						MatrixPointJS point = MatrixPointJS.createScaledPoint(x, y, UiClickContextMenu.this.surface.getScaleFactor());
		//						setCurrentPosition(
		//								point.getX() - ScaleHelpers.scaleValue(surface.getRootLayer().getTransformX(), surface.getScaleFactor()), 
		//								point.getY() - ScaleHelpers.scaleValue(surface.getRootLayer().getTransformY(), surface.getScaleFactor()));
							popup.setPopupPosition(x + UiClickContextMenu.this.surface.getAbsoluteLeft() - offsetWidth / 2, 
											y + UiClickContextMenu.this.surface.getAbsoluteTop() - offsetHeight / 2 - 20);
							EffectHelpers.tooltipper();
						}
					});
//					cancelTimer = setTimeout(5000);
				}
			}
		});

		// ElementHelpers.addEventListener(freehandMore, new EventListener() {
		// 			@Override
		// 			public void onBrowserEvent(Event event) {
		// 				switch (DOM.eventGetType(event)) {
		// 				case Event.ONCLICK:
		// 					freehandMore();
		// 					break;
		// 				}
		// 			}
		// 		});
		// ElementHelpers.addEventListener(freehandSmooth, new EventListener() {
		// 			@Override
		// 			public void onBrowserEvent(Event event) {
		// 				switch (DOM.eventGetType(event)) {
		// 				case Event.ONCLICK:
		// 					freehandSmooth();
		// 					break;
		// 				}
		// 			}
		// 		});
		// ElementHelpers.addEventListener(freehandLess, new EventListener() {
		// 			@Override
		// 			public void onBrowserEvent(Event event) {
		// 				switch (DOM.eventGetType(event)) {
		// 				case Event.ONCLICK:
		// 					freehandLess();
		// 					break;
		// 				}
		// 			}
		// 		});

		closeOnSave();
	}
	
	private void closeOnSave() {
		surface.getEditorContext().getEventBus().addHandler(SaveButtonClickedEvent.TYPE, new SaveButtonClickedEventHandler() {
			@Override
			public void onSelection(SaveButtonClickedEvent event) {
				hide();
			}
		});
	}
	
//	private native void clearTimeout(int cancelTimer)/*-{
//		clearTimeout(cancelTimer);				
//	}-*/;
//	private native int setTimeout(int timeout)/*-{
//		var self = this;
//		function hide() {
//			self.@net.sevenscales.editor.content.ui.UiClickContextMenu::hide()();
//		}
//		return setTimeout(hide, timeout);
//	}-*/;

	@UiHandler("newNote")
	public void onNewNote(ClickEvent event) {
		stopEvent(event);
		newNote(event.getClientX(), event.getClientY());
	}
	
	private void newNote(final int x, final int y) {
		surface.getEditorContext().getEventBus().fireEvent(new CreateElementEvent(UMLDiagramType.NOTE, x, y));
		hide();
	}
	
	@UiHandler("addElement")
	public void onAddElement(ClickEvent event) {
		stopEvent(event);
		surface.getEditorContext().getEventBus().fireEvent(new SurfaceMouseUpNoHandlingYetEvent(event.getClientX(), event.getClientY()));
		hide();
	}

	@UiHandler("freehand")
	public void onFreeHand(ClickEvent event) {
		stopEvent(event);
		surface.getEditorContext().getEventBus().fireEvent(new FreehandModeChangedEvent(!surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE)));
		hide();
	}

	@UiHandler("commentMode")
	public void onCommentMode(ClickEvent event) {
		stopEvent(event);
		Tools.toggleCommentMode();
		hide();
	}

	public void freehandMore() {
		// stopEvent(event);
		surface.getEditorContext().getEventBus().fireEvent(new FreehandModeChangedEvent(true, FreehandModeType.FREEHAND_MORE));
		hide();
	}

	public void freehandSmooth() {
		// stopEvent(event);
		surface.getEditorContext().getEventBus().fireEvent(new FreehandModeChangedEvent(true, FreehandModeType.FREEHAND_SMOOTH));
		hide();
	}

	public void freehandLess() {
		// stopEvent(event);
		surface.getEditorContext().getEventBus().fireEvent(new FreehandModeChangedEvent(true, FreehandModeType.FREEHAND_LESS));
		hide();
	}

	@UiHandler("select")
	public void onSelect(ClickEvent event) {
		stopEvent(event);
		surface.getEditorContext().getEventBus().fireEvent(new StartSelectToolEvent());
		hide();
	}
	
	private void hide() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				popup.hide();
				EffectHelpers.tooltipperHide();
			}
		});
	}
	
	private void stopEvent(ClickEvent event) {
		event.stopPropagation();
		event.preventDefault();
	}
}
