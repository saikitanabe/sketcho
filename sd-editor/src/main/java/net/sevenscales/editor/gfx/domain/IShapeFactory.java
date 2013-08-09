package net.sevenscales.editor.gfx.domain;

import java.util.List;

import net.sevenscales.editor.gfx.dojosvg.ShapeFactoryDojoSvg;

public interface IShapeFactory {
  public static class Util {
//    private static final IShapeFactory factory = new ShapeFactorySvgWeb();
    private static final IShapeFactory editFactory;
    
    static {
//    	if (isCurrentGfxSvg()) {
    	editFactory = new ShapeFactoryDojoSvg();
//    	} else {
//    		editFactory = new ShapeFactoryDojo();
//    	}
    }
//    private static final IShapeFactory editFactory = new ShapeFactoryDojo();
    
    public static IShapeFactory factory(boolean editable) {
//      if (editable) {
        return editFactory;
//      }
//      return factory;
    }
    
    public static native boolean isCurrentGfxSvg()/*-{
    	if ($wnd.dojox.gfx.renderer == 'svg') {
    		return true;
    	} else {
    		return false;
    	}
    	
    }-*/; 
  }
  
  public ISurface createSurface();
  public ICircle createCircle(ISurface surface);
  public ICircle createCircle(IContainer container);
  public ICircle createCircle(ISurface surface, int circleX, int circleY, int radius);
  
  public IGroup createGroup(IContainer surface);
  public IGroup createGroup(ISurface surface);
  public ILine createLine(IContainer container);
  public IRectangle createRectangle(IContainer container);
  IImage createImage(IContainer container, int x, int y, int widht, int height, String src);
  public IEllipse createEllipse(IContainer surface);
  public IEllipse createEllipse(ISurface surface);
  public IText createText(ISurface surface);
  public IText createText(IContainer container);
  public IPolyline createPolyline(IContainer container);
  public IPolyline createPolyline(IContainer container, int[] points);
  public IPolyline createPolyline(IContainer container, List<Integer> points);
  public IPolyline createPolyline(ISurface surface, List<Integer> points);
  IPath createPath(IContainer surface, IPath.PathTransformer transformer);
}
