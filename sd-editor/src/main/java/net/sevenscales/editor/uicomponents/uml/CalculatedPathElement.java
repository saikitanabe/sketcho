package net.sevenscales.editor.uicomponents.uml;

import java.util.List;
import java.util.ArrayList;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.LibraryShapes;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.utils.AreaUtils;
import net.sevenscales.editor.content.utils.ContainerAttachHelpers;
import net.sevenscales.editor.diagram.ContainerType;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.diagram.shape.RectShape;
import net.sevenscales.editor.diagram.utils.GridUtils;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IContainer;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IPath;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.constants.Constants;
import net.sevenscales.domain.ElementType;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;


public abstract class CalculatedPathElement extends AbstractDiagramItem implements SupportsRectangleShape, ContainerType, IGenericElement {
  private List<IPath> paths;
  private int minimumWidth = 25;
  private int minimumHeight = 25;
  private GenericShape shape;
  private RectShape rectShape = new RectShape();
  private Point coords = new Point();
  private IGroup group;
  private TextElementFormatUtil textUtil;
  private GenericHasTextElement hasTextElement;

  public interface IPathFactory {
    String createPath(int left, int top, int width, int height);
    boolean supportsEvents();
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
    // group.setAttribute("cursor", "default");

    // set clipping area => text is visible only within canvas boundary
//    group.setClip(shape.left, shape.top, shape.width, shape.height);

    addMouseDiagramHandler(this);

    lazyAllocatePaths();
        
    resizeHelpers = ResizeHelpers.createResizeHelpers(surface);
    hasTextElement = new GenericHasTextElement(this, shape);
    hasTextElement.setMarginLeft(getMarginLeft());
    textUtil = new TextElementFormatUtil(this, hasTextElement, group, surface.getEditorContext());
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
    textUtil.setText(newText, editable);
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

    setPathShapes(left, top, width, height);

    textUtil.setTextShape();
  }

  private void lazyAllocatePaths() {
    if (paths == null) {
      paths = new ArrayList();
      for (IPathFactory f : getPathFactories()) {
        IPath p = IShapeFactory.Util.factory(editable).createPath(group, null);
        p.setStrokeWidth(Constants.SKETCH_MODE_LINE_WEIGHT);
        p.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, 0); // force transparent
        p.setAttribute("style", "stroke-linejoin:round;");
        paths.add(p);

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
      IPath p = paths.get(i);
      IPathFactory f = getPathFactories().get(i);
      p.setShape(f.createPath(left, top, width, height));
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
      setShape(left, top, width, height);
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
    for (IPath p : paths) {
      p.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, opacity);
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
    for (IPath p : paths) {
      p.setStroke(color);
    }
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

}