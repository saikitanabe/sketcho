package net.sevenscales.editor.diagram;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.domain.js.ImageInfo;
import net.sevenscales.domain.js.JsShape;
import net.sevenscales.domain.js.JsShapeConfig;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.LibraryShapes;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.content.utils.DiagramElementFactory;
import net.sevenscales.editor.content.utils.Rgb;
import net.sevenscales.editor.content.utils.ShapeParser;
import net.sevenscales.editor.diagram.shape.ActivityChoiceShape;
import net.sevenscales.editor.diagram.shape.ActivityEndShape;
import net.sevenscales.editor.diagram.shape.ActivityShape;
import net.sevenscales.editor.diagram.shape.ActivityStartShape;
import net.sevenscales.editor.diagram.shape.ActorShape;
import net.sevenscales.editor.diagram.shape.CommentThreadShape;
import net.sevenscales.editor.diagram.shape.ComponentShape;
import net.sevenscales.editor.diagram.shape.DbShape;
import net.sevenscales.editor.diagram.shape.ForkShape;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.diagram.shape.HorizontalPartitionShape;
import net.sevenscales.editor.diagram.shape.MindCentralShape;
import net.sevenscales.editor.diagram.shape.NoteShape;
import net.sevenscales.editor.diagram.shape.RectContainerShape;
import net.sevenscales.editor.diagram.shape.RectShape;
import net.sevenscales.editor.diagram.shape.SequenceShape;
import net.sevenscales.editor.diagram.shape.ServerShape;
import net.sevenscales.editor.diagram.shape.TextShape;
import net.sevenscales.editor.diagram.shape.UMLPackageShape;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.ElementColor;
import net.sevenscales.editor.uicomponents.uml.ActivityChoiceElement;
import net.sevenscales.editor.uicomponents.uml.ActivityElement;
import net.sevenscales.editor.uicomponents.uml.ActivityEnd;
import net.sevenscales.editor.uicomponents.uml.ActivityStart;
import net.sevenscales.editor.uicomponents.uml.Actor;
import net.sevenscales.editor.uicomponents.uml.ClassElement2;
import net.sevenscales.editor.uicomponents.uml.CommentThreadElement;
import net.sevenscales.editor.uicomponents.uml.ComponentElement;
import net.sevenscales.editor.uicomponents.uml.ForkElement;
import net.sevenscales.editor.uicomponents.uml.HorizontalPartitionElement;
import net.sevenscales.editor.uicomponents.uml.IShapeGroup;
import net.sevenscales.editor.uicomponents.uml.MindCentralElement;
import net.sevenscales.editor.uicomponents.uml.NoteElement;
import net.sevenscales.editor.uicomponents.uml.RectBoundaryElement;
import net.sevenscales.editor.uicomponents.uml.SequenceElement;
import net.sevenscales.editor.uicomponents.uml.ServerElement;
import net.sevenscales.editor.uicomponents.uml.ShapeCache;
import net.sevenscales.editor.uicomponents.uml.ShapeGroup;
import net.sevenscales.editor.uicomponents.uml.StorageElement;
import net.sevenscales.editor.uicomponents.uml.TextElement;
import net.sevenscales.editor.uicomponents.uml.UMLPackageElement;
import net.sevenscales.editor.utils.DiagramItemConfiguration;


public class DiagramFactory {

	private static DiagramFactory instance;

	private ISurfaceHandler surface;

	public static DiagramFactory getFactory(ISurfaceHandler surface) {
		if (instance == null) {
			instance = new DiagramFactory(surface);
		}
		return instance;
	}

	public DiagramFactory(ISurfaceHandler surface) {
		this.surface = surface;
	}

	public Diagram addDiagramAsSelected(String elementType, JsShapeConfig shapeConfig, ImageInfo imageInfo, int x, int y, Integer width, Integer height, int initialProperties) {
		ElementColor current = selectColor();
		Diagram result = createDiagram(elementType, shapeConfig, imageInfo, x, y, width, height, initialProperties, current);

		if (result != null) {
			surface.addAsSelected(result, true);
		}

		return result;
	}

	public Diagram addDiagram(String elementType, JsShapeConfig shapeConfig, int x, int y, Integer width, Integer height, int initialProperties) {
		ElementColor current = selectColor();
		Diagram result = createDiagram(elementType, shapeConfig, /*imageInfo*/ null, x, y, width, height, initialProperties, current);

		if (result != null) {
			surface.add(result, true);
		}

		return result;
	}

	public Diagram addDiagram(String elementType, JsShapeConfig shapeConfig, ImageInfo imageInfo, int x, int y, Integer width, Integer height, int initialProperties) {
		ElementColor current = selectColor();
		Diagram result = createDiagram(elementType, shapeConfig, imageInfo, x, y, width, height, initialProperties, current);

		if (result != null) {
			surface.add(result, true);
		}

		return result;
	}

	public Diagram createDiagram(
		String elementType,
		JsShapeConfig shapeConfig, 
		int x,
		int y,
		Integer width,
		Integer height,
		int initialProperties
	) {
		ElementColor current = selectColor();
		return createDiagram(elementType, shapeConfig, /*imageInfo*/ null, x, y, width, height, initialProperties, current);
	}

	public Diagram createDiagram(String elementType, JsShapeConfig shapeConfig, ImageInfo imageInfo, int x, int y, Integer width, Integer height, int initialProperties) {
		ElementColor current = selectColor();
		return createDiagram(elementType, shapeConfig, imageInfo, x, y, width, height, initialProperties, current);
	}

	public Diagram createDiagram(String elementType, JsShapeConfig shapeConfig, ImageInfo imageInfo, int x, int y, Integer width, Integer height, int initialProperties, ElementColor elementColor) {
		Diagram result = null;
		Color background = elementColor.getBackgroundColor().create();
		Color borderColor = elementColor.getBorderColor().create();
		Color color = elementColor.getTextColor().create();

		if (Tools.isSketchMode()) {
			// try first if sketch element is found
			// at first only some of the elements are supported...
			result = createGenericElement(elementType, x, y, width, height, background, borderColor, color, shapeConfig, initialProperties);
		}

		if (result == null) {
			// try to create with legacy way
			result = createLegacyDiagram(elementType, imageInfo, x, y, width, height, background, borderColor, color, shapeConfig, initialProperties);
		}

		return result;
	}

	public Diagram createLegacyDiagram(String elementType, ImageInfo imageInfo, int x, int y, Integer width, Integer height, Color background, Color borderColor, Color color, JsShapeConfig shapeConfig, int initialProperties) {
		Diagram result = null;
		String defaultText = "";
		if (shapeConfig != null && shapeConfig.isDefaultTextDefined()) {
			defaultText = shapeConfig.getDefaultText();
		}

		background = DiagramFactory.defaultBgColor(background, shapeConfig);
		borderColor = DiagramFactory.defaultBorderColor(borderColor, shapeConfig);

		if (width == null && height == null && shapeConfig.isTargetSizeDefined()) {
			width = (int) shapeConfig.getTargetWidth();
			height = (int) shapeConfig.getTargetHeight();
		}

		if (ElementType.IMAGE.getValue().equals(elementType)) {
			result = DiagramElementFactory.createImageElement(surface, imageInfo.getFilename(), imageInfo.getUrl(), x, y, imageInfo.getWidth(), imageInfo.getHeight());
		} else if (ElementType.CLASS.getValue().equals(elementType)) {
			Diagram ce = new ClassElement2(surface, new RectShape(x,
					y, 1, // auto resizes
					1), // auto resizes
					defaultText, background, borderColor, color, true, new DiagramItemDTO());
			result = ce;
		} else if (ElementType.SEQUENCE.getValue().equals(elementType)) {
			SequenceElement se = new SequenceElement(surface, 
	        new SequenceShape(x, y, 1, 1, 25),
	        defaultText,
	        background, borderColor, color, true, new DiagramItemDTO());
			result = se;
		} else if (ElementType.ACTOR.getValue().equals(elementType)) {
      Actor actor = new Actor(surface,
          new ActorShape(x, 
              y,
              25,
              40),
              defaultText,
              background, borderColor, color, 
              true,
              new DiagramItemDTO());
			result = actor;
		} else if (ElementType.NOTE.getValue().equals(elementType)) {
			int cwidth = 150;
			int cheight = 45;

			if (width != null) {
				cwidth = width;
			}
			if (height != null) {
				cheight = height;
			}

			surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, true);
			NoteElement ne = new NoteElement(surface,
	        new NoteShape(x, y, cwidth, cheight),
	        defaultText,
	        background, borderColor, color,
	        true, 
	        new DiagramItemDTO());
			surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, false);
			result = ne;
		} else if (ElementType.COMMENT_THREAD.getValue().equals(elementType)) {
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
		} else if (ElementType.TEXT_ITEM.getValue().equals(elementType)) {
			result = createTextElement(x, y, width, height, defaultText, background, borderColor, color, initialProperties);
		// } else if (ElementType.MIND_SUB_TOPIC.getValue().equals(elementType)) {
		// 	result = createTextElement(x, y, elementType, background, borderColor, color);
		} else if (ElementType.CHOICE.getValue().equals(elementType)) {
			ActivityChoiceElement ace = new ActivityChoiceElement(surface,
	        new ActivityChoiceShape(x, y, 32, 32),
	        defaultText,
	        background, borderColor, color, true, new DiagramItemDTO());
			result = ace;
		} else if (ElementType.ACTIVITY_START.getValue().equals(elementType)) {
			ActivityStart as = new ActivityStart(surface,
	        new ActivityStartShape(x, y, ActivityStart.ACTIVITY_START_RADIUS), true, new DiagramItemDTO());
			result = as;
		} else if (ElementType.FORK_HORIZONTAL.getValue().equals(elementType)) {
			ForkElement e = new ForkElement(surface, new ForkShape(x, y, 50, 5), Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true, new DiagramItemDTO());
			result = e;
		} else if (ElementType.FORK_VERTICAL.getValue().equals(elementType)) {
			ForkElement e = new ForkElement(surface, new ForkShape(x, y, 5, 50, 1), Theme.createDefaultBackgroundColor(), Theme.createDefaultBorderColor(), Theme.createDefaultTextColor(), true, new DiagramItemDTO());
			result = e;
		} else if (ElementType.ACTIVITY_END.getValue().equals(elementType)) {
			ActivityEnd ae = new ActivityEnd(surface,
	        new ActivityEndShape(x, y, ActivityEnd.ACTIVITY_END_RADIUS), true, new DiagramItemDTO());
			result = ae;
		} else if (ElementType.ACTIVITY.getValue().equals(elementType)) {
			ActivityElement ae = new ActivityElement(surface,
	        new ActivityShape(x, y, 1, 1),
	        defaultText,
	        background, borderColor, color, true, new DiagramItemDTO());
			result = ae;
		} else if (ElementType.STORAGE.getValue().equals(elementType)) {
			StorageElement ae = new StorageElement(surface,
	        new DbShape(x, y, 1, 1),
	        defaultText,
	        background, borderColor, color, true, new DiagramItemDTO());
			result = ae;
		} else if (ElementType.PACKAGE.getValue().equals(elementType)) {
			UMLPackageElement ce = new UMLPackageElement(surface, new UMLPackageShape(x,
					y, 100, // package has no auto resizes
					40), // package has no auto resizes
					defaultText, background, borderColor, color, true, new DiagramItemDTO());
			result = ce;
		} else if (ElementType.HORIZONTAL_PARTITION.getValue().equals(elementType)) {
			result = new HorizontalPartitionElement(surface,
        		new HorizontalPartitionShape(x, y, 170, 70),
            defaultText,
            background,
            borderColor,
            color,
        	  true,
        	  new DiagramItemDTO());			
		} else if (ElementType.VERTICAL_PARTITION.getValue().equals(elementType)) {
			result = new RectBoundaryElement(surface,
        		new RectContainerShape(x, y, 170, 225),
            defaultText,
            background,
            borderColor,
            color,
        	  true,
        	  new DiagramItemDTO());			
		} else if (ElementType.MIND_CENTRAL.getValue().equals(elementType)) {
			MindCentralElement ae = new MindCentralElement(surface,
	        new MindCentralShape(x, y, 1, 1),
	        defaultText,
	        background, borderColor, color, true, new DiagramItemDTO());
			result = ae;
		// } else if (ElementType.ACTIVITY.getValue().equals(elementType)) {
		// 	ActivityElement ae = new ActivityElement(surface,
	 //        new ActivityShape(x, y, 1, 1),
	 //        elementType,
	 //        background, borderColor, color, true, new DiagramItemDTO());
		// 	result = ae;
		} else if (ElementType.COMPONENT.getValue().equals(elementType)) {
			ComponentElement element = new ComponentElement(surface,
	        new ComponentShape(x, y, 1, 1),
	        defaultText,
	        background, borderColor, color, true, new DiagramItemDTO());
			result = element;
		} else if (ElementType.SERVER.getValue().equals(elementType)) {
			ServerElement element = new ServerElement(surface,
	        new ServerShape(x, y, 60, 80),
	        defaultText,
	        background, borderColor, color, true, new DiagramItemDTO());
			result = element;
		} else {
		// case USE_CASE:
		// case SMILEY:
		// case FIREWALL:
		// case POLYGON4:
		// case POLYGON8:
		// case RECT:
		// case TRIANGLE:
		// case CIRCLE:
		// case CLOUD:
		// case WBROWSER:
		// case IPHONE:
		// case STAR5:
		// case STAR4:
		// case ARROW_DOWN:
		// case ARROW_RIGHT:
		// case ARROW_UP:
		// case ARROW_LEFT:
		// case BUBBLE_LEFT:
		// case BUBBLE_RIGHT:
		// case ENVELOPE: {
			result = createGenericElement(elementType, x, y, null, null, background, borderColor, color, shapeConfig, 0);
			// break;
		// }
		}
		return result;
	}

	/**
	* @width optionally give width that overrides default width
	* @height optionally give height that overrides default height
	*/
	public Diagram createGenericElement(
		String elementType,
		int x,
		int y,
		Integer width,
		Integer height,
		Color background, 
		Color borderColor,
		Color color,
		JsShapeConfig shapeConfig,
		int initialProperties) {

		Diagram result = null;
		IShapeGroup proxy = ShapeCache.get(elementType, Tools.isSketchMode());
		ShapeGroup shapeGroup = proxy.getShape();

		double cwidth = 0;
		double cheight = 0;

		if (shapeGroup != null) {
			// there might not be generi library shape available
			// could multiply width and height

			String defaultText = shapeGroup.getDefaultText();

			if (shapeConfig != null && shapeConfig.isDefaultTextDefined()) {
				defaultText = shapeConfig.getDefaultText();
			}

			background = DiagramFactory.defaultBgColor(background, shapeConfig);
			borderColor = DiagramFactory.defaultBorderColor(borderColor, shapeConfig);

			if (cwidth == 0 || cheight == 0) {
				// if width or height is not set then get size from svg shape directly 
				cwidth = shapeGroup.width;
				cheight = shapeGroup.height;
			}

			if (shapeGroup.isTargetSizeDefined()) {
				// shape can override settings
				cwidth = shapeGroup.getShapeConfig().getTargetWidth();
				cheight = shapeGroup.getShapeConfig().getTargetHeight();
			}

			if (shapeConfig != null && shapeConfig.isTargetSizeDefined()) {
				// menu can have own configuration
				cwidth = shapeConfig.getTargetWidth();
				cheight = shapeConfig.getTargetHeight();
			}

			if (width != null) {
				cwidth = width;
			}
			if (height != null) {
				cheight = height;
			}

			result = _createGenericElement(shapeGroup.elementType, x, y, (int) cwidth, (int) cheight, initialProperties, background, borderColor, color, defaultText);
		}

		if (result == null && shapeGroup == null && Tools.isSketchMode()) {
			// exception cases that are not drawn using plain svg
			LibraryShapes.LibraryShape ls = LibraryShapes.getDefaultShape(elementType);
			if (ls != null) {

				if (shapeConfig != null && shapeConfig.isTargetSizeDefined()) {
					// menu can have own configuration
					width = (int) shapeConfig.getTargetWidth();
					height = (int) shapeConfig.getTargetHeight();
				}


				if (width == null || height == null) {
					// if width or height is not set then get size from svg shape directly 
					width = ls.width;
					height = ls.height;
				}

				// try crating through static code element mapping
				result = _createGenericElement(elementType, x, y, (int) width, (int) height, initialProperties, background, borderColor, color, "");
			}
		}
		return result;
	}

	private Diagram createTextElement(int x, int y, Integer width, Integer height, String sampleText, Color background, Color borderColor, Color color, int initialProperties) {
		int cwidth = 100;
		int cheight = 34;

		if (width != null) {
			cwidth = width;
		}
		if (height != null) {
			cheight = height;
		}

		// surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, true);
		DiagramItemDTO item = new DiagramItemDTO();
		item.setShapeProperties(initialProperties);
		TextElement result = new TextElement(surface,
        new TextShape(x, y, cwidth, cheight),
        background, borderColor, color, sampleText, true, item);
		// surface.getEditorContext().set(EditorProperty.ON_SURFACE_LOAD, false);
		return result;
	}

	private Diagram _createGenericElement(String elementType, int x, int y, int width, int height, Integer properties, Color background, Color borderColor, Color color, String defaultText) {
    DiagramItemDTO item = LibraryShapes.createByType(elementType);
    DiagramItemConfiguration.setColors(item, background, borderColor, color);
    item.setText(defaultText);
    item.setShape(createshape(elementType, x, y, width, height, properties));

    item.setShapeProperties(combineDynamicProperties(item.getShapeProperties(), properties));

    return ShapeParser.createDiagramElement(item, surface);
	}

	private Integer combineDynamicProperties(Integer oldProps, Integer newProps) {
		ShapeProperty textAlign = ShapeProperty.textAlignProperty(newProps);
		if (oldProps == null) {
			return newProps;
		}

		if (textAlign != null) {
			return oldProps | textAlign.getValue();
		}

		return oldProps;
	}

	private String createshape(String elementType, int x, int y, int width, int height, Integer properties) {
		if (ElementType.SEQUENCE.getValue().equals(elementType)) {
			return new SequenceShape(x, y, width, height, 20).toString();
		} else {
			return new GenericShape(elementType, x, y, width, height, properties, null).toString();
		}
	}

	private ElementColor selectColor() {
		// TODO enable global color when there is a generic color selection available
//		ElementColor current = (ElementColor) 
//				surface.getEditorContext().get(EditorProperty.CURRENT_COLOR);

		return Theme.defaultColor(); 
	}

	public void saveBoardShape(String elementType, IBoardSaved callback) {
  	if (ShapeCache.hasShape(elementType)) {
  		callback.saved(elementType);
  	} else {
  		// save shape on board
  		_saveBoardShape(this, elementType, Tools.getCurrentSketchMode(), callback);
  	}
	}

	public void saveBoardShapes(List<? extends IDiagramItemRO> items, IBoardSaved callback) {
		List<String> elementTypes = missingShapes(items);
		if (elementTypes.size() > 0) {
			_saveBoardShapes(
				this,
				toJsArrayString(elementTypes),
				Tools.getCurrentSketchMode(),
				callback
			);
		} else {
			callback.savedAll();
		}
	}

	private List<String> missingShapes(List<? extends IDiagramItemRO> items) {
		List<String> result = new ArrayList<String>();

		for (IDiagramItemRO item : items) {
			if (!ElementType.RELATIONSHIP.getValue().equals(item.getType()) 
					&& !ElementType.CHILD_TEXT.getValue().equals(item.getType())
					&& !ElementType.FREEHAND2.getValue().equals(item.getType())
					&& !ShapeCache.hasShape(item.getType())) {
				result.add(item.getType());
			}
		}
		return result;
	}

	private JsArrayString toJsArrayString(List<String> list) {
		JsArrayString result = JavaScriptObject.createArray().cast();
		for (String i : list) {
			result.push(i);
		}
		return result;
	}

  private native void _saveBoardShapes(DiagramFactory me, JsArrayString elementTypes, int shapeType, IBoardSaved callback)/*-{
  	if (typeof $wnd.libraryService.saveBoardShapes != 'undefined') {
  		$wnd.libraryService.saveBoardShapes({
  			board_id: $wnd.currentBoard().boardId,
  			element_types: elementTypes,
  			shape_type: shapeType
  		}).then(function(data) {
  			if (data.ok == 0) {
	  			me.@net.sevenscales.editor.diagram.DiagramFactory::shapesSavedToBoard(Lnet/sevenscales/editor/diagram/IBoardSaved;)(callback)
  			} else {
  				console.error("Failed save board shape...", elementTypes)
  			}
  		})
  	}
  }-*/;

  private void shapesSavedToBoard(IBoardSaved callback) {
		callback.savedAll();
  }

  private native void _saveBoardShape(DiagramFactory me, String elementType, int shapeType, IBoardSaved callback)/*-{
  	if (typeof $wnd.libraryService.saveBoardShape != 'undefined') {
  		$wnd.libraryService.saveBoardShape({
  			board_id: $wnd.currentBoard().boardId,
  			element_type: elementType,
  			shape_type: shapeType
  		}).then(function(data) {
  			if (data.ok == 0) {
	  			me.@net.sevenscales.editor.diagram.DiagramFactory::shapeSavedToBoard(Lnet/sevenscales/domain/js/JsShape;Lnet/sevenscales/editor/diagram/IBoardSaved;)(data.shape, callback)
  			} else {
  				console.error("Failed save board shape...", shape.et)
  			}
  		})
  	}
  }-*/;

  private void shapeSavedToBoard(JsShape shape, IBoardSaved callback) {
		ShapeCache.addShape(shape);
		callback.saved(shape.getElementType());
  }

  public static Color defaultBgColor(Color background, JsShapeConfig shapeConfig) {
		if (background.opacity == 0 && shapeConfig != null && shapeConfig.isDefaultBgColor()) {
			Color bg = Color.hexToColor(shapeConfig.getDefaultBgColor());
			background = bg;
		}

		return background;
  }

  public static Color defaultBorderColor(Color color, JsShapeConfig shapeConfig) {
		if (shapeConfig != null && shapeConfig.isDefaultBorderColor()) {
			Color c = Color.hexToColor(shapeConfig.getDefaultBorderColor());
			color = c;
		}

		return color;
  }

}