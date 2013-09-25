package net.sevenscales.editor.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.event.ColorSelectedEvent;
import net.sevenscales.editor.api.event.ColorSelectedEventHandler;
import net.sevenscales.editor.api.event.DiagramElementAddedEvent;
import net.sevenscales.editor.api.event.DiagramElementAddedEventHandler;
import net.sevenscales.editor.api.event.PotentialOnChangedEvent;
import net.sevenscales.editor.api.event.RelationshipTypeSelectedEvent;
import net.sevenscales.editor.api.event.RelationshipTypeSelectedEventHandler;
import net.sevenscales.editor.api.event.ShowDiagramPropertyTextEditorEvent;
import net.sevenscales.editor.api.event.ShowDiagramPropertyTextEditorEventHandler;
import net.sevenscales.editor.api.impl.TouchHelpers;
import net.sevenscales.editor.content.ui.CustomPopupPanel;
import net.sevenscales.editor.content.ui.LineSelections.RelationShipType;
import net.sevenscales.editor.content.utils.ColorHelpers;
import net.sevenscales.editor.diagram.ClickDiagramHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.Diagram.SizeChangedHandler;
import net.sevenscales.editor.diagram.DiagramSelectionHandler;
import net.sevenscales.editor.diagram.SelectionHandler;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.uicomponents.AnchorElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementVerticalFormatUtil;
import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.editor.uicomponents.uml.CommentThreadElement;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.EditorCommon;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.NativeEvent;
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

public class Properties extends SimplePanel implements DiagramSelectionHandler, ClickDiagramHandler, SizeChangedHandler, IEditor {
	private static final SLogger logger = SLogger.createLogger(Properties.class);

	private static final String TAB_AS_SPACES = "    ";
	private static final String PROPERTIES_EDITOR_STYLE = "properties-TextArea2";
	
  private Map<ISurfaceHandler,Boolean> surfaces = new HashMap<ISurfaceHandler, Boolean>();
	private TextArea textArea;
	private Diagram selectedDiagram;
	private EditorContext editorContext;
	private SelectionHandler selectionHandler;
	private CustomPopupPanel popup;
	private ISurfaceHandler surface;
	private Diagram lastDiagramAdded;
	private boolean sending;
	private int textEditX;
	private int textEditY;
	private CommentEditor commentEditor;
	private EditorCommon editorCommon;
	
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
				surface.getEditorContext().set(EditorProperty.PROPERTY_EDITOR_SELECT_ALL_ENABLED, true);
				if (event.getPoint() != null) {
					setTextCoordinatesAndShowEditor(event.getPoint().getScreenX(), event.getPoint().getScreenY(),
																					event.getPoint().getX(), event.getPoint().getY());
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
		VerticalPanel panel = new VerticalPanel();
		panel.setHeight("100%");

		editorCommon = new EditorCommon(surface, new EditorCommon.HideEditor() {
			public void hide() {
				Properties.this.hide();
			}
		});
		
		this.textArea = new TextArea();

		textArea.addKeyDownHandler(new KeyDownHandler() {
		  @Override
		  public void onKeyDown(KeyDownEvent event) {
		    if (event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
		      event.preventDefault();
		      event.stopPropagation();
					if(event.getSource() instanceof TextArea) {
		        TextArea ta = (TextArea) event.getSource();
		        int index = ta.getCursorPos();
		        String text = ta.getText();
		        ta.setText(text.substring(0, index) 
		                   + TAB_AS_SPACES + text.substring(index));
		        ta.setCursorPos(index + TAB_AS_SPACES.length());
		      }		      
		    }
		  }
		});
//		this.textArea.setWidth("170px");
//		this.textArea.setHeight(height + "px");
		textArea.setStyleName(PROPERTIES_EDITOR_STYLE);
//		textArea.setVisibleLines(20); 
//		textArea.setCharacterWidth(30);
		textArea.addKeyUpHandler(new KeyUpHandler() {
		  public void onKeyUp(KeyUpEvent event) {
  	    if (selectedDiagram != null) {
  	      // enable auto resize if element supports that
  	      // when text is inserted by user.
  	      selectedDiagram.setAutoResize(true);
  	      setSelectedDiagramText(textArea.getText());
          selectedDiagram.setAutoResize(false);
        }
		  }
		});
		
		editorContext.getEventBus().addHandler(RelationshipTypeSelectedEvent.TYPE, new RelationshipTypeSelectedEventHandler() {
			private void changeSelected(Diagram diagram, RelationshipTypeSelectedEvent event) {
				if (diagram instanceof Relationship2) {
					Relationship2 rel = (Relationship2) diagram;
					String text = diagram.getText();
//					System.out.println("caps: " + ((Relationship2) selectedDiagram).getRelationshipShape().type + " => " + event.getRelationshipType().getValue());
					if (event.getRelationshipType().equals(RelationShipType.REVERSE)) {
						rel.reverse();
				    Properties.this.editorContext.getEventBus().fireEvent(new PotentialOnChangedEvent(rel));
					} else {
						String what = ((Relationship2) diagram).getRelationshipShape().type.getValue();
						String to = event.getRelationshipType().getValue();
						System.out.println("what => to : " + what + " => " + to);
						if (!"".equals(text)) {
							text = text.replace(what, to);
						} else {
							// fallback to just set the value
							text = new String(to);
						}
						rel.setText(text);
				    Properties.this.editorContext.getEventBus().fireEvent(new PotentialOnChangedEvent(rel));
					}
				}
			}
			@Override
			public void onSelection(RelationshipTypeSelectedEvent event) {
				for (Diagram d : Properties.this.selectionHandler.getSelectedItems()) {
					changeSelected(d, event);
				}
			}
		});
		
		editorContext.getEventBus().addHandler(ColorSelectedEvent.TYPE, new ColorSelectedEventHandler() {
			@Override
			public void onSelection(ColorSelectedEvent event) {
				logger.debug("onSelection selected {}", Properties.this.selectionHandler.getSelectedItems());
				for (Diagram d : Properties.this.selectionHandler.getSelectedItems()) {
					switch (event.getColorTarget()) {
					case BACKGROUND:
						logger.debug("onSelection background...");
						if (d.canSetBackgroundColor()) {
						  Color newbg = new Color(event.getColor().getRr(), event.getColor().getGg(), event.getColor().getBb(), event.getColor().getOpacity());
							d.setBackgroundColor(newbg);
					    String borderWebColor = ColorHelpers.borderColorByBackground(newbg.red, newbg.green, newbg.blue);
				      d.setBorderColor(borderWebColor);
						}
						
						if (!"transparent".equals(d.getTextAreaBackgroundColor())) {
						  // can set text color only if element has some color as text background
						  // this is due to theme switching, e.g. actor text element is on always on top of 
						  // board background color
  						d.setTextColor(event.getColor().getR(), event.getColor().getG(), event.getColor().getB());
						}
						logger.debug("onSelection background... done");
						break;
					case BORDER:
						if (d.canSetBackgroundColor()) {
							d.setBorderColor(event.getColor().getBorderColor4Web());
						}
						break;
					}
				}
				
				Properties.this.editorContext.getEventBus().fireEvent(new PotentialOnChangedEvent(Properties.this.selectionHandler
						.getSelectedItems()));
			}
		});

		commentEditor = new CommentEditor(surface);

		popup = new CustomPopupPanel(textArea);
		popup.setStyleName("propertyPopup");
		popup.setWidget(textArea);
		// autohide is not enabled since property editor is closed manually and autohide causes problems
		popup.setAutoHideEnabled(false);
//		popup.setAutoHideEnabled(true);
		popup.setAutoHideOnHistoryEventsEnabled(false);
//		popup.setAnimationEnabled(true);
		popup.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				logger.info("close properties editor...");
				applyTextToDiagram();
				Properties.this.editorCommon.fireEditorClosed();
				if (selectedDiagram == null) {
					return;
				}
				
				if (!TouchHelpers.isSupportsTouch()) {
					// selection stays if not unselected separately on non touch device
					selectedDiagram.unselect();
				}
//				selectedDiagram.setVisible(true);
//				Properties.this.surface.scale(currentScale);
//				Properties.this.surface.scaleDiagram(selectedDiagram);
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

		setWidget(panel);
	}
	
	private void hide() {
		popup.hide();
	}
	
	private void setSelectedDiagramHeight() {
		selectedDiagram.setHeight(MeasurementPanel.getMeasurementPanel().getOffsetHeight() + TextElementVerticalFormatUtil.DEFAULT_VERTICAL_TEXT_MARGIN);
	}
	
	public void addSurface(ISurfaceHandler surfaceHandler, boolean modifiable) {
	  surfaces.put(surfaceHandler, modifiable);
	  surfaceHandler.addSelectionListener(this);
//	  if (modifiable) {
	  // currently it is allowed to modify diagram item text, because otherwise 
	  // would need to implement text area handling
	  // this might be even good idea that user can try out things without touching the modeling area...
//	    surfaceHandler.addKeyEventHandler(this);
//	  }
	}

	public void selected(List<Diagram> senders) {
		if (senders.size() == 1) {
			// apply text first to current diagram, otherwise on close
			// tries to apply text to new diagram and old ref is gone
			applyTextToDiagram();
			selectedDiagram = senders.get(0);
		}
		
//		textArea.setText(sender.getText());
//    textArea.setFocus(true);
//    
//		Info info = sender.getInfo();
//		popup.setPopupPosition(surface.getAbsoluteLeft() - 180, surface.getAbsoluteTop() + info.getTop());
//		popup.show();
//    
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
	  boolean reset = true;
	  for (ISurfaceHandler s : surfaces.keySet()) {
	    if (s.getSelectionHandler().getSelectedItems().size() > 0) {
	      reset = false;
	      break;
	    }
	  }
	   
	  applyTextToDiagram();
	  clean();
//	  if (reset) {
//	    selectedDiagram = null;
//	    textArea.setText("");
//	  }
	}
	
	private void clean() {
    selectedDiagram = null;
    textArea.setText("");
	}

	private void applyTextToDiagram() {
	  boolean editorIsOpen = editorContext.isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN);
		if (editorIsOpen && selectedDiagram != null && selectedDiagram.supportsOnlyTextareaDynamicHeight()) {
			// TODO check later if problems when not hiding/showin any longer; note element doesn't dynamically change size
//			selectedDiagram.setVisible(true);
			textArea.setVisible(false);
			
			// reset text edit location
			textEditX = 0;
			textEditY = 0;
//			selectedDiagram.endTextEdit();
		}
		
		if (editorIsOpen && selectedDiagram != null) {
			selectedDiagram.showText();
		}
	}
	
	public void unselect(Diagram sender) {
	}

//  private void setFocus(final boolean b) {
//  	Scheduler.get().scheduleDeferred(new ScheduledCommand() {
//			@Override
//			public void execute() {
//		    textArea.setFocus(b);
//		    textArea.setCursorPos(textArea.getText().length());
//			}
//		});
//  }
  
//  private void setSelectedDiagramText() {
//		selectedDiagram.setAutoResize(true);
//		setSelectedDiagramText(textArea.getText());
//		selectedDiagram.setAutoResize(false);
//  }
  
	private void setSelectedDiagramText(String text) {
		logger.debug("setSelectedDiagramText {}...", text);
		if (text.equals(selectedDiagram.getText())) {
			// do not allow sending same content again
			return;
		}
		
		if (selectedDiagram.supportsOnlyTextareaDynamicHeight()) {
	    // DIV tryouts,
			setMeasurementPanelText(text);
			
			// TODO is this still needed?
//			selectedDiagram.setVisible(false);
		}

    selectedDiagram.setText(textArea.getText(), textEditX, textEditY);
//    fireChanged(selectedDiagram);
    sendBuffer();
	}
	
	private void sendBuffer() {
		if (!sending) {
			buffer.text = textArea.getText();
			buffer.diagram = selectedDiagram;

			// do not send update on every key stroke
			sending = true;
			sendScheduled();
		} else {
			// ensure sending... try at least once more
//			onceMore = true;
			buffer.text = textArea.getText();
			buffer.diagram = selectedDiagram;
		}
	}

	private void sendScheduled() {
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
			@Override
			public boolean execute() {
				lastSentText = buffer.diagram.getText(textEditX, textEditY);
				
				Properties.this.editorCommon.fireChanged(buffer.diagram);
		    sending = false;
		    
//		    System.out.println("buffer != lastSentText: " + buffer + " " + lastSentText + " " + buffer != lastSentText);
				boolean doitagain = !bufferTextIsSent();
				return doitagain;
			}
		}, 1500);
	}
	
	private boolean bufferTextIsSent() {
	  return buffer.text.equals(lastSentText);
	}

	@Override
	public void onClick(Diagram sender, int x, int y, int keys) {
//		selectedDiagram = sender;
	}

	@Override
	public void onDoubleClick(Diagram sender, final MatrixPointJS point) {
		if (sender != null) {
			selectedDiagram = sender;
			
			// hacking focus set on text area chrome
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
//					selectedDiagram.startTextEdit(point.getX(), point.getY());
					setTextCoordinatesAndShowEditor(point.getScreenX(), point.getScreenY(), point.getX(), point.getY());
				}
			});
		}
	}

	private void setTextCoordinatesAndShowEditor(int screenX, int screenY, int x, int y) {
		textEditX = screenX;
		textEditY = screenY;
		showEditor(selectedDiagram, selectedDiagram.getText(textEditX, textEditY), x, y, false);
	}

	private void showEditor(Diagram diagram, String text, final int left, final int top, boolean justCreated) {
		logger.info("SHOW EDITOR...");
		if (!selectedDiagram.supportsTextEditing()) {
			// if diagram text editing is not supported => return
			return;
		}

		if (selectedDiagram instanceof CommentThreadElement) {
			commentEditor.showEditor(selectedDiagram);
			return;
		}
		
		selectedDiagram.hideConnectionHelpers();
		
		diagram.setSizeChangedHandlerByText(this);
		// diagram might be under popup and doesn't receive mouse up and drag will continue 
		// without this.
		surface.getMouseDiagramManager().getDragHandler().releaseDrag();
		lastDiagramAdded = null; // reset state because editing has not been done right away
														 // at least this is very precise way and perhaps easy to understand...
		
		setTextAreaText(text);
		_setTextAreaSize(diagram);
		textArea.setCursorPos(textArea.getText().length());
//		setFocus(true);

//		currentScale = surface.getScaleFactor();
//		surface.invertScale();
		
//		MatrixPointJS point = MatrixPointJS.createUnscaledPoint(diagram.getTextAreaLeft(), diagram.getTextAreaTop(), surface.getScaleFactor());
//		int x = point.getX() + surface.getRootLayer().getTransformX();
//		int y = point.getY() + surface.getRootLayer().getTransformY();
//
//		surface.invertScaleDiagram(diagram, diagram.getTextAreaLeft(), diagram.getTextAreaTop());

		this.editorCommon.fireEditorOpen();
		popup.selectAll(justCreated);
		popup.show();
		
	  
//	  if (editorContext.get(EditorProperty.PROPERTY_EDITOR_SELECT_ALL_ENABLED).equals(true)) {
//	  	// property used => need to be reset if needed again
//			surface.getEditorContext().set(EditorProperty.PROPERTY_EDITOR_SELECT_ALL_ENABLED, false);
//			textArea.setFocus(true);
//			textArea.setSelectionRange(0, textArea.getText().length());
//	  	Scheduler.get().scheduleDeferred(new ScheduledCommand() {
//				@Override
//				public void execute() {
//					if (textArea.getText().length() > 0) {
//						logger.debug("selected whole text...");
//						textArea.setFocus(true);
//						textArea.setSelectionRange(0, textArea.getText().length());
//					}
//				}
//			});
//	  }

//	  	Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
//				@Override
//				public boolean execute() {
//					if (textArea.getText().length() > 0) {
//						logger.debug("selected whole text...");
//						textArea.setFocus(true);
//						textArea.selectAll();
//						textArea.setSelectionRange(0, textArea.getText().length());
//					}
//					return !(textArea.getCursorPos() == textArea.getText().length()); 
//				}
//			}, 50);

//	  }
	}

	private void setTextAreaText(String text) {
		if (selectedDiagram == null) {
			return;
		}
		
		textArea.setText(text);
		
		if (selectedDiagram.supportsOnlyTextareaDynamicHeight()) {
			setMeasurementPanelText(text);
		}
	}

	private void setMeasurementPanelText(String text) {
		if (selectedDiagram == null) {
			return;
		}

		MeasurementPanel.setText(text, selectedDiagram.getMeasurementAreaWidth());
		MeasurementPanel.setPosition(selectedDiagram.getLeft() + selectedDiagram.getWidth() + 20, selectedDiagram.getTop());
		// textArea.getElement().getStyle().setHeight(MeasurementPanel.getOffsetHeight(), Unit.PX);
		setTextAreaHeight(MeasurementPanel.getOffsetHeight());
		setSelectedDiagramHeight();
	}

	private void _setTextAreaSize(Diagram diagram) {
		// no need to hide text any longer since markdown editor hides the text with background color.
		diagram.hideText();
		
		MatrixPointJS point = MatrixPointJS.createUnscaledPoint(diagram.getTextAreaLeft(), diagram.getTextAreaTop(), surface.getScaleFactor());
		int x = point.getX() + surface.getRootLayer().getTransformX() + surface.getAbsoluteLeft();
		int y = point.getY() + surface.getRootLayer().getTransformY() + surface.getAbsoluteTop();

		popup.setPopupPosition(x, y);

		textArea.setVisible(true);

		if ("transparent".equals(diagram.getTextAreaBackgroundColor())) {
			textArea.getElement().getStyle().setBackgroundColor(Theme.getCurrentThemeName().getBoardBackgroundColor());
		} else {
			textArea.getElement().getStyle().setBackgroundColor("#" + diagram.getBackgroundColor());
		}

		textArea.getElement().getStyle().setColor("#" + diagram.getTextColor());
		textArea.getElement().getStyle().setWidth(diagram.getTextAreaWidth(), Unit.PX);
		setTextAreaHeight(diagram.getTextAreaHeight());
		textArea.getElement().getStyle().setProperty("textAlign", diagram.getTextAreaAlign());
		
		textArea.getElement().getStyle().setBorderColor("#bbb");
		textArea.getElement().getStyle().setBorderWidth(1, Unit.PX);
		textArea.getElement().getStyle().setBorderStyle(BorderStyle.DASHED);


// 		if (surface.getScaleFactor() != 1.0f && !"transparent".equals(diagram.getTextAreaBackgroundColor())) {
// //			diagram.setVisible(false);
// 			textArea.getElement().getStyle().setBackgroundColor("#" + diagram.getBackgroundColor());
// 		}
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

	private void setTextAreaHeight(int height) {
		// String[] rows = textArea.getText().split("\n");
		int rows = rows(textArea.getText());
		logger.debug("rows.length: {}", rows);
		// this is some magical approximation of the textarea height
		// it is at least the size of measurement panel, but extra
		// line breaks in editor might not increase measurment panel height
		// therefore need to add something based on line breaks...
		int textAreaHeight = height + rows / 4 * TextElementFormatUtil.ROW_HEIGHT;
		textArea.getElement().getStyle().setHeight(textAreaHeight, Unit.PX);
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
