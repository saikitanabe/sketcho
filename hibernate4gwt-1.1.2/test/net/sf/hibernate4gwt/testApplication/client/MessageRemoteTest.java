package net.sf.hibernate4gwt.testApplication.client;

import net.sf.hibernate4gwt.testApplication.client.message.MessagePanel;
import net.sf.hibernate4gwt.testApplication.client.message.NewMessagePanel;
import net.sf.hibernate4gwt.testApplication.domain.IMessage;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;

/**
 * Message remote service test case
 * @author bruno.marchesson
 *
 */
public class MessageRemoteTest extends GWTTestCase
{
	//-------------------------------------------------------------------------
	//
	// Required method
	//
	// -------------------------------------------------------------------------
	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() {
		return "net.sf.hibernate4gwt.testApplication.TestApplication";
	}

	// -----------------------------------------------------------------------
	//
	// Test method
	//
	// -----------------------------------------------------------------------
	
	/**
	 * Test message load, save, details and creation
	 */
	public void testMessagePanel()
	{
	// 	Load main module
	//
		final TestApplication application = TestHelper.createLoggedApplication();

	// 	Test that the message list has been loaded
	//  (asynchronous RPC call)
	//
		Timer loadTimer = new Timer() {
			public void run() {
				MessagePanel messagePanel = application.getMessageBoard().getFirstMessageLine().getMessagePanel();
				
				// Test last message load
				assertNotNull(messagePanel);
				assertNotNull(messagePanel.getMessage());
				
				// The author must not have been loaded
				assertNull(messagePanel.getMessage().getAuthor());
				
				// Create a new message
				messagePanel.getMessageLabel().setText("Saving unit test");
				messagePanel.saveMessage();
			}
		};
		
	//	Test that the first message has been saved
	//  (asynchronous RPC call)
	//
		Timer saveTimer = new Timer() {
			public void run() {
				MessagePanel messagePanel = application.getMessageBoard().getFirstMessageLine().getMessagePanel();
				
				// Test that a new message 
				IMessage message = messagePanel.getMessage();
				assertNotNull(message);
				assertEquals("Saving unit test", message.getMessage());
				assertTrue(message.getId() > 0);
				assertNotNull(message.getVersion());
				assertTrue(message.getVersion().intValue() > 0);
				
				// author not loaded
				assertNull(message.getAuthor());
				
				// showDetails
				messagePanel.getMainPanel().setOpen(true);
			}
		};
		
	//	Test details loading for first message
	//
		Timer detailsTimer = new Timer() {
			public void run() {
				MessagePanel messagePanel = application.getMessageBoard().getFirstMessageLine().getMessagePanel();
				
				// Test that the saved message is still associated with a user
				IMessage message = messagePanel.getMessage();
				assertNotNull(message);
				assertEquals("Saving unit test", message.getMessage());
				assertTrue(message.getId() > 0);
				assertNotNull(message.getVersion());
				assertTrue(message.getVersion().intValue() > 0);
				
				// Test author loading
				assertNotNull(message.getAuthor());
				
				//	Create a new message
				NewMessagePanel newMessagePanel = application.getNewMessagePanel();
				newMessagePanel.getTextBox().setText("Creation unit test");
				newMessagePanel.getPostButton().click();
				
				application.getMessageBoard().refresh();
			}
		};
		
		Timer createTimer = new Timer() {
			public void run() {
				NewMessagePanel messagePanel = application.getNewMessagePanel();
				
				// Test that the saved message is still associated with a user
				IMessage message = messagePanel.getMessage();
				assertNotNull(message);
				assertEquals("Creation unit test", message.getMessage());
				assertTrue(message.getId() > 0);
				assertNotNull(message.getVersion());

				// check that created message 
				
				// delete the created message
				application.getMessageBoard().getFirstMessageLine().doDelete(false);
				application.getMessageBoard().refresh();
			}
		};
		
		Timer deleteTimer = new Timer() {
			public void run() {
				MessagePanel messagePanel = application.getMessageBoard().getFirstMessageLine().getMessagePanel();
				
				// Test that the saved message is still associated with a user
				IMessage message = messagePanel.getMessage();
				assertNotNull(message);
				assertFalse(message.getMessage().equals("Creation unit test"));

				// tell the test system the test is now done
				finishTest();
			}
		};

		// Set a delay period significantly longer than the
		// event is expected to take.
		delayTestFinish(40000);

		// Schedule the event and return control to the test system.
		int delay = 12000;
		loadTimer.schedule(delay);
		delay += 4000;
		saveTimer.schedule(delay);
		delay+= 4000;
		detailsTimer.schedule(delay);
		delay += 4000;
		createTimer.schedule(delay);
		delay += 4000;
		deleteTimer.schedule(delay);
	}
}