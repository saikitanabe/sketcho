/**
 * 
 */
package net.sf.hibernate4gwt.testApplication.client.ui;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Icon button editable label, derived from GWT-WL
 * @author bruno.marchesson
 *
 */
public class IconEditableLabel extends Composite implements HasText
{
	//----
	// Constants
	//----
	/**
	 * Default confirm icon url
	 */
	private static String CONFIRM_ICON_URL = "img/ok.png";
	
	/**
	 * Default cancel icon url
	 */
	private static String CANCEL_ICON_URL = "img/cancel.png";
	

	//----
	// Attributes
	//----
    /**
     * TextBox element to enable text to be changed if Label is not word wrapped
     */
    private TextBox changeText;
    
    /**
     * Label element, which is initially is diplayed.
     */
    private Label text;
    
    /**
     * String element that contains the original text of a
     * Label prior to it being edited.
     */
    private String originalText;

    /**
     * Simple button to confirm changes
     */
    private Widget confirmChange;
    
    /**
     * Simple button to cancel changes
     */
    private Widget cancelChange;
    
    /**
     * Flag to indicate that Label is in editing mode.
     */
    private boolean isEditing = false;
    
    /**
     * Flag to indicate that label can be edited.
     */
    private boolean isEditable = true;
    
    /**
     * Local copy of the update class passed in to the constructor.
     */
    private ChangeListener updater = null;
    
    /**
     * Constructor that uses default text values for buttons.  
     * 
     * @param labelText The initial text of the label.
     * @param onUpdate Handler object for performing actions once label is updated.
     */
    public IconEditableLabel (String labelText, ChangeListener onUpdate)
    {
          createEditableLabel(labelText, onUpdate);
    }

    /**
     * Sets the Label text to the original value, restores the display.
     *
     */
    public void cancelLabelChange ()
    {
        // Set the Label text back to what it was originally
        text.setText(originalText);         
        // Set the object back to display Label rather than TextBox and Buttons
        restoreVisibility();
    }

    /**
     * Change the displayed label to be a TextBox and copy label 
     * text into the TextBox.
     *
     */
    public void changeTextLabel ()
    {
        if (isEditable) {
            // Set up the TextBox
            originalText = text.getText();
            
            // Change the view from Label to TextBox and Buttons
            text.setVisible(false);
            confirmChange.setVisible(true);
            cancelChange.setVisible(true);

            changeText.setText(originalText);
            changeText.setVisible(true);
            changeText.setFocus(true);
            
            //Set instance as being in editing mode.
            isEditing = true;
        }
    }

    /**
	 * Creation of the cancel button
	 */
	protected Widget createCancelButton()
	{
		return new Image(CANCEL_ICON_URL);
	}

    /**
	 * Creation of the Confirm button
	 */
	protected Widget createConfirmButton()
	{
		return new Image(CONFIRM_ICON_URL);
	}

    /**
     * Creates the Label, the TextBox and Buttons.  Also associates
     * the update method provided in the constructor with this instance.
     * 
     * @param labelText The value of the initial Label.
     * @param onUpdate The class that provides the update method called when the Label has been updated.
     * @param visibleLength The visible length (width) of the TextBox/TextArea.
     * @param maxLength The maximum length of text in the TextBox.
     * @param maxHeight The maximum number of visible lines of the TextArea
     * @param okButtonText The text diplayed in the OK button.
     * @param cancelButtonText The text displayed in the Cancel button.
     */
    private void createEditableLabel (String labelText, ChangeListener onUpdate)
    {
        // Put everything in a VerticalPanel
        HorizontalPanel instance = new HorizontalPanel();

        // Create the Label element and add a ClickListener to call out Change method when clicked
        text = new Label(labelText);
        text.setStyleName("editableLabel-label");

        text.addClickListener(new ClickListener()
        {
            public void onClick (Widget sender)
            {
                changeTextLabel();
            }
        });

        // Create the TextBox element used for non word wrapped Labels 
        // and add a KeyboardListener for Return and Esc key presses
        changeText = new TextBox();
        changeText.setStyleName("editableLabel-textBox");

        changeText.addKeyboardListener(new KeyboardListenerAdapter()
        {
            public void onKeyPress (Widget sender, char keyCode, int modifiers)
            {
                // If return then save, if Esc cancel the change, otherwise do nothing
                switch (keyCode) {
                    case 13:
                        setTextLabel();
                        break;
                    case 27:
                        cancelLabelChange();
                        break;
                }
            }
        });
        
        // Set up Confirmation Button
        confirmChange = createConfirmButton();

        if (!(confirmChange instanceof SourcesClickEvents)) {
            throw new RuntimeException("Confirm change button must allow for click events");
        }
        
        ((SourcesClickEvents) confirmChange).addClickListener(new ClickListener()
        {
            public void onClick (Widget sender)
            {
                setTextLabel();
            }
        });

        // Set up Cancel Button
        cancelChange = createCancelButton();
        if (!(cancelChange instanceof SourcesClickEvents)) {
            throw new RuntimeException("Cancel change button must allow for click events");
        }
        
        ((SourcesClickEvents)cancelChange).addClickListener(new ClickListener()
        {
            public void onClick (Widget sender)
            {
                cancelLabelChange();
            }
        });
        
        // Put the buttons in a panel
        FlowPanel buttonPanel = new FlowPanel();
        buttonPanel.setStyleName("editableLabel-buttonPanel");
        buttonPanel.add(confirmChange);
        buttonPanel.add(cancelChange);
        
        // Add panels/widgets to the widget panel
        instance.add(text);
        instance.add(changeText);
        instance.add(buttonPanel);

        // Set initial visibilities.  This needs to be after
        // adding the widgets to the panel because the FlowPanel
        // will mess them up when added.
        text.setVisible(true);
        changeText.setVisible(false);
        confirmChange.setVisible(false);
        cancelChange.setVisible(false);

        // Set the updater method.
        updater = onUpdate;

        // Assume that this is a non word wrapped Label unless explicitly set otherwise
        text.setWordWrap(false);
        
        // Set the widget that this Composite represents
        initWidget(instance);
    }

    /**
     * Get maximum length of editable area.
     * @return maximum length of editable area.
     */
    public int getMaxLength(){
        return changeText.getMaxLength();   
    }

    /**
     * Return the text value of the Label
     */
    public String getText() {
        return text.getText();
    }

    /**
     * Get the visible length of the editable area.
     * @return Visible length of editable area if not a word wrapped label.
     * @throws RuntimeExcpetion If editable label is word wrapped. 
     */
    public int getVisibleLength(){
        return changeText.getVisibleLength();
    }

    /**
     * Returns the value of the isEditable flag.
     *
     * @return
     */
    public boolean isFieldEditable ()
    {
        return isEditable;
    }

    /**
     * Returns the value of the isEditing flag, allowing outside 
     * users to see if the Label is being edited or not.
     *
     * @return
     */
    public boolean isInEditingMode ()
    {
        return isEditing;
    }

    /**
     * Restores visibility of Label and hides the TextBox and Buttons
     *
     */
    private void restoreVisibility ()
    {
        // Change appropriate visibilities
        text.setVisible(true);
        confirmChange.setVisible(false);
        cancelChange.setVisible(false);
        
        changeText.setVisible(false);
        
        // Set isEditing flag to false as we are no longer editing
        isEditing = false;
    }

    /**
     * Allows the setting of the isEditable flag, marking 
     * the label as editable or not.
     *
     * @param flag True or False value depending if the Label is to be editable or not
     */
    public void setEditable (boolean flag)
    {
        isEditable = flag;
    }
    
    /**
     * Set maximum length of editable area.
     * @param length Length of editable area.
     */
    public void setMaxLength(int length){
        changeText.setMaxLength(length);    
    }

    /**
     * Set the text value of the Label 
     */
    public void setText(String newText) {
        text.setText(newText);
    }

	/**
     * Sets the Label text to the new value, restores the 
     * display and calls the update method.
     *
     */
    private void setTextLabel ()
    {
        // Set the Label to be the text in the Text Box
        text.setText(changeText.getText());
        
        // Set the object back to display label rather than TextBox and Buttons
        restoreVisibility();

        // Call the update method provided in the Constructor
        // (this could be anything from alerting the user through to
        // Making an AJAX call to store the data.
        updater.onChange(this);
    }
	
	/**
     * Set the visible length of the editable area.
     * @throws RuntimeExcpetion If editable label is word wrapped. 
     */
    public void setVisibleLength(int length){
        changeText.setVisibleLength(length);
    }
}
