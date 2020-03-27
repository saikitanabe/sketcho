package net.sevenscales.editor.diagram;

import java.util.ArrayList;

import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.OrgEvent;

public class MouseDiagramHandlerCollection extends ArrayList<MouseDiagramHandler> {
  public void addMouseDiagramHandler(MouseDiagramHandler handler) {
    add(handler);
  }

  public void fireMouseUp(Diagram sender, MatrixPointJS point, int keys) {
    for (MouseDiagramHandler h : this) {
      h.onMouseUp(sender, point, keys);
    }
  }

  public void fireMouseDown(OrgEvent event, Diagram sender, MatrixPointJS point, int keys) {
    for (MouseDiagramHandler h : this) {
      h.onMouseDown(event, sender, point, keys);
    }
  }

  public void fireMouseEnter(OrgEvent event, Diagram sender, MatrixPointJS point) {
    for (MouseDiagramHandler h : this) {
      h.onMouseEnter(event, sender, point);
    }
  }

  public void fireMouseMove(OrgEvent event, Diagram sender, MatrixPointJS point) {
    for (MouseDiagramHandler h : this) {
      h.onMouseMove(event, sender, point);
    }
  }

  public void fireMouseLeave(Diagram sender, MatrixPointJS point) {
    for (MouseDiagramHandler h : this) {
      h.onMouseLeave(sender, point);
    }
  }
}
