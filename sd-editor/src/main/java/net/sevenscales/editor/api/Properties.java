package net.sevenscales.editor.api;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.constants.Constants;
import net.sevenscales.domain.utils.Debug;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.auth.AuthHelpers;
import net.sevenscales.editor.api.event.BoardRemoveDiagramsEvent;
import net.sevenscales.editor.api.event.BoardRemoveDiagramsEventHandler;
import net.sevenscales.editor.api.event.ChangeTextSizeEvent;
import net.sevenscales.editor.api.event.ChangeTextSizeEventHandler;
import net.sevenscales.editor.api.event.RotateEvent;
import net.sevenscales.editor.api.event.RotateEventHandler;
import net.sevenscales.editor.api.event.ColorSelectedEvent;
import net.sevenscales.editor.api.event.ColorSelectedEvent.ColorSetType;
import net.sevenscales.editor.api.event.ColorSelectedEvent.ColorTarget;
import net.sevenscales.editor.api.event.ColorSelectedEventHandler;
import net.sevenscales.editor.api.event.DiagramElementAddedEvent;
import net.sevenscales.editor.api.event.DiagramElementAddedEventHandler;
import net.sevenscales.editor.api.event.PotentialOnChangedEvent;
import net.sevenscales.editor.api.event.RelationshipTypeSelectedEvent;
import net.sevenscales.editor.api.event.RelationshipTypeSelectedEventHandler;
import net.sevenscales.editor.api.event.SelectionMouseUpEvent;
import net.sevenscales.editor.api.event.ShowDiagramPropertyTextEditorEvent;
import net.sevenscales.editor.api.event.ShowDiagramPropertyTextEditorEventHandler;
import net.sevenscales.editor.api.event.SurfaceScaleEvent;
import net.sevenscales.editor.api.impl.EditorCommon;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.TouchHelpers;
import net.sevenscales.editor.api.texteditor.ITextEditor;
import net.sevenscales.editor.content.RelationShipType;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.content.utils.ScaleHelpers;
import net.sevenscales.editor.diagram.ClickDiagramHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.Diagram.SizeChangedHandler;
import net.sevenscales.editor.diagram.DiagramSelectionHandler;
import net.sevenscales.editor.diagram.SelectionHandler;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.diagram.utils.MouseDiagramEventHelpers;
import net.sevenscales.editor.gfx.domain.ElementColor;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.uicomponents.uml.CommentThreadElement;
import net.sevenscales.editor.uicomponents.uml.Relationship2;




public class Properties extends SimplePanel implements DiagramSelectionHandler, ClickDiagramHandler, SizeChangedHandler, IEditor, ITextEditor.TextChanged {
  private static final SLogger logger = SLogger.createLogger(Properties.class);
  
	static {
		SLogger.addFilter(Properties.class);
	}

	private static final String PROPERTIES_EDITOR_STYLE = "properties-TextArea2";
	
  private Map<ISurfaceHandler,Boolean> surfaces = new HashMap<ISurfaceHandler, Boolean>();
  private ITextEditor codeMirror;
	// private TextArea textArea;
	private Diagram selectedDiagram;
	private EditorContext editorContext;
	private SelectionHandler selectionHandler;
	private CustomPopupCodeMirror popup;
	private ISurfaceHandler surface;
	private Diagram lastDiagramAdded;
	private boolean sending;
	private int textEditX;
	private int textEditY;
	private CommentEditor commentEditor;
	private EditorCommon editorCommon;
  private boolean modifiedAtLeastOnce;
	private boolean dialogMode;
	
	private Buffer buffer = new Buffer();

	private ShowDiagramPropertyTextEditorEventHandler showDiagramText = new ShowDiagramPropertyTextEditorEventHandler() {
		@Override
		public void on(ShowDiagramPropertyTextEditorEvent event) {
			Diagram diagram = event.getDiagram();
			if (diagram.supportsTextEditing()) {
        logger.debug("ShowDiagramPropertyTextEditorEvent...");
				selectedDiagram = diagram;
				modifiedAtLeastOnce = event.markAsDirty();
				surface.getEditorContext().set(EditorProperty.PROPERTY_EDITOR_SELECT_ALL_ENABLED, true);
				if (event.getPoint() != null) {
					// iPad needs to show editor and direct show of text area shows input
					// if there is anything deferred, keyboard will not be shown
					onDoubleClick(diagram, event.getPoint());
				} else {
					showEditor(diagram, diagram.getText(), diagram.getLeft(), diagram.getTop(), event.isJustCreated());
				}
			}
		}
	};

	public Properties(int height, ISurfaceHandler surface, SelectionHandler selectionHandler, EditorContext editorContext) {
		this.surface = surface;
		this.selectionHandler = selectionHandler;
		this.editorContext = editorContext;
		addStyleName("properties-TextArea");
		setHeight("100%");

		editorCommon = new EditorCommon(surface, new EditorCommon.HideEditor() {
			public void hide() {
				Properties.this.hide();
			}
		});
				
		editorContext.getEventBus().addHandler(RelationshipTypeSelectedEvent.TYPE, new RelationshipTypeSelectedEventHandler() {
			private void changeSelected(Diagram diagram, RelationshipTypeSelectedEvent event) {
				if (diagram instanceof Relationship2) {
					Relationship2 rel = (Relationship2) diagram;
					if (event.getRelationshipType().equals(RelationShipType.REVERSE)) {
						rel.reverse();
				    Properties.this.editorContext.getEventBus().fireEvent(new PotentialOnChangedEvent(rel));
					} else {
						rel.setType(event.getRelationshipType().getValue());
				    Properties.this.editorContext.getEventBus().fireEvent(new PotentialOnChangedEvent(rel));
					}
				}
			}
			@Override
			public void onSelection(RelationshipTypeSelectedEvent event) {
				// Properties.this.surface.beginTransaction();

				// to avoid concurrent modification exception
				// NOTE this should be refactored in some better way that selection cannot change
				Set<Diagram> sels = Properties.this.selectionHandler.getSelectedItems();
				Diagram[] selected = new Diagram[sels.size()];
				sels.toArray(selected);
				for (Diagram d : selected) {
					changeSelected(d, event);
				}

				// Properties.this.surface.commitTransaction();
			}
		});
		
		editorContext.getEventBus().addHandler(ColorSelectedEvent.TYPE, new ColorSelectedEventHandler() {
			@Override
			public void onSelection(ColorSelectedEvent event) {
				logger.debug("onSelection selected {}", Properties.this.selectionHandler.getSelectedItems());
				Set<Diagram> modified = new HashSet<Diagram>();
				for (Diagram d : Properties.this.selectionHandler.getSelectedItems()) {
					if (AuthHelpers.allowedToEdit(d) && AuthHelpers.allowColorChange(d)) {
						setColors(
							d,
							event.getColorTarget(),
							event.getElementColor(),
							event.getColorSetType()
						);
						modified.add(d);
					}
				}
				Properties.this.editorContext.getEventBus().fireEvent(new PotentialOnChangedEvent(modified));

			}
		});

		editorContext.getEventBus().addHandler(ChangeTextSizeEvent.TYPE, new ChangeTextSizeEventHandler() {
			@Override
			public void on(ChangeTextSizeEvent event) {
				logger.debug("change text size {}", Properties.this.selectionHandler.getSelectedItems());
				changeFontSize(event.getFontSize());
			}
		});

		editorContext.getEventBus().addHandler(
      RotateEvent.TYPE, 
      new RotateEventHandler() {
        @Override
        public void on(RotateEvent event) {
          logger.debug("rotate {}", Properties.this.selectionHandler.getSelectedItems());
          rotate(event.getRotateDeg(), true);
        }
		  }
    );

		commentEditor = new CommentEditor(surface);

		popup = new CustomPopupCodeMirror();
		popup.setStyleName("propertyPopup");
		// iPad uses legacy text area editor
		codeMirror = ITextEditor.Factory.createEditor(this);
		popup.setWidget(codeMirror.getUi());
		// autohide is not enabled since property editor is closed manually and autohide causes problems
		popup.setAutoHideEnabled(false);
		popup.setAutoHideOnHistoryEventsEnabled(false);
		popup.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
        logger.info("close properties editor...");
        // ST 8.3.2019: Clean sends buffer
				// _doSendBuffer();
				boolean forceApplyAlways = true;
				applyTextToDiagram(forceApplyAlways);
				Properties.this.editorCommon.fireEditorClosed();
				if (selectedDiagram == null) {
					return;
				}
				
				if (!TouchHelpers.isSupportsTouch()) {
					// selection stays if not unselected separately on non touch device
					selectedDiagram.unselect();
				}
				clean();
				logger.debug("close properties editor... done");
			}
		});
		
		editorContext.getEventBus().addHandler(DiagramElementAddedEvent.TYPE, new DiagramElementAddedEventHandler() {
			@Override
			public void onAdded(DiagramElementAddedEvent event) {
				lastDiagramAdded = null;
				if (!(event.getDiagrams().get(0).getOwnerComponent() instanceof Relationship2) &&
						  !(event.getDiagrams().get(0) instanceof Relationship2) &&
						  !event.isDuplicate()) {
					// read property, how element was added; if duplicated then do not remember
					// lastDiagramAdded to replace text later, because that is not wanted in duplicate case
					// if not duplicated it is not possible to add multiple elements; safe to use 0th element
					lastDiagramAdded = event.getDiagrams().get(0);
				}
			}
		});

		editorContext.getEventBus().addHandler(BoardRemoveDiagramsEvent.TYPE, new BoardRemoveDiagramsEventHandler() {
			@Override
			public void on(BoardRemoveDiagramsEvent event) {
				for (Diagram d : event.getRemoved()) {
					if (d == selectedDiagram) {
						closeIfOpen();
						selectedDiagram = null;
					}
				}
			}
		});

    editorContext.getEventBus().addHandler(ShowDiagramPropertyTextEditorEvent.TYPE, showDiagramText);
    
		handleEditorCloseStream(this);
		handleItemRealTimeModify(this);
		initAPI(this);

		// >>>>>>>>>>>> SOLU
		// handleExternalKeyCode(this);
		// <<<<<<<<<<<< SOLU
		popup.setCodeMirror(codeMirror);
	}

	@Override
	public void onTextChanged() {
		_setTextFromTextArea();
	}

	private void _setTextFromTextArea() {
		if (selectedDiagram != null) {
	    selectedDiagram.setAutoResize(true);
	    setSelectedDiagramText(codeMirror.getText());
	    setTextAreaHeight();
	    selectedDiagram.setAutoResize(false);
		}
	}

	// >>>>>>>>>>>>>> SOLU
	// private native void handleExternalKeyCode(Properties me)/*-{
	// 	$wnd.globalStreams.keyCodeStream.onValue(function(values) {
	// 		me.@net.sevenscales.editor.api.Properties::onExternalKeyCode(Ljava/lang/String;I)(values[0], values[1]);
	// 	})
	// }-*/;

	// private void onExternalKeyCode(String character, int keyCode) {
	// 	String text = selectedDiagram.getText();
	// 	if (keyCode != 0) {
	// 		if (text.length() > 1 && keyCode == KeyCodes.KEY_BACKSPACE) {
	// 			text = text.substring(0, text.length() - 1);
	// 		}  else if (keyCode == KeyCodes.KEY_BACKSPACE) {
	// 			text = "";
	// 		} else if (keyCode == KeyCodes.KEY_ENTER) {
	// 			text += "\n";
	// 		} else {
	// 			text += fromCharCode(keyCode);
	// 		}
	// 	} else {
	// 		text += character;
	// 	}
	// 	textArea.setText(text);

	// 	_setTextFromTextArea();
	// }

	// private native String fromCharCode(int keyCode)/*-{
 // 		return String.fromCharCode(keyCode)
	// }-*/;

	// <<<<<<<<<<<<<< SOLU

	private native void handleEditorCloseStream(Properties me)/*-{
		$wnd.globalStreams.closeEditorStream.onValue(function() {
			me.@net.sevenscales.editor.api.Properties::unselectAll()()
		})

		$wnd.globalStreams.enterCmdShortcutStream.onValue(function() {
			me.@net.sevenscales.editor.api.Properties::closeIfOpen()()
		})

		$wnd.globalStreams.editShapeStream.onValue(function() {
			me.@net.sevenscales.editor.api.Properties::editOneSelected()()
		})
	}-*/;

	private native void handleItemRealTimeModify(Properties me)/*-{
		$wnd.globalStreams.dataItemModifyStream.onValue(function(dataItem) {
			me.@net.sevenscales.editor.api.Properties::onItemRealTimeModify(Lnet/sevenscales/domain/IDiagramItemRO;)(dataItem)
		})

		$wnd.globalStreams.dataItemDeleteStream.onValue(function(dataItem) {
			me.@net.sevenscales.editor.api.Properties::onItemRealTimeDelete(Lnet/sevenscales/domain/IDiagramItemRO;)(dataItem)
		})
	}-*/;

	private native void initAPI(Properties me)/*-{
		$wnd.gwtSetShapesRotateDegrees = function(client_id, angle, save) {
			me.@net.sevenscales.editor.api.Properties::gwtSetShapesRotateDegrees(Ljava/lang/String;IZ)(client_id, angle, save)
		}
	}-*/;

  private void gwtSetShapesRotateDegrees(
    String clientId,
    int rotateDegrees,
    boolean save
  ) {
    // no need to pass selected shape, since rotate gets selected shape
    rotate(rotateDegrees, save);
  }

	private void onItemRealTimeModify(IDiagramItemRO item) {
		if (selectedDiagram != null && item.getClientId().equals(selectedDiagram.getDiagramItem().getClientId())) {
			String text = selectedDiagram.getDiagramItem().getText();
			setTextAreaText(text);
			// this is state from the server, so kept as sent
      buffer.setText(text);
      buffer.markSent();

      // ST 8.3.2019: Close editor if open for the shape or editor window
      // is not in correct place. Could move editor window to the new location.
      // But in the end new text is used as well.
			closeIfOpen();
		}
	}

	private void onItemRealTimeDelete(IDiagramItemRO item) {
		if (selectedDiagram != null && item.getClientId().equals(selectedDiagram.getDiagramItem().getClientId())) {
			closeIfOpen();
			selectedDiagram = null;
		}
	}

  private void changeFontSize(Integer fontSize) {
		Set<Diagram> modified = new HashSet<Diagram>();
		for (Diagram d : Properties.this.selectionHandler.getSelectedItems()) {
			if (ContextMenuItem.supportsFontSize(d.supportedMenuItems())) {
				d.setFontSize(fontSize);
				modified.add(d);
			}
		}
		MouseDiagramEventHelpers.fireDiagramsChangedEvenet(modified, surface, ActionType.FONT_CHANGE);
	}  

	private void rotate(
		Integer rotateDeg,
		boolean save
	) {
		Set<Diagram> modified = new HashSet<Diagram>();
		for (Diagram d : Properties.this.selectionHandler.getSelectedItems()) {
			if (ContextMenuItem.supportsRotate(d.supportedMenuItems())) {
				d.rotate(
          rotateDeg,
          save
        );

        if (save) {
					modified.add(d);
				}
			}
		}

		if (save) {
			MouseDiagramEventHelpers.fireDiagramsChangedEvenet(modified, surface, ActionType.ROTATE);
		}
	}

	private void setColors(Diagram d, ColorTarget colorTarget, ElementColor color, ColorSetType colorSetType) {

		if (colorSetType == ColorSetType.RESTORE_COLORS && d.hasDefaultColors()) {
			// e.g. AWS icons have default colors
			d.restoreDefaultColors();
			return;
		}

		switch (colorTarget) {
		case BACKGROUND:
			logger.debug("onSelection background {}...", d);

			if (d.canSetBackgroundColor()) {
				if (d instanceof Relationship2) {
					if (color.getBackgroundColor().opacity > 0) {
						// do not apply transparent color, relationship gets invisible
						// relationship has only border color
						d.setBorderColor(color.getBackgroundColor());	
					}
				} else {
					// Color newbg = new Color(color.getRr(), color.getGg(), color.getBb(), color.getOpacity());
					// Color newbg = color.getBackgroundColor();
					d.setBackgroundColor(color.getBackgroundColor());
					// ST 19.11.2018: Save with special border color
					// that is changed based on theme and calculated on runtime.
					// Color borderColor = ColorHelpers.borderColorByBackground(newbg.red, newbg.green, newbg.blue);
					if (color.getBackgroundColor().opacity > 0) {
            // do not apply theme color if color is transparent
            // ST 14.3.2019: Fix markdown line color is not set --
						// d.setBorderColor(Theme.THEME_BORDER_COLOR_STORAGE);
						d.setBorderColor(color.getBorderColor());
					}
				}
			}
		
			if (!"transparent".equals(d.getTextAreaBackgroundColor())) {
		  	// can set text color only if element has some color as text background
		  	// this is due to theme switching, e.g. actor text element is on always on top of 
		  	// board background color
				d.setTextColor(color.getTextColor());
			} else if (color.getBackgroundColor().opacity == 0) {
				// if background is transparent set default theme text color that is changed
				// based on theme
				d.setTextColor(Theme.createDefaultTextColor());
			}
			logger.debug("onSelection background... done");
			break;
		case BORDER:
			if (d.canSetBackgroundColor()) {
				d.setBorderColor(color.getBorderColor());
			}
			break;
		case TEXT:
      d.setTextColor(color.getTextColor());
			break;
		case ALL:
      d.setTextColor(color.getTextColor());
      d.setBackgroundColor(color.getBackgroundColor());
			if (d.canSetBackgroundColor()) {
				d.setBorderColor(color.getBorderColor());
			}
			break;
		}
	}

	private void closeIfOpen() {
      hide();
  }
  
	private void hide() {
    // Debug.log("Properties.hide");
    // Debug.callstack("Properties.hide");

    if (popup.isShowing()) {
      popup.hide();
    }
    selectedDiagram = null;
	}
		
	public void addSurface(ISurfaceHandler surfaceHandler, boolean modifiable) {
	  surfaces.put(surfaceHandler, modifiable);
	}

	public void selected(List<Diagram> senders) {
		if (senders.size() == 1) {
			// apply text first to current diagram, otherwise on close
			// tries to apply text to new diagram and old ref is gone
			applyTextToDiagram(false);
			selectedDiagram = senders.get(0);
		}
		
//    if (editorContext.get(EditorProperty.PROPERTY_EDITOR_SELECT_ALL_ENABLED).equals(true)) {
//    	Scheduler.get().scheduleDeferred(new ScheduledCommand() {
//				@Override
//				public void execute() {
//		    	textArea.selectAll();
//				}
//			});
//    }
	}
	
	public void unselectAll() {
	  applyTextToDiagram(false);
	  clean();
	  Properties.this.editorCommon.fireEditorClosed();
	}
	
	private void clean() {
    selectedDiagram = null;
    if (isEditorOpen()) {
	    codeMirror.setText("");
    }
    // send buffer if not sent and clean buffer as well
    _doSendBuffer();
    buffer = new Buffer();
    modifiedAtLeastOnce = false;
    
    closeIfOpen();
	}

	private boolean isEditorOpen() {
		return editorContext.isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN);
	}

	private void applyTextToDiagram(boolean forceApplyAlways) {
	  boolean editorIsOpen = isEditorOpen();
		if (editorIsOpen && selectedDiagram != null && selectedDiagram.supportsOnlyTextareaDynamicHeight()) {
			// reset text edit location
			textEditX = 0;
			textEditY = 0;
//			selectedDiagram.endTextEdit();
		}
		
		if ((editorIsOpen && selectedDiagram != null) || (forceApplyAlways && selectedDiagram != null)) {
			selectedDiagram.editingEnded(modifiedAtLeastOnce);
			AnchorElement.dragEndAnchors(selectedDiagram);
		}
	}

	public void unselect(Diagram sender) {
	}

	private void setSelectedDiagramText(String text) {
		logger.debug("setSelectedDiagramText {}...", text);
		if (text.equals(selectedDiagram.getText())) {
			// do not allow sending same content again
			return;
		}

    // set diagram text synchronously, because editor might be closed
    // before buffer is saved and would show a wrong state for a while
    // if set only when buffer is sent
    selectedDiagram.setText(codeMirror.getText(), textEditX, textEditY);

    modifiedAtLeastOnce = true;
    sendBuffer();
    // synchronous version, starts to lag with long text
    // editorCommon.fireChangedWithRelatedRelationships(selectedDiagram);
	}
	
	private void sendBuffer() {
		if (!sending) {
			buffer.setText(codeMirror.getText());
			buffer.setDiagram(selectedDiagram);

			sendScheduled();
		} else {
			// ensure sending... try at least once more
//			onceMore = true;
			if (!buffer.isSent()) {
				// do not send if check says it is already sent
				// this is due to automatic chile text element delete when text is empty
				// text is no longer found for undo/redo
				buffer.setText(codeMirror.getText());
				buffer.setDiagram(selectedDiagram);
			} else {
				buffer.setText("");
				buffer.setDiagram(null);
			}
		}
	}

	private void sendScheduled() {
    // do not send update on every key stroke
    sending = true;

		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
			@Override
			public boolean execute() {
				_doSendBuffer();		    
				boolean doitagain = !buffer.isSent();
				return doitagain;
			}
		}, 1500);
	}

	private void _doSendBuffer() {
		if (buffer.getDiagram() != null && !buffer.isSent()) {
      // lastSentText = buffer.diagram.getText(textEditX, textEditY);
      Properties.this.editorCommon.fireChangedWithRelatedRelationships(buffer.getDiagram());
      buffer.markSent();
    }
    
    sending = false;
  }


	@Override
	public void onClick(Diagram sender, int x, int y, int keys) {
	}

	@Override
	public void onDoubleClick(Diagram sender, final MatrixPointJS point) {
		if (sender != null) {
			// parent element can create + switch to child element
			// e.g. relationship creates child element and that should be edited after that
			selectedDiagram = sender.showEditorForDiagram(point.getScreenX(), point.getScreenY());

			if (TouchHelpers.isSupportsTouch()) {
				// iPad cannot have any deferred to focus keyboard
				setTextCoordinatesAndShowEditor(point.getScreenX(), point.getScreenY(), point.getX(), point.getY());
			} else {
				// chrome on macbook air doesn't focus keyboard if not deferred
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						setTextCoordinatesAndShowEditor(point.getScreenX(), point.getScreenY(), point.getX(), point.getY());
					}
				});
			}
		}
	}

	private void editOneSelected() {
		Diagram selected = selectionHandler.getOnlyOneSelected();
		if (selected != null) {
			// selectedDiagram = selected;
			// showEditor(selectedDiagram, selectedDiagram.getText(0, 0), 0, 0, false);
			onDoubleClick(selected, MatrixPointJS.createScaledPoint(0, 0, 0));
		}
	}

	private void setTextCoordinatesAndShowEditor(int screenX, int screenY, int x, int y) {
		textEditX = screenX;
		textEditY = screenY;

		if (selectedDiagram != null) {
			// ST 28.10.2018: Try to fix null pointer on next line
			showEditor(selectedDiagram, selectedDiagram.getText(textEditX, textEditY), x, y, false);
		}
	}

	private void showEditor(Diagram diagram, String text, final int left, final int top, boolean justCreated) {
		if (popup.isShowing()) {
			return;
    }
    
		logger.info("SHOW EDITOR...");
		if (!selectedDiagram.supportsTextEditing() || !AuthHelpers.allowedToEdit(diagram)) {
			// if diagram text editing is not supported => return
			return;
		}

		if (selectedDiagram instanceof CommentThreadElement) {
			commentEditor.showEditor(selectedDiagram);
			return;
		}

    if (surface.getScaleFactor() < 1
        // ST 15.2.2019: commented out to enable zooming on iPad.
        // && !UiUtils.isMobile()
        ) {
          // centers diagram into the middle
        // editorContext.getEventBus().fireEvent(
        // 	new SurfaceScaleEvent(
        //     true,
        //     selectedDiagram.getCenterX(),
        //     selectedDiagram.getCenterY()
        //   )
        // );

        Point p = ScaleHelpers.diagramPositionToScreenPoint(
          selectedDiagram,
          surface,
          true
        );

        // ST 29.3.2019: Center into the same and don't put it in the middle
        editorContext.getEventBus().fireEvent(
          new SurfaceScaleEvent(
            Constants.ZOOM_DEFAULT_INDEX,
            false,
            p.x,
            p.y
        ));
		}
		
		selectedDiagram.hideConnectionHelpers();
		diagram.hideText();

		diagram.setSizeChangedHandlerByText(this);
		// diagram might be under popup and doesn't receive mouse up and drag will continue 
		// without this.
		surface.getMouseDiagramManager().getDragHandler().releaseDrag();
		lastDiagramAdded = null; // reset state because editing has not been done right away
														 // at least this is very precise way and perhaps easy to understand...
		
		setTextAreaText(text);
		_setTextAreaSize(diagram);

		codeMirror.cursorEnd();
		codeMirror.setMarkdownMode(selectedDiagram.isMarkdownEditor());

//		setFocus(true);

		this.editorCommon.fireEditorOpen();
		popup.selectAll(justCreated);
		popup.show();
		popup.getElement().getStyle().setPosition(com.google.gwt.dom.client.Style.Position.FIXED);

		// show element context menu always when showing editor
		surface.getEditorContext().getEventBus().fireEvent(new SelectionMouseUpEvent(diagram));
	}

	private void setTextAreaText(String text) {
		if (selectedDiagram == null) {
			return;
		}
		
		codeMirror.setText(text);
	}

	private void setMeasurementPanelText(String text) {
		if (selectedDiagram == null) {
			return;
		}

		MeasurementPanel.setPlainTextAsHtml(text, selectedDiagram.getMeasurementAreaWidth());
    MeasurementPanel.setPosition(selectedDiagram.getLeft() + selectedDiagram.getWidth() + 20, selectedDiagram.getTop());
    // MeasurementPanel.setZoom(surface.getScaleFactor());

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
        int height = (int) (MeasurementPanel.getOffsetHeight() * surface.getScaleFactor());
				codeMirror.setHeight(height);
			}
		});
	}

	private void setTextAreaHeight() {
		if (codeMirror.isCodeMirror()) {
			// code mirror manages height by it self
			return;
		}

		if (selectedDiagram != null && !this.dialogMode) {
      // ST 15.3.2019: Use always measurment panel or horizontally scaled editors
      // don't show big enough text area when zoomed in
      setMeasurementPanelText(codeMirror.getText());

			// if (selectedDiagram.supportsOnlyTextareaDynamicHeight()) {
			// 	// in case dynamically resized text should use measurement panel!!!
			// 	setMeasurementPanelText(codeMirror.getText());
			// } else {
			// 	int rows = rows(codeMirror.getText());
			// 	int dFontSize = selectedDiagram.getFontSize() != null ? selectedDiagram.getFontSize() : 12;
			// 	int lheight = lineHeight(dFontSize);
			// 	int textAreaHeight = lheight * rows;
			// 	codeMirror.setHeight(textAreaHeight + lheight);
			// }
		}
	}

	private void _setTextAreaSize(Diagram diagram) {
		// no need to hide text any longer since markdown editor hides the text with background color.
		// diagram.hideText();
		
		MatrixPointJS point = MatrixPointJS.createUnscaledPoint(diagram.getTextAreaLeft(), diagram.getTextAreaTop(), surface.getScaleFactor());
		int x = point.getX() + surface.getRootLayer().getTransformX() + surface.getAbsoluteLeft();
    int y = point.getY() + surface.getRootLayer().getTransformY() + surface.getAbsoluteTop();
    
    int posx = x;
    int posy = y;
    boolean showAsDialog = false;

    // move popup position to a visible area
    if (posx < Window.getScrollLeft()) {
      // add padding
      // posx = Window.getScrollLeft() + 60;
      showAsDialog = true;
    }
    if (posy < Window.getScrollTop()) {
      // add padding
      // posy = Window.getScrollTop() + 60;
      showAsDialog = true;
    }

		popup.setPopupPosition(posx, posy);

		// textArea.setVisible(true);

		if (diagram.supportsOnlyTextareaDynamicHeight()) {
			if ("transparent".equals(diagram.getTextAreaBackgroundColor())) {
				codeMirror.setBackgroundColor(Theme.getCurrentThemeName().getBoardBackgroundColor().toHexStringWithHash());
			} else {
				codeMirror.setBackgroundColor(diagram.getTextAreaBackgroundColor());
			}
		} else {
			codeMirror.setBackgroundColor("transparent");
		}

		if ("transparent".equals(diagram.getTextAreaBackgroundColor())) {
			codeMirror.setColor(Theme.getCurrentColorScheme().getTextColor().toHexStringWithHash());
		} else {
			codeMirror.setColor("#" + diagram.getTextColor().toHexString());
			codeMirror.setBackgroundColor(diagram.getTextAreaBackgroundColor());
    }
    
		// ST 14.11.2018: Fix code mirror cursor not visible on black background
		// set cursor color based on background, black or white
		codeMirror.setCursorColorByBgColor(diagram.getTextAreaBackgroundColor());

		double scaleFactor = surface.getScaleFactor();
		
		int dFontSize = diagram.getFontSize() != null ? diagram.getFontSize() : 12;
		int fontSize = ((int) (dFontSize * scaleFactor));
    codeMirror.setFontSize(fontSize + "px");
    codeMirror.setLineHeight(lineHeight(fontSize) + "px");
    
    int editorWidth = (int) (diagram.getTextAreaWidth() * scaleFactor);
    // if (editorWidth > Window.getClientWidth()) {
    //   editorWidth = Window.getClientWidth();
    // }
    codeMirror.setWidth(editorWidth);
    
    // codeMirror.setHeight("100vh");

    Point p = ScaleHelpers.diagramPositionToScreenPoint(
      diagram,
      surface,
      false
    );
    
    int clientHeight = Window.getClientHeight();
    int clientWidth = Window.getClientWidth();

    // take zoom into calculation, it might not fit if board is zoomed
    int posHeight = p.y + (int) (diagram.getHeight() * scaleFactor);
    int posWidth = p.x + (int) (diagram.getWidth() * scaleFactor);

    // restore default height
    codeMirror.setHeight("auto");

    // remove diagram dialog editor by default
    setDialogMode(false);

    if ((posWidth > clientWidth
        || diagram.getWidth() > clientWidth
        || posHeight > clientHeight
        || showAsDialog) &&
        // prevent opening modal dialog on mobile layout
        // especially on tutorial
        clientWidth > 400) {
      // open editor as a modal dialog

      int maxDialoagWidth = 700;
      if (maxDialoagWidth > clientWidth) {
        showDynamicDialog(clientWidth, clientHeight);
      } else {
        showFixedDialog(clientWidth, clientHeight, maxDialoagWidth);
      }

      if ("transparent".equals(codeMirror.getBackgroundColor())) {
        // dialog editor needs to have a solid background always
        codeMirror.setBackgroundColor(
          Theme.getCurrentThemeName().getBoardBackgroundColor().toHexStringWithHash()
        );
      }

      setDialogMode(true);
    } else {
      // use legacy editor height setup      
      setTextAreaHeight();
      popup.setContentWidth((int) (diagram.getTextAreaWidth() * scaleFactor));
    }

		if (surface.getScaleFactor() > 1) {
			String paddingTop = ((int) (2 * surface.getScaleFactor())) + "";
		}

		codeMirror.setTextAlign(diagram.getTextAreaAlign());
		
		if (surface.getScaleFactor() != 1.0f && !"transparent".equals(diagram.getTextAreaBackgroundColor())) {
			codeMirror.setBackgroundColor("#" + diagram.getBackgroundColor());
		}
  }

  private void showDynamicDialog(
    int clientWidth,
    int clientHeight
  ) {

    int popupLeft = 60;
    int popupTop = 60;
    int popupMarginRigth = 60;
    int diagramDialogPadding = 22;

    codeMirror.setHeight(clientHeight - diagramDialogPadding - 40 - popupLeft * 2);
    codeMirror.setWidth(clientWidth - diagramDialogPadding - popupMarginRigth * 2);
    popup.setContentWidth(clientWidth - popupMarginRigth * 2);

    popup.setPopupPosition(popupLeft, popupTop);
    
    firePropertyEditorOpenPosition(popupLeft, popupTop);
  }

  private void showFixedDialog(
    int clientWidth,
    int clientHeight,
    int maxDialoagWidth
  ) {
    int popupLeft = clientWidth / 2 - maxDialoagWidth / 2;
    int popupTop = 80;
    int diagramDialogPadding = 30;
  
    // codeMirror.setHeight(clientHeight - diagramDialogPadding - 20 - popupLeft * 2);
    int bottomMargin = 200;
    codeMirror.setHeight(clientHeight - bottomMargin);
    // codeMirror.setWidth(clientWidth - diagramDialogPadding - popupMarginRigth * 2);
    codeMirror.setWidth(maxDialoagWidth - diagramDialogPadding);
    // popup.setContentWidth(clientWidth - popupMarginRigth * 2);
    popup.setContentWidth(maxDialoagWidth);
  
    popup.setPopupPosition(popupLeft, popupTop);
    firePropertyEditorOpenPosition(popupLeft, popupTop);
  }

  private native void firePropertyEditorOpenPosition(int popupLeft, int popupTop)/*-{
    $wnd.setTimeout(function() {
      $wnd.globalStreams.contextMenuStream.push({
        type: 'property-editor-pos',
        x: popupLeft,
        y: popupTop,
      })
    }, 100)
  }-*/;
  
  private void setDialogMode(boolean enable) {
    if (enable) {
      codeMirror.addClass("diagram-dialog-editor");
      this.dialogMode = true;
    } else {
      codeMirror.removeClass("diagram-dialog-editor");
      this.dialogMode = false;
    }

  }

	private int lineHeight(int fontSize) {
		// return ((int) ((fontSize) * surface.getScaleFactor())) + 5;
		return (int) (fontSize + 5 * surface.getScaleFactor());
	}

	private int rows(String text) {
		int result = 0;
		char[] array = text.toCharArray();
		for (int i = 0; i < array.length; ++i) {
			if (text.charAt(i) == '\n') {
				++result;
			}
		}
		return result;
	}

	public void showEditor(final KeyDownEvent event) {
		if (selectedDiagram != null) {
			_showEditor(event.getNativeKeyCode(), event);
		}
	}

	private void _showEditor(int nativeKeyCode, KeyEvent event) {
		switch (nativeKeyCode) {
		case KeyCodes.KEY_DELETE:
			// do not catch delete key
			break;
		case KeyCodes.KEY_BACKSPACE:
			String text = selectedDiagram.getText();
			if (text.length() > 1) {
				showEditor(selectedDiagram, text.substring(0, text.length() - 1), 0, 0, false);
				event.preventDefault();
			}
			break;
		case 113: // F2
		case KeyCodes.KEY_TAB:
		case KeyCodes.KEY_ENTER:
			showEditor(selectedDiagram, selectedDiagram.getText(), 0, 0, false);
			event.preventDefault();
			break;
		default:
//			if (selectedDiagram != null) {
//				showEditor(selectedDiagram, textArea.getText());
//				textArea.getElement().dispatchEvent(event.getNativeEvent());
//			}
			break;
		}

	}

	@Override
	public void onSizeChanged(Diagram diagram, int width, int height) {
		if (popup.isShowing() && selectedDiagram == diagram) {
			_setTextAreaSize(diagram);
		}
	}

	@Override
	public boolean hasPendingChanges() {
		// return !bufferTextIsSent();
		return !buffer.isSent();
	}

}
