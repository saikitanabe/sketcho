package net.sevenscales.domain.js;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

// case class LinearGradient(
// 	id: String,
// 	y1: String,
// 	y2: String,
// 	x1: String,
// 	x2: String,
// 	stop: List[GradientStop]
// )

public class JsGradient extends JavaScriptObject {
	protected JsGradient() {
	}

	public final native String getId()/*-{
		return this.id
	}-*/;
	public final native String setId(
    String id
  )/*-{
		return this.id = id
	}-*/;
	public final native String getY1()/*-{
		return this.y1
	}-*/;
	public final native String getY2()/*-{
		return this.y2
	}-*/;
	public final native String getX1()/*-{
		return this.x1
	}-*/;
	public final native String getX2()/*-{
		return this.x2
	}-*/;
	public final native JsArray<JsGradientStop> getStop()/*-{
		return this.stop
	}-*/;
}
