package net.sevenscales.editor.uicomponents;


import java.util.List;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ActionType;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.content.ui.ContextMenuItem;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.DiagramProxy;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.diagram.shape.CircleShape;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.ICircle;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.uicomponents.helpers.ConnectionHelpers;
import net.sevenscales.editor.uicomponents.helpers.IConnectionHelpers;


public class CircleElement extends AbstractDiagramItem {
	private static final SLogger logger = SLogger.createLogger(CircleElement.class);
	
	private ICircle circle;
//  private List<IShape> elements = new ArrayList<IShape>();
//  private CircleShape shape = new CircleShape();
	private IGroup group;
	private ICircle selectionArea;
	private DeleteHandler deleteHandler;

	public interface DeleteHandler {
		void remove(CircleElement ce);
		int supportedMenuItems(CircleElement ce);
	}

	public CircleElement(IGroup layer, ISurfaceHandler surface, DiagramProxy parent, int circleX, int circleY, int radius, boolean editable, IDiagramItemRO item, String debugId) {
		this(layer, surface, parent, circleX, circleY, radius, 0, editable, item, debugId);
	}
	
	public CircleElement(
		IGroup layer, 
		ISurfaceHandler surface, 
		DiagramProxy parent, 
		int circleX, 
		int circleY, 
		int radius, 
		int selectionRadius, 
		boolean editable,
		IDiagramItemRO item,
		String debugId
	) {
		super(editable, surface, item);
		setOwnerComponent(parent);
		addMouseDiagramHandler(this);
		group = IShapeFactory.Util.factory(editable).createGroup(layer);
		
		circle = IShapeFactory.Util.factory(editable).createCircle(group);
		// This is just for debugging to understand what are all circles
		// on svg.
		circle.setAttribute("id", debugId);
		circle.setShape(circleX, circleY, radius);
		circle.setFill(255, 255, 255, 0.6);
		circle.setStroke(Theme.getCurrentColorScheme().getBorderColor());
		shapes.add(circle);

		if (selectionRadius <= radius) {
			listen(circle);
		} else {
			selectionArea = IShapeFactory.Util.factory(editable).createCircle(group);
			selectionArea.setShape(circleX, circleY, selectionRadius);
			selectionArea.setFill(200, 200, 200, 0);
			selectionArea.moveToFront();
			shapes.add(selectionArea);
			listen(circle);
			listen(selectionArea);
		}
	}

	public void setDeleteHandler(DeleteHandler deleteHandler) {
		this.deleteHandler = deleteHandler;
	}
	
	private void listen(ICircle circle) {
		circle.addGraphicsMouseDownHandler(this);
    circle.addGraphicsMouseUpHandler(this);
    circle.addGraphicsTouchStartHandler(this);
    circle.addGraphicsTouchEndHandler(this);
	}

	@Override
	protected IConnectionHelpers createConnectionHelpers() {
		return ConnectionHelpers.createEmptyConnectionHelpers();
	}

	public Point getDiffFromMouseDownLocation() {
		return new Point(diffFromMouseDownX, diffFromMouseDownY);
	}
	
	public void accept(ISurfaceHandler surface) {
		surface.makeDraggable(this);
	}
	public void select() {
	  super.select();
	  circle.setStroke(DEFAULT_SELECTION_COLOR);
	  setVisible(true);
	}
	public void unselect() {
	  super.unselect();
    circle.setStroke(Theme.getCurrentColorScheme().getBorderColor());
    // CircleElement is only used with owner component
    if (!getOwnerComponent().isRemoved()) {
    	// parent element might be already removed
    	// where some global editor refers though
    	// circle element, e.g. life line editor in sequence case
    	// where seq and circle element are selected and removed
    	// then clicked board to unselect all => circle would be visible.
	    setVisible(getOwnerComponent().isSelected());
    }
	}
	
	public List<IShape> getElements() {
		return shapes;
	}

	public void setHighlight(boolean highlight) {
		// TODO Auto-generated method stub		
	}

	public Info getInfo() {
	  CircleShape result = new CircleShape();
	  result.centerX = circle.getX() + getTransformX();
	  result.centerY = circle.getY() + getTransformY();
	  result.radius = circle.getRadius();
		return result;
	}

	public void setShape(Info shape) {
	  CircleShape cs = (CircleShape) shape;
	  circle.setShape(cs.centerX, cs.centerY, cs.radius);
	  circle.moveToFront();
	  
	  if (selectionArea != null) {
	  	selectionArea.setShape(cs.centerX, cs.centerY, selectionArea.getRadius());
	  }
	  resetTransform();
	}

	@Override
	protected void doSetShape(int[] shape) {

	}
	
	public void setShape(int cx, int cy, int radius) {
	  circle.setShape(cx, cy, radius);
	  
	  if (selectionArea != null) {
	  	selectionArea.setShape(cx, cy, selectionArea.getRadius());
	  }
	}

	@Override
	public Diagram getOwnerComponent(ActionType actionType) {
		switch (actionType) {
			case DELETE:
				return this;
			case SELECT:
				return this;
		}
		return super.getOwnerComponent(actionType);
	}

	@Override
  public void removeFromParent() {
  	if (deleteHandler != null) {
  		deleteHandler.remove(this);
  	} else {
  		// normal remove parent element
  		super.getOwnerComponent().removeFromParent();
  	}
  }

  public void removeFromParentForce() {
		// normal remove parent element
		super.getOwnerComponent().removeFromParent();
  }

  @Override
  public boolean changeRemoveToModify() {
    if (deleteHandler !=null) {
    	return true;
    } else {
    	return super.changeRemoveToModify();
    }
  }

  @Override
  public Diagram duplicate(ISurfaceHandler surface, boolean partOfMultiple) {
  	// not possible to duplicate helper diagram element; 
  	// can be duplicated only through parent element
  	return null;
//    return new CircleElement(group, circle.getX(), circle.getY(), circle.getRadius(), getEditable());
  }
  
  @Override
  public int getRelativeLeft() {
  	return circle.getX();
  }
  @Override
  public int getRelativeTop() {
  	return circle.getY();
  }

	public int getLocationX() {
		return circle.getX();
	}
	public int getLocationY() {
		return circle.getY();
	}
	public int getRadius() {
	  return circle.getRadius();
	}
	public void setRadius(int radius) {
	  circle.setRadius(radius);
	}
	
	public void setStroke(Color color) {
	  circle.setStroke(color);
	}
  
  @Override
  public void setReadOnly(boolean value) {
    for (IShape s : shapes) {
		s.setVisibility(!value);
    }
  }
  
  public String getDefaultRelationship() {
    return "-|>";
  }
  
  @Override
  public void moveToBack() {
  	super.moveToBack();
    for (IShape s : shapes) {
    	s.moveToBack();
    }
  }

	public void moveToFront() {
		group.moveToFront();
	}
	
//	@Override
//	public void applyTransform(int dx, int dy) {
//		group.applyTransform(dx, dy);
//	}
	
	@Override
	public IGroup getGroup() {
		return group;
	}
	
	@Override
	public AnchorElement onAttachArea(Anchor anchor, int x, int y) {
		// cannot attach to circle sub element
		return null;
	}
	
	@Override
	public int supportedMenuItems() {
		if (deleteHandler != null) {
			return deleteHandler.supportedMenuItems(this);
		}
		return ContextMenuItem.NO_MENU.getValue();
	}

	@Override
	public boolean isAnnotation() {
		return false;
	}

	public void setFill(int red, int green, int blue, double opacity) {
		circle.setFill(red, green, blue, opacity);
	}

//	@Override
//	public void applyTransform(MatrixPointJS point) {
//		mygroup.applyTransform(point.getDX(), point.getDY());
//	}
	
}
