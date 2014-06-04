package net.sevenscales.editor.gfx.dojosvg;

import java.util.List;

import net.sevenscales.editor.gfx.domain.ICircle;
import net.sevenscales.editor.gfx.domain.IContainer;
import net.sevenscales.editor.gfx.domain.IEllipse;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IImage;
import net.sevenscales.editor.gfx.domain.ILine;
import net.sevenscales.editor.gfx.domain.IPath;
import net.sevenscales.editor.gfx.domain.IPath.PathTransformer;
import net.sevenscales.editor.gfx.domain.IPolyline;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.ISurface;
import net.sevenscales.editor.gfx.domain.IText;

public class ShapeFactoryDojoSvg implements IShapeFactory {

  public ISurface createSurface() {
    return new Surface();
  }
  
  public ICircle createCircle(ISurface surface) {
    return new Circle(surface);
  }

  public ICircle createCircle(IContainer container) {
    return new Circle(container);
  }

  public ICircle createCircle(ISurface surface, int circleX, int circleY,
      int radius) {
    return new Circle((Surface) surface, circleX, circleY, radius);
  }

  public IEllipse createEllipse(IContainer container) {
    return new Ellipse(container) ;
  }

  public IEllipse createEllipse(ISurface surface) {
    return new Ellipse((Surface) surface) ;
  }

  public IGroup createGroup(IContainer container) {
    return new Group(container);
  }

  public IGroup createGroup(ISurface surface) {
    return new Group((Surface) surface);
  }

  public ILine createLine(IContainer container) {
    return new Line(container);
  }

  public IPolyline createPolyline(IContainer container) {
    return new Polyline(container);
  }

  public IPolyline createPolyline(IContainer container, int[] points) {
    return new Polyline(container, points);
  }

  public IPolyline createPolyline(IContainer container, double[] points) {
    return new Polyline(container, points);
  }

  public IPolyline createPolyline(IContainer container, List<Integer> points) {
    return new Polyline(container, points);
  }

  public IPolyline createPolyline(ISurface surface, List<Integer> points) {
    return new Polyline((Surface) surface, points);
  }
  
  public IPath createPath(IContainer surface, PathTransformer transformer) {
  	return new Path(surface, transformer);
  }

  public IRectangle createRectangle(IContainer container) {
    return new Rectangle(container);
  }
  
  @Override
  public IImage createImage(IContainer container, int x, int y, int widht, int height, String src) {
  	return new Image(container, x, y, widht, height, src);
  }

  public IText createText(ISurface surface) {
    return new Text((Surface) surface);
  }

  public IText createText(IContainer container) {
    return new Text(container);
  }

}
