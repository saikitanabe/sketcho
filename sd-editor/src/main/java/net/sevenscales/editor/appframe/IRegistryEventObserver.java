package net.sevenscales.editor.appframe;

public interface IRegistryEventObserver {

  void handleEvent(Integer eventId, Object data);

}
