package net.sevenscales.editor.gfx.dojosvg;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.editor.gfx.domain.IContainer;
import net.sevenscales.editor.gfx.domain.IPolyline;
import net.sevenscales.editor.gfx.domain.Point;

import com.google.gwt.core.client.JavaScriptObject;

class Polyline extends Shape implements IPolyline {

//	public Polyline(Surface surface, Point[] points) {
//		this.rawNode = createPolyline(surface.getNativeSurface());
//		this.points = points;
//		
//		setShape(this.points);
//	}

  Polyline(IContainer container) {
    this.rawNode = createPolyline(container.getContainer());
  }

  Polyline(IContainer container, int[] points) {
    this.rawNode = createPolyline(
    container.getContainer(), createNativePoints(points));
  }

  Polyline(IContainer container, List<Integer> points) {
    this.rawNode = createPolyline(
    container.getContainer(), createNativePoints(points));
  }

	Polyline(Surface surface, int[] points) {
		this.rawNode = createPolyline(
		surface.getContainer(), createNativePoints(points));
	}

	Polyline(Surface surface, List<Integer> points) {
		this.rawNode = createPolyline(
		surface.getContainer(), createNativePoints(points));
	}
	
	public void setShape(Point[] points) {
		JavaScriptObject array = JavaScriptObject.createArray();
		for (Point point : points) {
			addPoint(point.getNativePoint(), array);
		}
		
		setShape(rawNode, array);
	}
	
	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.IPolyline#setShape(int[])
   */
	public void setShape(int[] points) {
		JavaScriptObject array = JavaScriptObject.createArray();
		for (int i = 0; i < points.length; i += 2) {
			addPoint(points[i], points[i + 1], array);
		}
		setShape(rawNode, array);
	}

  /* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.IPolyline#setShape(java.util.List)
   */
  public void setShape(List<Integer> points) {
    JavaScriptObject array = JavaScriptObject.createArray();
    for (int i = 0; i < points.size(); i += 2) {
      addPoint(points.get(i), points.get(i + 1), array);
    }
    
    setShape(rawNode, array);
	}
  
  public int getArrayValue(int index) {
  	int pointIndex = index / 2;
    JavaScriptObject points = nativeGetShape(rawNode);
  	if (index % 2 == 0) {
  		// x value
    	return getFromIndexX(points, pointIndex);
  	}
  	
  	// otherwise point y value
  	return getFromIndexY(points, pointIndex);
  }
    
  /* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.IPolyline#getShape()
   */
  public List<Integer> getShape() {
    JavaScriptObject points = nativeGetShape(rawNode);
    int size = getSize(points);
    List<Integer> result = new ArrayList<Integer>(size);
    for (int i = 0; i < size; ++i) {
      result.add(getFromIndexX(points, i));
      result.add(getFromIndexY(points, i));
    }
    return result;
  }
	private native JavaScriptObject nativeGetShape(JavaScriptObject rawNode)/*-{
    return rawNode.getShape().points;
  }-*/;
	
	private native int getFromIndexX(JavaScriptObject array, int i)/*-{
	  return parseInt(array[i].x);
	}-*/;
  private native int getFromIndexY(JavaScriptObject array, int i)/*-{
    return parseInt(array[i].y);
  }-*/;
	private native int getSize(JavaScriptObject array)/*-{
    return array.length;
  }-*/;

  private JavaScriptObject createNativePoints(int[] points) {
		JavaScriptObject array = JavaScriptObject.createArray();
		for (int i = 0; i < points.length; i += 2) {
			addPoint(points[i], points[i + 1], array);
		}
		return array;
	}

	private JavaScriptObject createNativePoints(List<Integer> points) {
		JavaScriptObject array = JavaScriptObject.createArray();
		for (int i = 0; i < points.size(); i += 2) {
			addPoint(points.get(i), points.get(i+1), array);
		}
		return array;
	}
	
//	@Override
//	public void applyTransform(int dx, int dy) {
//		nativeApplyTransform(rawNode, dx, dy);
//	}
	
	@Override
	public void applyTransformToShape(int dx, int dy) {
		_applyTransformToShape(rawNode, dx, dy);
	}

	private native void _applyTransformToShape(JavaScriptObject rawNode, int dx, int dy)/*-{
		var points = rawNode.getShape().points;
		for (var i = 0; i < points.length; ++i) {
      points[i].x += dx;
      points[i].y += dy; 
		}
		rawNode.setShape({points: points});
	}-*/;

	private native void setShape(JavaScriptObject polyline, JavaScriptObject points)/*-{
		polyline.setShape( {points: points} );
	}-*/;
	
	private native void addPoint(JavaScriptObject point, JavaScriptObject array)/*-{
		array.push(point);
	}-*/;

	private native void addPoint(int x, int y, JavaScriptObject array)/*-{
		array.push( {x:x, y:y} );
	}-*/;

  public static native JavaScriptObject createPolyline
	  (JavaScriptObject surface)/*-{
	  return surface.createPolyline();
	}-*/;

    public static native JavaScriptObject createPolyline
      (JavaScriptObject surface, JavaScriptObject points)/*-{
	    return surface.createPolyline( {points:points} );
	  }-*/;
    
}
