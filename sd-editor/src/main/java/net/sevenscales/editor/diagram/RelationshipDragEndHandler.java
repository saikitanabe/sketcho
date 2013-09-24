package net.sevenscales.editor.diagram;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.event.CreateElementEvent;
import net.sevenscales.editor.api.event.CreateElementEventHandler;
import net.sevenscales.editor.api.event.LibrarySelectionEvent;
import net.sevenscales.editor.api.event.LibrarySelectionEventHandler;
import net.sevenscales.editor.api.event.PotentialOnChangedEvent;
import net.sevenscales.editor.api.event.RelationshipNotAttachedEvent;
import net.sevenscales.editor.api.event.RelationshipNotAttachedEventHandler;
import net.sevenscales.editor.api.event.ShowDiagramPropertyTextEditorEvent;
import net.sevenscales.editor.api.event.SurfaceMouseUpNoHandlingYetEvent;
import net.sevenscales.editor.api.event.SurfaceMouseUpNoHandlingYetEventHandler;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.content.ui.UMLDiagramSelections;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.SelectionHandler;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.utils.ScaleHelpers;
import net.sevenscales.editor.content.utils.ScaleHelpers.ScaledAndTranslatedPoint;
import net.sevenscales.editor.diagram.shape.ActivityChoiceShape;
import net.sevenscales.editor.diagram.shape.ActivityEndShape;
import net.sevenscales.editor.diagram.shape.ActivityShape;
import net.sevenscales.editor.diagram.shape.ActivityStartShape;
import net.sevenscales.editor.diagram.shape.ActorShape;
import net.sevenscales.editor.diagram.shape.DbShape;
import net.sevenscales.editor.diagram.shape.EllipseShape;
import net.sevenscales.editor.diagram.shape.MindCentralShape;
import net.sevenscales.editor.diagram.shape.NoteShape;
import net.sevenscales.editor.diagram.shape.CommentThreadShape;
import net.sevenscales.editor.diagram.shape.RectShape;
import net.sevenscales.editor.diagram.shape.SequenceShape;
import net.sevenscales.editor.diagram.shape.TextShape;
import net.sevenscales.editor.diagram.shape.UMLPackageShape;
import net.sevenscales.editor.diagram.utils.DiagramAnchorUtils;
import net.sevenscales.editor.diagram.utils.RelationshipHelpers;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.uicomponents.Anchor;
import net.sevenscales.editor.uicomponents.Point;
import net.sevenscales.editor.uicomponents.uml.ActivityChoiceElement;
import net.sevenscales.editor.uicomponents.uml.ActivityElement;
import net.sevenscales.editor.uicomponents.uml.ActivityEnd;
import net.sevenscales.editor.uicomponents.uml.ActivityStart;
import net.sevenscales.editor.uicomponents.uml.Actor;
import net.sevenscales.editor.uicomponents.uml.ClassElement2;
import net.sevenscales.editor.uicomponents.uml.EllipseElement;
import net.sevenscales.editor.uicomponents.uml.MindCentralElement;
import net.sevenscales.editor.uicomponents.uml.NoteElement;
import net.sevenscales.editor.uicomponents.uml.CommentThreadElement;
import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.editor.uicomponents.uml.SequenceElement;
import net.sevenscales.editor.uicomponents.uml.StorageElement;
import net.sevenscales.editor.uicomponents.uml.TextElement;
import net.sevenscales.editor.uicomponents.uml.UMLPackageElement;

import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.PopupPanel;

public class RelationshipDragEndHandler implements
		RelationshipNotAttachedEventHandler, SelectionHandler, SurfaceMouseUpNoHandlingYetEventHandler, LibrarySelectionEventHandler {
  private static SLogger logger = SLogger.createLogger(RelationshipDragEndHandler.class);
  
	private PopupPanel popup;
	private ISurfaceHandler surface;
	private int currentX;
	private int currentY;
	private Anchor currentAnchor;
	private Relationship2 currentRel;
	private boolean startNode;
	private UMLDiagramSelections diagramSelections;

	public RelationshipDragEndHandler(ISurfaceHandler surface) {
		this.surface = surface;
		popup = new PopupPanel();
		popup.setStyleName("RelationshipDragEndHandler");
		diagramSelections = new UMLDiagramSelections(surface.getEditorContext());
		diagramSelections.setSelectionHandler(this);
		popup.setWidget(diagramSelections);
		popup.setAutoHideEnabled(true);
		popup.setAnimationEnabled(true);
		
		surface.addDomHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				if (popup.isShowing()) {
					hide();
				}
			}
		}, TouchStartEvent.getType());
		
		surface.getEditorContext().getEventBus().addHandler(SurfaceMouseUpNoHandlingYetEvent.TYPE, this);
		surface.getEditorContext().getEventBus().addHandler(LibrarySelectionEvent.TYPE, this);
		surface.getEditorContext().getEventBus().addHandler(CreateElementEvent.TYPE, new CreateElementEventHandler() {
			@Override
			public void on(CreateElementEvent event) {
				// need to variate if location is given in the event
				// if not use current location
				int x = 0;
				int y = 0;
				
				if (event.getX() == 0 && event.getY() == 0) {
					x = currentX;
					y = currentY;
				} else {
					ScaledAndTranslatedPoint stp = ScaleHelpers.scaleAndTranslateScreenpoint
							(event.getX(), event.getY(), RelationshipDragEndHandler.this.surface);
					x = stp.scaledAndTranslated.x;
					y = stp.scaledAndTranslated.y;
				}
				itemSelected(event.getElementType(), x, y);
			}
		});
	}

	@Override
	public void onNotAttached(RelationshipNotAttachedEvent event) {
	  logger.start("show popoup");
		// System.out.println("onNotAttached: " + event.getRelationship() +
		// " anchor: " + event.getAnchor());
		if (!popup.isShowing()) {
//			MatrixPointJS point = MatrixPointJS.createUnscaledPoint(event.getX(), event.getY(), surface.getScaleFactor());
//			setCurrentPosition(point.getX(), point.getY());
			
//			ScaleHelpers.scaleValue(surface.getRootLayer().getTransformX(), surface.getScaleFactor())
//			ScaleHelpers.scaleValue(surface.getRootLayer().getTransformY(), surface.getScaleFactor())
			MatrixPointJS point = MatrixPointJS.createUnscaledPoint(event.getX(), event.getY(), surface.getScaleFactor());
			setCurrentPosition(point.getX() + surface.getRootLayer().getTransformX(), point.getY() + surface.getRootLayer().getTransformY());

			currentAnchor = event.getAnchor();
			currentRel = event.getRelationship();
			
			// TODO: event already knows is it start or end => to event attribute
			startNode = (event.getRelationship().getStartX() == currentX &&
									 event.getRelationship().getStartY() == currentY);
			
//			System.out.println("startNode: " + startNode + " currentX: " + currentX + " currentY: " + currentY + " this: " + this);
			
			if (!startNode && currentRel.getStartAnchor().getDiagram() != null) {
				UMLDiagramType diagtype = currentRel.getStartAnchor().getDiagram().getDiagramType();
				diagramSelections.setGroup(diagtype.getGroup());
			}
	
			// event.getRelationship();
	
//			ScaleHelpers.unscaleValue(surface.getRootLayer().getTransformX(), surface.getScaleFactor())
//			 + ScaleHelpers.unscaleValue(surface.getRootLayer().getTransformY(), surface.getScaleFactor()))
			int x = currentX;
			int y = currentY;
//			final int popupWidth = 250;
//			final int popupHeight = 115;
//			if (x + popupWidth >= surface.getAbsoluteLeft() + surface.getOffsetWidth()) {
//				x -= popupWidth;
//			}
//			if (y + popupHeight >= surface.getAbsoluteTop() + surface.getOffsetHeight()) {
//				y -= popupHeight;
//			}
			
			popup.setVisible(false);
			popup.show();
			popup.setPopupPosition(x + surface.getAbsoluteLeft() - popup.getOffsetWidth() / 2, 
					y + surface.getAbsoluteTop() - popup.getOffsetHeight() / 2);
			popup.setVisible(true);
			
			// face mouse down to release anything running on background
      surface.getMouseDiagramManager().onMouseUp(null, point);
		}
		logger.debugTime();
	}

  private void setCurrentPosition(int x, int y) {
//		MatrixPointJS p = MatrixPointJS.createUnscaledPoint(x, y, surface.getScaleFactor());
		currentX = x;
		currentY = y;
	}

	public void itemSelected(UMLDiagramType type, int x, int y) {
		logger.debug("itemSelected {}", type);
		hide();
		Diagram diagram = null;
		if (currentRel != null) {
			diagram = createDiagramFromRelationShip(type, x, y);
			surface.addAsSelected(diagram, true);
			// this is connect drop element
			currentRel.anchor(true);
			currentRel.setText(RelationshipHelpers.relationship(diagram, surface.getEditorContext(), currentRel.getStartAnchor().getDiagram()));
			surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(currentRel));
		} else {
			// surface empty case
			diagram = createDiagram(type, x, y);
			surface.addAsSelected(diagram, true);
		}
		
		// fire event show property text editor
		surface.getEditorContext().getEventBus().fireEvent(new ShowDiagramPropertyTextEditorEvent(diagram).setJustCreated(true));
		currentRel = null;
	}

	private void hide() {
		popup.hide();
	}

	private Diagram createDiagramFromRelationShip(UMLDiagramType type, int x, int y) {
		Diagram diagram = createDiagram(type, x, y);
		Point p = DiagramAnchorUtils.startCoordinate(
				currentRel.getStartX(),
				currentRel.getStartY(),
				currentRel.getEndX(),
				currentRel.getEndY(),
				startNode,
				diagram.getWidth(),
				diagram.getHeight());
		
		if (diagram instanceof SupportsRectangleShape) {
			((SupportsRectangleShape) diagram).setShape(p.x, p.y, diagram.getWidth(), diagram.getHeight());
		}
		return diagram;
	}

	private Diagram createDiagram(UMLDiagramType type, int x, int y) {
		Diagram result = null;
		net.sevenscales.editor.diagram.utils.Color current = selectColor();
		Color background = new Color(current.getRr(), current.getGg(), current.getBb(), current.getOpacity());
		Color borderColor = new Color(current.getBorR(), current.getBorG(), current.getBorB(), 1);
		Color color = new Color(current.getR(), current.getG(), current.getB(), 1);
		
		switch (type) {
		case CLASS: {
			ClassElement2 ce = new ClassElement2(surface, new RectShape(x,
					y, 1, // auto resizes
					1), // auto resizes
					type.getValue(), background, borderColor, color, true);
			result = ce;
			break;
		}
		case USE_CASE: {
      EllipseElement usecase = new EllipseElement(surface, 
          new EllipseShape(x, y, 1, 1), 
          type.getValue(), background, borderColor, color, true);
			result = usecase;
			break;
		}
		case SEQUENCE: {
			SequenceElement se = new SequenceElement(surface, 
	        new SequenceShape(x, y, 1, 1, 25),
	        type.getValue(),
	        background, borderColor, color, true);
			result = se;
			break;
		}
		case ACTOR: {
      Actor actor = new Actor(surface,
          new ActorShape(x, 
              y,
              25,
              40),
              type.getValue(),
              background, borderColor, color, 
              true);
			result = actor;
			break;
		}
		case NOTE: {
			surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, true);
			NoteElement ne = new NoteElement(surface,
	        new NoteShape(x, y, 150, 1),
	        type.getValue(),
	        background, borderColor, color, true);
			surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, false);
			result = ne;
			break;
		}
		case COMMENT_THREAD: {
			surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, true);
			CommentThreadElement ne = new CommentThreadElement(surface,
	        new CommentThreadShape(x, y, CommentThreadElement.MINIMUM_WIDTH, CommentThreadElement.MINIMUM_HEIGHT),
	        "",
	        Theme.getCommentColorScheme().getBackgroundColor().create(), 
	        Theme.getCommentColorScheme().getBorderColor().create(),
	        Theme.getCommentColorScheme().getTextColor().create(), true);
			surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, false);
			result = ne;
			break;
		}
		case TEXT: {
			result = createTextElement(x, y, type, background, borderColor, color);
			break;
			}
		case MIND_SUB_TOPIC: {
			result = createTextElement(x, y, type, background, borderColor, color);
			break;
			}
		case CHOICE: {
			ActivityChoiceElement ace = new ActivityChoiceElement(surface,
	        new ActivityChoiceShape(x, y, 32, 32),
	        type.getValue(),
	        background, borderColor, color, true);
			result = ace;
			break;
		}
		case START: {
			ActivityStart as = new ActivityStart(surface,
	        new ActivityStartShape(x, y, ActivityStart.ACTIVITY_START_RADIUS), true);
			result = as;
			break;
		}
		case END: {
			ActivityEnd ae = new ActivityEnd(surface,
	        new ActivityEndShape(x, y, ActivityEnd.ACTIVITY_END_RADIUS), true);
			result = ae;
			break;
		}
		case ACTIVITY: {
			ActivityElement ae = new ActivityElement(surface,
	        new ActivityShape(x, y, 1, 1),
	        type.getValue(),
	        background, borderColor, color, true);
			result = ae;
			break;
			}
		case DB: {
			StorageElement ae = new StorageElement(surface,
	        new DbShape(x, y, 1, 1),
	        type.getValue(),
	        background, borderColor, color, true);
			result = ae;
			break;
			}
		case PACKAGE: {
			UMLPackageElement ce = new UMLPackageElement(surface, new UMLPackageShape(x,
					y, 100, // package has no auto resizes
					40), // package has no auto resizes
					type.getValue(), background, borderColor, color, true);
			result = ce;
			break;
		}
		case MIND_CENTRAL_TOPIC: {
			MindCentralElement ae = new MindCentralElement(surface,
	        new MindCentralShape(x, y, 1, 1),
	        type.getValue(),
	        background, borderColor, color, true);
			result = ae;
			break;
		}
		case MIND_MAIN_TOPIC: {
			ActivityElement ae = new ActivityElement(surface,
	        new ActivityShape(x, y, 1, 1),
	        type.getValue(),
	        background, borderColor, color, true);
			result = ae;
			break;
			}
		}
		return result;
	}

	private net.sevenscales.editor.diagram.utils.Color selectColor() {
		// TODO enable global color when there is a generic color selection available
//		net.sevenscales.editor.diagram.utils.Color current = (net.sevenscales.editor.diagram.utils.Color) 
//				surface.getEditorContext().get(EditorProperty.CURRENT_COLOR);

		return Theme.defaultColor(); 
	}

	private Diagram createTextElement(int x, int y, UMLDiagramType type, Color background, Color borderColor, Color color) {
		surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, true);
		TextElement result = new TextElement(surface,
        new TextShape(x, y, 100, 1),
        background, borderColor, color, type.getValue(), true);
		surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, false);
		return result;
	}

	@Override
	public void on(final SurfaceMouseUpNoHandlingYetEvent event) {
		popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				ScaledAndTranslatedPoint stp = ScaleHelpers.scaleAndTranslateScreenpoint(event.getX(), event.getY(), surface);
				setCurrentPosition(stp.scaledAndTranslated.x, stp.scaledAndTranslated.y);
				popup.setPopupPosition(event.getX() + surface.getAbsoluteLeft() - offsetWidth / 2, 
								event.getY() + surface.getAbsoluteTop() - offsetHeight / 2);
				// face mouse down to release anything running on background
	      surface.getMouseDiagramManager().onMouseUp(null, stp.scaledPoint);
			}
		});
	}

	@Override
	public void onSelection(LibrarySelectionEvent event) {
		diagramSelections.filterByGroup(event.getLibrary());
	}

	@Override
	public void hidePopup() {
		hide();
	}

}
