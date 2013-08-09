package net.sevenscales.sketchoconfluenceapp.client.view;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class SlideShow extends Composite {

	private static SlideShowUiBinder uiBinder = GWT
			.create(SlideShowUiBinder.class);

	interface SlideShowUiBinder extends UiBinder<Widget, SlideShow> {
	}
	
	@UiField DialogBox dialog;
//	@UiField Image slide;
	@UiField SimplePanel slidePanel;
	@UiField Button prevButton;
	@UiField Button nextButton;
	private List<String> urls;
	private int currentIndex;

	public SlideShow() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void show(String url) {
		updateNavigation(url);
		dialog.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				dialog.center();
			}
		});
	}

	private void updateNavigation(String url) {
		// when creating a new image, image will be centered
		Image slide = new Image(url);
		slidePanel.setWidget(slide);
		
		slide.addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				dialog.center();
			}
		});
		
		currentIndex = urls.indexOf(url);
		prevButton.setEnabled(false);
		nextButton.setEnabled(false);
		
		if (currentIndex == 0) {
			prevButton.setEnabled(false);
			nextButton.setEnabled(true);
		} else if (currentIndex > 0 && currentIndex < urls.size() - 1) {
			prevButton.setEnabled(true);
			nextButton.setEnabled(true);
		} else {
			prevButton.setEnabled(true);
			nextButton.setEnabled(false);
		}
	}

	public void setSlideUrls(List<String> urls) {
		this.urls = urls;
		for (String url : urls) {
			Image.prefetch(url);
		}
	}
	
	@UiHandler("prevButton")
	public void onPrev(ClickEvent event) {
		// some safe coding
		int index = currentIndex - 1 >= 0 ? currentIndex - 1 : currentIndex;
		updateNavigation(urls.get(index));
	}

	@UiHandler("nextButton")
	public void onNext(ClickEvent event) {
		// some safe coding
		int index = currentIndex + 1 < urls.size() ? currentIndex + 1 : currentIndex;
		updateNavigation(urls.get(index));
	}

}
