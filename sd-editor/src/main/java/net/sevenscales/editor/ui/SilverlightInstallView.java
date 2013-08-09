package net.sevenscales.editor.ui;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SilverlightInstallView extends VerticalPanel {
  private FlexTable table = new FlexTable();
  private String silverlightText = 
    "<a href='http://go.microsoft.com/fwlink/?LinkID=124807' style='text-decoration: none;'>"+
        "<img src='http://go.microsoft.com/fwlink/?LinkId=108181' alt='Get Microsoft Silverlight' style='border-style: none'/>"+
    "</a>";
  
  private String linuxMoonlightText = 
    "<a href='http://www.go-mono.com/moonlight/'>"+
      "<img src=images/moonlight_logo.png alt='Get Moonlight'/>"+
    "</a>";
  
  
  public SilverlightInstallView() {
    setSpacing(20);
    setWidth("100%");
    add(new HTML("<b>Sketcho System Requirements</b>"));
    add(new HTML("Your browser doesn't support <b>SVG</b> rendering. " +
    						 "Change your browser to one of svg rendering supported browsers like <b>Google Chrome</b>, <b>Firefox</b>, <b>Safari</b> or <b>Opera</b>." +
    						 "<p>In case you are using <b>Internet Explorer</b> install Silverlight plugin to edit diagrams."));
    add(new HTML("<b>Restart</b> browser after plugin installation!"));
//    Microsoft Silverlight is required to use Sketcho on Windows and on Macintosh
    add(table);
    table.setWidth("100%");
    table.addStyleName("SilverlightInstallView");
    table.getRowFormatter().addStyleName(0, "SilverlightInstallView-header");
    setCellHorizontalAlignment(table, VerticalPanel.ALIGN_CENTER);

    String link = silverlightText;
    if (isLinux()) {
      link = linuxMoonlightText;
    }
    
//    HTML installLink = new HTML(link);

    table.setWidget(0, 0, new HTML("<b>Operating System</b>"));
    table.setWidget(0, 1, new HTML("<b>Browser</b>"));
    table.setWidget(0, 2, new HTML("<b>Plugin</b>"));
    table.getRowFormatter().addStyleName(0, "SilverlightInstallView-RowFormatter");
//    table.getColumnFormatter().addStyleName(0, "SilverlightInstallView-column");

    table.setWidget(1, 0, new HTML("Windows"));
    table.setWidget(1, 1, new HTML("IE8"));
    table.setWidget(1, 2, new HTML(silverlightText));
    table.getRowFormatter().addStyleName(1, "SilverlightInstallView-RowFormatter");

//    table.setWidget(2, 0, new HTML("Machintosh"));
//    table.setWidget(2, 1, new HTML("Safari or Firefox"));
//    table.setWidget(2, 2, new HTML(silverlightText));
//    table.getRowFormatter().addStyleName(2, "SilverlightInstallView-RowFormatter");
//
//    table.setWidget(3, 0, new HTML("Linux"));
//    table.setWidget(3, 1, new HTML("Firefox"));
//    table.setWidget(3, 2, new HTML(linuxMoonlightText));
//    table.getRowFormatter().addStyleName(3, "SilverlightInstallView-RowFormatter");
    
//    setCellHorizontalAlignment(installLink, VerticalPanel.ALIGN_CENTER);
  }

  private native boolean isLinux()/*-{
    if (navigator.platform == 'Linux') {
      return true;
    }
    return false;
  }-*/;

  public native boolean silverlightInstalled()/*-{
    var isSilverlightInstalled = false;
   
    try
    {
        //check on IE
        try
        {
            var slControl = new ActiveXObject('AgControl.AgControl');
            isSilverlightInstalled = true;
        }
        catch (e)
        {
            //either not installed or not IE. Check Firefox
            if ( navigator.plugins["Silverlight Plug-In"] )
            {
                isSilverlightInstalled = true;
            }
        }
    }
    catch (e)
    {
        //we don't want to leak exceptions. However, you may want
        //to add exception tracking code here.
    }
    return isSilverlightInstalled;
  }-*/;
}
