package net.sevenscales.editor.api.dojo;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

import net.sevenscales.domain.constants.Constants;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.IBirdsEyeView;
import net.sevenscales.editor.api.IModelingPanel;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.LongPressHandler;
import net.sevenscales.editor.api.ToolBar;
import net.sevenscales.editor.api.ToolFrame;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.api.event.DiagramElementAddedEvent;
import net.sevenscales.editor.api.event.DiagramElementAddedEventHandler;
import net.sevenscales.editor.api.event.RelationshipNotAttachedEvent;
import net.sevenscales.editor.api.event.SurfaceScaleEvent;
import net.sevenscales.editor.api.event.SurfaceScaleEventHandler;
import net.sevenscales.editor.api.impl.ModelingPanelEventHandler;
import net.sevenscales.editor.api.ot.OTBuffer;
import net.sevenscales.editor.api.ot.OperationTransaction;
import net.sevenscales.editor.content.ui.IModeManager;
import net.sevenscales.editor.content.ui.ScaleSlider;
import net.sevenscales.editor.content.ui.UiClickContextMenu;
import net.sevenscales.editor.content.ui.UiContextMenu;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramSelectionHandler;
import net.sevenscales.editor.diagram.KeyEventListener;
import net.sevenscales.editor.diagram.RelationshipDragEndHandler;
import net.sevenscales.editor.diagram.utils.UiUtils;
import net.sevenscales.editor.uicomponents.uml.ShapeCache;

class ModelingPanel extends HorizontalPanel implements IModelingPanel, IBirdsEyeView {
	private static final SLogger logger = SLogger.createLogger(ModelingPanel.class);

	static {
		SLogger.addFilter(ModelingPanel.class);
	}
	
	private SurfaceHandler surface;
	private int minimumHeight = 700;
	private int minimumWidth = 500;
	private ToolFrame toolFrame;
	
	private boolean inDialog;
	private UIObject _parent;
	private int prevScrollLeft;
	private int prevScrollTop;
//	private boolean initializing = true;
  private ScaleSlider scaleSlider;
	
	public ModelingPanel(UIObject parent, int width, int height, boolean editable,
			IModeManager modeManager, EditorContext editorContext, boolean inDialog, boolean autohide, OTBuffer otBuffer, OperationTransaction operationTransaction, Boolean superQuickMode) {
		this._parent = parent;
		this.inDialog = inDialog;
		setStyleName("ModelingPanel");
		setSpacing(0);
		
		logger.debug("ModelingPanel...");

		// For now it is decided to set width through properties
		// dynamic size might have difficulties containing all the
		// objects in absolute positions (some objects might get hided on some
		// sizes)

		// could calculate minimum size according to objects positions
		// if read only size can be calculated based on lowest x and y and highest x
		// and y
		// on edit mode object positions are not relocated
		// on read only mode object positions are relocated and lowest x and y are
		// diminished
		// on each element

		// now default implementation is in firefox impl.
		surface = GWT.create(SurfaceHandlerImplFirefox.class);
		surface.setName(SurfaceHandler.DRAWING_AREA);
		surface.init(width, height, editable, modeManager, true, editorContext, otBuffer, operationTransaction, this);

		surface.addLoadEventListener(new net.sevenscales.editor.api.SurfaceLoadedEventListener() {
			public void onLoaded() {
				surface.addToDefs(ShapeCache.icons());
			}
		});

		Tools.create(surface.getEditorContext(), superQuickMode);
		
		editorContext.getEventBus().addHandler(SurfaceScaleEvent.TYPE, new SurfaceScaleEventHandler() {
			@Override
			public void on(SurfaceScaleEvent event) {
        if (!event.isResetScale()) {
          double factor = Constants.ZOOM_FACTORS[event.getScaleFactor()];
  //				surface.invertScale();
          surface.scale(factor, event.isWheel(), event.getMiddleX(), event.getMiddleY());
        } else {
          resetScaleAt(event.getMiddleX(), event.getMiddleY());
        }
//			  int dx = 0;
//			  int dy = 0;
//		  	double val = ScaleHelpers.scaleValue(1, factor);
//			  if (factor > 1) {
//			  	dx -= val;
//			  	dy -= val;
//			  } else if (factor < 1) {
//			  	dx += val;
//			  	dy += val;
//			  }
//	      surface.getRootLayer().setTransform(surface.getRootLayer().getTransformX() + dx, 
//	      																		surface.getRootLayer().getTransformY() + dy);
			}
    });
    
		editorContext.getEventBus().addHandler(
				RelationshipNotAttachedEvent.TYPE, 
				new RelationshipDragEndHandler(surface));
		
		final SimplePanel fp = new SimplePanel();
		fp.setWidget(surface);
		add(fp);
		// if (editable) {
			surface.setStyleName("sd-editor-surface-modeling-area");
			this.toolFrame = new ToolFrame(surface, 700, modeManager, editorContext, autohide, otBuffer, operationTransaction);
			surface.setPropertiesTextArea(toolFrame.getProperties());
			editorContext.setPropertiesArea(toolFrame.getProperties());

			if (editorContext.isTrue(EditorProperty.SKETCHO_BOARD_MODE)) {
				editorContext.registerAndAddToRootPanel(toolFrame);
//				RootPanel.get().add(toolFrame);
			} else {
				add(toolFrame);
			}

			fp.setStyleName("no-show-focus");
//			fp.addKeyDownHandler(new KeyDownHandler() {
//				@Override
//				public void onKeyDown(KeyDownEvent event) {
//					// disable Confluence default keys when having focus on
//					// sketcho diagram area; otherwise will move somewhere else
////					ContentEventUtils.hanleKeyEvent(event);
//					toolFrame.getProperties().showEditor(event);
//				}
//			});
			
//			fp.addDoubleClickHandler(surface);

			// fix: intially set focus so it doesn't jump there later
//			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
//				@Override
//				public void execute() {
//					Window.scrollTo(0, ModelingPanel.this._parent.getElement().getAbsoluteTop());
//				}
//			});
//			Window.scrollTo(0, ModelingPanel.this._parent.getElement().getAbsoluteTop());
			
			setCellHeight(toolFrame, "100%");
			toolFrame.addSelectionHandler(new DiagramSelectionHandler() {
				public void selected(List<Diagram> sender) {
					// this makes quick insert from toolbar slightly more usable
					// focus is removed from surface
					surface.getSelectionHandler().unselectAll();
				}

				public void unselectAll() {
				}

				public void unselect(Diagram sender) {
				}
			});
		// }
		// RootPanel.get().add((new MainMenu(surface)));

		this.scaleSlider = new ScaleSlider(surface);

//		});
//		surface.addSelectionListener(new DiagramSelectionHandler() {
//			@Override
//			public void unselectAll() {
//			}
//			@Override
//			public void unselect(Diagram sender) {
//			}
//			@Override
//			public void selected(List<Diagram> sender) {
//				setModelingAreaFocus(fp);
//			}
//		});
		
//  	editorContext.getEventBus().addHandler(EditDiagramPropertiesEndedEvent.TYPE, new EditDiagramPropertiesEndedEventHandler() {
//			@Override
//			public void on(EditDiagramPropertiesEndedEvent event) {
//				if (UiUtils.isFirefox()) {
//					// firefox needs to have focus or back space might be still working
//					fp.setFocus(true);
//				}
//			}
//		});

		editorContext.getEventBus().addHandler(DiagramElementAddedEvent.TYPE, new DiagramElementAddedEventHandler() {
			@Override
			public void onAdded(DiagramElementAddedEvent event) {
				// need to set focus back also when element is added
				// after poup add focus is not modeling panel
//				if (!initializing) {
//					setModelingAreaFocus(fp);
//				}
			}
		});

		// fix for firefox 6 on mouse enter event
		// if drag has been started outside current element
		// when crossing another element no mouse enter event is fired
		// in this case modeling panel handles both toolbar and drawing area
		// and can recognize when mouse enters drawing area
		
		// native preview handler doesn't work with resize helpers for some reason
		// on mouse down..., would need to study more...
		// this commit is last to have routed through native preview handler
		// but basically it is just to copy above code for cases (e6e97d9a499118749dbbc4600aa72f12d2558670)
		// Event.addNativePreviewHandler(new NativePreviewHandler() {
		//   public void onPreviewNativeEvent(NativePreviewEvent event) {
	 //        case Event.ONMOUSEDOWN: {
	 //        case Event.ONMOUSEUP: {

		RootPanel.get().add((new ToolBar(surface)));
		new ModelingPanelEventHandler(surface, toolFrame);
		
		// click menu is not used at the moment...
		if (editable) {
			// <<<<< long press disabled - 29.10.2015 Saiki T. 
			new UiContextMenu(surface, editorContext, surface.getSelectionHandler());
			new UiClickContextMenu(surface);
		}

		init(this);
		
		// >>>>>>>>>>> SOLU
		// scaleOnLoad(this);
		// <<<<<<<<<<< SOLU
  }
  
  private void resetScaleAt(int left, int top) {
    // 1. zero transform to make calculations easy
    surface.setTransform(0, 0);

    // 2. scale at 0,0
    // need to scale directly without setting scale slider
    // index and having call scale like that
    // because that comes later and iPad doesn't focus
    // editor any longer.
    double factor = Constants.ZOOM_FACTORS[Constants.ZOOM_DEFAULT_INDEX];
//				surface.invertScale();
    surface.scale(factor, false, left, top);

    // // 3. move mouse point to 0,0 then move half the screen size to right
    // // 		to center the mouse point
    double clientWidth = Window.getClientWidth();
    double clientHeight = Window.getClientHeight();

    int posx = (int) (-left + clientWidth / 2);
    int posy = (int) (-top + clientHeight / 2);
    surface.setTransform(posx, posy);
  }

	// >>>>>>>>>> SOLU
  private native void scaleOnLoad(ModelingPanel me)/*-{
    $wnd.boardReadyStream.onValue(function() {
      me.@net.sevenscales.editor.api.dojo.ModelingPanel::onBoardReady()();
    })
  }-*/;

  private void onBoardReady() {
  	surface.scale(2f, false, 0, 0);
  }
	// <<<<<<<<<< SOLU

	private native void init(ModelingPanel me)/*-{
		$wnd.isEditorOpen = function() {
			return me.@net.sevenscales.editor.api.dojo.ModelingPanel::isEditorOpen()();
		}

    $wnd.gwtGetSurfaceElement = function() {
      return me.@net.sevenscales.editor.api.dojo.ModelingPanel::gwtGetSurfaceElement()();
    }

    $wnd.gwtGetSurfaceRootLayer = function() {
      return me.@net.sevenscales.editor.api.dojo.ModelingPanel::gwtGetSurfaceRootLayer()();
    }
	}-*/;

	private boolean isEditorOpen() {
		return surface.getEditorContext().isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN);
	}

  private Element gwtGetSurfaceElement() {
    return surface.getElement();
  }

  private Element gwtGetSurfaceRootLayer() {
    if (surface.getRootLayer() != null) {
      return surface.getRootLayer().getElement();
    }

    return null;
  }

	public boolean isBirdsEyeViewOn() {
		return scaleSlider.getBirdsEyeView().isBirdsEyeViewOn();
	}

	public void off() {
		scaleSlider.getBirdsEyeView().off();
	}
	
	public ISurfaceHandler getSurface() {
		return this.surface;
	}

	public void addKeyEventHandler(KeyEventListener keyEventHandler) {
		surface.addKeyEventHandler(keyEventHandler);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		// surface.setVisible(visible);
		// if (toolFrame != null) {
		// toolFrame.setVisible(visible);
		// }
	}

	public void setSize(Integer width, Integer height) {
		surface.setSize(width, height);
	}

	public ToolFrame getToolFrame() {
		return toolFrame;
	}
	
	private void setModelingAreaFocus(FocusPanel fp) {
		// for some reason this doesn't work...
//		int left = Window.getScrollLeft();
//		int top = Window.getScrollTop();
//		fp.setFocus(true);
//		Window.scrollTo(left, top);
		if (UiUtils.isChrome()) {
			// chrome changes focus to input top unless having some hacking...
			setfocus(fp.getElement());
		} else {
			fp.setFocus(true);
		}
	}
	
	private native void setfocus(Element element)/*-{
		var x = $wnd.scrollX, y = $wnd.scrollY;
	  element.focus();
	  $wnd.scrollTo(x, y);
  }-*/;

  public void reset() {
    surface.reset();
    // surface.setTransform(0, 0);

		// >>>>>>>>>>> SOLU 14.11.2014 -- commented
    // scaleSlider.reset();
    // <<<<<<<<<<< SOLU 14.11.2014 -- commented
  }

  public Widget getWidget() {
  	return this;
  }

}
