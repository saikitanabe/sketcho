package net.sevenscales.editor.uicomponents.uml;


import com.google.gwt.user.client.Event;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.content.ui.UMLDiagramType;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.diagram.shape.ActorShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.gfx.base.GraphicsBase;
import net.sevenscales.editor.gfx.base.GraphicsEvent;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseEnterHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseLeaveHandler;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.ICircle;
import net.sevenscales.editor.gfx.domain.IContainer;
import net.sevenscales.editor.gfx.domain.IEventHandler;
import net.sevenscales.editor.gfx.domain.IGraphics;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.ILine;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;

public class Actor extends AbstractDiagramItem implements IEventHandler, SupportsRectangleShape {
  private ICircle head;
  private ILine body;
  private ILine hands;
  private ILine leftLeg;
  private ILine rightLeg;
  private IRectangle background;
	private int minimumWidth = 25;
	private int minimumHeight = 25;
	private ActorShape shape;
//	private String text;
	private Point coords = new Point();
	private IRectangle resizeElement;
	private boolean onResizeArea;
  private IGroup group;
  private int dispachSequence;
  private TextElementFormatUtil textUtil;
	protected int textareaWidth;
	protected int textareaHeight;
  
	public Actor(ISurfaceHandler surface, ActorShape newShape, String text, Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
		super(editable, surface, backgroundColor, borderColor, textColor, item);
		// actor doesn't use saved text color, it is always based on theme
		this.shape = newShape;
		
		group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());		
    // set clipping area => text is visible only within canvas boundary
//		group.setClip(shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, shape.rectShape.height);
		
		body = IShapeFactory.Util.factory(editable).createLine(group);
		body.setStrokeWidth(2.0);
		
    head = IShapeFactory.Util.factory(editable).createCircle(group);
    head.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
    head.setStrokeWidth(1.0);
		
		hands = IShapeFactory.Util.factory(editable).createLine(group);
    hands.setStrokeWidth(2.0);
		
		leftLeg = IShapeFactory.Util.factory(editable).createLine(group);
		leftLeg.setStrokeWidth(2.0);
    
		rightLeg = IShapeFactory.Util.factory(editable).createLine(group);
		rightLeg.setStrokeWidth(2.0);

    background = IShapeFactory.Util.factory(editable).createRectangle(group);
    background.setFill(0, 0 , 0, 0); // transparent

		resizeElement = IShapeFactory.Util.factory(editable).createRectangle(group);
		resizeElement.setFill(200, 200, 200, 0.4);
		
		addEvents(background);

		// resize support
		resizeElement.addGraphicsMouseEnterHandler(new GraphicsMouseEnterHandler() {
      public void onMouseEnter(GraphicsEvent event) {
        onResizeArea = true;
      }
    });
		resizeElement.addGraphicsMouseLeaveHandler(new GraphicsMouseLeaveHandler() {
      public void onMouseLeave(GraphicsEvent event) {
        onResizeArea = false;
      }
    });
		
		resizeElement.addGraphicsMouseDownHandler(this);
		resizeElement.addGraphicsMouseUpHandler(this);
		
//		addMouseDiagramHandler(this);
		
    shapes.add(background);
    shapes.add(resizeElement);
    shapes.add(head);
    shapes.add(body);
    shapes.add(hands);
    shapes.add(leftLeg);
    shapes.add(rightLeg);
    
    textUtil = new TextElementFormatUtil(this, hasTextElement, group, surface.getEditorContext());
    textUtil.setForceTextAlign(true);

    setShape(shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, shape.rectShape.height);
    setText(text);
    

    setReadOnly(!editable);
    setHighlightColor(borderColor);
//		shapes.add(group);
    super.constructorDone();
	}
	
  private HasTextElement hasTextElement = new AbstractHasTextElement(this) {
    public int getWidth() {
    	return Actor.this.getWidth();
    }
    public int getX() {
    	return Actor.this.getRelativeLeft();
    }
    public int getY() {
    	return getRelativeTop() + getHeight() - TextElementFormatUtil.ROW_HEIGHT + 5;
    }
    public int getHeight() {
    	return Actor.this.getHeight();
    }
    public void removeShape(IShape shape) {
      group.remove(shape);
      shapes.remove(shape);
    }

    public String getLink() {
    	return Actor.this.getLink();
    }

    public boolean isAutoResize() {
      return true;
    }

    public void resize(int x, int y, int width, int height) {
    	Actor.this.textareaWidth = width;
    	Actor.this.textareaHeight = height;
//    	Actor.this.setShape(x, y, width, height);
      fireSizeChanged();
    }

    public void setLink(String link) {
//      NoteElement.this.setLink(link);      
    }
    public boolean supportsTitleCenter() {
      return false;
    }
    public int getTextMargin(int defaultMarging) {
      return (int) (defaultMarging * 20f / 30f);
    }
    public boolean forceAutoResize() {
      return true;
    }
    
    public GraphicsEventHandler getGraphicsMouseHandler() {
      return Actor.this;
    }
		@Override
		public Color getTextColor() {
			return textColor;
		};
  };

	
	public void setShape(int left, int top, int width, int height) {
		group.setTransform(left, top);

		int x = 0;
		int y = 0;

    background.setShape(x, y, width, height, 0);
    head.setShape(x+width/2, y+width/5+1, width/5);
    
    int x1 = background.getX()+background.getWidth()/2;
    int x2 = x1;
    int y1 = head.getY()+head.getRadius();
    int y2 = (int) (y1+(height)*0.5);
    body.setShape(x1, y1, x2, y2);
  
    x1 = x;
    x2 = x+width;
    y1 = (int) (body.getY1()+(body.getY2()-body.getY1())/2.5);
    y2 = y1;
    hands.setShape(x1, y1, x2, y2);
  
    leftLeg.setShape(body.getX2(), body.getY2(), 
                     x+5, y+height);
  
    rightLeg.setShape(body.getX2(), body.getY2(), x+width-5, y+height);

    resizeElement.setShape(
        background.getX() + background.getWidth() - 10, background.getY() + background.getHeight() - 10, 10, 10, 0);
    textUtil.setTextShape();
//    setText(text);
    super.applyHelpersShape();
	}

	protected IShape createElement(IContainer surface) {
		return IShapeFactory.Util.factory(editable).createRectangle(surface);
	}

//	public void saveLastTransform() {
//	  // get transformation
//    int dx = SilverUtils.getTransformX(group.getContainer());
//    int dy = SilverUtils.getTransformY(group.getContainer());
//	    
//	  // reset transformations
//    SilverUtils.resetRenderTransform(group.getContainer());
//	    
//    // apply transformations to shapes
//    for (IShape s : shapes) {
//      s.applyTransform(dx, dy);
//    }
//	}

	public Point getDiffFromMouseDownLocation() {
		return new Point(diffFromMouseDownX, diffFromMouseDownY);
	}
	
	public void accept(ISurfaceHandler surface) {
	  super.accept(surface);
		surface.makeDraggable(this);
	}
	
	@Override
	public int getLeftWithText() {
		int twidth = (int) textUtil.getTextWidth();
		int width = getWidth();
		if (twidth > width) {
			return getLeftText();
		}

		return getLeft();
	}
	private int getLeftText() {
		return (int) (getLeft() + getWidth() / 2 - textUtil.getTextWidth() / 2);
	}

	@Override
	public int getRelativeLeft() {
		return background.getX();
	}
	@Override
	public int getRelativeTop() {
		return background.getY();
	}
	@Override
	public int getWidth() {
		return background.getWidth();
	}
	@Override
	public int getWidthWithText() {
		int twidth = (int) textUtil.getTextWidth();
		int width = getWidth();
		if (twidth > width) {
			return twidth;
		}
    return width;
  }
	@Override
	public int getHeight() {
		return background.getHeight();
	}
		
	public String getText() {
		return textUtil.getText();
	}
	
	public void doSetText(String newText) {
		textUtil.setText(newText, editable);
//	  this.text = newText != null ? newText.replaceAll("\\\\n", "\n") : "";
//
////	  ModelTextUtil mtu = new ModelTextUtil(newText);
////	  setLink(mtu.parseLink());
//	  
//    for (IShape l : innerShapes) {
//      group.remove(l);
//      shapes.remove(l);
//    }
//    innerShapes.clear();
//
//    double widestWidth = 0;
//	  // split with \n
//    // convert text to shapes
//	  String[] texts = text.split("\n");
//	  for (String t : texts) {
//  		if (t.length()>0 && t.charAt(t.length()-1) == '\r') {
//  			// on windows line feeds are \r\n
//  			t = t.substring(0, t.length()-1);
//  		}
//  		IText text = IShapeFactory.Util.factory(editable).createText(group);
//      text.setFontWeight(IText.WEIGHT_NORMAL);
//      t = t.replaceAll("<<", Character.toString('\u00AB'));
//      t = t.replaceAll(">>", Character.toString('\u00BB'));
//       
//      text.setText(t);
//      innerShapes.add(text);
//      shapes.add(text);
//       
//      double textWidth = text.getTextWidth();
//      widestWidth = textWidth > widestWidth ? textWidth : widestWidth; 
//
//      text.addGraphicsMouseDownHandler(this);
//      text.addGraphicsMouseUpHandler(this);
//      text.addGraphicsMouseMoveHandler(this);
//	  }
//
//	  setTextShape();
	}

//  private void setTextShape() {
//    int row = 0;
//    int rowHeight = 13;
//    int separated = -1;
//    for (IShape s : innerShapes) {
//      IText t = (IText) s;
//      int x = background.getX()+background.getWidth()/2;
//      String align = IText.ALIGN_CENTER;;
//      String weight = IText.WEIGHT_NORMAL;
//      // bold first segment if not stereo type
//      if (!t.getText().matches("\u00AB.*\u00BB")) {
//        weight = IText.WEIGHT_BOLD;
//      }
//      t.setShape(x, background.getY() + background.getHeight() + 16 + (row*rowHeight));
//      t.setAlignment(align);
//      t.setFontWeight(weight);
//      ++row;
//    }
//     
//    // by default last line
//    int linkLine = innerShapes.size() - 1;
//    if (separated - 1 >= 0) {
//      linkLine = separated - 1;
//    }
//    if (getLink() != null) {
//      IText t = (IText) innerShapes.get(linkLine);
//      t.setStroke("#E18400");
//      t.setFill("#E18400");
//    }
// }

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
    ActorShape newShape = new ActorShape(x, y, background.getWidth(), background.getHeight());
    Diagram result = createDiagram(surface, newShape, getText(), getEditable());
    return result;
  }
	
  protected Diagram createDiagram(ISurfaceHandler surface, ActorShape newShape,
      String text, boolean editable) {
    return new Actor(surface, newShape, text, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, new DiagramItemDTO());
  }
	
//////////////////////////////////////////////////////////////////////
	
	public boolean onResizeArea(int x, int y) {
		return onResizeArea;
	}

//	public JavaScriptObject getResizeElement() {
//		return rectSurface.getRawNode();
//	}
	
	public boolean resize(Point diff) {
	  ++this.dispachSequence;
//  	MatrixPointJS point = MatrixPointJS.createScaledPoint(diff.x, diff.y, surface.getScaleFactor()); 
    for (AnchorElement a : getAnchors()) {
      if (a.getAx() == background.getX() + background.getWidth() || 
          a.getAy() == background.getY() + background.getHeight()) {
        a.dispatch(diff.x, diff.y, dispachSequence);
      }
    }
    return resize(background.getX(), background.getY(), background.getWidth() + diff.x, background.getHeight() + diff.y);     
	}

	protected boolean resize(int left, int top, int width, int height) {
    if (width >= minimumWidth && height >= minimumHeight) {
      setShape(left, top, width, height);
      textUtil.setTextShape();
      connectionHelpers.setShape(left, top, width, height);
      return true;
    }
    return false;
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

	public void onMouseEnter(IGraphics shape, Event event) {
//		if (shape == resizeElement) {
//			onResizeArea = true;
//		}
	}
	
	public void onMouseMove(IGraphics graphics, Event event) {
		// TODO Auto-generated method stub
		
	}

	public void onMouseLeave(IGraphics shape, Event event) {
//		if (shape == resizeElement) {
//			onResizeArea = false;
//		}
	}
	
	public void onDoubleClick(IGraphics shape, Event event) {
		// TODO Auto-generated method stub
		
	}
	
	public void onMouseDown(IGraphics shape, Event event, int keys) {
		// TODO Auto-generated method stub
		
	}

	public void onMouseUp(IGraphics graphics, Event event, int keys) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onTouchMove(GraphicsBase shape, GraphicsEvent event) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onTouchStart(GraphicsBase shape, GraphicsEvent event) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onTouchEnd(GraphicsBase shape, GraphicsEvent event) {
		// TODO Auto-generated method stub
	}

	public void setReadOnly(boolean value) {
	  super.setReadOnly(value);
	  resizeElement.setVisibility(!value);
	  background.setVisibility(!value);
	}
	
	public String getDefaultRelationship() {
	  return "-|>";
	}
	
  @Override
  public UMLDiagramType getDiagramType() {
  	return UMLDiagramType.ACTOR;
  }
  
  @Override
	public void setBackgroundColor(int red, int green, int blue, double opacity) {
  	super.setBackgroundColor(red, green, blue, opacity);
  	head.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
  }
  
  @Override
  protected void doSetShape(int[] shape) {
  	setShape(shape[0], shape[1], shape[2], shape[3]);
  }
  
  @Override
  public void setHighlightColor(Color color) {
//  	if (color.equals(borderColor)) {
//  		// actor uses line color
//  		color = DEFAULT_LINE_COLOR;
//  	}
		head.setStroke(color);
    hands.setStroke(color);
    body.setStroke(color);
    leftLeg.setStroke(color);
    rightLeg.setStroke(color);
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
	public int getTextAreaLeft() {
		return getLeft() - getTextAreaWidth() / 2  + textUtil.getMargin() - 8; // need to add mystical margin from text width and something :)
	}
	
	@Override
	public int getTextAreaTop() {
		return getTop() + getHeight() - 1;
	}
	
	@Override
	public int getTextAreaWidth() {
//		return textareaWidth == 0 ? textUtil.getTextWidth() : textareaWidth;
		return (int) textUtil.getTextWidth();
	}
	
	@Override
	public int getTextAreaHeight() {
//		return textareaHeight == 0 ? textUtil.getTextHeight() : textareaHeight;
		return (int) textUtil.getTextHeight();
	}

	@Override
	public String getTextAreaAlign() {
		return "center";
	}
	
	public String getTextAreaBackgroundColor() {
		return "transparent";
	}
	
	@Override
	public String getBackgroundColor() {
		return "transparent";
	}
	
  @Override
  public boolean supportsTextEditing() {
  	return true;
  }
  
  @Override
  public boolean isTextElementBackgroundTransparent() {
    return true;
  }
  
  @Override
  public boolean isTextColorAccordingToBackgroundColor() {
    return true;
  }

  @Override
  public int supportedMenuItems() {
  	return super.supportedMenuItems() | ContextMenuItem.FONT_SIZE.getValue() |
           ContextMenuItem.LAYERS.getValue();
  }

  @Override
  public int getHeightWithText() {
	  TextElementFormatUtil textFormatter = getTextFormatter();
    return getHeight() + ((int)textFormatter.getTextHeight());
  }

}
