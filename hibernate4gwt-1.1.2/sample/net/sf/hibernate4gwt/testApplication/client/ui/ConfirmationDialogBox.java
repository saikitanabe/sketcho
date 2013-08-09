/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.client.ui;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Generic confirmation dialog box
 * @author bruno.marchesson
 *
 */
public class ConfirmationDialogBox extends DialogBox
{	
	//-------------------------------------------------------------------------
	//
	// Constructor
	//
	//-------------------------------------------------------------------------
	/**
	 * Complete constructor
	 * @param title
	 * @param text
	 */
	public ConfirmationDialogBox(String title, String text,
								 final Command yesCommand, 
								 final Command noCommand)
	{
	//	Confirmation dialog is modal and must not be hidden until Yes or No has been chosen
	//
		super(false, true);

		init(title, text, yesCommand, noCommand);	
	}

	//-------------------------------------------------------------------------
	//
	// Internal methods
	//
	//-------------------------------------------------------------------------
	/**
	 * Graphic initialisation
	 */
	protected void init(String title, String text,
						final Command yesCommand, 
						final Command noCommand)
	{
		setHeight("100px");
	//	Title
	//
		setTitle(title);
		setText(title);
		
	//	Text
	//
		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.add(new Label(text));
		mainPanel.setHeight("100%");
		
	//	Set Yes / No buttons
	//
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setWidth("100%");
		buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		mainPanel.add(buttonPanel);
		
		// YES
		Button yesButton = new Button("Yes");
		yesButton.setWidth("50%");
	    yesButton.addClickListener(new ClickListener()
	    {
	        public void onClick(Widget sender)
	        {
	          exectue(yesCommand);
	        }
	    });
	    buttonPanel.add(yesButton);
	    
	    // NO
		Button noButton = new Button("No");
		noButton.setWidth("50%");
	    noButton.addClickListener(new ClickListener()
	    {
	        public void onClick(Widget sender)
	        {
	          exectue(noCommand);
	        }
	    });
	    buttonPanel.add(noButton);
	    
	    setWidget(mainPanel);
	}
	
	/**
	 * Command execution
	 * @param command
	 */
	protected void exectue(Command command)
	{
		hide();
		if (command != null)
		{
			command.execute();
		}
	}
	
	//-------------------------------------------------------------------------
	//
	// Center dialog box
	//
	//------------------------------------------------------------------------
	/**
	 * Show override
	 */
	public void show() {
      super.show();
      int left = (Window.getClientWidth() - getOffsetWidth()) / 2 + getBodyScrollLeft();
      int top = (Window.getClientHeight() - getOffsetHeight()) / 2 + getBodyScrollTop();
      setPopupPosition(left, top);
    }

    private native int getBodyScrollLeft() /*-{
      return $doc.body.scrollLeft;
    }-*/;

    private native int getBodyScrollTop() /*-{
      return $doc.body.scrollTop;
    }-*/;
}
