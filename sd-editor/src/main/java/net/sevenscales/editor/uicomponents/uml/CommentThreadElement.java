package net.sevenscales.editor.uicomponents.uml;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.PotentialOnChangedEvent;
import net.sevenscales.editor.api.event.CommentThreadModifiedOutsideEvent;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.CommentThreadShape;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.diagram.utils.CommentList2;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IContainer;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IImage;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IPath;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.uicomponents.Point;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;
import net.sevenscales.editor.uicomponents.TextElementVerticalFormatUtil;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;
import net.sevenscales.editor.diagram.shape.CommentShape;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.CommentDTO;
import net.sevenscales.domain.utils.SLogger;


import com.google.gwt.core.client.GWT;

public class CommentThreadElement extends AbstractDiagramItem implements SupportsRectangleShape {
	private static final SLogger logger = SLogger.createLogger(CommentThreadElement.class);

//	private Rectangle rectSurface;
//  private IPolyline boundary;
	private IRectangle boundary;
	public static int MINIMUM_WIDTH = 250;
	public static int MINIMUM_HEIGHT = 40;

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
  		return calcShape(doGetLeft() + dx, doGetTop() + dy, getWidth(), getTop());
  	}
  };

  
	public CommentThreadElement(ISurfaceHandler surface, CommentThreadShape newShape, String text, 
										 Color backgroundColor, Color borderColor, Color textColor, boolean editable) {
		super(editable, surface, backgroundColor, borderColor, textColor);
		this.shape = newShape;

		comments = new CommentList2();
		
		group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    group.setAttribute("cursor", "default");
    
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
    
    setBorderColor(borderWebColor);
    
		restoreHighlighColor();

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
  private HasTextElement hasTextElement = new AbstractHasTextElement() {
    public void addShape(IShape shape) {
      shapes.add(shape);    
    }
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
		public String getTextColorAsString() {
			return "#" + textColor.toHexString();
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

	public void removeFromParent() {
		for (CommentElement ce : comments) {
			ce.removeFromParent();
		}

		surface.remove(this);
    surface.remove(group.getContainer());
	}

	protected void removeComment(CommentElement child) {
		comments.remove(child);
		if (comments.size() == 0) {
			surface.getSelectionHandler().addToBeRemovedCycle(this);
			removeFromParent();
		} else {
			// resizeWithKnownChildren();
			setShape(doGetLeft(), doGetTop(), getWidth(), getHeight() - child.getHeight());
	    _sort(false);
		}
    // surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(this));

    // TODO change comments position after this and minus removed heidht from top
    // - find, eachFromIndex(index, c.setTop(-height))
	}
	
	@Override
	protected int doGetLeft() {
		return boundary.getX();
	}
	
	@Override
	protected int doGetTop() {
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
	
	public void setHeight(int height) {
		setShape(doGetLeft(), doGetTop(), getWidth(), height);
		dispatchAndRecalculateAnchorPositions();
	}
	
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

	public void setText(String newText) {
    textUtil.setText(newText, editable);
	}
	
  protected CommentThreadElement createDiagram(ISurfaceHandler surface, CommentThreadShape newShape,
      String text, boolean editable) {
    return new CommentThreadElement(surface, newShape, text, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable);
  }
	
//////////////////////////////////////////////////////////////////////
	
	public boolean onResizeArea(int x, int y) {
		return resizeHelpers.isOnResizeArea();
	}

	public void resizeStart() {
		hideChildren();
	}

	public boolean resize(Point diff) {
		return resize(doGetLeft(), doGetTop(), getWidth() + diff.x, getHeight() + diff.y);
	}

	protected boolean resize(int left, int top, int width, int height) {
	  if (width >= MINIMUM_WIDTH && height >= MINIMUM_HEIGHT) {
      setShape(left, top, width, height);
      connectionHelpers.setShape(getLeft(), getTop(), getWidth(), height);

      dispatchAndRecalculateAnchorPositions();
      return true;
	  }
	  return false;
	}

	public void resizeEnd() {
		textUtil.setText(getText(), editable, true);

		showChildren();
		resizeWithKnownChildren();
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
		queueSorting();
	}

	public void restoreSize() {
		_sort(false);
	}

	public Info getInfo() {
    super.fillInfo(shape);
		return this.shape;
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
	public void setBackgroundColor(int red, int green, int blue, double opacity) {
  	super.setBackgroundColor(red, green, blue, opacity);
  	
//		leftShadow.setVisibility(!(opacity == 0));
//		rightShadow.setVisibility(!(opacity == 0));
  	boundary.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
  }
  
  @Override
	public void setTextColor(int red, int green, int blue) {
  	super.setTextColor(red, green, blue);
  	textUtil.applyTextColor();
  }

  @Override
  protected void doSetShape(int[] shape) {
  	setShape(shape[0], shape[1], shape[2], shape[3]);
  }

  @Override
	public void restoreHighlighColor() {
		// need to override since default border color restoration
		// doesn't handle transparent border!
		boundary.setStroke(0, 0, 0, 0);
	}

  @Override
  public void setHighlight(boolean highlight) {
  	super.setHighlight(highlight);
  	if (!highlight) {
  		restoreHighlighColor();
  	}
  }

  @Override
	public void setHighlightColor(String color) {
		boundary.setStroke(color);
	}
  
	// @Override
	// public void resetTransform() {
	// 	// unrotate before saving
	// 	tape.unrotate(-2, getLeft(), getTop());
	// 	group.resetAllTransforms();
	// }
	
	@Override
		public void setVisible(boolean visible) {
			super.setVisible(visible);
			applyShadowVisiblity();
		}
	
	// @Override
	// public void saveLastTransform(int dx, int dy) {
	// 	super.saveLastTransform(dx, dy);
	// 	tape.rotate(-2, getLeft(), getTop());
	// 	textUtil.show();
	// }
	
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
  
  @Override
  public int getTextAreaWidth() {
  	return getWidth() - 21;
  }
  
  @Override
	public int getMeasurementAreaWidth() {
  	return getWidth() - hasTextElement.getTextMargin() - 3;
	}
  
	@Override
	public int getTextAreaLeft() {
		return getLeft() + 7;
	}

	public void createComment(String text) {
		Theme.ElementColorScheme commentColor = Theme.getCommentColorScheme();

		surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, true);
		CommentDTO jsComment = new CommentDTO(this.getDiagramItem().getClientId(), surface.getEditorContext().getCurrentUser(), surface.getEditorContext().getCurrentUserDisplayName());
		CommentElement commentElement = new CommentElement(surface,
        new CommentShape(doGetLeft(), doGetTop() + getHeight(), getWidth(), 1),
        text,
        commentColor.getBackgroundColor().create(), 
        commentColor.getBorderColor().create(), 
        commentColor.getTextColor().create(), 
        true, 
        this, 
        jsComment);

		// get current user to show quickly
    // commentElement.setUser(surface.getEditorContext().getCurrentUser());
		surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, false);

		surface.addAsSelected(commentElement, true);
	}

	public void accept(CommentElement comment) {
		comments.add(comment);
		queueSorting();
	}

	private void queueSorting() {
		queueSorting(false);
	}
	private void queueSorting(final boolean outside) {
		if (!sorting) {
			// queue sorting outside this loop
			sorting = true;
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					// free lock
					sorting = false;
					_sort(false);

					switch (sortState) {
						case RESIZE_CHILDREN: {
							resizeChildrenState();
							break;
						}
						case SORT: {
							sortState(outside);
							break;
						}
					}
				}
			});
		}
	}

	private void resizeChildrenState() {
		resizeChildren();
		sortState = SortState.SORT;
		// now children has correct position and size 
		// recursively sort again to position comments and thread correctly
		// according to comments real size (need to redraw => async)
		queueSorting();
	}

	private void sortState(boolean outside) {
		_sort(false);
		// next time start from resize children
		sortState = SortState.RESIZE_CHILDREN;
		if (outside) {
			surface.getEditorContext().getEventBus().fireEvent(new CommentThreadModifiedOutsideEvent(this));
		}
	}

	private void resizeChildren() {
		_sort(true);
	}

	private void _sort(boolean resizeChild) {
		int left = doGetLeft();
		int top = doGetTop();
		int currentHeight = 18;
		int width = getWidth();

		int height = currentHeight;
		int size = comments.size();
		CommentElement last = null;
		for (int i = 0; i < size; ++i) {
			CommentElement ce = comments.get(i);

			int commentHeight = ce.getHeight();
			// if ( ce.doGetTop() != (top + height) ) {
				ce.setShape(left, top + height, width, commentHeight);
			if (resizeChild) {
				ce.resizeText();
			} else {
				ce.setVisible(true);
			}
			// }
			height += commentHeight + 1;
			last = ce;
		}

		if (last != null) {
			last.hideBottomLine();
		}

		// if (height != currentHeight) {
		// 	logger.debug("CommentThreadElement height changed current {} new {}...", currentHeight, height);
		setShape(left, top, width, height + 16);
		// }
	}

 	public List<? extends Diagram> getChildElements() {
    return comments;
  }

  public void childResized(CommentElement comment, int diff) {
  	if (!surface.getEditorContext().isTrue(EditorProperty.ON_SURFACE_LOAD)) {
  		repositionCommentsAfter(comment, diff);
  		if (diff != 0) {
				setShape(doGetLeft(), doGetTop(), getWidth(), getHeight() + diff);
				surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(this));
  		}
  	}
  }

  private void repositionCommentsAfter(CommentElement comment, int diff) {
  	boolean reposition = false;
  	for (int i = 0; i < comments.size(); ++i) {
  		CommentElement ce = comments.get(i);
  		if (reposition) {
  			ce.setTopDiff(diff);
  		}

  		if (ce == comment) {
  			reposition = true;
  		}

  	}
  }

	@Override
	public int getTextAreaTop() {
		return getTop();
	}

	public void setIncrementHeight(int value) {
		setShape(doGetLeft(), doGetTop(), getWidth(), getHeight() + value);
	}

	@Override
	public void copyFrom(IDiagramItemRO diagramItem) {
		super.copyFrom(diagramItem);
		queueSorting(true);
	}

}
