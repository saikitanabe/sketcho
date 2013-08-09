package net.sevenscales.sketcho.client.app.controller.impl.page;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.HandlerBase;
import net.sevenscales.appFrame.impl.uicomponents.ListSimpleComponent;
import net.sevenscales.domain.api.IContent;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IPageOrderedContent;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.api.ITextContent;
import net.sevenscales.domain.constants.Constants;
import net.sevenscales.domain.dto.DiagramContentDTO;
import net.sevenscales.domain.dto.TextContentDTO;
import net.sevenscales.domain.utils.PageIterator;
import net.sevenscales.editor.content.utils.DiagramItemFactory;
import net.sevenscales.serverAPI.remote.PageRemote;
import net.sevenscales.sketcho.client.app.controller.PageController;
import net.sevenscales.sketcho.client.app.utils.PageListFormatter;
import net.sevenscales.sketcho.client.app.utils.PageListFormatter.INameFormatter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CopyContentHandler extends HandlerBase implements ClickHandler {
  private IProject project;
  private ListSimpleComponent listComponent;
  private DialogBox dialog;
  private HorizontalPanel buttons;
  private VerticalPanel panel;
  private PageController pageController;
  private CopyContentHandler self;
  private Tree pages;

  public CopyContentHandler(IProject project, PageController pageController) {
    this.project = project;
    this.pageController = pageController;
  }
  
  private class Link extends Action {
    private HTML html;
    
    public Link(String text, ClickHandler clickHandler) {
      html = new HTML("<a href='javascript:;'>"+text+"</a>");
      addDomHandler(clickHandler, ClickEvent.getType());
      setWidget(html);
    }

//    @Override
    public String getName() {
      return html.getHTML();
    }

//    @Override
    public void setName(String name) {
      html.setHTML(name);
    }    
  }
  
  private class ListSketches implements AsyncCallback<List<IPage>> {
    public ListSketches() {
    }

    public void onSuccess(List<IPage> result) {
      listComponent.clear();
      for (IPage t : result) {
        listComponent.newRow();

        Link link = new Link(t.getName(), new ListPageContentItems());
        link.setData(t);
        listComponent.add(link);
        
        Date date = new Date(t.getModifiedTime());
        listComponent.add(new HTML(date.toLocaleString()));
  
        listComponent.add(new HTML(t.getModifier()));
      }
    }
    public void onFailure(Throwable caught) {
    }
  }
  
  private class ListPageContentItems implements ClickHandler {
    public void onClick(ClickEvent event) {
      if (pages != null) {
        pages.setVisible(false);
      }
      dialog.setText("Select Content:");
      Action a = (Action) event.getSource();
      IPage page = (IPage) a.getData();
      
      PageRemote.Util.inst.open(page.getId(), new AsyncCallback<IPage>() {
        public void onSuccess(IPage result) {
          listComponent.clear();
          
          for (IPageOrderedContent c : (Set<IPageOrderedContent>) result.getContentItems()) {
            if (c.getContent() instanceof ITextContent) {
              addRow(c);
              String type = "Text";
              listComponent.add(new HTML(type));
            } else if (c.getContent() instanceof IDiagramContent) {
              addRow(c);
              String type = "Model";
              listComponent.add(new HTML(type));
            }
          }
        }
        public void onFailure(Throwable caught) {
          
        }
        
        void addRow(IPageOrderedContent c) {
          listComponent.newRow();
          Link link = new Link(c.getId().toString(), self);
          link.setData(c.getContent());
          listComponent.add(link);
        }
      });
    }
  }

  public void execute() {
    int x = DOM.eventGetClientX(DOM.eventGetCurrentEvent());
    int y = DOM.eventGetClientY(DOM.eventGetCurrentEvent());
    this.self = this;

    dialog = new DialogBox();
    dialog.setText("Select Content Source:");

    panel = new VerticalPanel();
    listComponent = new ListSimpleComponent();
    panel.add(listComponent);

    listComponent.newRow();
//    listComponent.add(new Link("Sketches", new ClickHandler() {
//      public void onClick(ClickEvent sender) {
//        TicketRemote.Util.inst.findAll(project.getId(), new ListSketches());    
//      }
//    }));
    
    listComponent.add(new Link("Sketches", new ClickHandler() {
      public void onClick(ClickEvent sender) {
        listComponent.clear();        
        PageRemote.Util.inst.findAll(project.getId(), Constants.PAGE_TYPE_SKETCH, new ListSketches());
      }
    }));

    listComponent.newRow();
    listComponent.add(new Link("Pages", new ClickHandler() {
      public void onClick(ClickEvent sender) {
        listComponent.clear();        
        pages = new Tree();
        PageIterator pi = new PageIterator(project.getDashboard(), new PageListFormatter(project, pages, new INameFormatter() {
          public Widget format(IPage page, int level) {
            Link result = new Link(page.getName(), new ListPageContentItems());
            result.setData(page);
            return result;
          }
        }));
        pi.iterate();
        
        Iterator<TreeItem> i = pages.treeItemIterator();
        while (i.hasNext()) {
          i.next().setState(true);
        }
        panel.insert(pages, 1);
      }
    }));

    buttons = new HorizontalPanel();
    
    Button cancel = new Button("Cancel");
    cancel.addClickListener(new ClickListener() {
      public void onClick(com.google.gwt.user.client.ui.Widget sender) {
        dialog.hide();
      }
    });
    
//    buttons.add(ok);
    buttons.add(cancel);
    panel.add(buttons);
    dialog.setWidget(panel);

    dialog.setHeight("100px");
    dialog.setPopupPosition(x + 10, y + 10);
//    dialog.center();
    dialog.show();    
  }

  public void onClick(ClickEvent event) {
    Action a = (Action) event.getSource();
    
    IContent newContent = null;
    if (a.getData() instanceof ITextContent) {
      ITextContent t = (ITextContent) a.getData();
      ITextContent text = new TextContentDTO(); 
      text.setText(t.getText());
      newContent = text;
    } else if (a.getData() instanceof IDiagramContent) {
      IDiagramContent dc = (IDiagramContent) a.getData();
      IDiagramContent newdc = new DiagramContentDTO();
      for (IDiagramItem item : (Set<IDiagramItem>) dc.getDiagramItems()) {
        newdc.addItem(DiagramItemFactory.createCopy(item));
      }
      newContent = newdc;
    }
      
    pageController.addPageContent(newContent);
    dialog.hide();
  }
}
