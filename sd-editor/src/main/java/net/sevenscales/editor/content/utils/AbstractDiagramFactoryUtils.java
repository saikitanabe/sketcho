package net.sevenscales.editor.content.utils;

import net.sevenscales.editor.api.LibraryShapes;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.api.IDiagramItem;


class AbstractDiagramFactoryUtils {
  static void fixUninitializedDiagramItem(IDiagramItemRO item, GenericShape newShape) {
    LibraryShapes.ShapeProps sh = LibraryShapes.getShapeProps(item.getType());
    Integer props = item.getShapeProperties();
    IDiagramItem _item = null;
    if (item instanceof IDiagramItem) {
      _item = (IDiagramItem) item;
    }

    if (props == null) {
      props = 0;
    }

    if (props == 0 || (props != null && props == ShapeProperty.DISABLE_SHAPE_AUTO_RESIZE.getValue())) {
      // if not set at all or only shape auto resize has been disabled
      // props = 0;
      if (sh != null) {
        // keep dynamic properties
        props = sh.properties | (props & ShapeProperty.DISABLE_SHAPE_AUTO_RESIZE.getValue());
        newShape.setShapeProperties(props);
      }
    }

    if (_item.getFontSize() == null && sh != null) {
      if (_item instanceof IDiagramItem) {
        ((IDiagramItem)_item).setFontSize(sh.fontSize);
      }
    }
  }
}