package net.sevenscales.editor.uicomponents.uml;


import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.content.ui.UMLDiagramType;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.ActivityChoiceShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.gfx.domain.Color;
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
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;


public class ActivityChoiceElement extends AbstractDiagramItem implements SupportsRectangleShape {
	private IRectangle boundary;
  private IPath path;
	private int minimumWidth = 25;
	private int minimumHeight = 25;
	private ActivityChoiceShape shape;
	private Point coords = new Point();
  // utility shape container to align text and make separators
//  private List<IShape> innerShapes = new ArrayList<IShape>();
  private IGroup group;
  private Integer[] fixedAnchorPoints;
  private TextElementFormatUtil textUtil;
//	private int boundaryWidth;
//	private int boundaryHeight;
  private IPath.PathTransformer pathTransformer = new IPath.PathTransformer() {
  	public String getShapeStr(int dx, int dy) {
  		// removing transformation is needed since fixedAnchorPoints
  		// are updated on saveLastTransform(int dx, int dy)
  		// this is only for svg generation
  		return calcShape(-getTransformX(), -getTransformY());
  	}
  };
  
	public ActivityChoiceElement(ISurfaceHandler surface, ActivityChoiceShape newShape, String text, 
			Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
		super(editable, surface, backgroundColor, borderColor, textColor, item);
		this.shape = newShape;
		
		group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    // group.setAttribute("cursor", "default");

    fixedAnchorPoints = new Integer[]{shape.rectShape.left, shape.rectShape.top + shape.rectShape.height/2,
											 shape.rectShape.left + shape.rectShape.width/2, shape.rectShape.top,
											 shape.rectShape.left + shape.rectShape.width, shape.rectShape.top + shape.rectShape.height/2,
											 shape.rectShape.left + shape.rectShape.width/2, shape.rectShape.top + shape.rectShape.height,
											 shape.rectShape.left, shape.rectShape.top + shape.rectShape.height/2};
    
    path = IShapeFactory.Util.factory(editable).createPath(group, pathTransformer);
//  path = IShapeFactory.Util.factory(editable).createPolyline(group);
	  path.setStroke(borderColor);
	  path.setStrokeWidth(STROKE_WIDTH);
	  path.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);

    textUtil = new TextElementFormatUtil(this, hasTextElement, group, surface.getEditorContext());
    // textUtil.setMargin(50);

		boundary = IShapeFactory.Util.factory(editable).createRectangle(group);
//	boundaryWidth = (int) Math.sqrt(Math.pow(shape.rectShape.width / 2, 2) + Math.pow(shape.rectShape.height / 2, 2));
//	boundaryHeight = boundaryWidth; // shape.rectShape.height
//	boundary.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
		boundary.setFill(0, 0 , 0, 0); // transparent
	  boundary.setShape(shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, shape.rectShape.height, 3);
		
//		resizeElement = IShapeFactory.Util.factory(editable).createRectangle(group);
//		resizeElement.setFill(200, 200, 200, 0.4);
		
    addEvents(boundary);

		// resize support
//		resizeElement.addGraphicsMouseEnterHandler(new GraphicsMouseEnterHandler() {
//      public void onMouseEnter(GraphicsEvent event) {
//        onResizeArea = true;
//      }
//    });
//		resizeElement.addGraphicsMouseLeaveHandler(new GraphicsMouseLeaveHandler() {
//      public void onMouseLeave(GraphicsEvent event) {
//        onResizeArea = false;
//      }
//    });
//		
//		resizeElement.addGraphicsMouseDownHandler(this);
//    resizeElement.addGraphicsMouseUpHandler(this);
		
		addMouseDiagramHandler(this);
		
    shapes.add(boundary);
    shapes.add(path);
//    shapes.add(resizeElement);
    
    resizeHelpers = ResizeHelpers.createResizeHelpers(surface);
    
    setText(text);

    setReadOnly(!editable);
    setShape(shape.rectShape.left, shape.rectShape.top, 
             shape.rectShape.width, shape.rectShape.height);
    
    setBorderColor(borderColor);
    
    super.constructorDone();
	}

//	private void translateBoundary(IRectangle boundary, int b) {
//		int dx = (int) Math.sqrt(Math.pow(b, 2) + Math.pow(b, 2)) + 1;
//		boundary.applyTransform(dx, 0);
//	}
	
  // nice way to clearly separate interface methods :)
  private HasTextElement hasTextElement = new AbstractHasTextElement(this) {
    public int getWidth() {
      return ActivityChoiceElement.this.getWidth();
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
    
    public boolean verticalAlignMiddle() {
    	return true;
    }
    
  	@Override
  	public boolean boldText() {
  		return false;
  	}

    public void removeShape(IShape shape) {
      group.remove(shape);
      shapes.remove(shape);
    }

    public String getLink() {
      return ActivityChoiceElement.this.getLink();
    }

    public boolean isAutoResize() {
      return ActivityChoiceElement.this.isAutoResize();
    }

    public void resize(int x, int y, int width, int height) {
      // Text Element doesn't support resize
      ActivityChoiceElement.this.resize(x, y, width, width);
      fireSizeChanged();
    }

    public void setLink(String link) {
      ActivityChoiceElement.this.setLink(link);      
    }
    public boolean supportsTitleCenter() {
      return true;
    }
    public int getTextMargin(int defaultMargin) {
    	return (int) (defaultMargin * 50f/30f);
    }
    public boolean forceAutoResize() {
      return false;
    }
    
    public GraphicsEventHandler getGraphicsMouseHandler() {
      return ActivityChoiceElement.this;
    };
    
		@Override
		public Color getTextColor() {
			return textColor;
		};

  };

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
		return boundary.getWidth();
		// currently cannot resize => using accurate width 
//		return shape.rectShape.width;
//    return 2 * (int) Math.sqrt(Math.pow(boundary.getWidth() / 2, 2)); //(int) Math.sqrt(Math.pow(boundary.getWidth()/2, 2) + Math.pow(boundary.getHeight()/2, 2)) * 2;
	}
	
	@Override
	public int getHeight() {
    return boundary.getHeight(); // (int) Math.sqrt(Math.pow(boundary.getWidth()/2, 2) + Math.pow(boundary.getHeight()/2, 2)) * 2;
	}
  
  public Integer[] getFixedAnchorPoints() {
		return fixedAnchorPoints;
	}

	public String getText() {
		return textUtil.getText();
	}

	public void doSetText(String newText) {
    textUtil.setText(newText, editable, isForceTextRendering());
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
    ActivityChoiceShape newShape = new ActivityChoiceShape(x, y, getWidth(), getHeight());
    Diagram result = createDiagram(surface, newShape, getText(), getEditable());
    return result;
  }
	
  protected Diagram createDiagram(ISurfaceHandler surface, ActivityChoiceShape newShape,
      String text, boolean editable) {
    return new ActivityChoiceElement(surface, newShape, text, 
    		new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, new DiagramItemDTO());
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
    	dispatchAndRecalculateAnchorPositions();

  		return true;
	  }
	  return false;
	}

	public void setShape(int left, int top, int width, int height) {
		fixedAnchorPoints[0] = left; fixedAnchorPoints[1] = top + height/2;
		fixedAnchorPoints[2] = left + width/2; fixedAnchorPoints[3] = top;
		fixedAnchorPoints[4] = left + width; fixedAnchorPoints[5] = top + height/2;
		fixedAnchorPoints[6] = left + width/2; fixedAnchorPoints[7] = top + height;
		fixedAnchorPoints[8] = left; fixedAnchorPoints[9] = top + height/2;

    boundary.setShape(left, top, width, height, 3);
  	path.setShape(calcShape(0, 0));
	  path.setAttribute("stroke-linecap", "square");
	  path.setAttribute("stroke-linejoin", "round");
	  
    textUtil.setTextShape();
    super.applyHelpersShape();
	}

	@Override
  protected void doSetShape(int[] shape) {
  	setShape(shape[0], shape[1], shape[2], shape[3]);
  }


//<path id="k9ffd8001" d="M64.5 45.5 82.5 45.5 82.5 64.5 64.5 64.5 z" stroke="#808600" stroke-width="5" transform="rotate(0 0 0)" stroke-linecap="square" stroke-linejoin="round" fill="#a0a700"></path>
	private String calcShape(int dx, int dy) {
		String result = "";
		String prefix = "";
		for (int i = 0; i < fixedAnchorPoints.length; i += 2) {
			if (i == 0) {
				prefix = "M";
			} else {
				prefix = "";
			}
			int x = fixedAnchorPoints[i] + dx;
			int y = fixedAnchorPoints[i + 1] + dy;
			result += prefix + x + "," + y + " ";
		}
		result += " z";
		return result;
	}

	public void resizeEnd() {
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
    return "->";
  }
  
  @Override
  public UMLDiagramType getDiagramType() {
  	return UMLDiagramType.CHOICE;
  }
  
  @Override
	public void setBackgroundColor(Color color) {
  	super.setBackgroundColor(color);
    path.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
	  path.setAttribute("stroke-linecap", "square");
	  path.setAttribute("stroke-linejoin", "round");
  }
  
  @Override
	public void setTextColor(int red, int green, int blue) {
  	super.setTextColor(red, green, blue);
  	textUtil.applyTextColor();
  }
  
  @Override
  public void setHighlightColor(Color color) {
		path.setStroke(color);
	  path.setAttribute("stroke-linecap", "square");
	  path.setAttribute("stroke-linejoin", "round");
  }

// 	@Override
// 	public void resetTransform() {
// //		// unrotate before saving
// //		boundary.unrotate(45, getLeft(), getTop());
// 		group.resetAllTransforms();
// 	}
	
	@Override
	public void saveLastTransform(int dx, int dy) {
		super.saveLastTransform(dx, dy);
		// reread shape values
		getInfo();
		// reset fixed points
		fixedAnchorPoints[0] = shape.rectShape.left; fixedAnchorPoints[1] = shape.rectShape.top + shape.rectShape.height/2;
		fixedAnchorPoints[2] = shape.rectShape.left + shape.rectShape.width/2; fixedAnchorPoints[3] = shape.rectShape.top;
		fixedAnchorPoints[4] = shape.rectShape.left + shape.rectShape.width; fixedAnchorPoints[5] = shape.rectShape.top + shape.rectShape.height/2;
		fixedAnchorPoints[6] = shape.rectShape.left + shape.rectShape.width/2; fixedAnchorPoints[7] = shape.rectShape.top + shape.rectShape.height;
		fixedAnchorPoints[8] = shape.rectShape.left; fixedAnchorPoints[9] = shape.rectShape.top + shape.rectShape.height/2;
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
	public String getTextAreaAlign() {
		return "center";
	}

  @Override
  public boolean supportsTextEditing() {
  	return true;
  }

  @Override
  public int supportedMenuItems() {
  	return super.supportedMenuItems() | ContextMenuItem.FONT_SIZE.getValue() |
  				 ContextMenuItem.LAYERS.getValue();
  }

}
