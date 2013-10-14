package net.sevenscales.domain;

import java.io.Serializable;

import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.utils.JsonFormat;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.json.client.JSONValue;

public interface IDiagramItemRO extends Serializable, IsSerializable {
	long getId();
	String getText();
	String getType();
	String getShape();
	String getBackgroundColor();
	String getTextColor();
	int getVersion();
	String getClientId();
	/**
	 * This data needs to be parsed separately by each diagram element, no
	 * generic parsing.
	 * @return
	 */
	String getCustomData();
	double getCrc32();
	int getAnnotation();
	int getResolved();
	boolean isAnnotation();
	boolean isResolved();
	IDiagramItem copy();
	void copyFrom(IDiagramItemRO item);

	boolean isComment();
	JSONValue toJson(JsonFormat jsonFormat);

}