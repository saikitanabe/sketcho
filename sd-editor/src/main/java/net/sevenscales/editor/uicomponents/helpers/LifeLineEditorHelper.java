package net.sevenscales.editor.uicomponents.helpers;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.ISequenceElement;
import net.sevenscales.editor.diagram.DiagramProxy;
import net.sevenscales.editor.diagram.shape.CircleShape;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.domain.DiagramItemDTO;

public class LifeLineEditorHelper implements ILifeLineEditor, DiagramProxy {
  private static SLogger logger = SLogger.createLogger(LifeLineEditorHelper.class);

  public static final int RADIUS_START = 12;
  public static final int RADIUS_SELECTION = 25;
  
  private CircleElement startSelection;
  private CircleShape circleShape = new CircleShape();
  private ISurfaceHandler surface;
  private ISequenceElement parent;
  private static Map<ISurfaceHandler, ILifeLineEditor> instances;
  private int debugIdIndex;

  private static ILifeLineEditor EMPTY_EDITOR = new ILifeLineEditor() {
    @Override
    public void setShape(CircleShape circleShape) {
    }
    @Override
    public void applyTransform(int dx, int dy) {
    }
    @Override
    public void show(ISequenceElement parent) {
    }
    @Override
    public void setShape(ISequenceElement parent) {
    }
    @Override
    public void hide(Diagram candidate) {
    }
    @Override
    public void forceHide() {
    }
    @Override
    public void saveLastTransform(int dx, int dy) {
    }

    @Override
    public Diagram getStartSelection() {
      return null;
    }
    @Override
    public AbstractDiagramItem getParent() {
      return null;
    }
    @Override
    public void hideGlobalElement() {
    }
    @Override
    public void release() {
    }
  };
  
  static {
    instances = new HashMap<ISurfaceHandler, ILifeLineEditor>();
  }

  public LifeLineEditorHelper(ISurfaceHandler surface, ISequenceElement parent, boolean editable) {
    this.surface = surface;
    this.parent = parent;
    
    // use it as global singleton element, it is actually a visual editor that can be visible only once on the board.
    startSelection = new CircleElement(surface.getInteractionLayer(), surface, this, 0, 0, RADIUS_START, RADIUS_SELECTION, editable, new DiagramItemDTO(), createDebugId());
    startSelection.setVerticalMovement(true);
    surface.add(startSelection, true);
    startSelection.setStroke(new Color(0x1D, 0x00, 0xFF, 1));
//    startSelection.addMouseDiagramHandler(this);
    startSelection.setVisible(false);

//    startSelection.addDiagramSelectionHandler(new DiagramSelectionHandler() {
//      public void unselectAll() {
//      }
//      public void unselect(Diagram sender) {
//      }
//      public void selected(List<Diagram> sender) {
//        SequenceElement.this.unselect();
//      }
//    });
  }

  public static ILifeLineEditor createLifeLineEditorHelper(ISurfaceHandler surface, ISequenceElement parent, boolean editable) {
    ILifeLineEditor result = instances.get(surface);
    if (result == null) {
      if (ISurfaceHandler.DRAWING_AREA.equals(surface.getName())) {
        result = new LifeLineEditorHelper(surface, parent, editable);
        instances.put(surface, result);
      } else {
        // currently other surfaces do not support life line editors
        result = EMPTY_EDITOR;
      }
    }
    return result;
  }
  
  public static ILifeLineEditor getIfAny(ISurfaceHandler surface) {
    return instances.get(surface);
  }

  private String createDebugId() {
    ++debugIdIndex;
    return "relhh-" + debugIdIndex;
  }

  @Override
  public Diagram getDiagram() {
    return parent.getDiagram();
  }

  @Override
  public void setShape(CircleShape circleShape) {
    startSelection.setShape(circleShape);
  }

  @Override
  public void applyTransform(int dx, int dy) {
    startSelection.setVerticalMovement(false);
    startSelection.applyTransform(dx, dy);
    startSelection.setVerticalMovement(true);
  }

  @Override
  public void show(ISequenceElement parent) {
    logger.debug("LifeLineEditorHelper.show...");
    this.parent = parent;
    setStartSelectionPosition();
    startSelection.setVisible(true);
  }

  @Override
  public void setShape(ISequenceElement parent) {
    this.parent = parent;
    circleShape.centerX = parent.getLine().getX2() + parent.getLeft();
    circleShape.centerY = parent.getLine().getY2() + parent.getTop();
    circleShape.radius = 10;
    startSelection.setShape(circleShape);
    startSelection.setStroke(parent.getBorderColor());
  }

  @Override
  public void hide(Diagram candidate) {
    if (candidate == parent) {
      forceHide();
    }
  }
  
  @Override
  public void forceHide() {
    logger.debug("LifeLineEditorHelper.forceHide...");
    startSelection.setVisible(false);
  }

  @Override
  public void saveLastTransform(int dx, int dy) {
    startSelection.saveLastTransform(dx, dy);
    setStartSelectionPosition();
  }
  
  private void setStartSelectionPosition() {
    setShape(parent);
  }

  @Override
  public Diagram getStartSelection() {
    return startSelection;
  }

  @Override
  public AbstractDiagramItem getParent() {
    return (AbstractDiagramItem) parent.getDiagram();
  }

  @Override
  public void hideGlobalElement() {
    forceHide();
  }

  @Override
  public void release() {
    startSelection.removeFromParent();
    startSelection = null;
    parent = null;   
    instances.clear();
  }


}
