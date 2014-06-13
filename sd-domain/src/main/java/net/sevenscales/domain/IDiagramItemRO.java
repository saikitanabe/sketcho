package net.sevenscales.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.api.IExtension;
import net.sevenscales.domain.utils.JsonFormat;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.json.client.JSONValue;

public interface IDiagramItemRO extends Serializable, IsSerializable {
	long getId();
	String getText();
	String getType();
	String getShape();
	IExtension getExtension();
	String getBackgroundColor();
	String getTextColor();
	Integer getFontSize();
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
	double getCrc32();
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
}