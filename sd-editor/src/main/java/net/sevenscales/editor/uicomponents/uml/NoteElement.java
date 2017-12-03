package net.sevenscales.editor.uicomponents.uml;


import java.util.ArrayList;
import java.util.List;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.api.ActionType;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.content.ui.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.NoteShape;
import net.sevenscales.editor.diagram.utils.MouseDiagramEventHelpers;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IContainer;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IPath;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;
import net.sevenscales.editor.uicomponents.TextElementVerticalFormatUtil;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;

public class NoteElement extends AbstractDiagramItem implements SupportsRectangleShape {
//	private Rectangle rectSurface;
//  private IPolyline boundary;
	private IRectangle boundary;
	private int minimumWidth = 25;
	private int minimumHeight = 25;
	private NoteShape shape;
	private Point coords = new Point();
  // utility shape container to align text and make separators
  private List<IShape> innerShapes = new ArrayList<IShape>();
	private IGroup group;
	private IGroup subgroup;
//  private int[] points;
//  private static final int FOLD_SIZE = 10;
//  private IPolyline fold;
  private IPath tape;
//  private IImage leftShadow;
//  private IImage rightShadow;
//  private IImage topBlur;
  private TextElementVerticalFormatUtil textUtil;
  
  private static final int LEFT_SHADOW_LEFT = 6; 
  private static final int LEFT_SHADOW_HEIGHT = 41; 
  private static final int RIGHT_SHADOW_LEFT = 44; 
  private static final int RIGHT_SHADOW_HEIGHT = 40; 

  private IPath.PathTransformer pathTransformer = new IPath.PathTransformer() {
  	public String getShapeStr(int dx, int dy) {
  		return calcShape(getRelativeLeft() + dx, getRelativeTop() + dy, getWidth(), getTop());
  	}
  };

  
	public NoteElement(ISurfaceHandler surface, NoteShape newShape, String text, 
										 Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
		super(editable, surface, backgroundColor, borderColor, textColor, item);
		this.shape = newShape;
		
		group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());

		// >>>> ST 3.12.2017: regression bug: background color hides text
		boundary = IShapeFactory.Util.factory(editable).createRectangle(group);
		boundary.setStrokeWidth(STROKE_WIDTH);
		boundary.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
		// <<<< ST 3.12.2017: regression bug: background color hides text

		subgroup = IShapeFactory.Util.factory(editable).createGroup(group);
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
		tape.setSupportsTheme(false);

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
    
    textUtil = new TextElementVerticalFormatUtil(this, hasTextElement, subgroup, surface.getEditorContext());

    setReadOnly(!editable);
    setShape(shape.rectShape.left, shape.rectShape.top, 
             shape.rectShape.width, shape.rectShape.height);
    
    setText(text);
    
    // setBorderColor(borderWebColor);
    
    super.constructorDone();
	}
	
	private void applyShadowVisiblity() {
//		leftShadow.setVisibility(!(backgroundColor.opacity == 0));
//		rightShadow.setVisibility(!(backgroundColor.opacity == 0));
	}

	public void setShape(int left, int top, int width, int height) {
//    points = new int[]{left, top, 
//        left+width-FOLD_SIZE, top,
//        left+width, top+FOLD_SIZE,
//        left+width, top+height,
//        left, top+height,
//        left, top};


		// group.setTransform(left, top);
		boundary.setShape(left, top, width, height, 0);
		subgroup.setTransform(left, top);
		
//		leftShadow.setShape(left - LEFT_SHADOW_LEFT, top + height - LEFT_SHADOW_HEIGHT, 50, 50);
//		rightShadow.setShape(left + width - RIGHT_SHADOW_LEFT, top + height - RIGHT_SHADOW_HEIGHT, 50, 50);

//    boundary.setShape(points);
    
		// tape.resetAllTransforms();
		// tape.setShape(left + width / 2 - 15, top - 4, 30, 15, 0);
    tape.setShape(calcShape(left, top, width, height));
    // tape.rotate(-3, getCenterX(), getLeft() + (getWidth() / 2));
    
    textUtil.setTextShape();
    setBorderColor(borderColor);
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


	private final int MARGIN_TOP = 5;
	private final int MARGIN_LEFT = 13;
  private HasTextElement hasTextElement = new AbstractHasTextElement(this) {
    public int getWidth() {
    	return boundary.getWidth() - MARGIN_LEFT * 2;
    }
    public int getX() {
    	// return boundary.getX() + MARGIN_LEFT;
    	return MARGIN_LEFT;
    }
    public int getY() {
    	// return boundary.getY() + MARGIN_TOP;
    	return MARGIN_TOP;
    }
    public int getHeight() {
    	return boundary.getHeight() - MARGIN_TOP;
    }
    public void removeShape(IShape shape) {
      group.remove(shape);
      shapes.remove(shape);
    }

    public String getLink() {
      return NoteElement.this.getLink();
    }

    public boolean isAutoResize() {
      return NoteElement.this.isAutoResize();
    }

    public void resize(int x, int y, int width, int height) {
      NoteElement.this.resize(x, y, width, height);
      fireSizeChanged();
    }

    public void resizeHeight(int height) {
      NoteElement.this.setHeight(height);
		}    

    public void setLink(String link) {
      NoteElement.this.setLink(link);      
    }
    public boolean supportsTitleCenter() {
      return false;
    }
    public int getTextMargin(int defaultMargin) {
      return 21;
    }
    public boolean forceAutoResize() {
      return true;
    }
    
    public GraphicsEventHandler getGraphicsMouseHandler() {
      return NoteElement.this;
    }
		@Override
		public Color getTextColor() {
			return textColor;
		}

    public int getMarginLeft() {
      return MARGIN_LEFT;
    }
    public int getMarginTop() {
      return MARGIN_TOP;
    }

    public int getMarginBottom() {
      return MARGIN_TOP;
    }

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

	@Override	
	public void setHeight(int height) {
		setShape(getRelativeLeft(), getRelativeTop(), getWidth(), height);
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

	public void doSetText(String newText) {
    textUtil.setText(newText, editable);
	}

  @Override
	public Diagram duplicate(boolean partOfMultiple) {
		return duplicate(surface, partOfMultiple);
	}
	
  @Override
	public Diagram duplicate(ISurfaceHandler surface, boolean partOfMultiple) {
		Point p = getCoords();
		return duplicate(surface, p.x + 20, p.y + 20);
	}
	
  @Override
  public Diagram duplicate(ISurfaceHandler surface, int x, int y) {
    NoteShape newShape = new NoteShape(x, y, getWidth(), getHeight());
    NoteElement result = createDiagram(surface, newShape, getText(), getEditable());
    // refresh text, it is not visible...
		result.textUtil.show();
    return result;
  }
	
  protected NoteElement createDiagram(ISurfaceHandler surface, NoteShape newShape,
      String text, boolean editable) {
    return new NoteElement(surface, newShape, text, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, new DiagramItemDTO());
  }
	
//////////////////////////////////////////////////////////////////////
	
	public boolean onResizeArea(int x, int y) {
		return resizeHelpers.isOnResizeArea();
	}

	public boolean resize(Point diff) {
		return resize(getRelativeLeft(), getRelativeTop(), getWidth() + diff.x, getHeight() + diff.y);
	}

	protected boolean resize(int left, int top, int width, int height) {
	   if (width >= minimumWidth && height >= minimumHeight) {
       setShape(left, top, width, height);
       connectionHelpers.setShape(getLeft(), getTop(), getWidth(), getHeight());
       dispatchAndRecalculateAnchorPositions();
       return true;
	   }
	   return false;
	}

	public void resizeEnd() {
		super.resizeEnd();
		textUtil.setText(getText(), editable, true);
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
	public void setHighlightColor(Color color) {
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
		textUtil.setVisible(visible);
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
	public IGroup getSubgroup() {
		return subgroup;
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
  public void editingEnded(boolean modified) {
  	if (modified) {
	  	// Scheduler.get().scheduleDeferred(new ScheduledCommand() {
	 		// 	public void execute() {
	 				applyText();
	 		// 	}
	 		// });
	  }

	  // need to call as last to make sure attached relationships use
	  // closest path if set
  	super.editingEnded(modified);
  }

  private void applyText() {
  	textUtil.setText(textUtil.getText(), true, true);
  	MouseDiagramEventHelpers.fireChangedWithRelatedRelationships(surface, this, ActionType.TEXT_CHANGED);
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

  @Override
  public int supportedMenuItems() {
  	return super.supportedMenuItems() |
           ContextMenuItem.LAYERS.getValue();
  }

}
