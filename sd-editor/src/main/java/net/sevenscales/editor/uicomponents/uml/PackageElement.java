package net.sevenscales.editor.uicomponents.uml;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.LibraryShapes;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.Point;


public class PackageElement extends CalculatedPathElement {
  private static final int HEADER_HEIGHT = 13;
  private static final int HEADER_WIDTH = (int) (HEADER_HEIGHT * 1.5);
  private static final int HEADER_OFF = 1;
  private static final int OFF = 1;
  private List<IPathFactory> factories;

  public PackageElement(ISurfaceHandler surface, GenericShape newShape, String text, 
  		Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
    this(surface, newShape, text, backgroundColor, borderColor, textColor, editable, false, item);
  }
  
  public PackageElement(ISurfaceHandler surface, GenericShape newShape, String text, 
  		Color backgroundColor, Color borderColor, Color textColor, boolean editable, boolean delayText, IDiagramItemRO item) {
    super(surface, newShape, text, backgroundColor, borderColor, textColor, editable, item);
    super.constructorDone();
  }

  protected int off() {
    return OFF;
  }
  protected int headerOff() {
    return HEADER_OFF;
  }

  protected List<IPathFactory> getPathFactories() {
    if (factories == null) {
      factories = new ArrayList<IPathFactory>();
      factories.add(new IPathFactory() {
        public String createPath(int left, int top, int width, int height) {
          return "m" + left + "," + top + 
                 "l" + (0 + headerOff()) + "," + -HEADER_HEIGHT + 
                 "l" + (HEADER_WIDTH + headerOff()) + "," + 0 +
                 "l" + (-headerOff()) + "," + HEADER_HEIGHT;

        }
        public boolean supportsEvents() {
          return true;
        }
      });
      factories.add(new IPathFactory() {
        public String createPath(int left, int top, int width, int height) {
          return "m" + left + "," + top + 
                 "l" + (width + off()) + "," + -off() + 
                 "l" + (0 - off()) + "," + height +
                 "l" + (-width - off()) + "," + off() + 
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
    GenericShape newShape = new GenericShape(ElementType.PACKAGE.getValue(), x, y, getWidth() * factorX, getHeight() * factorY);
    Diagram result = createDiagram(surface, newShape, getText(), getEditable());
    return result;
  }
  
  protected Diagram createDiagram(ISurfaceHandler surface, GenericShape newShape,
      String text, boolean editable) {
    DiagramItemDTO item = LibraryShapes.createFrom(getDiagramItem());
    return new PackageElement(surface, newShape, text, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, item);
  }

}
