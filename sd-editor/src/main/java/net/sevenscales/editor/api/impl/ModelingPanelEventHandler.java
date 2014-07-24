package net.sevenscales.editor.api.impl;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.ToolFrame;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.MouseDiagramHandler;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;

public class ModelingPanelEventHandler implements MouseDiagramHandler {
	private ISurfaceHandler surface;
	private ToolFrame toolFrame;

	public ModelingPanelEventHandler(ISurfaceHandler surface, ToolFrame toolFrame) {
		this.surface = surface;
		this.toolFrame = toolFrame;
		
		surface.addMouseDiagramHandler(this);
	}

	@Override
	public boolean onMouseDown(Diagram sender, MatrixPointJS point, int keys) {
		toolFrame.hideToolbar();
		return false;
	}

	@Override
	public void onMouseUp(Diagram sender, MatrixPointJS point, int keys) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseMove(Diagram sender, MatrixPointJS point) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseLeave(Diagram sender, MatrixPointJS point) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseEnter(Diagram sender, MatrixPointJS point) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTouchStart(Diagram sender, MatrixPointJS point) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTouchMove(Diagram sender, MatrixPointJS point) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTouchEnd(Diagram sender, MatrixPointJS point) {
		// TODO Auto-generated method stub
		
	}
}
