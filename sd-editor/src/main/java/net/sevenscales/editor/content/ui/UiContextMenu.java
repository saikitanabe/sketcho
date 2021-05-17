package net.sevenscales.editor.content.ui;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.api.auth.AuthHelpers;
import net.sevenscales.editor.api.event.BoardRemoveDiagramsEvent;
import net.sevenscales.editor.api.event.BoardRemoveDiagramsEventHandler;
import net.sevenscales.editor.api.event.ChangeTextSizeEvent;
import net.sevenscales.editor.api.event.RotateEvent;
import net.sevenscales.editor.api.event.ColorSelectedEvent;
import net.sevenscales.editor.api.event.ColorSelectedEvent.ColorTarget;
import net.sevenscales.editor.api.event.pointer.PointerDownEvent;
import net.sevenscales.editor.api.event.pointer.PointerDownHandler;
import net.sevenscales.editor.api.event.pointer.PointerEventsSupport;
import net.sevenscales.editor.api.event.ColorSelectedEvent.ColorSetType;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent;
import net.sevenscales.editor.api.event.FreehandModeChangedEventHandler;
import net.sevenscales.editor.api.event.PotentialOnChangedEvent;
import net.sevenscales.editor.api.event.RelationshipTypeSelectedEvent;
import net.sevenscales.editor.api.event.SaveButtonClickedEvent;
import net.sevenscales.editor.api.event.SaveButtonClickedEventHandler;
import net.sevenscales.editor.api.event.SelectionMouseUpEvent;
import net.sevenscales.editor.api.event.SelectionMouseUpEventHandler;
import net.sevenscales.editor.api.event.SwitchElementEvent;
import net.sevenscales.editor.api.event.UnselectAllEvent;
import net.sevenscales.editor.api.event.UnselecteAllEventHandler;
import net.sevenscales.editor.api.impl.EditorCommon;
import net.sevenscales.editor.api.impl.FastElementButton;
import net.sevenscales.editor.api.impl.TouchHelpers;
import net.sevenscales.editor.content.RelationShipType;
import net.sevenscales.editor.content.ui.layers.LayersPopup;
import net.sevenscales.editor.content.ui.lineweight.LineWeightPopup;
import net.sevenscales.editor.content.ui.link.EditLinkForm;
import net.sevenscales.editor.content.ui.menu.TextAlignPopup;
import net.sevenscales.editor.content.ui.textsize.TextSizeHandler;
import net.sevenscales.editor.content.ui.textsize.TextSizePopup;
import net.sevenscales.editor.content.utils.DuplicateHelpers;
import net.sevenscales.editor.content.utils.EffectHelpers;
import net.sevenscales.editor.content.utils.ScaleHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.SelectionHandler;
import net.sevenscales.editor.gfx.domain.ElementColor;
import net.sevenscales.editor.gfx.domain.IChildElement;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.uicomponents.uml.ChildTextElement;
import net.sevenscales.editor.uicomponents.uml.CommentElement;
import net.sevenscales.editor.uicomponents.uml.CommentThreadElement;
import net.sevenscales.editor.uicomponents.uml.ImageElement;
import net.sevenscales.editor.uicomponents.uml.Relationship2;


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
	@UiField AnchorElement comment;
	// @UiField AnchorElement rotate;
	// @UiField AnchorElement annotate;
	// @UiField AnchorElement unannotate;
	@UiField AnchorElement addlink;
	@UiField AnchorElement openlink;
	@UiField AnchorElement textSize;
	@UiField AnchorElement layersMenuButton;
	@UiField AnchorElement lineWeight;
	@UiField AnchorElement textAlign;
	@UiField AnchorElement fileLink;
	@UiField AnchorElement group;

	private PopupPanel editLinkPopup;
	private PopupPanel colorpopup;
	private LayersPopup layersPopup;
	private LineWeightPopup lineWeightPopup;
	private TextSizePopup fontSizePopup;
	private TextAlignPopup textAlignPopup;
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
		layersPopup = new LayersPopup(surface, layersMenuButton);
		lineWeightPopup = new LineWeightPopup(surface, lineWeight);
		textAlignPopup = new TextAlignPopup(surface, textAlign);

    if (PointerEventsSupport.isSupported()) {
      supportPointerEvents();
    } else {
      supportMouseTouchEvents();
    }

		changeConnection.setWidget(new SelectButtonBox(new SelectButtonBox.IParent() {
			public void show() {
				showContextMenu();
			}
		}, editorContext, selectionHandler, false));

		editorContext.getEventBus().addHandler(BoardRemoveDiagramsEvent.TYPE, new BoardRemoveDiagramsEventHandler() {
			public void on(BoardRemoveDiagramsEvent event) {
				hide();
			}
		});

		editorContext.getEventBus().addHandler(UnselectAllEvent.TYPE, new UnselecteAllEventHandler() {
			public void onUnselectAll(UnselectAllEvent event) {
				hide();
			}
		});
		
		editorContext.getEventBus().addHandler(SelectionMouseUpEvent.TYPE, new SelectionMouseUpEventHandler() {
			@Override
			public void onSelection(SelectionMouseUpEvent event) {
				Diagram[] selected = new Diagram[]{};
				selected = UiContextMenu.this.selectionHandler.getSelectedItems().toArray(selected);
				if (selected.length >= 1) {
					// do not show popup menu if even one of the items is comment
					// diagram position is scaled value, so need to translate to screen pixels...
					Diagram d = event.getLastSelected();
					// logger.debug("Last Selected type {} x({}) y({})", d.toString(), d.getLeft(), d.getTop());

					Point screenPosition = ScaleHelpers.diagramPositionToScreenPoint(d, UiContextMenu.this.surface, false);
					screenPosition.y += adjustByDiagramType(d);
										
					if (UiContextMenu.this.surface.getEditorContext().isTrue(EditorProperty.CONFLUENCE_MODE)) {
						screenPosition.y = UiContextMenu.this.surface.getAbsoluteTop() + ScaleHelpers.unscaleValue(d.getTop(), UiContextMenu.this.surface.getScaleFactor()) + adjustByDiagramType(d) + 
								UiContextMenu.this.surface.getRootLayer().getTransformY();
					}

					boolean allMenusHidden = setMenuItemVisibility(d, selected);
					
					if (!allMenusHidden) {
						mainContextPosition.left = screenPosition.x;
						mainContextPosition.top = screenPosition.y;
						showContextMenu();
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

		handleBirdsEyeOnOff(this);
		
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

		new FastElementButton(comment).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				comment();
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

		new FastElementButton(lineWeight).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				showLineWeightMenu(lineWeight, false, true);
			}
		});

		new FastElementButton(textAlign).addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopEvent(event);
				showTextAlign(textAlign);
			}
		});

		// new FastElementButton(rotate).addClickHandler(new ClickHandler() {
		// 	@Override
		// 	public void onClick(ClickEvent event) {
		// 		stopEvent(event);
		// 		rotate();
		// 	}
		// });

		// do not handle undo/redo if property editor is open
		Event.addNativePreviewHandler(new NativePreviewHandler() {
		  @Override
		  public void onPreviewNativeEvent(NativePreviewEvent event) {
		    if (event.getTypeInt() == Event.ONKEYDOWN && 
		    		!UiContextMenu.this.editorContext.isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN)) {
		      NativeEvent ne = event.getNativeEvent();
		      if (ne.getKeyCode() == KeyCodes.KEY_DELETE && UIKeyHelpers.allMenusAreClosed()) {
						UiContextMenu.this.selectionHandler.removeSelected();
		      }
		    }
		  }
		});
		
		handleStreams(this);
		closeOnSave();
		tapCurvedArrow(curvedArrow, this);
		tapRectifiedArrow(rectifiedArrow, this);
		tapGroup(group, this);
  }

  private void supportPointerEvents() {
		surface.addDomHandler(new PointerDownHandler() {
			@Override
			public void onPointerDown(PointerDownEvent event) {
        touchStart();
			}
		}, PointerDownEvent.getType());
  }
  
  private void supportMouseTouchEvents() {
		surface.addDomHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
        touchStart();
			}
		}, TouchStartEvent.getType());
  }

  private void touchStart() {
    if (popup.isShowing()) {
      hide();
    }
  }

	private void showContextMenu() {
		popup.setPopupPositionAndShow(mainContextPosition);
	}

	private native void handleBirdsEyeOnOff(UiContextMenu me)/*-{
		$wnd.globalStreams.mapViewStateStream.onValue(function(value) {
			me.@net.sevenscales.editor.content.ui.UiContextMenu::birdsEyeViewOn(Z)(value);
		})
	}-*/;

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
		$wnd.globalStreams.showFreehandLineWeightStream.onValue(function(elem) {
			me.@net.sevenscales.editor.content.ui.UiContextMenu::showLineWeightMenu(Lcom/google/gwt/dom/client/Element;ZZ)(elem, true, false);
		})

		$wnd.globalStreams.spaceKeyStream.onValue(function(value) {
			if (!$wnd.globalState.contextMenuOpen && !$wnd.isEditorOpen()) {
				// do not allow to show switch if editor is open
				me.@net.sevenscales.editor.content.ui.UiContextMenu::switchElement()();
			}
    })
    
    $wnd.globalStreams.contextMenuStream.filter(function(e) {
			return e && e.type === 'property-editor-pos'
		}).onValue(function(v) {
			me.@net.sevenscales.editor.content.ui.UiContextMenu::onShowPropertyEditor(II)(v.x, v.y)
		})

		$wnd.globalStreams.contextMenuStream.filter(function(v) {
	    return v && v.type==='rotate-start'
	  }).onValue(function(v) {
			me.@net.sevenscales.editor.content.ui.UiContextMenu::cancel()();
		})

  }-*/;
  
  private void onShowPropertyEditor(final int x, final int y) {
    popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
      @Override
      public void setPosition(int offsetWidth, int offsetHeight) {
        int top = y - (offsetHeight + 7);
        popup.setPopupPosition(x, top);
      }
    });

  }

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

	private native void tapGroup(Element e, UiContextMenu me)/*-{
		$wnd.Hammer(e, {preventDefault: true}).on('tap', function() {
			$wnd.$('.tooltip').hide()
			me.@net.sevenscales.editor.content.ui.UiContextMenu::groupOrUngroupShapes()();
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
		setMenuItemVisibility();
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
		setMenuItemVisibility();
		Tools.disableCurvedArrow();
	}

	private void groupOrUngroupShapes() {
		Set<Diagram> selected = selectionHandler.getSelectedItems();

		if (selected.size() > 1) {
			if (isEvenOnePartOfGroup(selected)) {
				ungroupSelected(selected);
			} else {
				groupSelected(selected);
			}

			surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(selected));
		}
	}

	private boolean isEvenOnePartOfGroup(Set<Diagram> diagrams) {
		for (Diagram d : diagrams) {
			if (d.getDiagramItem().isGroup()) {
				return true;
			}
		}
		return false;
	}

	private void groupSelected(Set<Diagram> selected) {
		// ClientIdHelpers.generateClientId(1, graphicalDocumentCache, surface.getEditorContext());
		String groupId = null;
		Diagram sample = null;
		for (Diagram d : selected) {
			if (groupId == null) {
				groupId = d.getDiagramItem().getClientId();
				sample = d;
			}

			if (!(d instanceof CommentThreadElement) && !(d instanceof IChildElement)) {
				// comments and child text cannot be part of groups
				d.setGroupId(groupId);
			}
		}
		if (sample != null) {
			selectionHandler.select(sample);
		}
	}

	private void ungroupSelected(Set<Diagram> selected) {
		for (Diagram s : selected) {
			String group = s.getDiagramItem().getGroup();
			if (group != null) {
				for (Diagram d : surface.getDiagrams()) {
					if (group.equals(d.getDiagramItem().getGroup())) {
						d.getDiagramItem().setGroup(null);
						d.unselect();
						selected.add(d);
					}
				}
			}
		}

		selectionHandler.unselectAll();
		hide();
	}

	private void cancel() {
		hide();
	}

	private Point fixPosition(int left, int top, int offsetWidth, int offsetHeight) {
		popupPosition.x = left;
    popupPosition.y = top;
    
    // ST 26.11.2019: Prevent to show where pointer double clicked
    // and removes accidental clicks to context menu, which
    // could be delete.
    int marginTop = 20;

		if (left <= Window.getScrollLeft()) {
			// use mouse left
			popupPosition.x = surface.getCurrentClientX();
			// use mouse top
			popupPosition.y = surface.getCurrentClientY() - (offsetHeight + marginTop);
		} else if (top <= Window.getScrollTop()) {
			// use mouse left
			popupPosition.x = surface.getCurrentClientX();
			// use mouse top
			popupPosition.y = surface.getCurrentClientY() - (offsetHeight + marginTop);
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
			if (!(d instanceof Relationship2)) {
				// wrong switch for relationship
				editorContext.getEventBus().fireEvent(new SwitchElementEvent(d));
			}
		}
	}
	
	private void color() {
		if (colorpopup.isShowing()) {
			colorpopup.hide();
		} else {
      // ST 28.2.2019 no longer touch device have a different
      // position for showing color selections.
      // Color buttons are now bigger to be use on iPad
			// if (TouchHelpers.isSupportsTouch()) {

			// 	Diagram selected = selectionHandler.getOnlyOneSelected();
			// 	if (selected != null) {
			// 		colorSelections.setCurrentDiagramColor(
			// 			selected.getTextColor(),
			// 			selected.getBackgroundColorAsColor(),
			// 			selected.getBorderColor()
			// 		);
			// 	}

			// 	colorSelections.showHeader();
			// 	colorpopup.center();
			// } else {
				setColorPopupRelativePosition();
			// }
		}
		EffectHelpers.tooltipperHide();
	}

	private void colorMenu(final Element e) {
		// remove before adding, so array doesn't grow too much
		colorpopup.removeAutoHidePartner(e);
		colorpopup.addAutoHidePartner(e);

		if (colorpopup.isShowing()) {
			colorpopup.hide();
		} else {
			colorSelections.hideHeader();
			colorpopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
				@Override
				public void setPosition(int offsetWidth, int offsetHeight) {
					int left = e.getAbsoluteLeft() + e.getOffsetWidth() / 2 - offsetWidth / 2;
					int top = e.getAbsoluteTop() - offsetHeight;
					colorpopup.setPopupPosition(left, top);
				}
			});
		}
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
		if (selected != null && !editLinkPopup.isShowing()) {
			editLinkForm.setLink(selected.getLink());
			editLinkPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
				@Override
				public void setPosition(int offsetWidth, int offsetHeight) {
					positionEditLinkPopup(offsetWidth, offsetHeight);
				}
			});
			EditorCommon.fireEditorOpen(surface);
		} else {
			editLinkPopup.hide();
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
		if (layersPopup.isShowing()) {
			layersPopup.hide();
		} else {
			layersPopup.show(layersMenuButton.getAbsoluteLeft(), layersMenuButton.getAbsoluteTop() + popup.getOffsetHeight());
		}
	}
	private void showLineWeightMenu(Element element, boolean reduceHeight, boolean black) {
		if (lineWeightPopup.isShowing()) {
			lineWeightPopup.hide();
		} else {
			lineWeightPopup.show(element, popup.getOffsetHeight(), reduceHeight, black);
		}
	}

	private void showTextAlign(Element element) {
		if (textAlignPopup.isShowing()) {
			textAlignPopup.hide();
		} else {
			textAlignPopup.show(element.getAbsoluteLeft(), element.getAbsoluteTop() + popup.getOffsetHeight());
		}
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

				Diagram selected = selectionHandler.getOnlyOneSelected();
				if (selected != null) {
					colorSelections.setCurrentDiagramColor(selected.getTextColor(), selected.getBackgroundColorAsColor(), selected.getBorderColor());
				} else {
					// TODO clear API
				}
			}
		});
	}

	private void freehandOnOff(boolean on) {
		if (on) {
			freehandOff.addClassName("btn-success");
			freehandOff.removeClassName("btn-custom");
			surface.getElement().addClassName("freehand-on");
		} else {
			colorSelections.backgroundMode();
			freehandOff.removeClassName("btn-success");
			freehandOff.addClassName("btn-custom");
			surface.getElement().removeClassName("freehand-on");
		}
	}

	private void birdsEyeViewOn(boolean on) {
		if (on) {
			surface.getElement().addClassName("birds-eye-on");
		} else {
			surface.getElement().removeClassName("birds-eye-on");
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
	public void itemSelected(ElementColor color, ColorTarget colorTarget, ColorSetType colorSetType) {
		logger.debug2("itemSelected color {}, colorTarget {}...", color, colorTarget);
		this.color = color;
		if (editorContext.isFreehandMode()) {
			// >>>>>> usability 6.12.2015 color menu is kept open as long as clicked outside context menu
			colorpopup.hide();
			popup.hide();
			// <<<<<< usability ends
		}
		editorContext.getEventBus().fireEvent(new ColorSelectedEvent(color, colorTarget, colorSetType));
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

	private void comment() {
		_comment(comment);
		// hide();
	}
	private native void _comment(Element comment)/*-{
		$wnd.globalStreams.contextMenuStream.push({
			type:'comment.create',
			element: comment
		})
	}-*/;

  // private void rotate() {
		// hide();
		// editorContext.getEventBus().fireEvent(new RotateEvent(45));
  // }
	
	private void stopEvent(ClickEvent event) {
		event.stopPropagation();
		event.preventDefault();
	}

	private void setMenuItemVisibility() {
		Diagram[] selected = new Diagram[]{};
		selected = UiContextMenu.this.selectionHandler.getSelectedItems().toArray(selected);
		if (selected.length == 1) {
			setMenuItemVisibility(selected[0], selected);
		}
	}

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
		// Display rotateVisibility = Display.NONE;
		Display layersMenuVisibility = Display.NONE;
		Display switchElementVisibility = Display.NONE;
		Display lineWeightVisibility = Display.NONE;
		Display textAlignVisibility = Display.NONE;
		Display groupVisibility = Display.NONE;
		Display commentVisibility = Display.NONE;

		if (isCommentsAvailable()) {
			commentVisibility = Display.INLINE_BLOCK;
		}

		if (diagram.supportsMenu(ContextMenuItem.FREEHAND_MENU)) {
			freehandMenu = Display.INLINE_BLOCK;
			freehandOnOff(UiContextMenu.this.editorContext.isTrue(EditorProperty.FREEHAND_MODE));
		}

		if (allSupports(selected, ContextMenuItem.LINE_WEIGHT)) {
			lineWeightVisibility = Display.INLINE_BLOCK;
		}

		if (selected.length == 1 && diagram.supportsMenu(ContextMenuItem.REVERSE_CONNECTION_MENU)) {
			// cannot show reverse menu if multiple items, at least for now
			reverseMenu = Display.INLINE_BLOCK;
		}

		if (allSupports(selected, ContextMenuItem.FONT_SIZE)) {
			changeFontSizeVisibility = Display.INLINE_BLOCK;
		}

		// if (allSupports(selected, ContextMenuItem.ROTATE)) {
		// 	rotateVisibility = Display.INLINE_BLOCK;
		// }

		if (allSupports(selected, ContextMenuItem.LAYERS)) {
			layersMenuVisibility = Display.INLINE_BLOCK;
		}

		if (anySupports(selected, ContextMenuItem.COLOR_MENU)) {
			colorMenu = Display.INLINE_BLOCK;
		}

		if (anySupports(selected, ContextMenuItem.TEXT_ALIGN)) {
			textAlignVisibility = Display.INLINE_BLOCK;
		}

		if (notConfluence() && selected.length == 1 && !(selected[0] instanceof Relationship2) && !(selected[0] instanceof CircleElement) && !(selected[0] instanceof ChildTextElement)) {
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

		if (selected.length > 1 && !ifEvenOneIsComment(selected)) {
			groupVisibility = Display.INLINE_BLOCK;
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
		if (justConnections && selected.length > 1) {
			// multiple connections
			curvedArrowMenu = Display.INLINE_BLOCK;
			rectifiedArrowMenu = Display.INLINE_BLOCK;
		} else if (justConnections && selected.length == 1) {
			// just one
			if (selected[0] instanceof Relationship2) {
				Relationship2 r = (Relationship2) selected[0];
				if (r.isCurved()) {
					rectifiedArrowMenu = Display.INLINE_BLOCK;
				} else {
					curvedArrowMenu = Display.INLINE_BLOCK;
				}
			}
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
		// rotate.getStyle().setDisplay(rotateVisibility);
		layersMenuButton.getStyle().setDisplay(layersMenuVisibility);
		switchElement.getStyle().setDisplay(switchElementVisibility);
		lineWeight.getStyle().setDisplay(lineWeightVisibility);
		textAlign.getStyle().setDisplay(textAlignVisibility);
		group.getStyle().setDisplay(groupVisibility);
		comment.getStyle().setDisplay(commentVisibility);

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

	private boolean anySupports(Diagram[] selected, ContextMenuItem menuItem) {
		for (Diagram diagram : selected) {
			if (AuthHelpers.allowedToEdit(diagram) && 
				  (diagram.supportedMenuItems() & menuItem.getValue()) == menuItem.getValue() &&
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

	private native boolean isCommentsAvailable()/*-{
		return (typeof $wnd.backendComments != 'undefined')
	}-*/;

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

	private boolean allSupports(Diagram[] selected, ContextMenuItem menuItem) {
		boolean result = false;
		for (Diagram d : selected) {
			result = true;
			if (!ContextMenuItem.supported(d.supportedMenuItems(), menuItem)) {
				return false;
			}
		}
		return result;
	}



}
