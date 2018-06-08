package net.sevenscales.editor.uicomponents.uml;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.LibraryShapes;
import net.sevenscales.editor.content.utils.ContainerAttachHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.diagram.shape.HorizontalPartitionShape;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil;


public class HorizontalPartitionElementCorporate extends CalculatedPathElement {
  private int minimumWidth = 25;
  private int minimumHeight = 25;
  private HorizontalPartitionShape shape;
  private static final int HEADER_HEIGHT = 25;
  private static final int OFF = 0;
  private List<IPathFactory> factories;

  private static Integer resolveProperties() {
    LibraryShapes.ShapeProps sh = LibraryShapes.getShapeProps(ElementType.HORIZONTAL_PARTITION.getValue());
    if (sh != null) {
      return sh.properties;
    }
    return null;
  }

  public HorizontalPartitionElementCorporate(ISurfaceHandler surface, HorizontalPartitionShape newShape, String text, 
  		Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
    this(surface, newShape, text, backgroundColor, borderColor, textColor, editable, false, item);
  }
  
  public HorizontalPartitionElementCorporate(ISurfaceHandler surface, HorizontalPartitionShape newShape, String text, 
  		Color backgroundColor, Color borderColor, Color textColor, boolean editable, boolean delayText, IDiagramItemRO item) {
    super(surface, newShape.toGenericShape(resolveProperties()), text, backgroundColor, borderColor, textColor, editable, item);
    this.shape = newShape;
    getDiagramItem().setShapeProperties(getGenericShape().getShapeProperties());
    
    TextElementFormatUtil textUtil = getTextFormatter();
    textUtil.setMarginTop(0);
    textUtil.setRotate(-90);
    
    // if (!delayText) {
    	setText(text);
    // }
    super.constructorDone();
  }

  @Override
	public AnchorElement onAttachArea(Anchor anchor, int x, int y) {
  	return ContainerAttachHelpers.onAttachAreaManualOnly(this, anchor, x, y);
  }

  protected List<IPathFactory> getPathFactories() {
    if (factories == null) {
      factories = new ArrayList<IPathFactory>();
      factories.add(new IPathFactory() {
        public String createPath(int left, int top, int width, int height) {
          return "m" + (left + HEADER_HEIGHT) + "," + top + 
                 "l" + (width - HEADER_HEIGHT - OFF) + "," + 0 + 
                 "l" + OFF + "," + height +
                 "l" + -(width - HEADER_HEIGHT) + "," + 0;

        }
        public boolean supportsEvents() {
          return false;
        }
      });
      factories.add(new IPathFactory() {
        public String createPath(int left, int top, int width, int height) {
          return "m" + left + "," + top + 
                 "l" + (HEADER_HEIGHT + OFF) + "," + 0 + 
                 "l" + OFF + "," + height +
                 "l" + -(HEADER_HEIGHT + OFF) + "," + 0 + 
                 "z";
        }
        public boolean supportsEvents() {
          return true;
        }
      });
    }

    return factories;
  }

	@Override
	public String getTextAreaAlign() {
		return "center";
	}

  @Override
  public Diagram duplicate(ISurfaceHandler surface, boolean partOfMultiple) {
    Point p = getCoords();
    return duplicate(surface, p.x, p.y + getHeight());
  }
  
  @Override
  public Diagram duplicate(ISurfaceHandler surface, int x, int y) {
    HorizontalPartitionShape newShape = new HorizontalPartitionShape(x, y, getWidth() * factorX, getHeight() * factorY);
    Diagram result = createDiagram(surface, newShape, getText(), getEditable());
    return result;
  }
  
  protected Diagram createDiagram(ISurfaceHandler surface, HorizontalPartitionShape newShape,
      String text, boolean editable) {
    return new HorizontalPartitionElementCorporate(surface, newShape, text, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, LibraryShapes.createByType(ElementType.HORIZONTAL_PARTITION.getValue()));
  }

}
