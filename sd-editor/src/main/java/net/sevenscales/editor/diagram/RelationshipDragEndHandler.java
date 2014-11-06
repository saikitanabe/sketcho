package net.sevenscales.editor.diagram;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.js.ImageInfo;
import net.sevenscales.domain.ShapeProperty;
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
import net.sevenscales.editor.api.event.SwitchElementEvent;
import net.sevenscales.editor.api.event.SwitchElementEventHandler;
import net.sevenscales.editor.api.event.SwitchElementToEvent;
import net.sevenscales.editor.api.event.SwitchElementToEventHandler;
import net.sevenscales.editor.api.event.SwitchElementToEvent;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.api.LibraryShapes;
import net.sevenscales.editor.content.ui.UMLDiagramSelections;
import net.sevenscales.editor.content.ui.DiagramSelectionHandler;
import net.sevenscales.editor.content.ui.UMLDiagramSelections.UMLDiagramType;
import net.sevenscales.editor.content.utils.ScaleHelpers;
import net.sevenscales.editor.content.utils.ScaleHelpers.ScaledAndTranslatedPoint;
import net.sevenscales.editor.content.utils.DiagramElementFactory;
import net.sevenscales.editor.content.ClientIdHelpers;
import net.sevenscales.editor.diagram.shape.ActivityChoiceShape;
import net.sevenscales.editor.diagram.shape.ActivityEndShape;
import net.sevenscales.editor.diagram.shape.ActivityShape;
import net.sevenscales.editor.diagram.shape.ActivityStartShape;
import net.sevenscales.editor.diagram.shape.ForkShape;
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
import net.sevenscales.editor.diagram.shape.ComponentShape;
import net.sevenscales.editor.diagram.shape.ServerShape;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.diagram.utils.DiagramAnchorUtils;
import net.sevenscales.editor.diagram.utils.RelationshipHelpers;
import net.sevenscales.editor.diagram.utils.ReattachHelpers;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.ElementColor;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.uicomponents.uml.ActivityChoiceElement;
import net.sevenscales.editor.uicomponents.uml.ActivityElement;
import net.sevenscales.editor.uicomponents.uml.ActivityEnd;
import net.sevenscales.editor.uicomponents.uml.ActivityStart;
import net.sevenscales.editor.uicomponents.uml.ForkElement;
import net.sevenscales.editor.uicomponents.uml.Actor;
import net.sevenscales.editor.uicomponents.uml.ElementFactory;
import net.sevenscales.editor.uicomponents.uml.EllipseElement;
import net.sevenscales.editor.uicomponents.uml.MindCentralElement;
import net.sevenscales.editor.uicomponents.uml.NoteElement;
import net.sevenscales.editor.uicomponents.uml.CommentThreadElement;
import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.editor.uicomponents.uml.SequenceElement;
import net.sevenscales.editor.uicomponents.uml.StorageElement;
import net.sevenscales.editor.uicomponents.uml.TextElement;
import net.sevenscales.editor.uicomponents.uml.UMLPackageElement;
import net.sevenscales.editor.uicomponents.uml.ComponentElement;
import net.sevenscales.editor.uicomponents.uml.ServerElement;
import net.sevenscales.editor.uicomponents.uml.GenericElement;

import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.ui.PopupPanel;

public class RelationshipDragEndHandler implements
		RelationshipNotAttachedEventHandler, DiagramSelectionHandler, SurfaceMouseUpNoHandlingYetEventHandler, LibrarySelectionEventHandler, SwitchElementEventHandler, SwitchElementToEventHandler {
  private static SLogger logger = SLogger.createLogger(RelationshipDragEndHandler.class);
  
	private PopupPanel popup;
	private ISurfaceHandler surface;
	private int currentX;
	private int currentY;
	private Anchor currentAnchor;
	private Relationship2 currentRel;
	private boolean startNode;
	private UMLDiagramSelections diagramSelections;
	private Diagram switchFrom;

	public RelationshipDragEndHandler(ISurfaceHandler surface) {
		this.surface = surface;
		popup = new PopupPanel();
		popup.setStyleName("RelationshipDragEndHandler");
		diagramSelections = new UMLDiagramSelections(surface, this);
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
		surface.getEditorContext().getEventBus().addHandler(SwitchElementEvent.TYPE, this);
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
					x = stp.scaledAndTranslatedPoint.x;
					y = stp.scaledAndTranslatedPoint.y;
				}
				if (switchFrom == null) {
					itemSelected(event.getElementType(), event.getImageInfo(), x, y);
				} else {
					switchElement(event.getElementType(), event.getImageInfo(), x, y);
				}
			}
		});
	}

	@Override
	public void onNotAttached(RelationshipNotAttachedEvent event) {
		diagramSelections.showCommentElement();
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
			
			showPopup(x, y);
			
			// face mouse down to release anything running on background
      surface.getMouseDiagramManager().onMouseUp(null, point, 0);
		}
		logger.debugTime();
	}

	private void showPopup(int x, int y) {
		popup.setVisible(false);
		popup.show();
		int left = x + surface.getAbsoluteLeft() - popup.getOffsetWidth() / 2;
		int top = y + surface.getAbsoluteTop() - popup.getOffsetHeight() / 2;
		if (left < 0) {
			left = 20;
		}
		if (top < 0) {
			top = 20;
		}
		popup.setPopupPosition(left, top);
		popup.setVisible(true);
	}

	@Override
	public void on(SwitchElementEvent event) {
		switchFrom = event.getDiagram();
		Point screenPosition = ScaleHelpers.diagramPositionToScreenPoint(event.getDiagram(), surface);
		diagramSelections.hideCommentElement();
		showPopup(screenPosition.x, screenPosition.y);
	}

	@Override
	public void on(SwitchElementToEvent event) {
		// Diagram src = switchFrom;
		// switchFrom = null;

		// // do not delete connections!
		// // create new element
		// diagram = createDiagram(event.getElementType(), imageInfo, src.getLeft(), src.getTop());
		// AbstractDiagramFactory factory = ShapeParser.factory(item);
		// Diagram to = 
		// switch connections to point to the new element
		// delete switchFrom
	}

  private void setCurrentPosition(int x, int y) {
//		MatrixPointJS p = MatrixPointJS.createUnscaledPoint(x, y, surface.getScaleFactor());
		currentX = x;
		currentY = y;
	}

	public void itemSelected(UMLDiagramType type, ImageInfo imageInfo, int x, int y) {
		logger.debug("itemSelected {}", type);
		hide();
		Diagram diagram = null;
		if (currentRel != null) {
			diagram = createDiagramFromRelationShip(type, imageInfo, x, y);
      if (!(diagram instanceof SequenceElement)) {
        currentRel.asClosestPath();
      }
			surface.addAsSelected(diagram, true);
			// this is connect drop element
			currentRel.anchorEnd(true);
			currentRel.setType(RelationshipHelpers.relationship(diagram, surface.getEditorContext(), currentRel.getStartAnchor().getDiagram()));
			surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(currentRel));
		} else {
			// surface empty case
			diagram = createDiagram(type, imageInfo, x, y);
			surface.addAsSelected(diagram, true);
		}

		// fire event show property text editor
		surface.getEditorContext().getEventBus().fireEvent(new ShowDiagramPropertyTextEditorEvent(diagram).setJustCreated(true));
		currentRel = null;
	}

	public void switchElement(UMLDiagramType type, ImageInfo imageInfo, int x, int y) {
		hide();
		Diagram src = switchFrom;
		switchFrom = null;

		surface.beginTransaction();

		// do not delete connections!
		// create new element
		ReattachHelpers reattachHelpers = new ReattachHelpers();

		Diagram to = createDiagram(type, imageInfo, src.getLeft(), src.getTop());
		to.getDiagramItem().setClientId(null);
		ClientIdHelpers.generateClientIdIfNotSet(to.getDiagramItem(), 0, surface.getEditorContext().getGraphicalDocumentCache(), surface.getEditorContext());
		to.setText(src.getText());
		reattachHelpers.processDiagram(to);
		surface.addAsSelected(to, true);

    for (AnchorElement ae : src.getAnchors()) {
    	Relationship2 rel = ae.getRelationship();
    	if (rel != null) {
    		reattachHelpers.processDiagram(rel);
      	Diagram start = rel.getStartAnchor().getDiagram();
      	Diagram end = rel.getEndAnchor().getDiagram();
      	rel.asClosestPath();

      	String srcClientId = src.getDiagramItem().getClientId();
      	if (start != null && srcClientId.equals(start.getDiagramItem().getClientId())) {
      		rel.setStartConnectedDiagramId(to.getDiagramItem().getClientId());
      		// need to find the other end
      		reattachHelpers.processDiagram(end);
      	} else if (end != null && srcClientId.equals(end.getDiagramItem().getClientId())) {
      		rel.setEndConnectedDiagramId(to.getDiagramItem().getClientId());
      		// need to find the other end
      		reattachHelpers.processDiagram(start);
      	}
    	}
		}
		surface.getSelectionHandler().remove(src, true);
		reattachHelpers.reattachRelationshipsAndDrawClosestPath();
		surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(reattachHelpers.getRelationships()));

		surface.commitTransaction();

		boolean markAsDirty = true;
		boolean selectText = true;
		surface.getEditorContext().getEventBus().fireEvent(new ShowDiagramPropertyTextEditorEvent(to, selectText, markAsDirty));
	}

	private void hide() {
		popup.hide();
	}

	private Diagram createDiagramFromRelationShip(UMLDiagramType type, ImageInfo imageInfo, int x, int y) {
		Diagram diagram = createDiagram(type, imageInfo, x, y);
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

	private Diagram createDiagram(UMLDiagramType type, ImageInfo imageInfo, int x, int y) {
		Diagram result = null;
		ElementColor current = selectColor();
		Color background = current.getBackgroundColor().create();
		Color borderColor = current.getBorderColor().create();
		Color color = current.getTextColor().create();

		if (Tools.isSketchMode()) {
			// try first if sketch element is found
			// at first only some of the elements are supported...
			result = createGenericElement(type, x, y, background, borderColor, color);
		}

		if (result == null) {
			// try to create with legacy way
			result = createLegacyDiagram(type, imageInfo, x, y, background, borderColor, color);
		}

		return result;
	}

	private Diagram createLegacyDiagram(UMLDiagramType type, ImageInfo imageInfo, int x, int y, Color background, 
Color borderColor, Color color) {
		Diagram result = null;
		switch (type) {
		case IMAGE: {
			result = DiagramElementFactory.createImageElement(surface, imageInfo.getFilename(), imageInfo.getUrl(), x, y, imageInfo.getWidth(), imageInfo.getHeight());
			break;
		}
		case CLASS: {
			Diagram ce = ElementFactory.createClassElement(surface, new RectShape(x,
					y, 1, // auto resizes
					1), // auto resizes
					type.getValue(), background, borderColor, color, true, new DiagramItemDTO());
			result = ce;
			break;
		}
		case USE_CASE: {
      EllipseElement usecase = new EllipseElement(surface, 
          new EllipseShape(x, y, 1, 1), 
          type.getValue(), background, borderColor, color, true, new DiagramItemDTO());
			result = usecase;
			break;
		}
		case SEQUENCE: {
			SequenceElement se = new SequenceElement(surface, 
	        new SequenceShape(x, y, 1, 1, 25),
	        type.getValue(),
	        background, borderColor, color, true, new DiagramItemDTO());
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
              true,
              new DiagramItemDTO());
			result = actor;
			break;
		}
		case NOTE: {
			surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, true);
			NoteElement ne = new NoteElement(surface,
	        new NoteShape(x, y, 150, 45),
	        type.getValue(),
	        background, borderColor, color,
	        true, 
	        new DiagramItemDTO());
			surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, false);
			result = ne;
			break;
		}
		case COMMENT_THREAD: {
			surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, true);
			Tools.setCommentTool(true);
			CommentThreadElement ne = new CommentThreadElement(surface,
	        new CommentThreadShape(x, y, CommentThreadElement.MINIMUM_WIDTH, CommentThreadElement.MINIMUM_HEIGHT),
	        "",
	        Theme.getCommentThreadColorScheme().getBackgroundColor().create(), 
	        Theme.getCommentThreadColorScheme().getBorderColor().create(),
	        Theme.getCommentThreadColorScheme().getTextColor().create(), 
	        true, 
	        new DiagramItemDTO());
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
	        background, borderColor, color, true, new DiagramItemDTO());
			result = ace;
			break;
		}
		case START: {
			ActivityStart as = new ActivityStart(surface,
	        new ActivityStartShape(x, y, ActivityStart.ACTIVITY_START_RADIUS), true, new DiagramItemDTO());
			result = as;
			break;
		}
		case FORK: {
			ForkElement e = new ForkElement(surface, new ForkShape(x, y, 50, 5), Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true, new DiagramItemDTO());
			result = e;
			break;
		}
		case VFORK: {
			ForkElement e = new ForkElement(surface, new ForkShape(x, y, 5, 50, 1), Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true, new DiagramItemDTO());
			result = e;
			break;
		}
		case END: {
			ActivityEnd ae = new ActivityEnd(surface,
	        new ActivityEndShape(x, y, ActivityEnd.ACTIVITY_END_RADIUS), true, new DiagramItemDTO());
			result = ae;
			break;
		}
		case ACTIVITY: {
			ActivityElement ae = new ActivityElement(surface,
	        new ActivityShape(x, y, 1, 1),
	        type.getValue(),
	        background, borderColor, color, true, new DiagramItemDTO());
			result = ae;
			break;
			}
		case DB: {
			StorageElement ae = new StorageElement(surface,
	        new DbShape(x, y, 1, 1),
	        type.getValue(),
	        background, borderColor, color, true, new DiagramItemDTO());
			result = ae;
			break;
			}
		case PACKAGE: {
			UMLPackageElement ce = new UMLPackageElement(surface, new UMLPackageShape(x,
					y, 100, // package has no auto resizes
					40), // package has no auto resizes
					type.getValue(), background, borderColor, color, true, new DiagramItemDTO());
			result = ce;
			break;
		}
		case MIND_CENTRAL_TOPIC: {
			MindCentralElement ae = new MindCentralElement(surface,
	        new MindCentralShape(x, y, 1, 1),
	        type.getValue(),
	        background, borderColor, color, true, new DiagramItemDTO());
			result = ae;
			break;
		}
		case MIND_MAIN_TOPIC: {
			ActivityElement ae = new ActivityElement(surface,
	        new ActivityShape(x, y, 1, 1),
	        type.getValue(),
	        background, borderColor, color, true, new DiagramItemDTO());
			result = ae;
			break;
			}
		case COMPONENT: {
			ComponentElement element = new ComponentElement(surface,
	        new ComponentShape(x, y, 1, 1),
	        type.getValue(),
	        background, borderColor, color, true, new DiagramItemDTO());
			result = element;
			break;
		}
		case SERVER: {
			ServerElement element = new ServerElement(surface,
	        new ServerShape(x, y, 60, 80),
	        type.getValue(),
	        background, borderColor, color, true, new DiagramItemDTO());
			result = element;
			break;
		}
		case SMILEY:
		case FIREWALL:
		case POLYGON4:
		case POLYGON8:
		case RECT:
		case TRIANGLE:
		case CIRCLE:
		case CLOUD:
		case WBROWSER:
		case IPHONE:
		case STAR5:
		case STAR4:
		case ARROW_DOWN:
		case ARROW_RIGHT:
		case ARROW_UP:
		case ARROW_LEFT:
		case BUBBLE_LEFT:
		case BUBBLE_RIGHT:
		case ENVELOPE: {
			result = createGenericElement(type, x, y, background, borderColor, color);
			break;
		}
		}
		return result;
	}

	private Diagram createGenericElement(UMLDiagramType type, int x, int y, Color background, 
Color borderColor, Color color) {
		Diagram result = null;
		LibraryShapes.LibraryShape ls = LibraryShapes.get(type.getElementType());
		if (ls != null) {
			// there might not be generi library shape available
			// could multiply width and height
			GenericElement element = new GenericElement(surface,
	        new GenericShape(ls.elementType.getValue(), x, y, ls.width, ls.height, ls.shapeProperties, null),
	        type.getValue(),
	        background, borderColor, color, true, DiagramItemDTO.createGenericItem(ls.elementType));
			result = element;
		}
		return result;
	}

	private ElementColor selectColor() {
		// TODO enable global color when there is a generic color selection available
//		ElementColor current = (ElementColor) 
//				surface.getEditorContext().get(EditorProperty.CURRENT_COLOR);

		return Theme.defaultColor(); 
	}

	private Diagram createTextElement(int x, int y, UMLDiagramType type, Color background, Color borderColor, Color color) {
		surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, true);
		TextElement result = new TextElement(surface,
        new TextShape(x, y, 100, 34),
        background, borderColor, color, type.getValue(), true, new DiagramItemDTO());
		surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, false);
		return result;
	}

	@Override
	public void on(final SurfaceMouseUpNoHandlingYetEvent event) {
		popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				ScaledAndTranslatedPoint stp = ScaleHelpers.scaleAndTranslateScreenpoint(event.getX(), event.getY(), surface);
				setCurrentPosition(stp.scaledAndTranslatedPoint.x, stp.scaledAndTranslatedPoint.y);
				popup.setPopupPosition(event.getX() + surface.getAbsoluteLeft() - offsetWidth / 2, 
								event.getY() + surface.getAbsoluteTop() - offsetHeight / 2);
				// face mouse down to release anything running on background
	      surface.getMouseDiagramManager().onMouseUp(null, stp.scaledPoint, 0);
			}
		});
	}

	@Override
	public void onSelection(LibrarySelectionEvent event) {
		diagramSelections.sortByGroup(event.getLibrary());
	}

	@Override
	public void hidePopup() {
		hide();
	}

	@Override
	public void addScrollHandler(WhenScrolledHandler scrollHandler) {
		diagramSelections.addScrollHandler(scrollHandler);
	}

}
