package net.sevenscales.editor.uicomponents.uml;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArray;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.constants.Constants;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.content.ui.UMLDiagramType;
import net.sevenscales.editor.content.utils.ContainerAttachHelpers;
import net.sevenscales.editor.diagram.ContainerType;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.RectShape;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IPath;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;


public abstract class CalculatedPathElement extends AbstractDiagramItem implements SupportsRectangleShape, ContainerType, IGenericElement {
  private List<PathWrapper> paths;
  private int minimumWidth = 25;
  private int minimumHeight = 25;
  private GenericShape shape;
  private RectShape rectShape = new RectShape();
  private Point coords = new Point();
  private IGroup group;
  private IGroup subgroup;
  private TextElementFormatUtil textUtil;
  private GenericHasTextElement hasTextElement;
  private boolean legacy = false;

  public interface IPathFactory {
    String createPath(int left, int top, int width, int height);
    boolean supportsEvents();
  }

  static class PathWrapper {
    IPathFactory factory;
    IPath path;

    PathWrapper(IPathFactory factory, IPath path) {
      this.factory = factory;
      this.path = path;
    }
  }

  // public CalculatedPathElement(ISurfaceHandler surface, GenericShape newShape, String text, 
  // 		Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
  //   this(surface, newShape, text, backgroundColor, borderColor, textColor, editable, false, item);
  // }
  
  public CalculatedPathElement(ISurfaceHandler surface, GenericShape newShape, String text, 
  		Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
    super(editable, surface, backgroundColor, borderColor, textColor, item);
    this.shape = newShape;

    group = IShapeFactory.Util.factory(editable).createGroup(surface.getContainerLayer());
    subgroup = IShapeFactory.Util.factory(editable).createGroup(group);
    // group.setAttribute("cursor", "default");

    // set clipping area => text is visible only within canvas boundary
//    group.setClip(shape.left, shape.top, shape.width, shape.height);

    addMouseDiagramHandler(this);

    lazyAllocatePaths();
        
    resizeHelpers = ResizeHelpers.createResizeHelpers(surface);
    hasTextElement = new GenericHasTextElement(this, shape);
    hasTextElement.setMarginLeft(getMarginLeft());
    textUtil = new TextElementFormatUtil(this, hasTextElement, subgroup, surface.getEditorContext());
    // textUtil.setMarginTop(0);
    // textUtil.setRotate(-90);

    setShape(shape.rectShape.left, shape.rectShape.top, shape.rectShape.width, shape.rectShape.height);
    setText(text);
    
    setReadOnly(!editable);
    
    setBorderColor(borderColor);
    setBackgroundColor(backgroundColor);
    super.constructorDone();
  }

  protected int getMarginLeft() {
    return 0;
  }

  public Point getDiffFromMouseDownLocation() {
    return new Point(diffFromMouseDownX, diffFromMouseDownY);
  }
  
  public void accept(ISurfaceHandler surface) {
    super.accept(surface);
    surface.makeDraggable(this);
  }
  
  public AnchorElement onAttachArea(Anchor anchor, int x, int y) {
  	return ContainerAttachHelpers.onAttachArea(this, anchor, x, y);
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
  
//////////////////////////////////////////////////////////////////////
  
  public boolean onResizeArea(int x, int y) {
    return resizeHelpers.isOnResizeArea();
  }
  
  public boolean resize(Point diff) {
    return resize(getRelativeLeft(), getRelativeTop(), getWidth() + diff.x, getHeight() + diff.y);     
  }

  public void setShape(int left, int top, int width, int height) {
    // need to use temporary relative values cached, since getInfo
    // before transfer updates shape
    rectShape.left = left;
    rectShape.top = top;
    rectShape.width = width;
    rectShape.height = height;

    setPathShapes(0, 0, width, height);
    subgroup.setTransform(left, top);

    if (legacy) {
      textUtil.setTextShape();
    }
  }

  private void lazyAllocatePaths() {
    if (paths == null) {
      paths = new ArrayList();
      for (IPathFactory f : getPathFactories()) {
        IPath p = IShapeFactory.Util.factory(editable).createPath(subgroup, null);

        if (Tools.isSketchMode()) {
          p.setStrokeWidth(Constants.SKETCH_MODE_LINE_WEIGHT);
        } else {
          p.setStrokeWidth(Constants.CORPORATE_MODE_LINE_WEIGHT);
        }

        p.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, 0); // force transparent
        p.setAttribute("style", "stroke-linejoin:round;");
        paths.add(new PathWrapper(f, p));

        shapes.add(p);

        if (f.supportsEvents()) {
          addEvents(p);
        }
      }
    }
  }

  private void setPathShapes(int left, int top, int width, int height) {
    // some domain language that path calculation can be stored as string
    // and loaded from the server
    // shape would be calculated relatively from left, top, width, height
    for (int i = 0; i < paths.size(); ++i) {
      PathWrapper p = paths.get(i);
      IPathFactory f = getPathFactories().get(i);
      p.path.setShape(f.createPath(left, top, width, height));
    }
  }

  protected abstract List<IPathFactory> getPathFactories();

  private String round(String path) {
    JsArray<Shapes.JsPathData> pdata = parse(path);
    // return toPath(_round(pdata));
    return toPath(pdata);
  }

  private String toPath(JsArray<Shapes.JsPathData> pathDatas) {
    String result = "";
    for (int i = 0; i < pathDatas.length(); ++i) {
      Shapes.JsPathData current = pathDatas.get(i);
      result += current.toPath(factorX, factorY);
    }
    return result;
  }

  private native JsArray<Shapes.JsPathData> _round(JsArray<Shapes.JsPathData> commands)/*-{
    return $wnd.pathHelpers.round(commands, 0.05, true)
  }-*/;

  private native JsArray<Shapes.JsPathData> parse(String d)/*-{
    var result = $wnd.svgPathParser.parse(d)
    return result
  }-*/;

  @Override
  public boolean resize(int left, int top, int width, int height) {
    if (width >= minimumWidth && height >= minimumHeight) {
      setShape(getRelativeLeft(), getRelativeTop(), width, height);
      super.applyHelpersShape();
      dispatchAndRecalculateAnchorPositions();
      return true;
    }
    return false;
  }
  
  @Override
  public int getResizeIndentX() {
  	return 0;
  }

  /**
   * subclasses to override to decide own resize anchor algorithm.
   * @return
   */
  protected boolean resizeAnchors() {
    return true;
  }

  public void resizeEnd() {
  }

  public Info getInfo() {
    // shape.rectShape.left = rectSurface.getX();
    // shape.rectShape.top = rectSurface.getY();
    // shape.rectShape.width = rectSurface.getWidth();
    // shape.rectShape.height = rectSurface.getHeight();
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
  	return UMLDiagramType.PACKAGE;
  }
  
  @Override
	public void setBackgroundColor(int red, int green, int blue, double opacity) {
  	super.setBackgroundColor(red, green, blue, opacity);
    for (PathWrapper p : paths) {
      if (p.factory.supportsEvents()) {
        p.path.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, opacity);
      }
    }
  }
    
  @Override
  public void moveToBack() {
  	group.moveToBack();
  }
  
  @Override
  public int getRelativeLeft() {
  	return rectShape.left;
  }
  
  @Override
  public int getRelativeTop() {
  	return rectShape.top;
  }
  
  @Override
  public int getWidth() {
  	return rectShape.width;
  }
  @Override
  public int getHeight() {
  	return rectShape.height;
  }
  
  @Override
  protected void doSetShape(int[] shape) {
  	setShape(shape[0], shape[1], shape[2], shape[3]);
  }

  @Override
  public void setHighlightColor(Color color) {
    for (PathWrapper p : paths) {
      p.path.setStroke(color);
    }
  }
	
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
	public int getTextAreaTop() {
		return getTop() + 5;
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
  public boolean supportsModifyToCenter() {
    return false;
  }

  @Override
  public double getTextHeight() {
    return textUtil.getTextHeight();
  }  

  @Override
  public AbstractDiagramItem getDiagram() {
    return this;
  }

  protected GenericShape getGenericShape() {
    return shape;
  }

}
