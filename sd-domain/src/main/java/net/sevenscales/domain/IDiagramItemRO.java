package net.sevenscales.domain;

import java.io.Serializable;

import net.sevenscales.domain.api.IDiagramItem;

import com.google.gwt.user.client.rpc.IsSerializable;

public interface IDiagramItemRO extends Serializable, IsSerializable {
	Long getId();
	String getText();
	String getType();
	String getShape();
	String getBackgroundColor();
	String getTextColor();
	Integer getVersion();
	String getClientId();
	/**
	 * This data needs to be parsed separately by each diagram element, no
	 * generic parsing.
	 * @return
	 */
	String getCustomData();
	IDiagramItem copy();
	void copyFrom(IDiagramItemRO item);
}