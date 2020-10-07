package net.sevenscales.domain.js;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONArray;


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
  
  public static JsGradient copy(
    JsGradient gradient
  ) {
    JSONObject result = new JSONObject();

    result.put("id", new JSONString(gradient.getId()));
    result.put("y1", new JSONString(gradient.getY1()));
    result.put("y2", new JSONString(gradient.getY2()));
    result.put("x1", new JSONString(gradient.getX1()));
    result.put("x2", new JSONString(gradient.getX2()));

    JSONArray stops = new JSONArray();

    for (int i = 0; i < gradient.getStop().length(); ++i) {
    	stops.set(i, JsGradientStop.copyJSONValue(gradient.getStop().get(i)));
    }

    result.put("stop", stops);

    return result.getJavaScriptObject().cast();
  }

}
