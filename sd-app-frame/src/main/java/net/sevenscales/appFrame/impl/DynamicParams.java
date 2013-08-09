package net.sevenscales.appFrame.impl;

import java.util.HashMap;
import java.util.Map;

public class DynamicParams {
	private Map params = new HashMap();
	
	public static class Param {
		public Param(int id, Object data) {
			this.id = id;
			this.data = data;
		}
		public int id;
		public Object data;
	}
	
	public void addParam(Param param) {
		params.put(new Integer(param.id), param);
	}
	
	public Object getParam(int paramId) {
		Param p = (Param) params.get(new Integer(paramId));
		return p != null ? p.data : null;
	}

	public void addParam(int paramId, Object data) {
		Param p = new Param(paramId, data);
		addParam(p);
	}
	
}
