package net.sevenscales.editor.uicomponents.helpers;

import net.sevenscales.editor.gfx.domain.ICircle;

public class ConnectionHandle {
	public ICircle visibleHandle;
	public ICircle connectionHandle;
	
	public ConnectionHandle(ICircle visibleHandle, ICircle connectionHandle) {
		this.visibleHandle = visibleHandle;
		this.connectionHandle = connectionHandle;
	}
}
