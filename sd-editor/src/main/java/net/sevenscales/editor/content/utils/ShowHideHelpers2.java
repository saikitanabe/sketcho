package net.sevenscales.editor.content.utils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Widget;

public class ShowHideHelpers2 {
	private boolean onOuterElement = false;
	private Widget inner;

	public ShowHideHelpers2(Widget outer, final Widget inner) {
		this(outer, inner, 6000);
	}
	
	public ShowHideHelpers2(Widget outer, final Widget inner, int delay) {
		this.inner = inner;
		final RepeatingCommand hideslider = new RepeatingCommand() {
			@Override
			public boolean execute() {
				if (!onOuterElement) {
					EffectHelpers.fadeOut(inner.getElement());
				}
				return false;
			}
		};

		Scheduler.get().scheduleFixedDelay(hideslider, delay);

		outer.addDomHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				onOuterElement = true;
				EffectHelpers.fadeIn(inner.getElement());
			}
		}, MouseOverEvent.getType());
		outer.addDomHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				System.out.println(Element.as(event.getNativeEvent().getEventTarget()).getId());
//				if (!Element.as(event.getNativeEvent().getEventTarget()).isOrHasChild(inner.getElement())) {
					onOuterElement = false;
					Scheduler.get().scheduleFixedDelay(hideslider, 6000);
//				}
			}
		}, MouseOutEvent.getType());
	}
	
	public void forceFadeOut() {
		EffectHelpers.fadeOut(inner.getElement());
	}
}
