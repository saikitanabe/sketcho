package net.sevenscales.editor.api.dojo;

import java.util.List;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.IModelingPanel;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.api.ToolFrame;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.LongPressHandler;
import net.sevenscales.editor.api.event.DiagramElementAddedEvent;
import net.sevenscales.editor.api.event.DiagramElementAddedEventHandler;
import net.sevenscales.editor.api.event.RelationshipNotAttachedEvent;
import net.sevenscales.editor.api.event.SurfaceScaleEvent;
import net.sevenscales.editor.api.event.SurfaceScaleEventHandler;
import net.sevenscales.editor.api.impl.DragAndDropHandler;
import net.sevenscales.editor.api.impl.ModelingPanelEventHandler;
import net.sevenscales.editor.api.impl.SurfaceEventWrapper;
import net.sevenscales.editor.api.impl.TouchDragAndDrop;
import net.sevenscales.editor.api.ot.OTBuffer;
import net.sevenscales.editor.content.ui.IModeManager;
import net.sevenscales.editor.content.ui.ScaleSlider;
import net.sevenscales.editor.content.ui.UiClickContextMenu;
import net.sevenscales.editor.content.ui.UiContextMenu;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramSelectionHandler;
import net.sevenscales.editor.diagram.KeyEventListener;
import net.sevenscales.editor.diagram.RelationshipDragEndHandler;
import net.sevenscales.editor.diagram.utils.UiUtils;
import net.sevenscales.editor.gfx.base.GraphicsEvent;
import net.sevenscales.editor.gfx.domain.IGraphics;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

class ModelingPanel extends HorizontalPanel implements IModelingPanel {
	private static final SLogger logger = SLogger.createLogger(ModelingPanel.class);
	
	private SurfaceHandler surface;
	private int minimumHeight = 700;
	private int minimumWidth = 500;
	private ToolFrame toolFrame;
	
	private boolean inDialog;
	private UIObject _parent;
	private int prevScrollLeft;
	private int prevScrollTop;
	private TouchDragAndDrop touchManager;
//	private boolean initializing = true;
	
	private DragAndDropHandler dragAndDropHandler;

  private ScaleSlider scaleSlider;
	
	public ModelingPanel(UIObject parent, int width, int height, boolean editable,
			IModeManager modeManager, EditorContext editorContext, boolean inDialog, boolean autohide, OTBuffer otBuffer, Boolean superQuickMode) {
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
		surface.init(width, height, editable, modeManager, true, editorContext, otBuffer);

		Tools.create(surface, superQuickMode);
		
		editorContext.getEventBus().addHandler(SurfaceScaleEvent.TYPE, new SurfaceScaleEventHandler() {
			@Override
			public void on(SurfaceScaleEvent event) {
				final float[] factors = new float[]{0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.4f};
				float factor = factors[event.getScaleFactor()];
//				surface.invertScale();
			  surface.scale(factor);
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
			this.toolFrame = new ToolFrame(surface, 700, modeManager, editorContext, autohide, otBuffer);
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
		
		dragAndDropHandler = new DragAndDropHandler(surface, toolFrame.getToolbar());
		touchManager = new TouchDragAndDrop(dragAndDropHandler, toolFrame.getToolbar().getHasTouchStartHandlers());
		new SurfaceEventWrapper(surface, dragAndDropHandler);
		// 
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
		
		// add dom handler on the root panel, then drag and drop works safely
		RootPanel.get().addDomHandler(dragAndDropHandler, MouseMoveEvent.getType());
		RootPanel.get().addDomHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				com.google.gwt.user.client.Event e = Event.as(event.getNativeEvent());
				int keys = e.getShiftKey() ? IGraphics.SHIFT : 0;
				if (surface.getElement().isOrHasChild(Element.as(event.getNativeEvent().getEventTarget()))) {
					surface.onMouseDown((GraphicsEvent) e, keys);
					event.getNativeEvent().preventDefault();
				} else if (toolFrame.getToolbar().getElement().isOrHasChild(Element.as(event.getNativeEvent().getEventTarget()))) {
					toolFrame.getToolbar().onMouseDown((GraphicsEvent) e, keys);
				}
			}
		}, MouseDownEvent.getType());
		RootPanel.get().addDomHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				com.google.gwt.user.client.Event e = Event.as(event.getNativeEvent());
				if (surface.getElement().isOrHasChild(Element.as(event.getNativeEvent().getEventTarget()))) {
					surface.onMouseUp((GraphicsEvent) e);
				} else if (toolFrame.getToolbar().getElement().isOrHasChild(Element.as(event.getNativeEvent().getEventTarget()))) {
					toolFrame.getToolbar().onMouseUp((GraphicsEvent) e);
				}
			}
		}, MouseUpEvent.getType());
		
		// native preview handler doesn't work with resize helpers for some reason
		// on mouse down..., would need to study more...
		// this commit is last to have routed through native preview handler
		// but basically it is just to copy above code for cases (e6e97d9a499118749dbbc4600aa72f12d2558670)
		// Event.addNativePreviewHandler(new NativePreviewHandler() {
		//   public void onPreviewNativeEvent(NativePreviewEvent event) {
	 //        case Event.ONMOUSEDOWN: {
	 //        case Event.ONMOUSEUP: {

		new ModelingPanelEventHandler(surface, toolFrame);
		
		// click menu is not used at the moment...
		if (editable) {
			new LongPressHandler(surface);
			new UiContextMenu(surface, editorContext, surface.getSelectionHandler());
			new UiClickContextMenu(surface);
		}

		init(this);
	}

	private native void init(ModelingPanel me)/*-{
		$wnd.isEditorOpen = function() {
			return me.@net.sevenscales.editor.api.dojo.ModelingPanel::isEditorOpen()();
		}
	}-*/;

	private boolean isEditorOpen() {
		return surface.getEditorContext().isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN);
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
    surface.getRootLayer().setTransform(0, 0);
    scaleSlider.reset();
  }

  public Widget getWidget() {
  	return this;
  }

}
