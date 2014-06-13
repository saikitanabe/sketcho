package net.sevenscales.editor.uicomponents.uml;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.shape.TextShape;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.HasTextElement;
import net.sevenscales.editor.uicomponents.TextElementFormatUtil.AbstractHasTextElement;
import net.sevenscales.editor.gfx.domain.IParentElement;
import net.sevenscales.editor.gfx.domain.IChildElement;


public class ChildTextElement extends TextElement implements IChildElement {
	private IParentElement parent;

	public ChildTextElement(ISurfaceHandler surface, TextShape newShape,
			Color backgroundColor, Color borderColor, Color textColor, String text, boolean editable, IDiagramItemRO item, IParentElement parent) {
		super(surface, newShape, backgroundColor, borderColor, textColor, text, editable, item);
		this.parent = parent;
		parent.addChild(this);
    super.constructorDone();
	}

	public Info getInfo() {
		// TODO add parent client id
		return super.getInfo();
	}

	public Diagram getParent() {
		return parent.asDiagram();
	}

	public Diagram asDiagram() {
		return this;
	}


}
