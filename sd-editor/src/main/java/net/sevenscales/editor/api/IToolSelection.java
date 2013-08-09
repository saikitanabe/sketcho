package net.sevenscales.editor.api;

import net.sevenscales.editor.diagram.Diagram;

public interface IToolSelection {
  public Diagram selectedElement();
  public Diagram selectedRelationship();
}
