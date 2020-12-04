package net.sevenscales.editor.uicomponents.uml;

import net.sevenscales.editor.gfx.domain.IPath;

class PathWrapper {
	IPath path;
	ShapeProto proto;

	PathWrapper(IPath path) {
		this(path, null);
	}
	PathWrapper(IPath path, ShapeProto proto) {
		this.path = path;
		this.proto = proto;
	}

	boolean isProto() {
		return this.proto != null;
	}
}
