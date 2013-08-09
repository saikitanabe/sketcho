package net.sevenscales.editor.content.utils;

import net.sevenscales.domain.DiagramContentDTO;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.editor.content.UiSketchoBoardEditContent;
import net.sevenscales.editor.content.utils.ImageHelpers.UploadService;
import net.sevenscales.editor.gfx.svg.converter.SvgConverter;
import net.sevenscales.editor.gfx.svg.converter.SvgData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class ImageHelpers extends Composite {
	private static Frame downloadFrame;
	
	static {
		downloadFrame = new Frame();
		downloadFrame.getElement().getStyle().setDisplay(Display.NONE);
		RootPanel.get().add(downloadFrame);
	}

	private static ImageHelpersUiBinder uiBinder = GWT
			.create(ImageHelpersUiBinder.class);

	interface ImageHelpersUiBinder extends UiBinder<Widget, ImageHelpers> {
	}
	
	public interface UploadService {
		void upload(String boardName, String data);
	}
	
	@UiField CanvasElement imageCanvas;
	private PopupPanel popup;
	private UploadService uploadService;
	private UiSketchoBoardEditContent editorContent;
	
	public ImageHelpers(UploadService uploadService) {
		this.uploadService = uploadService;
		initWidget(uiBinder.createAndBindUi(this));
		
		popup = new PopupPanel();
		popup.setStyleName("ImageHelpers");
		popup.setAutoHideEnabled(true);
		popup.setWidget(this);
	}

	public void downloadAsImage(final UiSketchoBoardEditContent editorContent) {
		this.editorContent = editorContent;
//		popup.show();
		
//    SvgConverter sc = new SvgConverter();
//    
//    editorContent.externalize();
//    SvgData svg = sc.convertToSvg((IDiagramContent) editorContent.getContent(),
//    		editorContent.getModelingPanel().getSurface());
//    
//    _updateCanvas(Element.as(imageCanvas), svg.svg);

		popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
		    SvgConverter sc = new SvgConverter();
		    
		    editorContent.externalize();
		    SvgData svg = sc.convertToSvg((IDiagramContent) editorContent.getContent(),
		    		editorContent.getModelingPanel().getSurface());
		    
		    System.out.println(svg.svg);
		    _updateCanvas2(Element.as(imageCanvas), svg.svg, svg.width, svg.height);
//		    DiagramContentFactory.jsonUploadService(content, content.getName(), data);
//		    _setHref(data);
//		    String prev = Window.Location.getHref();
//		    Window.Location.replace(data);
//		    Window.Location.replace(prev);
		    
//		    downloadFrame.setUrl(data);

				popup.center();
			}
		});
	}
	
	private void imageLoaded() {
	  String data = imageCanvas.toDataUrl("image/png");
	  
	  DiagramContentDTO content = (DiagramContentDTO) editorContent.getContent();
	  uploadService.upload(content.getName(), data);
	}
	
	private native void _setHref(String data)/*-{
		var prev = $wnd.location.href;
		$wnd.location.href = data;
		$wnd.location.href = prev;
	}-*/;
	
	private native void _updateCanvas2(Element canvas, String svgstr, int width, int height)/*-{
	 	$wnd.canvg(canvas, svgstr, { ignoreMouse: true, ignoreAnimation: true });
		var self = this;
		canvas.width = width;
		canvas.height = height;
  	self.@net.sevenscales.editor.content.utils.ImageHelpers::imageLoaded()();
	}-*/;

	
	private native void _updateCanvas(Element canvas, String svgstr, int width, int height)/*-{
//		img.src = "data:image/svg+xml;base64," + btoa(svg_xml);
//		 $wnd.canvg(elem, svgstr, { ignoreMouse: true, ignoreAnimation: true });
		var self = this;
		canvas.width = width;
		canvas.height = height;
		var myimage = new Image();
		var ctx = canvas.getContext("2d");
		myimage.onload = function() {
       ctx.drawImage(myimage, 0, 0);
       self.@net.sevenscales.editor.content.utils.ImageHelpers::imageLoaded()();
   	}
//		myimage.src = "data:image/svg+xml;base64," + $wnd.btoa(svgstr);
		myimage.src = "data:image/svg+xml;base64," + $wnd.btoa($wnd.unescape($wnd.encodeURIComponent(svgstr)));
	}-*/;

}
