package net.sevenscales.editor.uicomponents.uml;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import net.sevenscales.domain.CommentDTO;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.CommentThreadDeletedEvent;
import net.sevenscales.editor.api.event.CommentThreadModifiedOutsideEvent;
import net.sevenscales.editor.api.event.PotentialOnChangedEvent;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.Theme.ElementColorScheme;
import net.sevenscales.editor.content.ui.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.diagram.shape.CommentShape;
import net.sevenscales.editor.diagram.shape.CommentThreadShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.utils.CommentList2;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IChildElement;
import net.sevenscales.editor.gfx.domain.IContainer;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IParentElement;
import net.sevenscales.editor.gfx.domain.IPath;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.gfx.domain.PointDouble;
import net.sevenscales.editor.gfx.domain.SegmentPoint;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;
import net.sevenscales.editor.uicomponents.TextElementVerticalFormatUtil;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;

public class CommentThreadElement extends AbstractDiagramItem implements SupportsRectangleShape, IParentElement {
	private static final SLogger logger = SLogger.createLogger(CommentThreadElement.class);

//	private Rectangle rectSurface;
//  private IPolyline boundary;
	private IRectangle boundary;
	public static int MINIMUM_WIDTH = 250;
	public static int MINIMUM_HEIGHT = 40;
	public static int MARGIN_TOP = 18;
	public static int MARGIN_BOTTOM = 16;

	private CommentThreadShape shape;
	private Point coords = new Point();
  // utility shape container to align text and make separators
  private List<IShape> innerShapes = new ArrayList<IShape>();
  private IGroup group;
//  private int[] points;
//  private static final int FOLD_SIZE = 10;
//  private IPolyline fold;
  private IPath tape;
//  private IImage leftShadow;
//  private IImage rightShadow;
//  private IImage topBlur;
  private TextElementVerticalFormatUtil textUtil;
  private CommentList2 comments;
  private boolean sorting;

  private enum SortState {
  	RESIZE_CHILDREN, SORT;
  }

  private SortState sortState = SortState.RESIZE_CHILDREN;
  
  private static final int LEFT_SHADOW_LEFT = 6; 
  private static final int LEFT_SHADOW_HEIGHT = 41; 
  private static final int RIGHT_SHADOW_LEFT = 44; 
  private static final int RIGHT_SHADOW_HEIGHT = 40; 

  private IPath.PathTransformer pathTransformer = new IPath.PathTransformer() {
  	public String getShapeStr(int dx, int dy) {
  		return calcShape(getRelativeLeft() + dx, getRelativeTop() + dy, getWidth(), getTop());
  	}
  };

  
	public CommentThreadElement(ISurfaceHandler surface, CommentThreadShape newShape, String text, 
										 Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
		super(editable, surface, backgroundColor, borderColor, textColor, item);
		this.shape = newShape;

		comments = new CommentList2();
		getDiagramItem().annotate();
		
		group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    // group.setAttribute("cursor", "default");
    
    // TODO, implement shadows using svg
//    leftShadow = IShapeFactory.Util.factory(editable)
//    		.createImage(group, shape.rectShape.left - LEFT_SHADOW_LEFT, shape.rectShape.top + shape.rectShape.height - LEFT_SHADOW_HEIGHT, 50, 50, 
//    			 surface.getEditorContext().get(EditorProperty.RESOURCES_PATH) + "images/shadow-left.png");
//    rightShadow = IShapeFactory.Util.factory(editable)
//    		.createImage(group, shape.rectShape.left + shape.rectShape.width - RIGHT_SHADOW_LEFT, shape.rectShape.top + shape.rectShape.height - RIGHT_SHADOW_HEIGHT, 50, 50, 
//    				surface.getEditorContext().get(EditorProperty.RESOURCES_PATH) + "images/shadow-right2.png");

    applyShadowVisiblity();

//		points = new int[]{shape.rectShape.left, shape.rectShape.top, 
//                       shape.rectShape.left+shape.rectShape.width-FOLD_SIZE, shape.rectShape.top,
//                       shape.rectShape.left+shape.rectShape.width, shape.rectShape.top+FOLD_SIZE,
//                       shape.rectShape.left+shape.rectShape.width, shape.rectShape.top+shape.rectShape.height,
//                       shape.rectShape.left, shape.rectShape.top+shape.rectShape.height,
//                       shape.rectShape.left, shape.rectShape.top};
		boundary = IShapeFactory.Util.factory(editable).createRectangle(group);
		boundary.setStrokeWidth(1);
		boundary.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);

//    topBlur = IShapeFactory.Util.factory(editable)
//    		.createImage(group, shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, shape.rectShape.height, "images/notetopblur.png");
		
//		int[] foldPoints = new int[]{points[2], points[3],
//		                             points[2], points[3]+FOLD_SIZE,
//		                             points[4], points[5]};
//		fold = IShapeFactory.Util.factory(editable).createPolyline(group, foldPoints);
//		fold.setStrokeWidth(3.0);
//    fold.setFill(255, 255, 255, 0.1);
    
		tape = IShapeFactory.Util.factory(editable).createPath(group, pathTransformer);
		if (surface.getEditorContext().isTrue(EditorProperty.SKETCHO_BOARD_MODE)) {
			tape.setFill(0xe2, 0x56, 0x56, 0.7);
			tape.setStroke(0xe2, 0x56, 0x56, 0.7);
		} else {
			tape.setFill(0xee, 0xee, 0xee, 0.8);
			tape.setStroke(0xee, 0xee, 0xee, 0.8);
		}

		tape.setStrokeWidth(1);

		// tape.setSvgFixX(-17);
		// tape.setSvgFixY(-5);

//		addObserver(rectSurface.getRawNode(), AbstractDiagramItem.EVENT_DOUBLE_CLICK);
		// addEvents(tape);
		addEvents(boundary);
    
    resizeHelpers = ResizeHelpers.createResizeHelpers(surface);

		addMouseDiagramHandler(this);
		
//    shapes.add(topBlur);
//    shapes.add(leftShadow);
//    shapes.add(rightShadow);
    shapes.add(boundary);
    shapes.add(tape);
//    shapes.add(fold);
    
    textUtil = new TextElementVerticalFormatUtil(this, hasTextElement, group, surface.getEditorContext());

    setReadOnly(!editable);
    setShape(shape.rectShape.left, shape.rectShape.top, 
             shape.rectShape.width, shape.rectShape.height);
    
    setText(text);
    
    // setBorderColor(borderWebColor);
    
		restoreHighlighColor(null);

    super.constructorDone();
	}
	
	private void applyShadowVisiblity() {
//		leftShadow.setVisibility(!(backgroundColor.opacity == 0));
//		rightShadow.setVisibility(!(backgroundColor.opacity == 0));
	}

	public void setShape(int left, int top, int width, int height) {
		height = MINIMUM_HEIGHT > height ? MINIMUM_HEIGHT : height;

//    points = new int[]{left, top, 
//        left+width-FOLD_SIZE, top,
//        left+width, top+FOLD_SIZE,
//        left+width, top+height,
//        left, top+height,
//        left, top};

		boundary.setShape(left, top, width, height, 20);
		
//		leftShadow.setShape(left - LEFT_SHADOW_LEFT, top + height - LEFT_SHADOW_HEIGHT, 50, 50);
//		rightShadow.setShape(left + width - RIGHT_SHADOW_LEFT, top + height - RIGHT_SHADOW_HEIGHT, 50, 50);

//    boundary.setShape(points);
    
		// tape.resetAllTransforms();
		// tape.setShape(left + width / 2 - 15, top - 4, 30, 15, 0);
    tape.setShape(calcShape(left, top, width, height));
    // tape.rotate(-3, getCenterX(), getLeft() + (getWidth() / 2));
    
    textUtil.setTextShape();
    super.applyHelpersShape();
	}

	private String calcShape(int left, int top, int width, int height) {
		int tapewidth = 30;
		int tapeheight = 17;
		int slope = -2;

		int leftx = left + width / 2 - tapewidth / 2;
		int lefty = top - 7;
		int rightx = leftx + tapewidth + slope;
		int righty = lefty + slope;
		int bottomrightx = leftx + tapewidth;
		int bottomrighty = righty + tapeheight + slope;
		int bottomleftx = leftx + slope;
		int bottomlefty = lefty + tapeheight - 3;

		int[] fixedAnchorPoints = new int[]{leftx, lefty, 
																				rightx, righty, 
																				bottomrightx, bottomrighty,
																				bottomleftx, bottomlefty};
		String result = "";
		String prefix = "";
		for (int i = 0; i < fixedAnchorPoints.length; i += 2) {
			if (i == 0) {
				prefix = "M";
			} else {
				prefix = "";
			}
			result += prefix + fixedAnchorPoints[i] + ","+ fixedAnchorPoints[i + 1] + " ";
		}
		result += " z";
		return result;
	}

	
  // nice way to clearly separate interface methods :)
  private HasTextElement hasTextElement = new AbstractHasTextElement(this) {
    public int getWidth() {
    	return boundary.getWidth();
    }
    public int getX() {
    	return boundary.getX();
    }
    public int getY() {
    	return boundary.getY();
    }
    public int getHeight() {
    	return boundary.getHeight();
    }
    public void removeShape(IShape shape) {
      group.remove(shape);
      shapes.remove(shape);
    }

    public String getLink() {
      return CommentThreadElement.this.getLink();
    }

    public boolean isAutoResize() {
    	return false;
      // return CommentThreadElement.this.isAutoResize();
    }

    public void resize(int x, int y, int width, int height) {
      // CommentThreadElement.this.resize(x, y, width, height);
      // fireSizeChanged();
    }

    public void setLink(String link) {
      CommentThreadElement.this.setLink(link);      
    }
    public boolean supportsTitleCenter() {
      return false;
    }
    public int getTextMargin() {
      return 21;
    }
    public boolean forceAutoResize() {
      return true;
    }
    
    public GraphicsEventHandler getGraphicsMouseHandler() {
      return CommentThreadElement.this;
    }
		@Override
		public Color getTextColor() {
			return textColor;
		};

  };

	protected IShape createElement(IContainer surface) {
		return IShapeFactory.Util.factory(editable).createRectangle(surface);
	}

	public Point getDiffFromMouseDownLocation() {
		return new Point(diffFromMouseDownX, diffFromMouseDownY);
	}
	
	public void accept(ISurfaceHandler surface) {
	  super.accept(surface);
		surface.makeDraggable(this);
	}

	public boolean allowToDelete() {
		return comments.size() == 0;
	}

	public void removeFromParent() {
		super.removeFromParent();
		removeConnections(true);
    surface.getEditorContext().getEventBus().fireEvent(new CommentThreadDeletedEvent(this));
	}

	protected void removeComment(CommentElement child) {
		comments.remove(child);
		if (comments.size() == 0) {
			surface.getSelectionHandler().addToBeRemovedCycle(this);
			// TODO remove all connected relationships if relationship doesn't contain any text!
			removeFromParent();
		} else {
			// resizeWithKnownChildren();
			setShape(getRelativeLeft(), getRelativeTop(), getWidth(), getHeight() - child.getHeight());
	    _sort(false);
		}
    // surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(this));

    // TODO change comments position after this and minus removed heidht from top
    // - find, eachFromIndex(index, c.setTop(-height))
	}

	private void removeConnections(boolean include) {
		for (AnchorElement ae : getAnchors()) {
			if (ae.getRelationship() != null && ae.getRelationship().noText()) {
				// logger.debug("ae.getHandler().connection().getTextLabel() {}", ae.getRelationship().getTextLabel());
				if (include) {
					surface.getSelectionHandler().addToBeRemovedCycle(ae.getRelationship());
				} else {
					surface.getSelectionHandler().remove(ae.getRelationship(), true);
				}
			}
		}
	}
	
	@Override
	public int getRelativeLeft() {
		return boundary.getX();
	}
	
	@Override
	public int getRelativeTop() {
		return boundary.getY();
	}
	
	@Override
	public int getWidth() {
//		return boundary.getArrayValue(4) - boundary.getArrayValue(0);
		return boundary.getWidth();
	}
	
	@Override
	public int getHeight() {
//    return boundary.getArrayValue(9) - boundary.getArrayValue(1);
		return boundary.getHeight();
	}
	
	// public void setHeight(int height) {
	// 	setShape(getRelativeLeft(), getRelativeTop(), getWidth(), height);
	// 	dispatchAndRecalculateAnchorPositions();
	// }
	
  @Override
	public boolean onArea(int left, int top, int right, int bottom) {
    int x = getLeft();
    int y = getTop();
    int bx = getLeft() + getWidth();
    int by = getTop() + getHeight();
		return AreaUtils.onArea(x, y, bx, by, left, top, right, bottom);
	}

	public String getText() {
		return textUtil.getText();
	}

	public void doSetText(String newText) {
    textUtil.setText(newText, editable);
	}
	
  protected CommentThreadElement createDiagram(ISurfaceHandler surface, CommentThreadShape newShape,
      String text, boolean editable) {
    return new CommentThreadElement(surface, newShape, text, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, new DiagramItemDTO());
  }
	
//////////////////////////////////////////////////////////////////////
	
	public boolean onResizeArea(int x, int y) {
		return resizeHelpers.isOnResizeArea();
	}

	@Override
	public void resizeStart() {
		hideChildren();
	}

	public boolean resize(Point diff) {
		return resize(getRelativeLeft(), getRelativeTop(), getWidth() + diff.x, getHeight() + diff.y);
	}

	protected boolean resize(int left, int top, int width, int height) {
	  if (width >= MINIMUM_WIDTH && height >= MINIMUM_HEIGHT) {
      setShape(left, top, width, height);
      // rotateDegrees is not used on old comments, and maybe these could be completely removed
      // at some point...
      connectionHelpers.setShape(getLeft(), getTop(), getWidth(), height, /*rotateDegrees*/ null);

      dispatchAndRecalculateAnchorPositions();
      return true;
	  }
	  return false;
	}

	public void resizeEnd() {
		textUtil.setText(getText(), editable, true);

		showChildren();
		resizeWithKnownChildren();
		// surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(new ArrayList(comments)));
	}

	private void hideChildren() {
		for (CommentElement comment : comments) {
			comment.setVisible(false);
		}
	}

	private void showChildren() {
		for (CommentElement comment : comments) {
			comment.setVisible(true);
		}
	}

	public void resizeWithKnownChildren() {
		// all children are known so children resize can be synchronous
    resizeChildren();
    // start async from SORT state
    sortState = SortState.SORT;
		// queueSorting();
	}

	public void restoreSize() {
		_sort(false);
	}

	public Info getInfo() {
    super.fillInfo(shape);
		return this.shape;
	}

	@Override
  protected ElementColorScheme getRefrenceColorScheme() {
    return Theme.getCommentThreadColorScheme(); // Theme.getColorScheme(ThemeName.PAPER);
  }

	@Override
  protected ElementColorScheme getCurrentColorScheme() {
    return Theme.getCommentThreadColorScheme(); // Theme.getCurrentColorScheme();
  }

	public void setShape(Info shape) {
		// TODO Auto-generated method stub
		
	}

	public void setReadOnly(boolean value) {
	  super.setReadOnly(value);
	}
	
  public String getDefaultRelationship() {
    return "--";
  }
  
  @Override
  public UMLDiagramType getDiagramType() {
  	return UMLDiagramType.NOTE;
  }

  @Override
	public void setBackgroundColor(Color color) {
  	super.setBackgroundColor(color);
  	
//		leftShadow.setVisibility(!(opacity == 0));
//		rightShadow.setVisibility(!(opacity == 0));
  	boundary.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
  }
  
  @Override
	public void setTextColor(int red, int green, int blue) {
  	super.setTextColor(red, green, blue);
  	textUtil.applyTextColor();
  	for (CommentElement c : comments) {
  		if (c.getBackgroundColorAsColor().opacity == 0) {
	  		c.setTextColor(red, green, blue);
  		}
  	}
  }

  @Override
  protected void doSetShape(int[] shape) {
  	setShape(shape[0], shape[1], shape[2], shape[3]);
  }

  @Override
	public void restoreHighlighColor(Color color) {
		// need to override since default border color restoration
		// doesn't handle transparent border!
		boundary.setStroke(0, 0, 0, 0);
	}

  @Override
  public void setHighlight(boolean highlight) {
  	super.setHighlight(highlight);
  	if (!highlight) {
  		restoreHighlighColor(null);
  	}
  }

  @Override
	public void setHighlightColor(Color color) {
		boundary.setStroke(color);
	}
  	
	@Override
	public IGroup getGroup() {
		return group;
	}
	
	@Override
	protected TextElementFormatUtil getTextFormatter() {
		return textUtil;
	}
	
	@Override
	public boolean supportsOnlyTextareaDynamicHeight() {
		return true;
	}
	
	public void hideProxy() {
//		group.setVisible(true);
		setVisible(true);
		showText();
	}
	
	public void showProxy() {
//		group.setVisible(false);
		setVisible(false);
		hideText();
		boundary.setVisibility(true);
	}
	
  @Override
  public boolean supportsTextEditing() {
  	return true;
  }
  
  // @Override
  // public int getTextAreaWidth() {
  // 	return getWidth() - 21;
  // }
  
  // @Override
	// public int getMeasurementAreaWidth() {
  // 	return getWidth() - textUtil.getMargin() - 3;
	// }
  
	// @Override
	// public int getTextAreaLeft() {
	// 	return getLeft() + 7;
	// }

	public void createComment(String text) {
		Theme.ElementColorScheme commentColor = Theme.getCommentColorScheme();

		// surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, true);
		CommentDTO jsComment = new CommentDTO(this.getDiagramItem().getClientId(), surface.getEditorContext().getCurrentUser(), surface.getEditorContext().getCurrentUserDisplayName());
		CommentElement commentElement = new CommentElement(surface,
        new CommentShape(getRelativeLeft(), getRelativeTop() + getHeight(), getWidth(), 1),
        text,
        commentColor.getBackgroundColor().create(), 
        commentColor.getBorderColor().create(), 
        commentColor.getTextColor().create(), 
        true, 
        this, 
        jsComment,
        new DiagramItemDTO());

		// get current user to show quickly
    // commentElement.setUser(surface.getEditorContext().getCurrentUser());
		// surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, false);

		surface.addAsSelected(commentElement, true);

		// need to sort deferred to attach newly created comment
		// before it's size can be calculated.
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				sort();
			}
		});
	}

	public void accept(CommentElement comment) {
		comments.add(comment);
		// queueSorting();
	}

	public void sort() {
		_sort(false);
	}

	// private void queueSorting() {
	// 	queueSorting(false);
	// }
	// private void queueSorting(final boolean outside) {
	// 	_sort(false);

	// 	// if (!sorting) {
	// 	// 	// queue sorting outside this loop
	// 	// 	sorting = true;
	// 	// 	Scheduler.get().scheduleDeferred(new ScheduledCommand() {
	// 	// 		@Override
	// 	// 		public void execute() {
	// 	// 			// free lock
	// 	// 			sorting = false;
	// 	// 			_sort(false);

	// 	// 			switch (sortState) {
	// 	// 				case RESIZE_CHILDREN: {
	// 	// 					resizeChildrenState();
	// 	// 					break;
	// 	// 				}
	// 	// 				case SORT: {
	// 	// 					sortState(outside);
	// 	// 					break;
	// 	// 				}
	// 	// 			}
	// 	// 		}
	// 	// 	});
	// 	// }
	// }

	private void resizeChildrenState() {
		resizeChildren();
		sortState = SortState.SORT;
		// now children has correct position and size 
		// recursively sort again to position comments and thread correctly
		// according to comments real size (need to redraw => async)
		// queueSorting();
	}

	private void sortState(boolean outside) {
		_sort(false);
		// next time start from resize children
		sortState = SortState.RESIZE_CHILDREN;
		if (outside) {
			surface.getEditorContext().getEventBus().fireEvent(new CommentThreadModifiedOutsideEvent(this));
		}
	}

	public void resizeChildren() {
		_sort(true);
	}

	private void _sort(boolean resizeChild) {
		int left = getRelativeLeft();
		int top = getRelativeTop();
		int currentHeight = MARGIN_TOP;
		int width = getWidth();

		int height = currentHeight;
		int size = comments.size();
		CommentElement last = null;
		for (int i = 0; i < size; ++i) {
			CommentElement ce = comments.get(i);

			int commentHeight = ce.getHeight();
			ce.setShape(left, top + height, width, commentHeight);
			if (resizeChild) {
				ce.resizeText();
				commentHeight = ce.getHeight();
			} else {
				if (ce.isVisible() != isVisible()) {
					ce.setVisible(isVisible());
				}
			}
			height += commentHeight + 1;
			last = ce;
		}

		if (last != null) {
			last.hideBottomLine();
		}


		// if (height != currentHeight) {
		// 	logger.debug("CommentThreadElement height changed current {} new {}...", currentHeight, height);
		setThreadHeight(height + MARGIN_BOTTOM);
		// }
	}

	private int calculateHeight() {
		int result = MARGIN_TOP;
		for (CommentElement ce : comments) {
			result += ce.getHeight() + 1;
		}
		return result + MARGIN_BOTTOM;
	}

	private void setThreadHeight(int height) {
		setShape(getRelativeLeft(), getRelativeTop(), getWidth(), height);
		dispatchAndRecalculateAnchorPositions();
	}

 	public List<? extends Diagram> getChildElements() {
    return comments;
  }

  @Override
  public void attachedRelationship(AnchorElement anchorElement) {
    if (anchorElement.getRelationship() != null) {
      // if attached to this comment thread
      // all relationships connected to comments are annotations
      anchorElement.getRelationship().annotate();
    }
  }

  public void childResized(CommentElement comment) {
  	setThreadHeight(calculateHeight());
  	if (!surface.getEditorContext().isTrue(EditorProperty.ON_SURFACE_LOAD)) {
			surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(this));
  	}
  }

	// @Override
	// public int getTextAreaTop() {
	// 	return getTop();
	// }

	public void setIncrementHeight(int value) {
		setShape(getRelativeLeft(), getRelativeTop(), getWidth(), getHeight() + value);
	}

	@Override
	public void copyFrom(IDiagramItemRO diagramItem) {
		super.copyFrom(diagramItem);
		_sort(true);
		// queueSorting(true);
	}

	public void markDone() {
		IDiagramItem di = getDiagramItem();
		if (!di.isResolved()) {
			Set<Diagram> resolved = new HashSet<Diagram>();
			di.resolve();
			resolved.add(this);

			for (Diagram ce : getChildElements()) {
				ce.getDiagramItem().resolve();
				resolved.add(ce);
				ce.setVisible(false);
			}
			// in case connections contain text those are kept on board as annotations
			removeConnections(false);
			surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(resolved));

			// remove thread with comments, user need to restore thread from comments dialog or using undo/redo
			// hmm, for this session it needs to be hidden with children since undo is modify operation
			// and cannot deal with removal
			// removeFromParent();
			setVisible(false);
		}
	}

	/**
	* To prevent accidental annotation removal.
	*/
	@Override
	public void annotate() {
	}
	@Override
	public void unannotate() {
	}

	@Override
	public void addChild(IChildElement child) {
	}
  @Override
  public void removeChild(IChildElement child) {
  }
	@Override
	public Diagram asDiagram() {
		return this;
	}
	@Override
	public List<IChildElement> getChildren() {
		return null;
	}
	@Override
	public SegmentPoint findClosestSegmentPointIndex(int x, int y) {
		return null;
	}
	@Override
	public PointDouble getPoint(SegmentPoint segmentPoint) {
		return null;
	}
	@Override
	public void moveChild(IChildElement child) {
	}

}
