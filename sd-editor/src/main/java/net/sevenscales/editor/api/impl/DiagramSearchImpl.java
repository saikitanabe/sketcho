package net.sevenscales.editor.api.impl;

import java.util.List;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.CircleElement;

public class DiagramSearchImpl {
  public static Diagram findByClientId(String clientId, List<Diagram> diagrams) {
    if (clientId == null || "".equals(clientId)) {
      return null;
    }
    
    for (Diagram d : diagrams) {
      if ( !(d instanceof CircleElement) && d.getDiagramItem() != null && clientId.equals(d.getDiagramItem().getClientId())) {
        return d;
      }
    }
    return null;
  }
  
}
