package net.sevenscales.editor.diagram;

import java.util.List;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.BoardDimensions;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.IPath;
import net.sevenscales.editor.gfx.domain.IGroup;


class GroupSelection {
	private IPath groupOutline;
  private IGroup groupOutlineGroup;

  GroupSelection(ISurfaceHandler surface, List<Diagram> groupShapes) {
    groupOutlineGroup = IShapeFactory.Util.factory(true).createGroup(surface.getRootLayer());
    groupOutline = IShapeFactory.Util.factory(true).createPath(groupOutlineGroup, null);
    groupOutline.setStroke(new Color(0x33, 0x33, 0x33, 1));
    groupOutline.setStrokeDashArray("5,10");

    groupOutline.setShape(groupOutlineShape(groupShapes));
  }

  private String groupOutlineShape(List<Diagram> groupShapes) {
    BoardDimensions.resolveDimensions(groupShapes);

    groupOutlineGroup.setTransform(BoardDimensions.getLeftmost(), BoardDimensions.getTopmost());

    int left = 0;
    int top = 0;
    int width = BoardDimensions.getWidth();
    int height = BoardDimensions.getHeight();
    int[] points = new int[]{left,
                             top,
                             width,
                             0,
                             0,
                             height,
                             -width,
                             0,
                             0,
                             -height};
    String result = null;
    for (int i = 0; i < points.length; i += 2) {
      String point = points[i] + "," + points[i + 1];
      if (result == null) {
        result = "m" + point;
      } else {
        result += " " + point;
      }
    }
    return result;
  }

  void setTransform(int dx, int dy) {
    groupOutlineGroup.setTransform(groupOutlineGroup.getTransformX() + dx, 
                                   groupOutlineGroup.getTransformY() + dy);
  }

  void remove() {
  	groupOutlineGroup.remove();
  }

}