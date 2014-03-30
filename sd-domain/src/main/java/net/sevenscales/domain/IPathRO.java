package net.sevenscales.domain;

public interface IPathRO {
	String getPath();
	String getStyle();
	IPathRO copy();
}