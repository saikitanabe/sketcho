package net.sevenscales.editor.gfx.dojosvg;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.content.utils.DiagramHelpers;
import net.sevenscales.editor.gfx.domain.IContainer;
import net.sevenscales.editor.gfx.domain.IPath;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

class Path extends Shape implements IPath {
	private static SLogger logger = SLogger.createLogger(Path.class);
	
	private final RegExp regExp = RegExp.compile("([-]*\\d+)");

  private String shape;
  private PathTransformer transformer;

	Path(IContainer container, PathTransformer transformer) {
		this.transformer = transformer;
    this.rawNode = createPath(container.getContainer());
  }
	
	@Override
	public void setPathTransformer(PathTransformer transformer) {
		this.transformer = transformer;
	}

  private static native JavaScriptObject createPath(JavaScriptObject surface)/*-{
    return surface.createPath();
  }-*/;

	@Override
	public void applyTransformToShape(int dx, int dy) {
		// TODO not in use at the moment
		// setShape(DiagramHelpers.applyTransformToShape(shape, dx, dy, transformer));
	}

	@Override
	public void setShape(int[] points) {
		// TODO Auto-generated method stub
		
	}
	
	public void setShape(String shape) {
		this.shape = shape;
		_setShape(rawNode, shape);
	}
	
	private native void _setShape(JavaScriptObject rawNode, String shape)/*-{
		// skipping dojo set shape since not using dojo features anyway
		// no dojo segment parsing and this is just plain svg
		// getting rid of path attribute that dojo maintains => smaller DOM!
		// rawNode.setShape({path: shape});
		rawNode.rawNode.setAttribute("d", shape)
	}-*/;

	public String getRawShape() {
		return _getShape(rawNode);
	}
	
	private native String _getShape(JavaScriptObject rawNode)/*-{
		return rawNode.rawNode.getAttribute("d")
	}-*/;

	@Override
	public void setShape(List<Integer> points) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Integer> getShape() {
		String[] list = shape.split("[\\s,]");
		List<Integer> result = new ArrayList<Integer>();
		
		for (String l : list) {
			MatchResult matcher = regExp.exec(l);
			if (matcher != null) {
        String number = matcher.getGroup(0);
        result.add(Integer.valueOf(number));
			}
		}
		return result;
	}

	@Override
	public int getArrayValue(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getShapeStr(int dx, int dy) {
		if (transformer == null) {
			return null;
		}
		return transformer.getShapeStr(dx, dy);
	}
}
