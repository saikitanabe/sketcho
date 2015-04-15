package net.sevenscales.editor.uicomponents.uml;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

import net.sevenscales.editor.api.ActionType;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.CommentShape;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IContainer;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IImage;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.ILine;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IPath;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.gfx.domain.IChildElement;
import net.sevenscales.editor.gfx.domain.IParentElement;
import net.sevenscales.editor.gfx.domain.SegmentPoint;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;
import net.sevenscales.editor.uicomponents.TextElementVerticalFormatUtil;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.api.event.CommentDeletedEvent;
import net.sevenscales.domain.IDiagramItemRO;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.CommentDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.ElementType;

import com.google.gwt.core.client.GWT;

public class CommentElement extends AbstractDiagramItem implements SupportsRectangleShape, IChildElement {
	private static final SLogger logger = SLogger.createLogger(CommentElement.class);
	public static String TYPE = ElementType.COMMENT.getValue();

//	private Rectangle rectSurface;
//  private IPolyline boundary;
	private IRectangle boundary;
	// private ILine separator;
	private int minimumWidth = 25;
	private int minimumHeight = 25;
	private CommentShape shape;
	private Point coords = new Point();
  // utility shape container to align text and make separators
  private List<IShape> innerShapes = new ArrayList<IShape>();
  private IGroup group;
//  private int[] points;
//  private static final int FOLD_SIZE = 10;
//  private IPolyline fold;
//  private IImage leftShadow;
//  private IImage rightShadow;
//  private IImage topBlur;
  private TextElementVerticalFormatUtil title;
  private TextElementVerticalFormatUtil textUtil;
  private CommentThreadElement parentThread;
  
  private static final int LEFT_SHADOW_LEFT = 6; 
  private static final int LEFT_SHADOW_HEIGHT = 41; 
  private static final int RIGHT_SHADOW_LEFT = 44; 
  private static final int RIGHT_SHADOW_HEIGHT = 40; 

  private IPath.PathTransformer pathTransformer = new IPath.PathTransformer() {
  	public String getShapeStr(int dx, int dy) {
  		return calcShape(getRelativeLeft() + dx, getRelativeTop() + dy, getWidth(), getTop());
  	}
  };

  
	public CommentElement(ISurfaceHandler surface, CommentShape newShape, String text, 
										 Color backgroundColor, Color borderColor, Color textColor, boolean editable,
										 CommentThreadElement parentThread, CommentDTO commentData, IDiagramItemRO item) {
		super(editable, surface, backgroundColor, borderColor, backgroundColor.opacity == 0 ? parentThread.getTextColor() : textColor, item);
		this.shape = newShape;
		this.parentThread = parentThread;
		setDiagramItem(commentData);
		commentData.annotate();

		group = IShapeFactory.Util.factory(editable).createGroup(parentThread.getGroup());
		setVisible(false);
		// group = parentThread.getGroup();
    // // group.setAttribute("cursor", "default");
    
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
		// boundary.setStroke(textColor.red, textColor.green, textColor.blue, textColor.opacity);
		boundary.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);

		// separator = IShapeFactory.Util.factory(editable).createLine(group);
		// // #bbbbbb
		// separator.setStroke(borderColor.red, borderColor.green, borderColor.blue, borderColor.opacity);
		// separator.setStrokeWidth(1);
		
//    topBlur = IShapeFactory.Util.factory(editable)
//    		.createImage(group, shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, shape.rectShape.height, "images/notetopblur.png");
		
//		int[] foldPoints = new int[]{points[2], points[3],
//		                             points[2], points[3]+FOLD_SIZE,
//		                             points[4], points[5]};
//		fold = IShapeFactory.Util.factory(editable).createPolyline(group, foldPoints);
//		fold.setStrokeWidth(3.0);
//    fold.setFill(255, 255, 255, 0.1);
    
		// tape.setSvgFixX(-17);
		// tape.setSvgFixY(-5);

//		addObserver(rectSurface.getRawNode(), AbstractDiagramItem.EVENT_DOUBLE_CLICK);
		// addEvents(tape);
		addEvents(boundary);
    
    // disable resizing
    // resizeHelpers = ResizeHelpers.createResizeHelpers(surface);

		addMouseDiagramHandler(this);
		
//    shapes.add(topBlur);
//    shapes.add(leftShadow);
//    shapes.add(rightShadow);
    shapes.add(boundary);
    // shapes.add(separator);
//    shapes.add(fold);
    
    title = new TextElementVerticalFormatUtil(this, hasTitleTextElement, group, surface.getEditorContext());
    textUtil = new TextElementVerticalFormatUtil(this, hasTextElement, group, surface.getEditorContext());

		// title.setMarginTop(0);
		// title.setMarginLeft(6);
  //   textUtil.setMarginTop(20);
  //   textUtil.setMarginLeft(6);

    setReadOnly(!editable);
    setShape(shape.rectShape.left, shape.rectShape.top, 
             shape.rectShape.width, shape.rectShape.height);
    
    setTitle();
    setText(text);

    // setBorderColor(borderWebColor);

    // HACK! need to set border as transparent
    restoreHighlighColor();
    
    parentThread.accept(this);
    super.constructorDone();
	}
	
	private void applyShadowVisiblity() {
//		leftShadow.setVisibility(!(backgroundColor.opacity == 0));
//		rightShadow.setVisibility(!(backgroundColor.opacity == 0));
	}

	private boolean checkIfChanged(int left, int top, int width, int height) {
		if (getRelativeLeft() != left) {
			return true;
		}
		if (getRelativeTop() != top) {
			return true;
		}
		if (getWidth() != width) {
			return true;
		}
		if (getHeight() != height) {
			return true;
		}
		return false;
	}

	public void setShape(int left, int top, int width, int height) {
		// if (!checkIfChanged(left, top, width, height)) {
		// 	return;
		// }
//    points = new int[]{left, top, 
//        left+width-FOLD_SIZE, top,
//        left+width, top+FOLD_SIZE,
//        left+width, top+height,
//        left, top+height,
//        left, top};
		boundary.setShape(left, top, width, height, 0);
		// separator.setShape(left, top + height, left + width, top + height);
//		leftShadow.setShape(left - LEFT_SHADOW_LEFT, top + height - LEFT_SHADOW_HEIGHT, 50, 50);
//		rightShadow.setShape(left + width - RIGHT_SHADOW_LEFT, top + height - RIGHT_SHADOW_HEIGHT, 50, 50);

//    boundary.setShape(points);
        
    title.setTextShape();
    textUtil.setTextShape();
    super.applyHelpersShape();
	}

	public void resizeText() {
		setTitle();
		textUtil.setText(getText(), editable, true);
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

	private final int MARGIN_LEFT = 16;
	private final int MARGIN_TOP = 20;
  private HasTextElement hasTitleTextElement = new AbstractHasTextElement(this) {
    public int getWidth() {
    	return boundary.getWidth() - MARGIN_LEFT;
    }
    public int getX() {
    	return boundary.getX() + MARGIN_LEFT;
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
      return CommentElement.this.getLink();
    }

    public boolean isAutoResize() {
      return false;
    }

    public void resize(int x, int y, int width, int height) {
    }

    public void setLink(String link) {
    }
    public boolean supportsTitleCenter() {
      return false;
    }
    public int getTextMargin() {
      return 21;
    }
    public boolean forceAutoResize() {
      return false;
    }
    
    public GraphicsEventHandler getGraphicsMouseHandler() {
      return CommentElement.this;
    }
		@Override
		public Color getTextColor() {
			return textColor;
		};

  };
	
  // nice way to clearly separate interface methods :)
  private HasTextElement hasTextElement = new AbstractHasTextElement(this) {
    public int getWidth() {
    	return boundary.getWidth() - MARGIN_LEFT;
    }
    public int getX() {
    	return boundary.getX() + MARGIN_LEFT;
    }
    public int getY() {
    	return boundary.getY() + MARGIN_TOP;
    }
    public int getHeight() {
    	return boundary.getHeight();
    }
    public void removeShape(IShape shape) {
      group.remove(shape);
      shapes.remove(shape);
    }

    public String getLink() {
      return CommentElement.this.getLink();
    }

    public boolean isAutoResize() {
      return CommentElement.this.isAutoResize();
    }

    public void resize(int x, int y, int width, int height) {
      CommentElement.this.resize(x, y, width, height);
      fireSizeChanged();
    }

    public void resizeHeight(int height) {
    	setHeight(height);
			// setOverallHeight(height);

      // CommentElement.this.resize(getRelativeLeft(), getRelativeTop(), CommentElement.this.getWidth(), height);
      // fireSizeChanged();
    }

    public void setLink(String link) {
      CommentElement.this.setLink(link);      
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
      return CommentElement.this;
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

	public void removeFromParent() {
		super.removeFromParent();
		title.remove();
		parentThread.removeComment(this);
    surface.getEditorContext().getEventBus().fireEvent(new CommentDeletedEvent(this));
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

	public void setHeight(int height) {
		setHeightAddTitleHeight(height);
		dispatchAndRecalculateAnchorPositions();
    parentThread.childResized(this);
	}

	@Override
	public void editingEnded(boolean modified) {
		super.editingEnded(modified);
		parentThread.sort();
	}

	private void setHeightAddTitleHeight(int height) {
		// 3 some magic margin
		setShape(getRelativeLeft(), getRelativeTop(), getWidth(), height + (int) title.getTextHeight() + 3);
	}

	public void setTopDiff(int diff) {
		setShape(getRelativeLeft(), getRelativeTop() + diff, getWidth(), getHeight());
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

	public void doSetText(String newText) {
    textUtil.setText(newText, editable, true);
	}
	
  protected CommentElement createDiagram(ISurfaceHandler surface, CommentShape newShape,
      String text, boolean editable) {
  	// not supported for now
  	return null;
    // return new CommentElement(surface, newShape, text, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, parentThread);
  }
	
//////////////////////////////////////////////////////////////////////
	
	public boolean resize(Point diff) {
		return resize(getRelativeLeft(), getRelativeTop(), getWidth() + diff.x, getHeight() + diff.y);
	}

	protected boolean resize(int left, int top, int width, int height) {
	   if (width >= minimumWidth && height >= minimumHeight) {
       setShape(left, top, width, height);
       // connectionHelpers.setShape(getLeft(), getTop(), getWidth(), getHeight());
       dispatchAndRecalculateAnchorPositions();
       return true;
	   }
	   return false;
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
  	title.applyTextColor();
  }

	@Override
	public void restoreHighlighColor() {
		// need to override since default border color restoration
		// doesn't handle transparent border!
		boundary.setStroke(0, 0, 0, 0);
	}

	/**
	* Comment element doesn't support hightlight on relationship attach.
	* TODO better solution would be if diagram can say if it supports
	* attach or not.
	*/
  @Override
  public void setHighlight(boolean highlight) {
  }
  @Override
  public void setHighlightColor(Color color) {
    boundary.setStroke(color);
  }

  @Override
  public boolean supportsAlignHighlight() {
    return false;
  }

  @Override
  protected void doSetShape(int[] shape) {
  	setShape(shape[0], shape[1], shape[2], shape[3]);
  }
  
 //  @Override
	// public void setHighlightColor(String color) {
	// 	boundary.setStroke(color);
	// }
  
	// @Override
	// public void resetTransform() {
	// 	// unrotate before saving
	// 	tape.unrotate(-2, getLeft(), getTop());
	// 	group.resetAllTransforms();
	// }
	
	@Override
	public void setVisible(boolean visible) {
		boolean current = isVisible();
		super.setVisible(visible);
		if (current != visible && visible && textUtil != null) {
			title.show();
			textUtil.show();
		} else if (current != visible && textUtil != null) {
			title.hide();
			textUtil.hide();
		}
		// applyShadowVisiblity();
	}
	
	// @Override
	// public void saveLastTransform(int dx, int dy) {
	// 	super.saveLastTransform(dx, dy);
	// 	tape.rotate(-2, getLeft(), getTop());
	// 	textUtil.show();
	// }
	
	@Override
	public IGroup getGroup() {
		// return parentThread.getGroup();
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
  	return getWidth() - textUtil.getMargin() - 3;
	}
  
	@Override
	public int getTextAreaLeft() {
		return getLeft() + 7;
	}

	public void setTitle() {
		if (getDiagramItem() instanceof CommentDTO) {
			CommentDTO comment = (CommentDTO) getDiagramItem();
			String dateTime = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(new Date(comment.getUpdatedAt()));
			String formattedTitle = SLogger.format("*{}* · {} ", comment.getUserDisplayName(), dateTime);
	    title.setText(formattedTitle, editable, true);
		}
	}

	public CommentThreadElement getParentThread() {
		return parentThread;
	}

	@Override
	public Diagram getParent() {
		return parentThread;
	}

	@Override
	public Diagram asDiagram() {
		return this;
	}

	public Diagram duplicate(IParentElement parent) {
		return null;
	}

	@Override
  public void saveRelativeDistance(double rleft, double rtop) {
  }
	@Override
  public double getRelativeDistanceLeft() {
  	return 0;
  }
	@Override
  public double getRelativeDistanceTop() {
  	return 0;
  }
	@Override
  public void setPosition(double left, double top) {
	}

	@Override
	public boolean isInitialized() {
		return false;
	}
	@Override
	public void resetInitialized() {
	}
	@Override
	public void updateFixedDistance() {
	}
	@Override
	public void updateFixedSegment() {
	}
	@Override
	public SegmentPoint fixedPointIndex() {
		return null;
	}
	@Override
  public double getFixedDistanceLeft() {
  	return 0;
  }
  @Override
  public double getFixedDistanceTop() {
  	return 0;
  }

	// @Override
	// public void setTransform(int x, int y) {

	// }

	@Override
	public int getTransformX() {
		return parentThread.getGroup().getTransformX();
	}

	@Override
	public int getTransformY() {
		return parentThread.getGroup().getTransformY();
	}

	@Override
  protected void toggleConnectionHelpers() {
    connectionHelpers.toggle(parentThread);
  }

  @Override
	public AnchorElement getAnchorElement(Anchor anchor) {
		return parentThread.getAnchorElement(anchor);
	}

	@Override
	public String getTextAreaBackgroundColor() {
		return parentThread.getTextAreaBackgroundColor();
	}


	// @Override
 //  public void select() {
 //    // surface.getEditorContext().getEventBus().fireEvent(new CommentSelectedEvent(this));
 //  	super.select();
	// }

	public void hideBottomLine() {
		// separator.setVisibility(false);
	}

	@Override
  public void setTransform(int dx, int dy) {
  	parentThread.setTransform(dx, dy);
  }

  public Collection<AnchorElement> getAnchors() {
  	return parentThread.getAnchors();
  }

  /**
  * Specialize on dragging action to return parent thread.
  * Will be sent to server as modified instead of comment element.
  */
  @Override
  public Diagram getOwnerComponent(ActionType actionType) {
  	if (actionType == ActionType.DRAGGING) {
  		return parentThread;
  	}
  	return super.getOwnerComponent(actionType);
	}

  @Override
  public void updateTimestamp(Long createdAt, Long updatedAt) {
  	super.updateTimestamp(createdAt, updatedAt);
		setTitle();
  }

	@Override
	public void copyFrom(IDiagramItemRO diagramItem) {
		super.copyFrom(diagramItem);
		parentThread.resizeWithKnownChildren();
	}	

	/**
	* Specialize to include title lines as well.
	*/
	@Override
  public List<List<IShape>> getTextElements() {
  	List<List<IShape>> result = super.getTextElements();
  	result.addAll(title.getLines());
  	return result;
  }

  /**
  * No need to show annotation colors separately since
  * comment thread is already by default using annotation
  * colors.
  */
  protected boolean supportsAnnotationColors() {
    return false;
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

}
