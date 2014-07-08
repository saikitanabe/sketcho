package net.sevenscales.editor.content.utils;

import java.util.logging.Level;
import java.lang.reflect.Constructor;

import com.google.gwt.logging.client.LogConfiguration;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.ElementType;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.*;
import net.sevenscales.domain.utils.SLogger;


class ShapeParser {
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
		new ParserMap(ElementType.SEQUENCE, new AbstractDiagramFactory.SequenceFactory()),
		new ParserMap(ElementType.COMPONENT, new AbstractDiagramFactory.ComponentFactory()),
		new ParserMap(ElementType.SERVER, new AbstractDiagramFactory.ServerFactory()),
		new ParserMap(ElementType.CLASS, new AbstractDiagramFactory.ClassFactory()),
		new ParserMap(ElementType.NOTE, new AbstractDiagramFactory.NoteFactory()),
		new ParserMap(ElementType.CHOICE, new AbstractDiagramFactory.ActivityChoiceFactory()),
		new ParserMap(ElementType.ACTIVITY_START, new AbstractDiagramFactory.ActivityStartFactory()),
		new ParserMap(ElementType.ACTIVITY_END, new AbstractDiagramFactory.ActivityEndFactory()),
		new ParserMap(ElementType.ACTIVITY, new AbstractDiagramFactory.ActivityFactory()),
		new ParserMap(ElementType.MIND_CENTRAL, new AbstractDiagramFactory.MindCentralFactory()),
		new ParserMap(ElementType.STORAGE, new AbstractDiagramFactory.StorageFactory()),
		// new ParserMap(ElementType.COMMENT, new AbstractDiagramFactory.CommentFactory()),
		new ParserMap(ElementType.TEXT_ITEM, new AbstractDiagramFactory.TextItemFactory()),
		new ParserMap(ElementType.CHILD_TEXT, new AbstractDiagramFactory.ChildTextItemFactory()),
		new ParserMap(ElementType.ACTOR, new AbstractDiagramFactory.ActorFactory()),
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
		new ParserMap(ElementType.FREEHAND2, new AbstractDiagramFactory.GenericFactory())
	};

	static AbstractDiagramFactory factory(IDiagramItemRO diro) {
		for (ParserMap pm : PARSER_MAP) {
			if (pm.elementType.getValue().equals(diro.getType())) {
				return pm.factory;
			}
		}
		throw new RuntimeException("Factory not found for " + diro.getType());
	}

	static Info parse(IDiagramItemRO diro, int moveX, int moveY) {
		for (ParserMap pm : PARSER_MAP) {
			if (pm.elementType.getValue().equals(diro.getType())) {
				return pm.factory.parseShape(diro, moveX, moveY);
			}
		}

		if (LogConfiguration.loggingIsEnabled(Level.FINEST)) {
			debugger();
		}

		throw new RuntimeException("Type not found: " + diro.getType());
	}

	private static native void debugger()/*-{
		debugger;
	}-*/;

}