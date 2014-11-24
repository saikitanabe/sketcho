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
import net.sevenscales.domain.ShapeProperty;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;


public class ComponentElement2 extends CalculatedPathElement {
  private int minimumWidth = 25;
  private int minimumHeight = 25;
  private GenericShape shape;
  private static final int BLOCK_HEIGHT = 10;
  private static final int BLOCK_WIDTH = 15;
  private static final int BLOCK_OFF = 1;
  private static final double BLOCK1_LEFT_DISTANCE_FACTORIAL = .15;
  private static final double BLOCK1_RIGHT_DISTANCE_FACTORIAL = .15;
  private static final int OFF = 1;

  private List<IPathFactory> factories;

  public ComponentElement2(ISurfaceHandler surface, GenericShape newShape, String text, 
  		Color backgroundColor, Color borderColor, Color textColor, boolean editable, IDiagramItemRO item) {
    this(surface, newShape, text, backgroundColor, borderColor, textColor, editable, false, item);
  }
  
  public ComponentElement2(ISurfaceHandler surface, GenericShape newShape, String text, 
  		Color backgroundColor, Color borderColor, Color textColor, boolean editable, boolean delayText, IDiagramItemRO item) {
    super(surface, newShape, text, backgroundColor, borderColor, textColor, editable, item);
    this.shape = newShape;
    super.constructorDone();
  }

  protected List<IPathFactory> getPathFactories() {
    if (factories == null) {
      factories = new ArrayList<IPathFactory>();
      factories.add(new IPathFactory() {
        public String createPath(int left, int top, int width, int height) {
          double _left = (left + width * BLOCK1_LEFT_DISTANCE_FACTORIAL) - BLOCK_OFF;
          return "m" + _left + "," + top + 
                 "l" + (0 + BLOCK_OFF) + "," + -BLOCK_HEIGHT + 
                 "l" + BLOCK_WIDTH + "," + 0 +
                 "l" + (0 + BLOCK_OFF) + "," + BLOCK_HEIGHT;

        }
        public boolean supportsEvents() {
          return true;
        }
      });
      factories.add(new IPathFactory() {
        public String createPath(int left, int top, int width, int height) {
          double _left = (left + width - width * BLOCK1_RIGHT_DISTANCE_FACTORIAL - BLOCK_WIDTH);
          return "m" + _left + "," + top + 
                 "l" + 0 + "," + -BLOCK_HEIGHT + 
                 "l" + BLOCK_WIDTH + "," + 0 +
                 "l" + 0 + "," + BLOCK_HEIGHT;

        }
        public boolean supportsEvents() {
          return true;
        }
      });
      factories.add(new IPathFactory() {
        public String createPath(int left, int top, int width, int height) {
          return "m" + left + "," + top + 
                 "l" + (width + OFF) + "," + 0 + 
                 "l" + (0 - OFF) + "," + height +
                 "l" + (-width - OFF) + "," + 0 + 
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
    return new ComponentElement2(surface, newShape, text, new Color(backgroundColor), new Color(borderColor), new Color(textColor), editable, item);
  }

}
