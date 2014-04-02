package net.sevenscales.editor.uicomponents.uml;

import java.util.List;
import java.util.ArrayList;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.content.utils.ContainerAttachHelpers;
import net.sevenscales.editor.content.utils.DiagramHelpers;
import net.sevenscales.editor.content.utils.IntegerHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.ImageShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.utils.UiUtils;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.ILine;
import net.sevenscales.editor.gfx.domain.IImage;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.silver.SilverUtils;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementVerticalFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.domain.IPathRO;
import net.sevenscales.domain.ExtensionDTO;


public class ImageElement extends AbstractDiagramItem implements SupportsRectangleShape {
	private static SLogger logger = SLogger.createLogger(ImageElement.class);

	public static double FREEHAND_STROKE_WIDTH = 2;
	public static int ACTIVITY_START_RADIUS = 10;
	public static int FREEHAND_TOUCH_WIDTH = 20;

	private ImageShape shape;
	private Point coords = new Point();
  // private IRectangle background;
  private IGroup group;
  private IImage image;

	public ImageElement(ISurfaceHandler surface, ImageShape newShape, Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
		super(editable, surface, backgroundColor, borderColor, textColor, item);

		this.shape = newShape;

		group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    group.setAttribute("cursor", "default");

    image = IShapeFactory.Util.factory(true).createImage(group, 
    	shape.rectShape.left, 
    	shape.rectShape.top,
    	shape.rectShape.width,
    	shape.rectShape.height, 
    	shape.getUrl());


    // background = IShapeFactory.Util.factory(editable).createRectangle(group);
    // background.setFill(0, 0 , 0, 0); // transparent
    // background.setStroke("#363636");

		addEvents(image);
		
		addMouseDiagramHandler(this);

    shapes.add(image);

    resizeHelpers = ResizeHelpers.createResizeHelpers(surface);

    setReadOnly(!editable);
    setShape(shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, shape.rectShape.height);

    setBorderColor(borderColor);

    super.constructorDone();
	}

	@Override
	public int getRelativeLeft() {
		return image.getX();
	}
	@Override
	public int getRelativeTop() {
		return image.getY();
	}
	@Override
	public int getWidth() {
		return image.getWidth();
	}
	@Override
	public int getHeight() {
		return image.getHeight();
	}

	public Point getDiffFromMouseDownLocation() {
		return new Point(diffFromMouseDownX, diffFromMouseDownY);
	}
	
	public void accept(ISurfaceHandler surface) {
	  super.accept(surface);
		surface.makeDraggable(this);
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
    ImageShape newShape = new ImageShape(x, y, getWidth() * factorX, getHeight() * factorY, shape.getUrl());
    Diagram result = new ImageElement(surface, newShape, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, new DiagramItemDTO());
    return result;
  }
	
	public void resizeStart() {
	}

  public boolean resize(Point diff) {
    return resize(getRelativeLeft(), getRelativeTop(), getWidth() + diff.x, getHeight() + diff.y);
  }

  protected boolean resize(int left, int top, int width, int height) {
    setShape(getRelativeLeft(), getRelativeTop(), width, height);
    dispatchAndRecalculateAnchorPositions();
    return true;
  }

	public void resizeEnd() {
	}

  public Info getInfo() {
    super.fillInfo(shape);
    return this.shape;
  }

	public void setShape(Info shape) {
	}

  public String getDefaultRelationship() {
    return "--";
  }
  
  @Override
  public UMLDiagramType getDiagramType() {
  	return UMLDiagramType.FREE_HAND;
  }
  
  @Override
  protected void doSetShape(int[] shape) {
    setShape(shape[0], shape[1], shape[2], shape[3]);
  }

  @Override
  public void setShape(int left, int top, int width, int height) {
  	// if ((width > 1 && height >= 0) || (height > 1 && width >= 0)) {
  	if (width >= 10 && height >= 10) {
	  	// TODO subgroup is needed for scaling
	  	// keep original widht, height + then left, top, widht, height scales
	  	// subgroup.setScale(factorX, factorY);
	  	// subgroup.setTransform(left, top);

			super.applyHelpersShape();
  	}
  }

  public void setHighlightColor(String color) {
		image.setStroke(color);
  }
  
	@Override
	public IGroup getGroup() {
		return group;
	}

	// public IGroup getSubgroup() {
	// 	return subgroup;
	// }
	
	@Override
  public boolean supportsTextEditing() {
  	return false;
  }
  
  @Override
  public int supportedMenuItems() {
  	return ContextMenuItem.COLOR_MENU.getValue() | 
  				 ContextMenuItem.LAYERS.getValue();
  }

}