package net.sevenscales.editor.content.ui;

import java.util.Set;
import java.util.HashSet;

import net.sevenscales.domain.utils.SLogger;
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
import net.sevenscales.editor.api.impl.FastElementButton;
import net.sevenscales.editor.api.impl.TouchHelpers;
import net.sevenscales.editor.api.auth.AuthHelpers;
import net.sevenscales.editor.content.RelationShipType;
import net.sevenscales.editor.content.utils.DuplicateHelpers;
import net.sevenscales.editor.content.utils.EffectHelpers;
import net.sevenscales.editor.content.utils.ScaleHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.editor.uicomponents.uml.CommentElement;
import net.sevenscales.editor.uicomponents.uml.CommentThreadElement;
import net.sevenscales.editor.diagram.SelectionHandler;
import net.sevenscales.editor.diagram.utils.Color;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
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

public class UiContextMenu extends Composite implements net.sevenscales.editor.content.ui.ColorSelections.SelectionHandler {
	private static final SLogger logger = SLogger.createLogger(UiContextMenu.class);
	
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
	@UiField AnchorElement reverseConnection;
	@UiField AnchorElement colorize;
	@UiField AnchorElement delete;
	@UiField AnchorElement annotate;
	@UiField AnchorElement unannotate;
	@UiField AnchorElement editlink;

	private PopupPanel editLinkPopup;
	private PopupPanel colorpopup;
	private Color color = new Color("444444", 0x44, 0x44, 0x44, "6699ff", 0x66, 0x99, 0xff, "FFFFFF", 255, 255, 255, AbstractDiagramItem.DEFAULT_FILL_OPACITY);
	private ColorSelections colorSelections;

	public UiContextMenu(ISurfaceHandler surface, EditorContext editorContext, SelectionHandler selectionHandler) {
		this.surface = surface;
		this.editorContext = editorContext;
		this.selectionHandler = selectionHandler;
		
		initWidget(uiBinder.createAndBindUi(this));
		
		popup = new PopupPanel();
		popup.setStyleName("UiContextMenu");
		popup.setAutoHideEnabled(true);
		popup.setWidget(this);
		
		colorpopup = new PopupPanel();
		colorpopup.setStyleName("ColorButtonBox");

		editLinkPopup = new PopupPanel();
		editLinkPopup.setStyleName("edit-link-popup");
		editLinkPopup.setAutoHideEnabled(true);
		editLinkPopup.addAutoHidePartner(editlink);
		editLinkPopup.setWidget(new EditLinkForm(applyLink));

		this.colorSelections = new ColorSelections(editorContext);
		colorSelections.setSelectionHandler(this);
		colorpopup.setWidget(colorSelections);
		colorpopup.setAutoHideEnabled(true);
		colorpopup.addAutoHidePartner(colorize);
		
		surface.addDomHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				if (popup.isShowing()) {
					hide();
				}
			}
		}, TouchStartEvent.getType());

		changeConnection.setWidget(new SelectButtonBox(editorContext, false));
		
		editorContext.getEventBus().addHandler(SelectionMouseUpEvent.TYPE, new SelectionMouseUpEventHandler() {

			/**
			* Return true if all menu items are hidden.
			*/
			private boolean setMenuItemVisibility(Diagram diagram, Diagram[] selected) {
				Display freehandMenu = Display.NONE;
				Display reverseMenu = Display.NONE;
				Display colorMenu = Display.NONE;
				boolean changeConnectionMenu = false;
				Display deleteMenuVisibility = Display.NONE;
				Display duplicateMenuVisibility = Display.NONE;
				Display annotateVisibility = Display.NONE;
				Display unannotateVisibility = Display.NONE;

				if ((diagram.supportedMenuItems() & ContextMenuItem.FREEHAND_MENU.getValue()) == ContextMenuItem.FREEHAND_MENU.getValue()) {
					freehandMenu = Display.INLINE;
					freehandOnOff(UiContextMenu.this.editorContext.isTrue(EditorProperty.FREEHAND_MODE));
				}
				if (selected.length == 1 && (diagram.supportedMenuItems() & ContextMenuItem.REVERSE_CONNECTION_MENU.getValue()) == ContextMenuItem.REVERSE_CONNECTION_MENU.getValue()) {
					// cannot show reverse menu if multiple items, at least for now
					reverseMenu = Display.INLINE;
				}
				if (anySupportsColorMenu(selected)) {
					colorMenu = Display.INLINE_BLOCK;
				}

				boolean onlyComms = onlyComments(selected);
				if (anyIsAnnotated(selected) && !onlyComms) {
					unannotateVisibility = Display.INLINE_BLOCK;
				} else if (!onlyComms) {
					annotateVisibility = Display.INLINE_BLOCK;
				}

				boolean allowedToShowDeleteMenu = AuthHelpers.allowedToShowDelete(selected);
				if (allowedToShowDeleteMenu) {
					deleteMenuVisibility = Display.INLINE_BLOCK;
				}

				if (!ifEvenOneIsComment(selected)) {
					duplicateMenuVisibility = Display.INLINE_BLOCK;
				}

				changeConnection.setVisible(allConnections(selected));
				freehandOff.getStyle().setDisplay(freehandMenu);
				reverseConnection.getStyle().setDisplay(reverseMenu);
				colorize.getStyle().setDisplay(colorMenu);
				delete.getStyle().setDisplay(deleteMenuVisibility);
				duplicate.getStyle().setDisplay(duplicateMenuVisibility);
				annotate.getStyle().setDisplay(annotateVisibility);
				unannotate.getStyle().setDisplay(unannotateVisibility);

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

			@Override
			public void onSelection(SelectionMouseUpEvent event) {
				Diagram[] selected = new Diagram[]{};
				selected = UiContextMenu.this.selectionHandler.getSelectedItems().toArray(selected);
				if (selected.length >= 1) {
					// do not show popup menu if even one of the items is comment
					// diagram position is scaled value, so need to translate to screen pixels...
					Diagram d = event.getLastSelected();
					// logger.debug("Last Selected type {} x({}) y({})", d.toString(), d.getLeft(), d.getTop());
					
					int left = ScaleHelpers.unscaleValue(UiContextMenu.this.surface.getAbsoluteLeft() + d.getLeft(), UiContextMenu.this.surface.getScaleFactor()) + 
							UiContextMenu.this.surface.getRootLayer().getTransformX(); 
					int top = ScaleHelpers.unscaleValue(UiContextMenu.this.surface.getAbsoluteTop() + d.getTop(), UiContextMenu.this.surface.getScaleFactor()) - 40 + 
							UiContextMenu.this.surface.getRootLayer().getTransformY();
					
					if (UiContextMenu.this.surface.getEditorContext().isTrue(EditorProperty.CONFLUENCE_MODE)) {
						top = UiContextMenu.this.surface.getAbsoluteTop() + ScaleHelpers.unscaleValue(d.getTop(), UiContextMenu.this.surface.getScaleFactor()) - 40 + 
								UiContextMenu.this.surface.getRootLayer().getTransformY();
					}

					boolean allMenusHidden = setMenuItemVisibility(d, selected);
					
					if (!allMenusHidden) {
						popup.setPopupPosition(left, top);
						popup.show();
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
		
		new FastElementButton(reverseConnection).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				reverse();
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

		new FastElementButton(annotate).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				annotate();
			}
		});

		new FastElementButton(unannotate).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				unannotate();
			}
		});

		new FastElementButton(editlink).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				editLink();
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
		
		closeOnSave();
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
	
	private void color() {
		if (TouchHelpers.isSupportsTouch()) {
			colorpopup.center();
		} else {
			setColorPopupRelativePosition();
		}
		EffectHelpers.tooltipperHide();
	}

	private void applyLink(String url) {
		Diagram[] selected = new Diagram[]{};
		selected = selectionHandler.getSelectedItems().toArray(selected);
		if (selected.length == 1) {
			Diagram diagram = selected[0];
			if (!url.equals(diagram.getLink())) {
				// url is set only if it differs
				diagram.setLink(url);
				surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(diagram));
			}
		}
		hide();
	}

	private void editLink() {
		editLinkPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				positionEditLinkPopup(offsetWidth, offsetHeight);
			}
		});
	}

	private void positionEditLinkPopup(int offsetWidth, int offsetHeight) {
		int left = editlink.getAbsoluteLeft() + 0;
		int top = editlink.getAbsoluteTop() + 30;
		Diagram[] selected = new Diagram[]{};
		selected = selectionHandler.getSelectedItems().toArray(selected);
		// int selectedTop = ScaleHelpers.scaleAndTranslateY(selected[0].getTop(), surface);
		// int selectedLeft = ScaleHelpers.scaleAndTranslateX(selected[0].getLeft() + selected[0].getWidth(), surface);
		// if (selected.length == 1 && selectedTop - offsetHeight - 30 >= 0) {
		// 	top = UiContextMenu.this.getAbsoluteTop() - offsetHeight;
		// }
		// if (selected.length == 1 && selectedLeft + offsetWidth >= Window.getClientWidth()) {
		// 	left = Window.getClientWidth() - offsetWidth; 
		// }
		// colorSelections.setCurrentDiagramColor(selected[0].getTextColor(), selected[0].getBackgroundColor());
		editLinkPopup.setPopupPosition(left, top);
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
				int left = colorize.getAbsoluteLeft() + 0;
				int top = colorize.getAbsoluteTop() + 30;
				Diagram[] selected = new Diagram[]{};
				selected = UiContextMenu.this.selectionHandler.getSelectedItems().toArray(selected);
				int selectedTop = ScaleHelpers.scaleAndTranslateY(selected[0].getTop(), UiContextMenu.this.surface);
				int selectedLeft = ScaleHelpers.scaleAndTranslateX(selected[0].getLeft() + selected[0].getWidth(), UiContextMenu.this.surface);
				if (selected.length == 1 && selectedTop - offsetHeight - 30 >= 0) {
					top = UiContextMenu.this.getAbsoluteTop() - offsetHeight;
				}
				if (selected.length == 1 && selectedLeft + offsetWidth >= Window.getClientWidth()) {
					left = Window.getClientWidth() - offsetWidth; 
				}
				colorSelections.setCurrentDiagramColor(selected[0].getTextColor(), selected[0].getBackgroundColor());
				colorpopup.setPopupPosition(left, top);
			}
		});
	}

	private void freehandOnOff(boolean on) {
		if (on) {
			freehandOff.addClassName("btn-success");
			freehandOff.removeClassName("btn-custom");
		} else {
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
	public void itemSelected(Color color, ColorTarget colorTarget) {
		logger.debug2("itemSelected color {}, colorTarget {}...", color, colorTarget);
		colorpopup.hide();
		this.color = color;
//		setCurrentColors();
		popup.hide();
		editorContext.getEventBus().fireEvent(new ColorSelectedEvent(color, colorTarget));
	}
	
	private void hide() {
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
