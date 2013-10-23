package net.sevenscales.domain;

import net.sevenscales.domain.api.IUrlLink;

public class UrlLinkDTO implements IUrlLink {
	private String url;
	private String name;

	public UrlLinkDTO(String url) {
		this(url, null);
	}

	public UrlLinkDTO(String url, String name) {
		this.url = url;
		this.name = name;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}