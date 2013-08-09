package net.sevenscales.appFrame.api;

public interface IRegistryEventObserver {

  void handleEvent(Integer eventId, Object data);

}
