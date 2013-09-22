package net.sevenscales.editor.uicomponents.uml;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.core.client.JsonUtils;

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

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.JsComment;

import com.google.gwt.core.client.GWT;

public class CommentElement extends AbstractDiagramItem implements SupportsRectangleShape {
	private static final SLogger logger = SLogger.createLogger(CommentElement.class);

//	private Rectangle rectSurface;
//  private IPolyline boundary;
	private IRectangle boundary;
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
  private IPath tape;
//  private IImage leftShadow;
//  private IImage rightShadow;
//  private IImage topBlur;
  private TextElementVerticalFormatUtil title;
  private TextElementVerticalFormatUtil textUtil;
  private String parentThread;
  
  private static final int LEFT_SHADOW_LEFT = 6; 
  private static final int LEFT_SHADOW_HEIGHT = 41; 
  private static final int RIGHT_SHADOW_LEFT = 44; 
  private static final int RIGHT_SHADOW_HEIGHT = 40; 

  private IPath.PathTransformer pathTransformer = new IPath.PathTransformer() {
  	public String getShapeStr(int dx, int dy) {
  		return calcShape(doGetLeft() + dx, doGetTop() + dy, getWidth(), getTop());
  	}
  };

  
	public CommentElement(ISurfaceHandler surface, CommentShape newShape, String text, 
										 Color backgroundColor, Color borderColor, Color textColor, boolean editable,
										 String parentThread) {
		super(editable, surface, backgroundColor, borderColor, textColor);
		this.shape = newShape;
		this.parentThread = parentThread;
		
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
		boundary.setStrokeWidth(STROKE_WIDTH);
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
    
    title = new TextElementVerticalFormatUtil(this, hasTitleTextElement, group, surface.getEditorContext());
    textUtil = new TextElementVerticalFormatUtil(this, hasTextElement, group, surface.getEditorContext());

    setReadOnly(!editable);
    setShape(shape.rectShape.left, shape.rectShape.top, 
             shape.rectShape.width, shape.rectShape.height);
    
    setText(text);
    
    setBorderColor(borderWebColor);
    
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

		boundary.setShape(left, top, width, height, 0);
		
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

  private HasTextElement hasTitleTextElement = new AbstractHasTextElement() {
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
    	return boundary.getY() - 30;
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
		public String getTextColorAsString() {
			return "#" + textColor.toHexString();
		};

  };
	
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
      return CommentElement.this.getLink();
    }

    public boolean isAutoResize() {
      return CommentElement.this.isAutoResize();
    }

    public void resize(int x, int y, int width, int height) {
      CommentElement.this.resize(x, y, width, height);
      fireSizeChanged();
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
		surface.remove(this);
    surface.remove(group.getContainer());
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
    CommentShape newShape = new CommentShape(x, y, getWidth(), getHeight());
    CommentElement result = createDiagram(surface, newShape, getText(), getEditable());
    // refresh text, it is not visible...
		result.textUtil.show();
    return result;
  }
	
  protected CommentElement createDiagram(ISurfaceHandler surface, CommentShape newShape,
      String text, boolean editable) {
    return new CommentElement(surface, newShape, text, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, parentThread);
  }
	
//////////////////////////////////////////////////////////////////////
	
	public boolean onResizeArea(int x, int y) {
		return resizeHelpers.isOnResizeArea();
	}

	public void resizeStart() {
	}

	public boolean resize(Point diff) {
		return resize(doGetLeft(), doGetTop(), getWidth() + diff.x, getHeight() + diff.y);
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

	@Override
	public String getCustomData() {
   	JSONObject json = new JSONObject();
    json.put("pthread", new JSONString(parentThread));

    String result = json.toString().replaceAll("\"", "\\\\\"");
    logger.debug("pthread: {}", result);
    return result;
	}

	@Override
  public void parseCustomData(String customData) {
    logger.debug("parseCustomData.customData {}", customData);
  	JsComment jsComment = JsonUtils.safeEval(customData);
    this.parentThread = jsComment.getParentThread();
    setUser(jsComment.getUser());
	}

	public void setUser(String user) {
    title.setText("*" + user + "*", false);
	}

}
