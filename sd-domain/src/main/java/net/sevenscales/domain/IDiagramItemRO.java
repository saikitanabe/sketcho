package net.sevenscales.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.IsSerializable;

import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.api.IExtension;

public interface IDiagramItemRO extends Serializable, IsSerializable {
	long getId();
	String getText();
	String getType();
	String getShape();
	IExtension getExtension();
	String getBackgroundColor();
	String getTextColor();
	Integer getFontSize();
	Integer getLineWeight();
	Integer getShapeProperties();
	Integer getDisplayOrder();
	int getVersion();
	String getClientId();
	String getParentId();
	/**
	 * This data needs to be parsed separately by each diagram element, no
	 * generic parsing.
	 * @return
	 */
	String getCustomData();
  Integer getRotateDegrees();
	double getCrc32();
	String getGroup();
	JavaScriptObject getData();
	boolean isGroup();
	Long getCreatedAt();
	Long getUpdatedAt();
	int getAnnotation();
	int getResolved();
	boolean isAnnotation();
	boolean isResolved();
	List<? extends IUrlLinkRO> getLinks();
	String getFirstLink();
	IDiagramItem copy();
	void copyFrom(IDiagramItemRO item);

	boolean isComment();
	boolean compare(IDiagramItemRO diro, Map<String, Boolean> dirtyFields);
  boolean isSketchiness();
}