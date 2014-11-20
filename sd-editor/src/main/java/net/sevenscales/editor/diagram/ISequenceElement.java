package net.sevenscales.editor.diagram;

import net.sevenscales.editor.gfx.domain.ILine;
import net.sevenscales.editor.gfx.domain.Color;

public interface ISequenceElement {
	Diagram getDiagram();
	ILine getLine();
	int getTransformX();
  int getTransformY();
  Color getBorderColor();
}