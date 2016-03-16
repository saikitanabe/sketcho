package net.sevenscales.editor.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.constants.Constants;
import net.sevenscales.editor.api.event.ColorSelectedEvent;
import net.sevenscales.editor.api.event.ColorSelectedEventHandler;
import net.sevenscales.editor.api.event.ColorSelectedEvent.ColorTarget;
import net.sevenscales.editor.api.event.DiagramElementAddedEvent;
import net.sevenscales.editor.api.event.DiagramElementAddedEventHandler;
import net.sevenscales.editor.api.event.PotentialOnChangedEvent;
import net.sevenscales.editor.api.event.RelationshipTypeSelectedEvent;
import net.sevenscales.editor.api.event.RelationshipTypeSelectedEventHandler;
import net.sevenscales.editor.api.event.ShowDiagramPropertyTextEditorEvent;
import net.sevenscales.editor.api.event.ShowDiagramPropertyTextEditorEventHandler;
import net.sevenscales.editor.api.event.ChangeTextSizeEvent;
import net.sevenscales.editor.api.event.ChangeTextSizeEventHandler;
import net.sevenscales.editor.api.event.SelectionMouseUpEvent;
import net.sevenscales.editor.api.event.SurfaceScaleEvent;
import net.sevenscales.editor.api.impl.TouchHelpers;
import net.sevenscales.editor.api.auth.AuthHelpers;
import net.sevenscales.editor.api.ActionType;
import net.sevenscales.editor.api.texteditor.ITextEditor;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.content.RelationShipType;
import net.sevenscales.editor.content.utils.ColorHelpers;
import net.sevenscales.editor.diagram.ClickDiagramHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.Diagram.SizeChangedHandler;
import net.sevenscales.editor.diagram.DiagramSelectionHandler;
import net.sevenscales.editor.diagram.SelectionHandler;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.ElementColor;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementVerticalFormatUtil;
import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.editor.uicomponents.uml.CommentThreadElement;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.EditorCommon;
import net.sevenscales.editor.diagram.utils.MouseDiagramEventHelpers;
import net.sevenscales.editor.diagram.utils.UiUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import net.sevenscales.editor.content.utils.JQuery;

public class Properties extends SimplePanel implements DiagramSelectionHandler, ClickDiagramHandler, SizeChangedHandler, IEditor, ITextEditor.TextChanged {
	private static final SLogger logger = SLogger.createLogger(Properties.class);

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
	
	private static class Buffer {
		String text;
		Diagram diagram;
	}
	private Buffer buffer = new Buffer();
	private String lastSentText;
	
	private ShowDiagramPropertyTextEditorEventHandler showDiagramText = new ShowDiagramPropertyTextEditorEventHandler() {
		@Override
		public void on(ShowDiagramPropertyTextEditorEvent event) {
			Diagram diagram = event.getDiagram();
			if (diagram.supportsTextEditing()) {
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
						setColors(d, event.getColorTarget(), event.getElementColor());
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
				_doSend();
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
				
		editorContext.getEventBus().addHandler(ShowDiagramPropertyTextEditorEvent.TYPE, showDiagramText);

		handleEditorCloseStream(this);
		handleItemRealTimeModify(this);

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
	}-*/;

	private native void handleItemRealTimeModify(Properties me)/*-{
		$wnd.globalStreams.dataItemModifyStream.onValue(function(dataItem) {
			me.@net.sevenscales.editor.api.Properties::onItemRealTimeModify(Lnet/sevenscales/domain/IDiagramItemRO;)(dataItem)
		})
	}-*/;

	private void onItemRealTimeModify(IDiagramItemRO item) {
		if (selectedDiagram != null && item.getClientId().equals(selectedDiagram.getDiagramItem().getClientId())) {
			String text = selectedDiagram.getDiagramItem().getText();
			setTextAreaText(text);
			// this is state in the server, so kept as sent
			buffer.text = text;
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

	private void setColors(Diagram d, ColorTarget colorTarget, ElementColor color) {
		switch (colorTarget) {
		case BACKGROUND:
			logger.debug("onSelection background {}...", d);
			if (d.canSetBackgroundColor()) {
		  	// Color newbg = new Color(color.getRr(), color.getGg(), color.getBb(), color.getOpacity());
		  	Color newbg = color.getBackgroundColor();
				d.setBackgroundColor(color.getBackgroundColor());
	    	Color borderColor = ColorHelpers.borderColorByBackground(newbg.red, newbg.green, newbg.blue);
	    	// Color newbordercolor = new Color(rgb.red, rgb.green, rgb.blue, rgb.a);
	      d.setBorderColor(borderColor);
			}
		
			if (!"transparent".equals(d.getTextAreaBackgroundColor())) {
		  	// can set text color only if element has some color as text background
		  	// this is due to theme switching, e.g. actor text element is on always on top of 
		  	// board background color
				d.setTextColor(color.getTextColor());
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
		if (popup.isShowing()) {
			hide();
		}
	}
	
	private void hide() {
		popup.hide();
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
    codeMirror.setText("");
		modifiedAtLeastOnce = false;
	}

	private void applyTextToDiagram(boolean forceApplyAlways) {
	  boolean editorIsOpen = editorContext.isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN);
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

    selectedDiagram.setText(codeMirror.getText(), textEditX, textEditY);

    modifiedAtLeastOnce = true;
    sendBuffer();
    // synchronous version, starts to lag with long text
    // editorCommon.fireChangedWithRelatedRelationships(selectedDiagram);
	}
	
	private void sendBuffer() {
		if (!sending) {
			buffer.text = codeMirror.getText();
			buffer.diagram = selectedDiagram;

			// do not send update on every key stroke
			sending = true;
			sendScheduled();
		} else {
			// ensure sending... try at least once more
//			onceMore = true;
			if (!bufferTextIsSent()) {
				// do not send if check says it is already sent
				// this is due to automatic chile text element delete when text is empty
				// text is no longer found for undo/redo
				buffer.text = codeMirror.getText();
				buffer.diagram = selectedDiagram;
			} else {
				buffer.text = "";
				buffer.diagram = null;
			}
		}
	}

	private void sendScheduled() {
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
			@Override
			public boolean execute() {
				_doSend();		    
				boolean doitagain = !bufferTextIsSent();
				return doitagain;
			}
		}, 1500);
	}

	private void _doSend() {
		if (buffer.diagram != null && !bufferTextIsSent()) {
			lastSentText = buffer.diagram.getText(textEditX, textEditY);
			Properties.this.editorCommon.fireChangedWithRelatedRelationships(buffer.diagram);
	    sending = false;
		}
  }

	private boolean bufferTextIsSent() {
	  return buffer.text.equals(lastSentText);
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

	private void setTextCoordinatesAndShowEditor(int screenX, int screenY, int x, int y) {
		textEditX = screenX;
		textEditY = screenY;
		showEditor(selectedDiagram, selectedDiagram.getText(textEditX, textEditY), x, y, false);
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

		if (surface.getScaleFactor() < 0.7 && !UiUtils.isMobile()) {
			// zoom to mouse position
			// not when using iPad (mobile) focus goes wrong
			editorContext.getEventBus().fireEvent(
				new SurfaceScaleEvent(Constants.ZOOM_DEFAULT_INDEX, true)
			);
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

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				codeMirror.setHeight(MeasurementPanel.getOffsetHeight());
			}
		});
	}

	private void setTextAreaHeight() {
		if (selectedDiagram != null) {
			if (selectedDiagram.supportsOnlyTextareaDynamicHeight()) {
				// in case dynamically resized text should use measurement panel!!!
				setMeasurementPanelText(codeMirror.getText());
			} else {
				int rows = rows(codeMirror.getText());
				int dFontSize = selectedDiagram.getFontSize() != null ? selectedDiagram.getFontSize() : 12;
				int lheight = lineHeight(dFontSize);
				int textAreaHeight = lheight * rows;
				codeMirror.setHeight(textAreaHeight + lheight);
			}
		}
	}

	private void _setTextAreaSize(Diagram diagram) {
		// no need to hide text any longer since markdown editor hides the text with background color.
		// diagram.hideText();
		
		MatrixPointJS point = MatrixPointJS.createUnscaledPoint(diagram.getTextAreaLeft(), diagram.getTextAreaTop(), surface.getScaleFactor());
		int x = point.getX() + surface.getRootLayer().getTransformX() + surface.getAbsoluteLeft();
		int y = point.getY() + surface.getRootLayer().getTransformY() + surface.getAbsoluteTop();

		popup.setPopupPosition(x, y);

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


		double scaleFactor = surface.getScaleFactor();
		
		int dFontSize = diagram.getFontSize() != null ? diagram.getFontSize() : 12;
		int fontSize = ((int) (dFontSize * scaleFactor));
		codeMirror.setFontSize(fontSize + "px");
		codeMirror.setLineHeight(lineHeight(fontSize) + "px");

		codeMirror.setWidth((int) (diagram.getTextAreaWidth() * scaleFactor ));
		setTextAreaHeight();
		popup.setContentWidth((int) (diagram.getTextAreaWidth() * scaleFactor));

		if (surface.getScaleFactor() > 1) {
			String paddingTop = ((int) (2 * surface.getScaleFactor())) + "";
		}

		codeMirror.setTextAlign(diagram.getTextAreaAlign());
		
		if (surface.getScaleFactor() != 1.0f && !"transparent".equals(diagram.getTextAreaBackgroundColor())) {
			codeMirror.setBackgroundColor("#" + diagram.getBackgroundColor());
		}
	}

	private int lineHeight(int fontSize) {
		return ((int) ((fontSize + 5) * surface.getScaleFactor()));
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
		return !bufferTextIsSent();
	}

}
