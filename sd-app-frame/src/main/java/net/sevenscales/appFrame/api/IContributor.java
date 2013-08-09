package net.sevenscales.appFrame.api;

public interface IContributor {

  <T> T cast(Class<T> clazz);

}
