package net.sevenscales.editor.diagram;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.js.ImageInfo;
import net.sevenscales.domain.js.JsShapeConfig;
import net.sevenscales.domain.ElementType;
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
// import net.sevenscales.editor.content.ui.UMLDiagramSelections;
// import net.sevenscales.editor.content.ui.ShapeContextMenu;
import net.sevenscales.editor.content.ui.DiagramSelectionHandler;
import net.sevenscales.editor.content.ui.UMLDiagramType;
import net.sevenscales.editor.content.utils.ScaleHelpers;
import net.sevenscales.editor.content.utils.ScaleHelpers.ScaledAndTranslatedPoint;
import net.sevenscales.editor.content.utils.DiagramElementFactory;
import net.sevenscales.editor.content.ClientIdHelpers;
import net.sevenscales.editor.diagram.utils.DiagramAnchorUtils;
import net.sevenscales.editor.diagram.utils.RelationshipHelpers;
import net.sevenscales.editor.diagram.utils.ReattachHelpers;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.SupportsRectangleShape;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.uicomponents.uml.Relationship2;

import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.ui.PopupPanel;

public class RelationshipDragEndHandler implements
		RelationshipNotAttachedEventHandler, DiagramSelectionHandler, SurfaceMouseUpNoHandlingYetEventHandler, LibrarySelectionEventHandler, SwitchElementEventHandler, SwitchElementToEventHandler
		 {
  private static SLogger logger = SLogger.createLogger(RelationshipDragEndHandler.class);
  
	private PopupPanel popup;
	private ISurfaceHandler surface;
	private int currentX;
	private int currentY;
	private Anchor currentAnchor;
	private Relationship2 currentRel;
	private boolean startNode;
	// private UMLDiagramSelections diagramSelections;
	// private ShapeContextMenu diagramSelections;
	private Diagram switchFrom;

	public RelationshipDragEndHandler(ISurfaceHandler surface) {
		this.surface = surface;
		popup = new PopupPanel();
		popup.setStyleName("RelationshipDragEndHandler");
		// diagramSelections = new UMLDiagramSelections(surface, this);
		// diagramSelections = new ShapeContextMenu();
		// popup.setWidget(diagramSelections);
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
				newShape(event.getElementType(), event.getShapeConfig(), event.getImageInfo(), event.getX(), event.getY());
			}
		});

		init(this);
	}

	private native void init(RelationshipDragEndHandler me)/*-{
		$wnd.globalStreams.newShapeStream.onValue(function(data) {
			me.@net.sevenscales.editor.diagram.RelationshipDragEndHandler::newShape(Ljava/lang/String;Lnet/sevenscales/domain/js/JsShapeConfig;Lnet/sevenscales/domain/js/ImageInfo;II)(data.element_type, data.shape_config, data.image_info, data.x, data.y)
		})

	  $wnd.globalStreams.contextMenuStream.filter(function(v) {
	    return v && v.type==='context-menu-hide'
	  }).onValue(function(v) {
	    me.@net.sevenscales.editor.diagram.RelationshipDragEndHandler::hideContextMenu()()
	  })

	}-*/;

	private void hideContextMenu() {
		// reset always current relationship when popup is hidden
		// otherwise might create shape to previous connection
		currentRel = null;
	}	

	// private void newShape(String elementType, double x, double y) {

	// }

	private void newShape(String elementType, JsShapeConfig shapeConfig, ImageInfo imageInfo, int eventX, int eventY) {
		// need to variate if location is given in the event
		// if not use current location
		int x = 0;
		int y = 0;
		
		if (eventX == 0 && eventY == 0) {
			x = currentX;
			y = currentY;
		} else {
			ScaledAndTranslatedPoint stp = ScaleHelpers.scaleAndTranslateScreenpoint
					(eventX, eventY, RelationshipDragEndHandler.this.surface);
			x = stp.scaledAndTranslatedPoint.x;
			y = stp.scaledAndTranslatedPoint.y;
		}
		if (switchFrom == null) {
			itemSelected(elementType, shapeConfig, imageInfo, x, y);
		} else {
			switchElement(elementType, shapeConfig, imageInfo, x, y);
		}
	}

	@Override
	public void onNotAttached(RelationshipNotAttachedEvent event) {
		// diagramSelections.showCommentElement();
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

			// >>>>> commented 4.3.2015 when started ShapeContextMenu			
			// if (!startNode && currentRel.getStartAnchor().getDiagram() != null) {
			// 	UMLDiagramType diagtype = currentRel.getStartAnchor().getDiagram().getDiagramType();
			// 	diagramSelections.setGroup(diagtype.getGroup());
			// }
			// <<<<> comment end
	
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
		_showShapeContextMenu(x + surface.getAbsoluteLeft(), y + surface.getAbsoluteTop());
		// popup.setVisible(false);
		// popup.show();
		// int left = x + surface.getAbsoluteLeft() - popup.getOffsetWidth() / 2;
		// int top = y + surface.getAbsoluteTop() - popup.getOffsetHeight() / 2;
		// if (left < 0) {
		// 	left = 20;
		// }
		// if (top < 0) {
		// 	top = 20;
		// }
		// popup.setPopupPosition(left, top);
		// popup.setVisible(true);
	}

	private native void _showShapeContextMenu(int x, int y)/*-{
		$wnd.globalStreams.shapeContextStream.push({x:x, y:y})
	}-*/;

	@Override
	public void on(SwitchElementEvent event) {
		switchFrom = event.getDiagram();
		if (!switchFrom.getDiagramItem().getType().equals(ElementType.CHILD_TEXT.getValue())) {
			Point screenPosition = ScaleHelpers.diagramPositionToScreenPoint(event.getDiagram(), surface);
			// >>>>>>> Commented out 4.3.2015
			// diagramSelections.hideCommentElement();
			// >>>>>>> Commented out 4.3.2015 end
			showPopup(screenPosition.x, screenPosition.y);
		}
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

	public void itemSelected(String elementType, JsShapeConfig shapeConfig, ImageInfo imageInfo, int x, int y) {
		logger.debug("itemSelected {}", elementType);
		hide();
		Diagram diagram = null;
		checkCurrentRelationshipexists();

		if (currentRel != null) {
			diagram = createDiagramFromRelationShip(elementType, shapeConfig, imageInfo, x, y);
			applyClosestPath(currentRel);
			surface.addAsSelected(diagram, true);
			// this is connect drop element
			currentRel.anchorEnd(true);
			currentRel.setType(RelationshipHelpers.relationship(diagram, surface.getEditorContext(), currentRel.getStartAnchor().getDiagram()));
			surface.getEditorContext().getEventBus().fireEvent(new PotentialOnChangedEvent(currentRel));
		} else {
			// surface empty case
			diagram = DiagramFactory.getFactory(surface)
				.addDiagramAsSelected(elementType, shapeConfig, imageInfo, x, y, null, null, 0);
		}

		// fire event show property text editor
		if (shapeConfig != null && shapeConfig.isOpenEditor()) {
			surface.getEditorContext().getEventBus().fireEvent(
				new ShowDiagramPropertyTextEditorEvent(diagram).setJustCreated(true)
			);
		}
		currentRel = null;
	}

	private void checkCurrentRelationshipexists() {
		if (currentRel != null) {
			Diagram d = surface.createDiagramSearch().findByClientId(currentRel.getDiagramItem().getClientId());
			if (d == null) {
				// relationship has been deleted through undo or just deleted
				currentRel = null;
			}
		}
	}

	public void switchElement(String elementType, JsShapeConfig shapeConfig, ImageInfo imageInfo, int x, int y) {
		hide();
		Diagram src = switchFrom;
		switchFrom = null;

		surface.beginTransaction();

		// do not delete connections!
		// create new element
		ReattachHelpers reattachHelpers = new ReattachHelpers();

		int initialShapeProperties = src.getDiagramItem().getShapeProperties() != null ? src.getDiagramItem().getShapeProperties() : 0;

		Diagram to = DiagramFactory.getFactory(surface)
			.createDiagram(elementType, shapeConfig, imageInfo, src.getLeft(), src.getTop(), src.getWidth(), src.getHeight(), initialShapeProperties);
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

      	applyClosestPath(rel);

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

	private void applyClosestPath(Relationship2 rel) {
    if (!rel.isOneOfEndSequenceElement()) {
      rel.asClosestPath();
    }
  }

	private void hide() {
		popup.hide();
	}

	private Diagram createDiagramFromRelationShip(String elementType, JsShapeConfig shapeConfig, ImageInfo imageInfo, int x, int y) {
		Diagram diagram = DiagramFactory.getFactory(surface)
			.createDiagram(elementType, shapeConfig, imageInfo, x, y, null, null, 0);
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

	@Override
	public void on(final SurfaceMouseUpNoHandlingYetEvent event) {
		_showShapeContextMenu(event.getX() + surface.getAbsoluteLeft(), event.getY() + surface.getAbsoluteTop());
		// popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
		// 	@Override
		// 	public void setPosition(int offsetWidth, int offsetHeight) {
		// 		ScaledAndTranslatedPoint stp = ScaleHelpers.scaleAndTranslateScreenpoint(event.getX(), event.getY(), surface);
		// 		setCurrentPosition(stp.scaledAndTranslatedPoint.x, stp.scaledAndTranslatedPoint.y);
		// 		popup.setPopupPosition(event.getX() + surface.getAbsoluteLeft() - offsetWidth / 2, 
		// 						event.getY() + surface.getAbsoluteTop() - offsetHeight / 2);
		// 		// face mouse down to release anything running on background
	 //      surface.getMouseDiagramManager().onMouseUp(null, stp.scaledPoint, 0);
		// 	}
		// });
	}

	@Override
	public void onSelection(LibrarySelectionEvent event) {
		// >>>>>>>> Commented out 4.3.2015
		// diagramSelections.sortByGroup(event.getLibrary());
		// >>>>>>>> Commented out 4.3.2015 end
	}

	@Override
	public void hidePopup() {
		hide();
	}

	@Override
	public void addScrollHandler(WhenScrolledHandler scrollHandler) {
		// >>>>> Commented out 4.3.2015
		// diagramSelections.addScrollHandler(scrollHandler);
		// <<<<< Commented out end
	}

}
