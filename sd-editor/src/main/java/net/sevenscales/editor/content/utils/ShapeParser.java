package net.sevenscales.editor.content.utils;

import java.util.logging.Level;

import com.google.gwt.logging.client.LogConfiguration;

import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.uicomponents.uml.Shapes;


public class ShapeParser {
	private static SLogger logger = SLogger.createLogger(ShapeParser.class);

	private static class ParserMap {
		ElementType elementType;
		AbstractDiagramFactory factory;

		ParserMap(ElementType elementType, AbstractDiagramFactory factory) {
			this.elementType = elementType;
			this.factory = factory;
		}
	}

	private static ParserMap[] PARSER_MAP = {
		new ParserMap(ElementType.ELLIPSE, new AbstractDiagramFactory.EllipseFactory()),
		new ParserMap(ElementType.USE_CASE, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.SEQUENCE, new AbstractDiagramFactory.SequenceFactory()),
		// new ParserMap(ElementType.COMPONENT, new AbstractDiagramFactory.ComponentFactory()),
		// new ParserMap(ElementType.SERVER, new AbstractDiagramFactory.ServerFactory()),
		// new ParserMap(ElementType.CLASS, new AbstractDiagramFactory.ClassFactory()),
		// new ParserMap(ElementType.NOTE, new AbstractDiagramFactory.NoteFactory()),
		new ParserMap(ElementType.CHOICE, new AbstractDiagramFactory.ActivityChoiceFactory()),
		new ParserMap(ElementType.ACTIVITY_START, new AbstractDiagramFactory.ActivityStartFactory()),
		new ParserMap(ElementType.ACTIVITY_END, new AbstractDiagramFactory.ActivityEndFactory()),
		new ParserMap(ElementType.ACTIVITY, new AbstractDiagramFactory.ActivityFactory()),
		// new ParserMap(ElementType.MIND_CENTRAL, new AbstractDiagramFactory.MindCentralFactory()),
		// new ParserMap(ElementType.STORAGE, new AbstractDiagramFactory.StorageFactory()),
		// new ParserMap(ElementType.COMMENT, new AbstractDiagramFactory.CommentFactory()),
		new ParserMap(ElementType.TEXT_ITEM, new AbstractDiagramFactory.TextItemFactory()),
		new ParserMap(ElementType.CHILD_TEXT, new AbstractDiagramFactory.ChildTextItemFactory()),
		// ST 9.3.2018: Legacy actor didn't work in realtime collaboration and when resizing
		// replaced with SVG Generic Element Actor icon.
		// new ParserMap(ElementType.ACTOR, new AbstractDiagramFactory.ActorFactory()),
		new ParserMap(ElementType.RELATIONSHIP, new AbstractDiagramFactory.RelationshipFactory()),
		new ParserMap(ElementType.FREEHAND, new AbstractDiagramFactory.FreehandFactory()),
		new ParserMap(ElementType.PACKAGE, new AbstractDiagramFactory.PackageFactory()),
		new ParserMap(ElementType.VERTICAL_PARTITION, new AbstractDiagramFactory.VerticalPartitionFactory()),
		new ParserMap(ElementType.COMMENT_THREAD, new AbstractDiagramFactory.CommentThreadFactory()),
		new ParserMap(ElementType.HORIZONTAL_PARTITION, new AbstractDiagramFactory.HorizontalPartitionFactory()),
		new ParserMap(ElementType.FORK, new AbstractDiagramFactory.ForkFactory()),
		new ParserMap(ElementType.IMAGE, new AbstractDiagramFactory.ImageFactory()),
		new ParserMap(ElementType.STAR4, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.STAR5, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.ENVELOPE, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.TRIANGLE, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.CLOUD, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.FIREWALL, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.BUBBLE, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.BUBBLE_R, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.CIRCLE, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.SMILEY, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.POLYGON4, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.POLYGON8, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.ARROW_UP, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.ARROW_DOWN, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.ARROW_RIGHT, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.ARROW_LEFT, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.IPHONE, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.WEB_BROWSER, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.RECT, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.SWITCH, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.ROUTER, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.DESKTOP, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.LAPTOP, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.SERVER2, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.TABLET_UP, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.TABLET_HORIZONTAL, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.OLD_PHONE, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.ANDROID, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.LIGHTBULB, new AbstractDiagramFactory.GenericFactory()),
		new ParserMap(ElementType.FREEHAND2, new AbstractDiagramFactory.GenericFactory())
	};

	public static AbstractDiagramFactory factory(IDiagramItemRO diro) {
		for (ParserMap pm : PARSER_MAP) {
			if (Tools.isSketchMode() && 
					// hacked to use sequence factory always, it creates correct sequence sketch element
					!diro.getType().equals(ElementType.SEQUENCE.getValue()) &&
					!diro.getType().equals(ElementType.HORIZONTAL_PARTITION.getValue()) &&
					!diro.getType().equals(ElementType.VERTICAL_PARTITION.getValue()) &&
					!diro.getType().equals(ElementType.PACKAGE.getValue()) &&
					Shapes.getSketch(diro.getType()) != null) {
				return new AbstractDiagramFactory.GenericFactory();
			} else {
				if (pm.elementType.getValue().equals(diro.getType())) {
					return pm.factory;
				}
			}
		}
		return new AbstractDiagramFactory.GenericFactory();
		// throw new RuntimeException("Factory not found for " + diro.getType());
	}

	public static Info parse(IDiagramItemRO diro, int moveX, int moveY) {
		for (ParserMap pm : PARSER_MAP) {
			if (pm.elementType.getValue().equals(diro.getType())) {
				return pm.factory.parseShape(diro, moveX, moveY);
			}
		}

		Info result = new AbstractDiagramFactory.GenericFactory().parseShape(diro, moveX, moveY);

		if (result == null && LogConfiguration.loggingIsEnabled(Level.FINEST)) {
			debugger();
		} else if (result == null) {
			throw new RuntimeException("Type not found: " + diro.getType());
		}
		return result;
	}

	public static Diagram createDiagramElement(IDiagramItemRO item, ISurfaceHandler surface) {
    AbstractDiagramFactory factory = ShapeParser.factory(item);
    Info shape = factory.parseShape(item, 0, 0);
    return factory.parseDiagram(surface, shape, true, item, null);
	}

	private static native void debugger()/*-{
		debugger;
	}-*/;

}