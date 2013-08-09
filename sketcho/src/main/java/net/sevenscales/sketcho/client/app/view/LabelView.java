package net.sevenscales.sketcho.client.app.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.Action;
import net.sevenscales.appFrame.impl.ActionFactory;
import net.sevenscales.appFrame.impl.DragControllerRegistration;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.appFrame.impl.uicomponents.ListUiHelper;
import net.sevenscales.domain.api.ILabel;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.IPermissionContributor;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.plugin.constants.TileId;
import net.sevenscales.sketcho.client.uicomponents.HorizontalEventPanel;
import net.sevenscales.sketcho.client.uicomponents.LabelColorSelector;
import net.sevenscales.sketcho.client.uicomponents.ColorSelector.ICallback;
import net.sevenscales.sketcho.client.uicomponents.drop.FlexTableRowDragController;
import net.sevenscales.sketcho.client.uicomponents.drop.FlexTableRowDropController;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class LabelView extends View<Context> {
  private DisclosurePanel main;
  private IProject project;
//  private HTML manageLabels;
  private DecoratorPanel decorator = new DecoratorPanel();
  private SimplePanel sizerPanel = new SimplePanel();
  private DecoratedPopupPanel simplePopup = new DecoratedPopupPanel(true);
  private ILabelViewListener listener;
  private LabelList labels;
  private List<ILabel> labelsData;
  private HTML help;
  private DeckPanel content = new DeckPanel();
  private FlexTableRowDragController dragController;
  private LabelButtonsUi labelButtons;
  
  private class LabelList extends ListUiHelper<ILabel> {
    private FlexTableRowDropController dropController;
    
    public LabelList(final PickupDragController dragController) {
//      super.allocate(rows, columns)
      controller.getContext().getEventRegistry().registerLabelsDragController(
        new DragControllerRegistration() {
          public void register(Panel dropPanel) {
            dropController = new FlexTableRowDropController(dropPanel);
            dragController.registerDropController(dropController);
          }
        });
      addStyleName("LabelView-LabelList");
    }
    @Override
    public void addRow(List<Widget> columns, ILabel domainObject) {
      super.addRow(columns, domainObject, "LabelView-LabelListRow");
    }
  }
  
  public interface ILabelViewListener {
    public void newLabel();
    public void createLabel(String text);
    public void changeLabelColor(ILabel label, String bc, String color);
    public void manageLabels();
  }
  
	public LabelView(IController<Context> controller, ILabelViewListener listener) {
		super(controller);
		this.listener = listener;
//		decorator.addStyleName("sd-app-HierarchyView");
		main = new DisclosurePanel("Labels for sketches");
		main.setOpen(false);
    main.setWidth("100%");
		main.addStyleName("sd-app-LabelView");

    this.dragController = new FlexTableRowDragController(RootPanel.get(), false);

    help = new HTML("Create labels by clicking a new button. Then drag label on top of a sketch.");
    labels = new LabelList(dragController);
    labels.setWidth("100%");
    

//    labels.setStyleName("");
    content.setWidth("100%");
    content.add(help);
    content.add(labels);
    content.showWidget(0);
//    content.addStyleName("debug");
    
//    this.organize = ActionFactory
//      .createButtonAction("Organize", ActionId.ORGANIZE, controller);
    this.labelButtons = new LabelButtonsUi(listener);
//    this.newpanel = new HorizontalEventPanel();
//    Image plus = new Image("images/01_plus.png");
////    plus.getElement().getStyle().setPadding(0, Unit.PX);
//    newpanel.add(plus);
//    newpanel.setCellVerticalAlignment(plus, HorizontalPanel.ALIGN_MIDDLE);
//    HTML newlabel = new HTML("New label");
////    newlabel.setWordWrap(false);
//    newpanel.add(newlabel);
//    newpanel.addStyleName("HierarchyView-Button");
//    newpanel.addClickHandler(new ClickHandler() {
//      public void onClick(ClickEvent event) {
//        LabelView.this.listener.newLabel(); 
//      }
//    });
    
//    manageLabels = new HTML("Manage");
//    manageLabels.addStyleName("HierarchyView-Button");
//    manageLabels.addClickHandler(new ClickHandler() {
//      public void onClick(ClickEvent event) {
//        LabelView.this.listener.manageLabels(); 
//      }
//    });
    
    VerticalPanel panel = new VerticalPanel();
    panel.setSpacing(0);
    
    HorizontalPanel buttons = new HorizontalPanel();
    buttons.setSpacing(5);
    buttons.add(labelButtons);
//    buttons.add(manageLabels);
    panel.add(buttons);
    panel.add(content);
//    content.setTitle("Drag on top of a sketch");
    
    main.setContent(panel);
    sizerPanel.setWidget(main);
    sizerPanel.setWidth("175px");
    sizerPanel.setStyleName("sd-app-HierarchyView");
    decorator.add(sizerPanel);
	}

  public void activate(ITilesEngine tilesEngine, 
						 DynamicParams params, IContributor contributor) {
		tilesEngine.setTile(TileId.CONTENT_LEFT_LABELS, decorator);
		
    IPermissionContributor permissionContributor = controller.getContext().getContributor().cast(
        IPermissionContributor.class);
    
    labelButtons.setVisible(permissionContributor.hasEditPermission());
//    newpanel.setVisible(permissionContributor.hasEditPermission());
//    manageLabels.setVisible(permissionContributor.hasEditPermission());
    help.setVisible(permissionContributor.hasEditPermission());
    labels.clear();
    
    decorator.setVisible(project != null);
    if (project != null) {
      for (final ILabel label : labelsData) {
        if (label.getVisible()) {
          List<Widget> columns = new ArrayList<Widget>();
          
          Map<Object, Object> requests = new HashMap<Object, Object>();
          requests.put(RequestId.CONTROLLER, RequestValue.SKETCHES_CONTROLLER);
          requests.put(RequestId.LABEL_ID, label.getId());
          requests.put(RequestId.PROJECT_ID, project.getId());
          
          Action labelAction = ActionFactory.createLinkAction(label.getValue(), requests);
          labelAction.setWidth("100px"); // limit too long label names; otherwise color picker is not shown
          labelAction.setTitle("Drag on top of a sketch");
          labelAction.setData(label);
          dragController.makeDraggable(labelAction);
          columns.add(labelAction);
          columns.add(new LabelColorSelector(label.getBackgroundColor(), label.getTextColor(), new ICallback() {
            public void changeColor(String bc, String color) {
              listener.changeLabelColor(label, bc, color);
            }
          }));
          labels.addRow(columns, label);
        }
      }
    }

  }

  public void setLabels(List<ILabel> result) {
    this.labelsData = result;
    content.showWidget(labelsData.size() > 0 ? 1 : 0);
  }

  public void setProject(IProject project) {
    this.project = project;
  }

  public void newLabel() {
    final DialogBox dialogBox = new DialogBox();
//    dialogBox.setWidth("250px");
    dialogBox.setGlassEnabled(true);
    dialogBox.setAnimationEnabled(true);

    final TextBox textBox = new TextBox();
    textBox.addKeyDownHandler(new KeyDownHandler() {
      public void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
          listener.createLabel(textBox.getText());
          dialogBox.hide();
        }
      }
    });
    textBox.setWidth("100%");
    DeferredCommand.addCommand(new Command() {
      public void execute() {
        textBox.setFocus(true);
      }
    });
    
    VerticalPanel content = new VerticalPanel();
    content.setSpacing(4);
    content.setWidth("250px");
    dialogBox.setWidget(content);
    
    HorizontalPanel buttons = new HorizontalPanel();
    buttons.setSpacing(5);
    Button ok = new Button("Ok");
    ok.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        listener.createLabel(textBox.getText());
        dialogBox.hide();
      }
    });
    Button cancel = new Button("Cancel");
    cancel.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        dialogBox.hide();
      }
    });
    buttons.add(ok);
    buttons.add(cancel);
    
//    HTML title = new HTML("New label");
    dialogBox.setText("New label");
//    content.add(title);
    content.add(new HTML("New label name:"));
    content.add(textBox);
    content.add(buttons);
    content.setCellHorizontalAlignment(buttons, VerticalPanel.ALIGN_RIGHT);
    
    dialogBox.center();
    dialogBox.show();
  }
}