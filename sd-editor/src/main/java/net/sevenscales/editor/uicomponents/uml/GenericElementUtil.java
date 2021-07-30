package net.sevenscales.editor.uicomponents.uml;

import java.util.List;

import net.sevenscales.editor.gfx.domain.IPath;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.domain.constants.Constants;

class GenericElementUtil {
  public static double FREEHAND_STROKE_WIDTH = 2;

  private static final String BOUNDARY_COLOR          = "#aaaaaa";
  private static final String FILL_BORDER_COLOR       = "fill:bordercolor;";
  private static final String FILL_BORDER_COLOR_DARK  = "fill:bordercolor-dark;";
  private static final String FILL_SHAPE_BG_COLOR     = "fill:shape-bgcolor;";
  private static final String FILL_BG_COLOR           = "fill:bgcolor;";
  private static final String FILL_BG_COLOR_LIGHT     = "fill:bgcolor-light;";
  private static final String FILL_BG_COLOR_DARK      = "fill:bgcolor-dark;";

  static class ElementData {
    List<PathWrapper> paths;
    List<IShape> shapes;
    IGroup subgroup;
    Color backgroundColor;
    Color borderColor;
    IDiagramItem diagramItem;
    ISurfaceHandler surface;
    boolean editable;

    ElementData(
      List<PathWrapper> paths,
      List<IShape> shapes,
      IGroup subgroup,
      Color backgroundColor,
      Color borderColor,
      IDiagramItem diagramItem,
      ISurfaceHandler surface,
      boolean editable
    ) {
      this.paths = paths;
      this.shapes = shapes;
      this.subgroup = subgroup;
      this.backgroundColor = backgroundColor;
      this.borderColor = borderColor;
      this.diagramItem = diagramItem;
      this.surface = surface;
      this.editable = editable;
    }
  }

  static private IPath.PathTransformer pathTransformer = new IPath.PathTransformer() {
    public String getShapeStr(int dx, int dy) {
      return null;
    }
  };

  static void createSubPaths(
    ShapeGroup groupData,
    ElementData element
  ) {
    if (element.paths.size() == 0) {
      // just in case make sure that initialized only once
      for (ShapeProto p : groupData.protos) {
        IPath path = GenericElementUtil.createSubPath(p, element);
        element.paths.add(new PathWrapper(path, p));
      }

      GenericElementUtil.addPaths(element.paths, element.shapes);
    }
  }

  static void addPaths(
    List<PathWrapper> paths,
    List<IShape> shapes
  ) {
    for (PathWrapper path : paths) {
      shapes.add(path.path);
    }
  }

  static private IPath createSubPath(
    ShapeProto proto,
    ElementData element
  ) {
    return createSubPath(/*proto.toPath(1, 1)*/ null, proto.style, element);
  }  

  static IPath createSubPath(
    String path,
    String style,
    ElementData element
  ) {
    IPath result = IShapeFactory.Util.factory(element.editable).createPath(element.subgroup, pathTransformer);
    result.setStroke(element.borderColor);
    if (element.diagramItem.getLineWeight() != null) {
      result.setStrokeWidth(element.diagramItem.getLineWeight());
    } else {
      if (Tools.isSketchMode()) {
        if (element.surface.isLibrary()) {
          result.setStrokeWidth(Constants.SKETCH_MODE_LINE_WEIGHT_LIBRARY);
        } else {
          result.setStrokeWidth(Constants.SKETCH_MODE_LINE_WEIGHT);
        }
      } else {
        result.setStrokeWidth(FREEHAND_STROKE_WIDTH); 
      }
    }
    // result.setFill(backgroundColor.red, backgroundColor.green, backgroundColor.blue, backgroundColor.opacity);
    // path.setStrokeCap("round");
    if (style != null && !"".equals(style)) {
      style = handleStyle(result, style);
      result.setAttribute("style", style);
    }

    // >>>>>>>>>>> Commented out 4.11.2014 - can be deleted after half a year :)
    // now scaling down line width and each point as inkscape
    // if (!surface.isLibrary()) {
    //  result.setAttribute("vector-effect", "non-scaling-stroke");
    // }
    // <<<<<<<<<<< Commented out 4.11.2014
    if (path != null) {
      result.setShape(path);
    }
    return result;
  }

  static String handleStyle(IPath path, String style) {
    if (style.contains(FILL_BORDER_COLOR)) {
      path.setFillAsBorderColor(true);
      // need to clear or will contain invalid fill valud since bordercolor is not hex code or pre color code
      style = style.replace(FILL_BORDER_COLOR, "");
    } else if (style.contains(FILL_SHAPE_BG_COLOR)) {
      path.setFillAsShapeBackgroundColor(true);
      style = style.replace(FILL_SHAPE_BG_COLOR, "");
    } else if (style.contains(FILL_BG_COLOR)) {
      path.setFillAsBoardBackgroundColor(true);
      style = style.replace(FILL_BG_COLOR, "");
    } else if (style.contains(FILL_BORDER_COLOR_DARK)) {
      path.setFillAsBorderColorDark(true);
      style = style.replace(FILL_BORDER_COLOR_DARK, "");
    } else if (style.contains(FILL_BG_COLOR_LIGHT)) {
      path.setFillAsBackgroundColorLight(true);
      style = style.replace(FILL_BG_COLOR_LIGHT, "");
    } else if (style.contains(FILL_BG_COLOR_DARK)) {
      path.setFillAsBackgroundColorDark(true);
      style = style.replace(FILL_BG_COLOR_DARK, "");
    }
    return style;
  }

  static void setBackgroundColor(
    Color clr,
    ElementData element
  ) {
    for (PathWrapper path : element.paths) {
      if (!path.path.isFillAsBorderColor() && !path.path.isFillAsBoardBackgroundColor()) {
        path.path.setFill(element.backgroundColor.red, element.backgroundColor.green, element.backgroundColor.blue, element.backgroundColor.opacity);

        if (element.backgroundColor.opacity > 0) {
          // uses rbg background color that is set as attribute instead of fill
          disableGradient(path.path);
        } else if (element.backgroundColor.isGradient()) {
          // it's gradient, enable gradient
          enableGradient(
            path.path,
            element.backgroundColor
          );
        }

      }

      if (path.path.isFillAsBackgroundColorLight()) {
        Color color = element.backgroundColor;
        path.path.setFill(color.toLighter());
      } else if (path.path.isFillAsBackgroundColorDark()) {
        Color color = element.backgroundColor;
        path.path.setFill(color.toDarker());
      } else if (path.path.isFillAsShapeBackgroundColor()) {
        path.path.setFill(clr.red, clr.green, clr.blue, clr.opacity);
      }
    }
  }

  private static void disableGradient(
    IPath path
  ) {
    // net.sevenscales.domain.utils.Debug.log("disableGradient");
    if (path.isFillGradient()) {
      // note needs to match full style string therefore .* in the beginning and in the end
      
      // this path supports gradients, e.g. fill:url(#9669320a-32d6-7208-0c6b-17322336ba99);fill-opacity:1;stroke:none"
      // do not replace any fixed fills e.g. fill:none that is set on style

      // style = style.replaceAll("fill:url\\([^;]+\\);", "");
      // could also store original url but this is easier
      String style = path.getAttribute("style");
      style = style.replace("fill:", "fill-disabled:");
      path.setAttribute("style", style);
    }
  }

  private static void enableGradient(
    IPath path,
    Color color
  ) {
    String style = path.getAttribute("style");

    if (style != null) {
      style = style.replace("fill-disabled:", "fill:");

      // fill:url(#9669320a-32d6-7208-0c6b-17322336ba99);fill-opacity:1;stroke:none"
      // style = color.gradient + ";" + style;
      path.setAttribute("style", style);
    }
  }

  static boolean scalePaths(
    double factorX,
    double factorY,
    IShapeGroup theshape,
    List<PathWrapper> paths
  ) {
    if (theshape.isReady()) {
      for (PathWrapper pw : paths) {
        if (pw.isProto()) {
          pw.path.setShape(pw.proto.toPath(factorX, factorY, theshape.getShape().width));
        }
      }
      // pathsSetAtLeastOnce = true;
      return true;
    }
    // for (ShapeProto p : groupData.protos) {
  //  for (IPath path : paths) {
  //    result.setShape(proto.toPath());

   //   // path.scale(factorX, factorY);
  //  }

    return false;
  }


}