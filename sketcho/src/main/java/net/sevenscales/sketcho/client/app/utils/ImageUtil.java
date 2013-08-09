package net.sevenscales.sketcho.client.app.utils;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ImageUtil {
  public static void show(String url, Integer width, Integer height) {
 // Create the dialog box
    DialogBox dialogBox = createDialogBox(url, width+"px", height+"px");
    dialogBox.setGlassEnabled(true);
    dialogBox.setAnimationEnabled(true);

    dialogBox.center();
    dialogBox.show();
  }

  /**
   * Create the dialog box for this example.
   * @param url 
   * 
   * @return the new dialog box
   */
  private static DialogBox createDialogBox(String url, String width, String height) {
    // Create a dialog box and set the caption text
    final DialogBox dialogBox = new DialogBox();
    dialogBox.setSize(width, height);
    dialogBox.ensureDebugId("cwDialogBox");
//    dialogBox.setText(constants.cwDialogBoxCaption());

    // Create a table to layout the content
    VerticalPanel dialogContents = new VerticalPanel();
    dialogContents.setSpacing(4);
    dialogBox.setWidget(dialogContents);

    // Add some text to the top of the dialog
//    HTML details = new HTML(constants.cwDialogBoxDetails());
//    dialogContents.add(details);
//    dialogContents.setCellHorizontalAlignment(details,
//        HasHorizontalAlignment.ALIGN_CENTER);

    // Add an image to the dialog
    HTML image = new HTML("<a href='"+url+"'><img src='"+url+"'></a>");
    dialogContents.add(image);
    dialogContents.setCellHorizontalAlignment(image,
        HasHorizontalAlignment.ALIGN_CENTER);

    // Add a close button at the bottom of the dialog
    Button closeButton = new Button("Close",
        new ClickHandler() {
          public void onClick(ClickEvent event) {
            dialogBox.hide();
          }
        });
    dialogContents.add(closeButton);
    if (LocaleInfo.getCurrentLocale().isRTL()) {
      dialogContents.setCellHorizontalAlignment(closeButton,
          HasHorizontalAlignment.ALIGN_LEFT);

    } else {
      dialogContents.setCellHorizontalAlignment(closeButton,
          HasHorizontalAlignment.ALIGN_RIGHT);
    }

    // Return the dialog box
    return dialogBox;
  }
}
