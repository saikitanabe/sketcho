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
		new ParserMap(ElementType.ELLIPSE, new AbstractDiagramFactory.EllipseFactory())
		// new ParserMap(ElementType.COMMENT),
	};

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