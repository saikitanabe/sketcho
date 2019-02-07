package net.sevenscales.editor.api.event.pointer;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler interface for {@link PointerUpEvent} events.
 */
public interface PointerUpHandler extends EventHandler {

  /**
   * Called when PointerUpEvent is fired.
   *
   * @param event the {@link PointerUpEvent} that was fired
   */
  void onPointerUp(PointerUpEvent event);
}