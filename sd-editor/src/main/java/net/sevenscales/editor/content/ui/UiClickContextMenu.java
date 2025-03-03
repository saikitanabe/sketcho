package net.sevenscales.editor.content.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.api.event.BoardEmptyAreaClickEventHandler;
import net.sevenscales.editor.api.event.BoardEmptyAreaClickedEvent;
import net.sevenscales.editor.api.event.CreateElementEvent;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent;
import net.sevenscales.editor.api.event.SaveButtonClickedEvent;
import net.sevenscales.editor.api.event.SaveButtonClickedEventHandler;
import net.sevenscales.editor.api.event.StartSelectToolEvent;
import net.sevenscales.editor.api.event.SurfaceMouseUpNoHandlingYetEvent;
import net.sevenscales.editor.api.event.pointer.PointerDownEvent;
import net.sevenscales.editor.api.event.pointer.PointerDownHandler;
import net.sevenscales.editor.api.event.pointer.PointerEventsSupport;
import net.sevenscales.editor.api.event.pointer.PointerMoveEvent;
import net.sevenscales.editor.api.event.pointer.PointerMoveHandler;
import net.sevenscales.editor.api.impl.FastButton;
import net.sevenscales.editor.content.utils.EffectHelpers;


public class UiClickContextMenu extends Composite {
	private static SLogger logger = SLogger.createLogger(UiClickContextMenu.class);

	static {
		SLogger.addFilter(UiClickContextMenu.class);
	}

	private static UiClickContextMenuUiBinder uiBinder = GWT
			.create(UiClickContextMenuUiBinder.class);

	interface UiClickContextMenuUiBinder extends
			UiBinder<Widget, UiClickContextMenu> {
	}
	
	private PopupPanel popup;
	private ISurfaceHandler surface;

	@UiField FastButton commentMode;

	// @UiField LIElement freehandMore;
	// @UiField LIElement freehandSmooth;
	// @UiField LIElement freehandLess;
	
//	@UiField AnchorElement newNote;
//	private int cancelTimer;

	public UiClickContextMenu(ISurfaceHandler surface) {
		this.surface = surface;
		
		initWidget(uiBinder.createAndBindUi(this));

		if (!notConfluence()) {
			// hide comments on confluence
			commentMode.setVisible(false);
		}
		
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
        
    if (PointerEventsSupport.isSupported()) {
      supportPointerEvents();
    } else {
      supportMouseTouchEvents();
    }

		surface.getEditorContext().getEventBus().addHandler(BoardEmptyAreaClickedEvent.TYPE, new BoardEmptyAreaClickEventHandler() {
			@Override
			public void on(BoardEmptyAreaClickedEvent event) {
				logger.debug("BoardEmptyAreaClickedEvent...");
				showAddElementMenu(event.getX(), event.getY());
				// openClickMenu(event);
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

  private void supportPointerEvents() {
		surface.addDomHandler(new PointerDownHandler() {
			@Override
			public void onPointerDown(PointerDownEvent event) {
        touchStart();
			}
		}, PointerDownEvent.getType());
		
		surface.addDomHandler(new PointerMoveHandler() {
			@Override
			public void onPointerMove(PointerMoveEvent event) {
        mouseMove();
			}
		}, PointerMoveEvent.getType());
  }
  
  private void supportMouseTouchEvents() {
		surface.addDomHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
        touchStart();
			}
		}, TouchStartEvent.getType());
		
		surface.addDomHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
        mouseMove();
			}
		}, MouseMoveEvent.getType());
  }

  private void touchStart() {
    if (popup.isShowing()) {
      hide();
    }
  }

  private void mouseMove() {
    if (UiClickContextMenu.this.surface.getMouseDiagramManager().isMovingBackground() ||
        UiClickContextMenu.this.surface.getMouseDiagramManager().isLassoing()) {
      hide();
    }
  }

	private void openClickMenu(final BoardEmptyAreaClickedEvent event) {
		if (UiClickContextMenu.this.surface.getSelectionHandler().getSelectedItems().size() == 0 &&
			 !UiClickContextMenu.this.surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE)) {
			popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
				@Override
				public void setPosition(int offsetWidth, int offsetHeight) {
					int x = event.getX();
					int y = event.getY();
					popup.setPopupPosition(x + UiClickContextMenu.this.surface.getAbsoluteLeft() - offsetWidth / 2, 
									y + UiClickContextMenu.this.surface.getAbsoluteTop() - offsetHeight / 2 - 20);
					EffectHelpers.tooltipper();
				}
			});
		}
	}

	private boolean notConfluence() {
		return !surface.getEditorContext().isTrue(EditorProperty.CONFLUENCE_MODE);
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
		surface.getEditorContext().getEventBus().fireEvent(new CreateElementEvent(UMLDiagramType.NOTE.getElementType().getValue(), null, x, y));
		hide();
	}
	
	@UiHandler("addElement")
	public void onAddElement(ClickEvent event) {
		stopEvent(event);
		showAddElementMenu(event.getClientX(), event.getClientY());
		hide();
	}

	private void showAddElementMenu(int x, int y) {
		if (!surface.getEditorContext().isTrue(EditorProperty.FREEHAND_MODE)) {
			surface.getEditorContext().getEventBus().fireEvent(new SurfaceMouseUpNoHandlingYetEvent(x, y));
		}
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

	@UiHandler("select")
	public void onSelect(ClickEvent event) {
		stopEvent(event);
		surface.getEditorContext().getEventBus().fireEvent(new StartSelectToolEvent());
		hide();
	}
	
	private void hide() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      @Override
			public void execute() {
				popup.hide();
				// EffectHelpers.tooltipperHide();
			}
		});
	}
	
	private void stopEvent(ClickEvent event) {
		event.stopPropagation();
		event.preventDefault();
	}
}
