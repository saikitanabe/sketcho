package net.sevenscales.editor.content.ui;

import java.util.Set;
import java.util.HashSet;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.content.ui.textsize.TextSizePopup;
import net.sevenscales.editor.content.ui.textsize.TextSizeHandler;
import net.sevenscales.editor.content.ui.layers.LayersPopup;
import net.sevenscales.editor.content.ui.link.EditLinkForm;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.ColorSelectedEvent;
import net.sevenscales.editor.api.event.ColorSelectedEvent.ColorTarget;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent;
import net.sevenscales.editor.api.event.FreehandModeChangedEventHandler;
import net.sevenscales.editor.api.event.RelationshipTypeSelectedEvent;
import net.sevenscales.editor.api.event.SaveButtonClickedEvent;
import net.sevenscales.editor.api.event.SaveButtonClickedEventHandler;
import net.sevenscales.editor.api.event.SelectionMouseUpEvent;
import net.sevenscales.editor.api.event.SelectionMouseUpEventHandler;
import net.sevenscales.editor.api.event.PotentialOnChangedEvent;
import net.sevenscales.editor.api.event.ChangeTextSizeEvent;
import net.sevenscales.editor.api.event.BoardRemoveDiagramsEventHandler;
import net.sevenscales.editor.api.event.BoardRemoveDiagramsEvent;
import net.sevenscales.editor.api.event.SwitchElementEvent;
import net.sevenscales.editor.api.impl.FastElementButton;
import net.sevenscales.editor.api.impl.TouchHelpers;
import net.sevenscales.editor.api.impl.EditorCommon;
import net.sevenscales.editor.api.auth.AuthHelpers;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.content.RelationShipType;
import net.sevenscales.editor.content.utils.DuplicateHelpers;
import net.sevenscales.editor.content.utils.EffectHelpers;
import net.sevenscales.editor.content.utils.ScaleHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.editor.uicomponents.uml.CommentElement;
import net.sevenscales.editor.uicomponents.uml.CommentThreadElement;
import net.sevenscales.editor.uicomponents.uml.ImageElement;
import net.sevenscales.editor.diagram.SelectionHandler;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.gfx.domain.ElementColor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SimplePanel;


public class UiContextMenu extends Composite implements net.sevenscales.editor.content.ui.ColorSelections.SelectionHandler, TextSizeHandler {
	private static final SLogger logger = SLogger.createLogger(UiContextMenu.class);

	static {
		SLogger.addFilter(UiContextMenu.class);
	}
	
	private static UiContextMenuUiBinder uiBinder = GWT
			.create(UiContextMenuUiBinder.class);

	interface UiContextMenuUiBinder extends UiBinder<Widget, UiContextMenu> {
	}
	
	private PopupPanel popup;
	private EditorContext editorContext;
	private SelectionHandler selectionHandler;
	private ISurfaceHandler surface;
	
	@UiField AnchorElement freehandOff;
	@UiField AnchorElement duplicate;
	@UiField SimplePanel changeConnection;
	@UiField AnchorElement curvedArrow;
	@UiField AnchorElement rectifiedArrow;
	@UiField AnchorElement reverseConnection;
	@UiField AnchorElement switchElement;
	@UiField AnchorElement colorize;
	@UiField AnchorElement delete;
	// @UiField AnchorElement annotate;
	// @UiField AnchorElement unannotate;
	@UiField AnchorElement addlink;
	@UiField AnchorElement openlink;
	@UiField AnchorElement textSize;
	@UiField AnchorElement layersMenuButton;
	@UiField AnchorElement fileLink;

	private PopupPanel editLinkPopup;
	private PopupPanel colorpopup;
	private LayersPopup layersPopup;
	private TextSizePopup fontSizePopup;
	private Point popupPosition;

	private ElementColor color = new ElementColor();
	private ColorSelections colorSelections;
	private EditLinkForm editLinkForm;

	private class MainContextMenuPosition implements PopupPanel.PositionCallback {
		int left;
		int top;
		@Override
		public void setPosition(int offsetWidth, int offsetHeight) {
			fixPosition(left, top, offsetWidth, offsetHeight);
			popup.setPopupPosition(popupPosition.x, popupPosition.y);
		}
	}

	private MainContextMenuPosition mainContextPosition = new MainContextMenuPosition();

	public UiContextMenu(ISurfaceHandler surface, EditorContext editorContext, SelectionHandler selectionHandler) {
		this.surface = surface;
		this.editorContext = editorContext;
		this.selectionHandler = selectionHandler;
		
		initWidget(uiBinder.createAndBindUi(this));

		popupPosition = new Point();
		
		popup = new PopupPanel();
		popup.setStyleName("UiContextMenu");
		popup.setAutoHideEnabled(true);
		popup.setWidget(this);
		popup.getElement().setId("tip-shape-added");
		
		colorpopup = new PopupPanel();
		colorpopup.setStyleName("ColorButtonBox");

		editLinkPopup = new PopupPanel();
		editLinkPopup.setStyleName("edit-link-popup");
		editLinkPopup.setAutoHideEnabled(true);
		editLinkPopup.addAutoHidePartner(addlink);
		editLinkPopup.addAutoHidePartner(openlink);
		this.editLinkForm = new EditLinkForm(applyLink);
		editLinkPopup.setWidget(editLinkForm);

		this.colorSelections = new ColorSelections(editorContext);
		colorSelections.setSelectionHandler(this);
		colorpopup.setWidget(colorSelections);
		colorpopup.setAutoHideEnabled(true);
		colorpopup.addAutoHidePartner(colorize);

		fontSizePopup = new TextSizePopup(this);
		layersPopup = new LayersPopup(surface);

		surface.addDomHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				if (popup.isShowing()) {
					hide();
				}
			}
		}, TouchStartEvent.getType());

		changeConnection.setWidget(new SelectButtonBox(editorContext, false));

		editorContext.getEventBus().addHandler(BoardRemoveDiagramsEvent.TYPE, new BoardRemoveDiagramsEventHandler() {
			public void on(BoardRemoveDiagramsEvent event) {
				hide();
			}
		});
		
		editorContext.getEventBus().addHandler(SelectionMouseUpEvent.TYPE, new SelectionMouseUpEventHandler() {

			/**
			* Return true if all menu items are hidden.
			*/
			private boolean setMenuItemVisibility(Diagram diagram, Diagram[] selected) {
				Display freehandMenu = Display.NONE;
				Display reverseMenu = Display.NONE;
				Display curvedArrowMenu = Display.NONE;
				Display rectifiedArrowMenu = Display.NONE;
				Display colorMenu = Display.NONE;
				boolean changeConnectionMenu = false;
				Display deleteMenuVisibility = Display.NONE;
				Display duplicateMenuVisibility = Display.NONE;
				Display annotateVisibility = Display.NONE;
				Display unannotateVisibility = Display.NONE;
				Display addLinkMenuVisibility = Display.NONE;
				Display openEditLinkMenuVisibility = Display.NONE;
				Display changeFontSizeVisibility = Display.NONE;
				Display layersMenuVisibility = Display.NONE;
				Display switchElementVisibility = Display.NONE;

				if (diagram.supportsMenu(ContextMenuItem.FREEHAND_MENU)) {
					freehandMenu = Display.INLINE_BLOCK;
					freehandOnOff(UiContextMenu.this.editorContext.isTrue(EditorProperty.FREEHAND_MODE));
				}

				if (selected.length == 1 && diagram.supportsMenu(ContextMenuItem.REVERSE_CONNECTION_MENU)) {
					// cannot show reverse menu if multiple items, at least for now
					reverseMenu = Display.INLINE_BLOCK;
				}

				if (allSupportsFontSizeChange(selected)) {
					changeFontSizeVisibility = Display.INLINE_BLOCK;
				}

				if (allSupportsLayers(selected)) {
					layersMenuVisibility = Display.INLINE_BLOCK;
				}

				if (anySupportsColorMenu(selected)) {
					colorMenu = Display.INLINE_BLOCK;
				}

				if (selected.length == 1 && !(selected[0] instanceof Relationship2)) {
					switchElementVisibility = Display.INLINE_BLOCK;
				}

				if (selected.length == 1 && selected[0].hasLink()) {
					openEditLinkMenuVisibility = Display.INLINE_BLOCK;
				} else if (selected.length == 1 && diagram.supportsMenu(ContextMenuItem.URL_LINK)) {
					addLinkMenuVisibility = Display.INLINE_BLOCK;
				}

				if (selected.length == 1 && selected[0] instanceof ImageElement) {
					ImageElement img = (ImageElement) selected[0];
					fileLink.setHref(img.getUrl());
					fileLink.getStyle().setDisplay(Display.INLINE_BLOCK);
				} else {
					fileLink.setHref("");
					fileLink.getStyle().setDisplay(Display.NONE);
				}

				boolean onlyComms = onlyComments(selected);
				if (notConfluence() && anyIsAnnotated(selected) && !onlyComms) {
					unannotateVisibility = Display.INLINE_BLOCK;
				} else if (notConfluence() && !onlyComms) {
					annotateVisibility = Display.INLINE_BLOCK;
				}

				boolean allowedToShowDeleteMenu = AuthHelpers.allowedToShowDelete(selected);
				if (allowedToShowDeleteMenu && diagram.supportsMenu(ContextMenuItem.DELETE)) {
					deleteMenuVisibility = Display.INLINE_BLOCK;
				}

				if (!ifEvenOneIsComment(selected) && diagram.supportsMenu(ContextMenuItem.DUPLICATE)) {
					duplicateMenuVisibility = Display.INLINE_BLOCK;
				}

				boolean justConnections = allConnections(selected);
				changeConnection.setVisible(justConnections);
				if (justConnections) {
					curvedArrowMenu = Display.INLINE_BLOCK;
					rectifiedArrowMenu = Display.INLINE_BLOCK;
				}

				freehandOff.getStyle().setDisplay(freehandMenu);
				reverseConnection.getStyle().setDisplay(reverseMenu);
				rectifiedArrow.getStyle().setDisplay(rectifiedArrowMenu);
				curvedArrow.getStyle().setDisplay(curvedArrowMenu);
				colorize.getStyle().setDisplay(colorMenu);
				delete.getStyle().setDisplay(deleteMenuVisibility);
				duplicate.getStyle().setDisplay(duplicateMenuVisibility);
				// annotate.getStyle().setDisplay(annotateVisibility);
				// unannotate.getStyle().setDisplay(unannotateVisibility);
				addlink.getStyle().setDisplay(addLinkMenuVisibility);
				openlink.getStyle().setDisplay(openEditLinkMenuVisibility);
				textSize.getStyle().setDisplay(changeFontSizeVisibility);
				layersMenuButton.getStyle().setDisplay(layersMenuVisibility);
				switchElement.getStyle().setDisplay(switchElementVisibility);

				if (freehandMenu == Display.NONE && 
						reverseMenu == Display.NONE &&
						colorMenu == Display.NONE &&
						changeConnectionMenu == false &&
						deleteMenuVisibility == Display.NONE &&
						duplicateMenuVisibility == Display.NONE) {
					return true;
				}
				return false;
			}

			private boolean anySupportsColorMenu(Diagram[] selected) {
				for (Diagram diagram : selected) {
					if (AuthHelpers.allowedToEdit(diagram) && 
						  (diagram.supportedMenuItems() & ContextMenuItem.COLOR_MENU.getValue()) == ContextMenuItem.COLOR_MENU.getValue() &&
						  AuthHelpers.allowColorChange(diagram)) {
						return true;
					}
				}
				return false;
			}

			private boolean anyIsAnnotated(Diagram[] selected) {
				for (Diagram diagram : selected) {
					if (diagram.isAnnotation() && 
						!(diagram instanceof CommentThreadElement || diagram instanceof CommentElement)) {
						return true;
					}
				}
				return false;
			}

			private boolean onlyComments(Diagram[] selected) {
				boolean result = true;
				for (Diagram diagram : selected) {
					if (!(diagram instanceof CommentThreadElement || diagram instanceof CommentElement)) {
						result = false;
					}
				}
				return result;
			}

			private boolean ifEvenOneIsComment(Diagram[] selected) {
				for (Diagram d : selected) {
					if (d instanceof CommentElement || d instanceof CommentThreadElement) {
						return true;
					}
				}
				return false;
			}

			private boolean allConnections(Diagram[] selected) {
				boolean result = false;
				for (Diagram d : selected) {
					result = true;
					if ( !(d instanceof Relationship2) ) {
						return false;
					}
				}
				return result;
			}

			private boolean allSupportsFontSizeChange(Diagram[] selected) {
				boolean result = false;
				for (Diagram d : selected) {
					result = true;
					if (!ContextMenuItem.supported(d.supportedMenuItems(), ContextMenuItem.FONT_SIZE)) {
						return false;
					}
				}
				return result;
			}

			private boolean allSupportsLayers(Diagram[] selected) {
				boolean result = false;
				for (Diagram d : selected) {
					result = true;
					if (!ContextMenuItem.supported(d.supportedMenuItems(), ContextMenuItem.LAYERS)) {
						return false;
					}
				}
				return result;
			}

			@Override
			public void onSelection(SelectionMouseUpEvent event) {
				Diagram[] selected = new Diagram[]{};
				selected = UiContextMenu.this.selectionHandler.getSelectedItems().toArray(selected);
				if (selected.length >= 1) {
					// do not show popup menu if even one of the items is comment
					// diagram position is scaled value, so need to translate to screen pixels...
					Diagram d = event.getLastSelected();
					// logger.debug("Last Selected type {} x({}) y({})", d.toString(), d.getLeft(), d.getTop());

					Point screenPosition = ScaleHelpers.diagramPositionToScreenPoint(d, UiContextMenu.this.surface);
					screenPosition.y += adjustByDiagramType(d);
										
					if (UiContextMenu.this.surface.getEditorContext().isTrue(EditorProperty.CONFLUENCE_MODE)) {
						screenPosition.y = UiContextMenu.this.surface.getAbsoluteTop() + ScaleHelpers.unscaleValue(d.getTop(), UiContextMenu.this.surface.getScaleFactor()) + adjustByDiagramType(d) + 
								UiContextMenu.this.surface.getRootLayer().getTransformY();
					}

					boolean allMenusHidden = setMenuItemVisibility(d, selected);
					
					if (!allMenusHidden) {
						mainContextPosition.left = screenPosition.x;
						mainContextPosition.top = screenPosition.y;
						popup.setPopupPositionAndShow(mainContextPosition);
						trigger("shape-context-menu-shown");
						EffectHelpers.tooltipper();
					}
				}
			}
		});
		
		editorContext.getEventBus().addHandler(FreehandModeChangedEvent.TYPE, new FreehandModeChangedEventHandler() {
			@Override
			public void on(FreehandModeChangedEvent event) {
				freehandOnOff(UiContextMenu.this.editorContext.isTrue(EditorProperty.FREEHAND_MODE));
			}
		});
		
		new FastElementButton(freehandOff).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				fireFreehandOnOff();
			}
		});
		
		new FastElementButton(duplicate).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				duplicate();
			}
		});

		duplicate.setTitle("Duplicate<br>Ctrl+C, Ctrl+V (copy-paste Win/Linux)<br>Cmd+C, Cmd+V (copy-paste Mac)");
		
		new FastElementButton(reverseConnection).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				reverse();
			}
		});

		new FastElementButton(switchElement).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				switchElement();
			}
		});
		
		new FastElementButton(colorize).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				color();
			}
		});
		
		new FastElementButton(delete).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				delete();
			}
		});

		// new FastElementButton(annotate).addClickHandler(new ClickHandler() {
		// 	@Override
		// 	public void onClick(ClickEvent event) {
		// 		stopEvent(event);
		// 		annotate();
		// 	}
		// });

		// new FastElementButton(unannotate).addClickHandler(new ClickHandler() {
		// 	@Override
		// 	public void onClick(ClickEvent event) {
		// 		stopEvent(event);
		// 		unannotate();
		// 	}
		// });

		new FastElementButton(addlink).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				showEditLink();
			}
		});

		new FastElementButton(openlink).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				showEditLink();
			}
		});

		new FastElementButton(textSize).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				showTextSizeEditor();
			}
		});

		new FastElementButton(layersMenuButton).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				showLayersMenu();
			}
		});

		// do not handle undo/redo if property editor is open
		Event.addNativePreviewHandler(new NativePreviewHandler() {
		  @Override
		  public void onPreviewNativeEvent(NativePreviewEvent event) {
		    if (event.getTypeInt() == Event.ONKEYDOWN && 
		    		!UiContextMenu.this.editorContext.isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN)) {
		      NativeEvent ne = event.getNativeEvent();
		      if (ne.getKeyCode() == KeyCodes.KEY_DELETE) {
						UiContextMenu.this.selectionHandler.removeSelected();
		      }
		    }
		  }
		});
		
		handleStreams(this);
		closeOnSave();
		tapCurvedArrow(curvedArrow, this);
		tapRectifiedArrow(rectifiedArrow, this);
	}

  private native void trigger(String event)/*-{
    $wnd.$($doc).trigger(event);
  }-*/;

	private native void handleStreams(UiContextMenu me)/*-{
		$wnd.cancelStream.onValue(function() {
			me.@net.sevenscales.editor.content.ui.UiContextMenu::cancel()();
		})
		$wnd.changeFreehandColorStream.onValue(function(e) {
			me.@net.sevenscales.editor.content.ui.UiContextMenu::colorMenu(Lcom/google/gwt/dom/client/Element;)(e);
		})
	}-*/;

	private native void tapCurvedArrow(Element e, UiContextMenu me)/*-{
		$wnd.Hammer(e, {preventDefault: true}).on('tap', function() {
			$wnd.$('.tooltip').hide()
			me.@net.sevenscales.editor.content.ui.UiContextMenu::curvedArrow()();
			// $wnd.$($doc).trigger('showFreehandColorMenu', e)
		})
	}-*/;

	private native void tapRectifiedArrow(Element e, UiContextMenu me)/*-{
		$wnd.Hammer(e, {preventDefault: true}).on('tap', function() {
			$wnd.$('.tooltip').hide()
			me.@net.sevenscales.editor.content.ui.UiContextMenu::rectifiedArrow()();
			// $wnd.$($doc).trigger('showFreehandColorMenu', e)
		})
	}-*/;

	private void curvedArrow() {
		logger.debug("curvedArrow...");
		Set<Diagram> selected = selectionHandler.getSelectedItems();
		for (Diagram d : selected) {
			if (d instanceof Relationship2) {
				((Relationship2) d).curve();
			}
		}
		Tools.enableCurvedArrow();
	}

	private void rectifiedArrow() {
		logger.debug("rectifiedArrow...");
		Set<Diagram> selected = selectionHandler.getSelectedItems();
		for (Diagram d : selected) {
			if (d instanceof Relationship2) {
				((Relationship2) d).straight();
			}
		}
		Tools.disableCurvedArrow();
	}

	private void cancel() {
		hide();
	}

	private Point fixPosition(int left, int top, int offsetWidth, int offsetHeight) {
		popupPosition.x = left;
		popupPosition.y = top;
		if (left <= Window.getScrollLeft()) {
			// use mouse left
			popupPosition.x = surface.getCurrentClientX();
			// use mouse top
			popupPosition.y = surface.getCurrentClientY() - offsetHeight;
		} else if (top <= Window.getScrollTop()) {
			// use mouse left
			popupPosition.x = surface.getCurrentClientX();
			// use mouse top
			popupPosition.y = surface.getCurrentClientY() - offsetHeight;
		}

		if ((popupPosition.x + offsetWidth) >= Window.getClientWidth()) {
			popupPosition.x = Window.getClientWidth() - offsetWidth;
		}
		return popupPosition;
	}

	private int adjustByDiagramType(Diagram diagram) {
		return (diagram instanceof Relationship2) ? -55 : -40;
	}

	private boolean notConfluence() {
		return !surface.getEditorContext().isTrue(EditorProperty.CONFLUENCE_MODE);
	}

	private EditLinkForm.ApplyCallback applyLink = new EditLinkForm.ApplyCallback() {
		public void applied(String url) {
			applyLink(url);
		}
	};
	
	private void closeOnSave() {
		editorContext.getEventBus().addHandler(SaveButtonClickedEvent.TYPE, new SaveButtonClickedEventHandler() {
			@Override
			public void onSelection(SaveButtonClickedEvent event) {
				hide();
			}
		});
	}

	private void duplicate() {
		DuplicateHelpers dup = new DuplicateHelpers(UiContextMenu.this.surface, UiContextMenu.this.selectionHandler);
		dup.duplicate(editorContext.getGraphicalDocumentCache());
		hide();
	}

	private void reverse() {
		UiContextMenu.this.editorContext.getEventBus().fireEvent(new RelationshipTypeSelectedEvent(RelationShipType.REVERSE));
		hide();
	}

	private void switchElement() {
		hide();
		Set<Diagram> selected = selectionHandler.getSelectedItems();
		if (selected.size() == 1) {
			Diagram d = selected.iterator().next();
			editorContext.getEventBus().fireEvent(new SwitchElementEvent(d));
		}
	}
	
	private void color() {
		if (TouchHelpers.isSupportsTouch()) {
			colorSelections.showHeader();
			colorpopup.center();
		} else {
			setColorPopupRelativePosition();
		}
		EffectHelpers.tooltipperHide();
	}

	private void colorMenu(final Element e) {
		colorpopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				int left = e.getAbsoluteLeft();
				int top = e.getAbsoluteTop() + e.getOffsetHeight();
				colorpopup.setPopupPosition(left, top);
				colorSelections.hideHeader();
			}
		});
	}

	private void applyLink(String url) {
		Diagram selected = selectionHandler.getOnlyOneSelected();
		if (selected != null) {
			if (!url.equals(selected.getLink())) {
				// url is set only if it differs
				selected.setLink(url);
				surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(selected));
			}
		}
		hide();
	}

	private void showEditLink() {
		Diagram selected = selectionHandler.getOnlyOneSelected();
		if (selected != null) {
			editLinkForm.setLink(selected.getLink());
			editLinkPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
				@Override
				public void setPosition(int offsetWidth, int offsetHeight) {
					positionEditLinkPopup(offsetWidth, offsetHeight);
				}
			});
			EditorCommon.fireEditorOpen(surface);
		}
	}

	private void positionEditLinkPopup(int offsetWidth, int offsetHeight) {
		int left = 0;
		int top = 0;
		Diagram selected = selectionHandler.getOnlyOneSelected();
		if (selected != null && !selected.hasLink()) {
			left = addlink.getAbsoluteLeft() + 0;
			top = addlink.getAbsoluteTop() + popup.getOffsetHeight();
		} else {
			left = openlink.getAbsoluteLeft() + 0;
			top = openlink.getAbsoluteTop() + popup.getOffsetHeight();
		}
		editLinkPopup.setPopupPosition(left, top);
	}

	private void showTextSizeEditor() {
		int fontSize = getFontSize();
		fontSizePopup.setCurrentSize(fontSize);
		fontSizePopup.show(textSize.getAbsoluteLeft(), textSize.getAbsoluteTop() + 30);
	}

	private void showLayersMenu() {
		layersPopup.show(layersMenuButton.getAbsoluteLeft(), layersMenuButton.getAbsoluteTop() + popup.getOffsetHeight());
	}

	/**
	* 0 means that there are multiple sizes.
	*/
	private int getFontSize() {
		Set<Diagram> selected = selectionHandler.getSelectedItems();
		Integer result = null;
		boolean first = true;
		for (Diagram d : selected) {
			Integer currentSize = d.getFontSize();
			if (first) {
				result = currentSize;
				first = false;
			} else {
				if (result == null && currentSize == null) {
						// same size
				} else if (result != null && result.equals(currentSize)) {
					// same size
				} else {
					// size differs
					return 0;
				}
				result = currentSize;
			}
		}
		return result != null ? result.intValue() : 0;
	}

	private void annotate() {
		Set<Diagram> annotated = new HashSet<Diagram>();
		for (Diagram d : selectionHandler.getSelectedItems()) {
			if (!d.isAnnotation()) {
				if (!(d instanceof CommentThreadElement &&  d instanceof CommentElement)) {
					d.annotate();
					annotated.add(d);
				}
			}
		}
		hide();
		surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(annotated));
	}

	private void unannotate() {
		Set<Diagram> annotated = new HashSet<Diagram>();
		for (Diagram d : selectionHandler.getSelectedItems()) {
			if (d.isAnnotation()) {
				if (!(d instanceof CommentThreadElement &&  d instanceof CommentElement)) {
					d.unannotate();
					annotated.add(d);
				}
			}
		}
		hide();
		surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(annotated));
	}
	
	private void setColorPopupRelativePosition() {
		colorpopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				// Diagram[] selected = new Diagram[]{};
				// selected = UiContextMenu.this.selectionHandler.getSelectedItems().toArray(selected);

				int left = colorize.getAbsoluteLeft() + 0;
				int top = colorize.getAbsoluteTop() + popup.getOffsetHeight();
				colorpopup.setPopupPosition(left, top);
				colorSelections.showHeader();
			}
		});
	}

	private void freehandOnOff(boolean on) {
		if (on) {
			freehandOff.addClassName("btn-success");
			freehandOff.removeClassName("btn-custom");
		} else {
			colorSelections.backgroundMode();
			freehandOff.removeClassName("btn-success");
			freehandOff.addClassName("btn-custom");
		}
	}
	
//	@UiHandler("freehandWrapper")
//	public void onFreehand(ClickEvent event) {
//		meditorContext.getEventBus().fireEvent
//			(new FreehandModeChangedEvent(!UiContextMenu.this.editorContext.isTrue(EditorProperty.FREEHAND_MODE)));
//	}
	
	private void fireFreehandOnOff() {
		editorContext.getEventBus().fireEvent
			(new FreehandModeChangedEvent(!UiContextMenu.this.editorContext.isTrue(EditorProperty.FREEHAND_MODE)));
	}
	
	@Override
	public void itemSelected(ElementColor color, ColorTarget colorTarget) {
		logger.debug2("itemSelected color {}, colorTarget {}...", color, colorTarget);
		colorpopup.hide();
		this.color = color;
//		setCurrentColors();
		popup.hide();
		editorContext.getEventBus().fireEvent(new ColorSelectedEvent(color, colorTarget));
	}

	@Override
	public void fontSizeChanged(int fontSize) {
		hide();
		editorContext.getEventBus().fireEvent(new ChangeTextSizeEvent(fontSize));
	}
	
	private void hide() {
		if (editLinkPopup.isShowing()) {
			EditorCommon.fireEditorClosed(surface);
		}
		editLinkPopup.hide();
		colorpopup.hide();
		popup.hide();
		EffectHelpers.tooltipperHide();
	}
	
	private void delete() {
		UiContextMenu.this.selectionHandler.removeSelected();
		hide();
	}
	
	private void stopEvent(ClickEvent event) {
		event.stopPropagation();
		event.preventDefault();
	}

}
