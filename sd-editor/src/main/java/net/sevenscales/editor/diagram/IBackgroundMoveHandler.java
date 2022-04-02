package net.sevenscales.editor.diagram;

import net.sevenscales.editor.gfx.domain.OrgEvent;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;

public interface IBackgroundMoveHandler {
  boolean onMouseDown(OrgEvent event, Diagram sender, MatrixPointJS point, int keys);
  void onMouseEnter(OrgEvent event, Diagram sender, MatrixPointJS point);
  void onMouseLeave(Diagram sender, MatrixPointJS point);
  void onMouseMove(OrgEvent event, Diagram sender, MatrixPointJS point);
  void onMouseUp(Diagram sender, MatrixPointJS point, int keys);
  boolean backgroundMoveIsOn();
  void cancelBackgroundMove();
}