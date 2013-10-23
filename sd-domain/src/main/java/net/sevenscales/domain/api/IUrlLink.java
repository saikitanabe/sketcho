package net.sevenscales.domain.api;

import net.sevenscales.domain.IUrlLinkRO;

public interface IUrlLink extends IUrlLinkRO {
	void setUrl(String url);
	void setName(String name);
}
