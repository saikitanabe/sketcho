package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

import net.sevenscales.editor.diagram.Diagram;

public class SwitchElementEvent extends GwtEvent<SwitchElementEventHandler> {
  public static Type<SwitchElementEventHandler> TYPE = new Type<SwitchElementEventHandler>();
	private Diagram diagram;

  public SwitchElementEvent(Diagram diagram) {
  	this.diagram = diagram;
	}

	@Override
  protected void dispatch(SwitchElementEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SwitchElementEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public Diagram getDiagram() {
		return diagram;
	}
}
