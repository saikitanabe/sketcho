package net.sevenscales.editor.diagram;

import java.util.ArrayList;

import net.sevenscales.editor.gfx.domain.MatrixPointJS;

public class MouseDiagramHandlerCollection extends ArrayList<MouseDiagramHandler> {
  public void addMouseDiagramHandler(MouseDiagramHandler handler) {
    add(handler);
  }

  public void fireMouseUp(Diagram sender, MatrixPointJS point) {
    for (MouseDiagramHandler h : this) {
      h.onMouseUp(sender, point);
    }
  }

  public void fireMouseDown(Diagram sender, MatrixPointJS point, int keys) {
    for (MouseDiagramHandler h : this) {
      h.onMouseDown(sender, point, keys);
    }
  }

  public void fireMouseEnter(Diagram sender, MatrixPointJS point) {
    for (MouseDiagramHandler h : this) {
      h.onMouseEnter(sender, point);
    }
  }

  public void fireMouseMove(Diagram sender, MatrixPointJS point) {
    for (MouseDiagramHandler h : this) {
      h.onMouseMove(sender, point);
    }
  }

  public void fireMouseLeave(Diagram sender, MatrixPointJS point) {
    for (MouseDiagramHandler h : this) {
      h.onMouseLeave(sender, point);
    }
  }
}
