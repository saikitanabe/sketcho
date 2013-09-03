package net.sevenscales.editor.uicomponents.helpers;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramProxy;
import net.sevenscales.editor.diagram.shape.CircleShape;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.uicomponents.uml.SequenceElement;

public class LifeLineEditorHelper implements ILifeLineEditor, DiagramProxy {
  private static SLogger logger = SLogger.createLogger(LifeLineEditorHelper.class);
  
  private CircleElement startSelection;
  private CircleShape circleShape = new CircleShape();
  private ISurfaceHandler surface;
  private SequenceElement parent;
  private static Map<ISurfaceHandler, ILifeLineEditor> instances;

  private static ILifeLineEditor EMPTY_EDITOR = new ILifeLineEditor() {
    @Override
    public void setShape(CircleShape circleShape) {
    }
    @Override
    public void applyTransform(int dx, int dy) {
    }
    @Override
    public void show(SequenceElement parent) {
    }
    @Override
    public void setShape(SequenceElement parent) {
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

  public LifeLineEditorHelper(ISurfaceHandler surface, SequenceElement parent, boolean editable) {
    this.surface = surface;
    this.parent = parent;
    
    // use it as global singleton element, it is actually a visual editor that can be visible only once on the board.
    startSelection = new CircleElement(surface.getInteractionLayer(), surface, this, 0, 0, SequenceElement.RADIUS_START, SequenceElement.RADIUS_SELECTION, editable);
    startSelection.setVerticalMovement(true);
    surface.add(startSelection, true);
    startSelection.setStroke("#1D00FF");
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

  public static ILifeLineEditor createLifeLineEditorHelper(ISurfaceHandler surface, SequenceElement parent, boolean editable) {
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

  @Override
  public Diagram getDiagram() {
    return parent;
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
  public void show(SequenceElement parent) {
    logger.debug("LifeLineEditorHelper.show...");
    this.parent = parent;
    setStartSelectionPosition();
    startSelection.setVisible(true);
  }

  @Override
  public void setShape(SequenceElement parent) {
    this.parent = parent;
    circleShape.centerX = parent.getLine().getX2() + parent.getTransformX();
    circleShape.centerY = parent.getLine().getY2() + parent.getTransformY();
    circleShape.radius = 10;
    startSelection.setShape(circleShape);
    startSelection.setStroke(parent.getColor());
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
    return parent;
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
