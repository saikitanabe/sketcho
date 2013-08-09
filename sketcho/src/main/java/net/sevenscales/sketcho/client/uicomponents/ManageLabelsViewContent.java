package net.sevenscales.sketcho.client.uicomponents;

import java.util.List;

import net.sevenscales.domain.api.ILabel;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.UiNotifier;
import net.sevenscales.plugin.constants.SdRegistryEvents;
import net.sevenscales.serverAPI.remote.LabelRemote;
import net.sevenscales.sketcho.client.uicomponents.ConfirmationDialog.ICallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ManageLabelsViewContent extends SimplePanel {
  interface MyUiBinder extends UiBinder<Widget, ManageLabelsViewContent> {}
  private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
  
  public interface MyStyle extends CssResource {
    String notactive();
    String active();
    String readstate();
    String editinglabel();
    String editTextBox();
  }
  
  @UiField FlexTable list;
  @UiField MyStyle style;

  public ManageLabelsViewContent(Context context) {
    setWidget(uiBinder.createAndBindUi(this));
    loadLabels(context);
  }
  
  public static class HTMLDouble extends HTML {
    public HTMLDouble(String value) {
      super(value);
    }

    public void addDoubleClickHandler(DoubleClickHandler handler) {
      addDomHandler(handler, DoubleClickEvent.getType());
    }
  }
  
  public void clear() {
    int row = 0;
    int count = list.getRowCount();
    while (--count >= 0) {
      for (int column = 0; column < list.getCellCount(row); ++column) {
        list.clearCell(row, column);
      }
      list.removeRow(row);
    }
    list.clear();
  }

  private void updateTitle(ILabel label, TextBox editLabel, final Context context, DeckPanel deck) {
    if (!label.getValue().equals(editLabel.getText())) {
      // update if changed
      label.setValue(editLabel.getText());
      LabelRemote.Util.inst.update(label, new AsyncCallback<ILabel>() {
        public void onSuccess(ILabel result) {
          context.getEventRegistry().handleEvent(SdRegistryEvents.LABEL_CHANGED, null);
          loadLabels(context);
        }
        public void onFailure(Throwable caught) {
          UiNotifier.instance().showError("Label update failed");
        }
      });
    }
    deck.showWidget(0);
  }
  
  private void updateOrder(ILabel label, TextBox orderEdit, final Context context, DeckPanel orderDeck) {
    label.setOrderValue(Integer.valueOf(orderEdit.getText()));
    LabelRemote.Util.inst.update(label, new AsyncCallback<ILabel>() {
      public void onSuccess(ILabel result) {
        context.getEventRegistry().handleEvent(SdRegistryEvents.LABEL_CHANGED, null);
        loadLabels(context);
      }
      public void onFailure(Throwable caught) {
      }
    });
    orderDeck.showWidget(0);
  }

  private void loadLabels(final Context context) {
    LabelRemote.Util.inst.findAll(context.getProjectId(), new AsyncCallback<List<ILabel>>() {
      public void onSuccess(List<ILabel> result) {
        clear();
        int row = 0;
        for (final ILabel label : result) {
          final DeckPanel deck = new DeckPanel();
          HTMLDouble labelHTML = new HTMLDouble(label.getValue());
          labelHTML.setTitle("Double click to edit");
          
          HorizontalPanel edit = new HorizontalPanel();
          edit.setStyleName(style.editinglabel());
          
          final TextBox editLabel = new TextBox();
          editLabel.setStyleName(style.editTextBox());
          editLabel.setText(label.getValue());
          
          editLabel.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
              if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                updateTitle(label, editLabel, context, deck);
              }
            }
          });
          
          Button saveEdit = new Button("save");
          saveEdit.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              updateTitle(label, editLabel, context, deck);
            }
          });
          
          edit.add(saveEdit);
          edit.add(editLabel);
          edit.setCellWidth(saveEdit, "40px");
//          edit.setCellHorizontalAlignment(editLabel, HorizontalPanel.ALIGN_LEFT);
          
          labelHTML.addDoubleClickHandler(new DoubleClickHandler() {
            public void onDoubleClick(DoubleClickEvent event) {
              deck.showWidget(1);
              editLabel.setFocus(true);
            }
          });
          
          labelHTML.addStyleName(style.readstate());
          labelHTML.getElement().getStyle().setFontWeight(FontWeight.BOLD);
          deck.add(labelHTML);
          deck.add(edit);
          deck.showWidget(0);
          list.setWidget(row, 0, deck);
          
          ////// order value
          {
            final DeckPanel orderDeck = new DeckPanel();
            HTMLDouble order = new HTMLDouble("Order: " +label.getOrderValue());
            order.addStyleName(style.readstate());
            
            final TextBox orderEdit = new TextBox();
            orderEdit.addKeyUpHandler(new KeyUpHandler() {
              public void onKeyUp(KeyUpEvent event) {
                if (!orderEdit.getText().matches("[1-9][0-9]*")) {
                  orderEdit.setText(label.getOrderValue().toString());
                  orderEdit.selectAll();
                }
              }
            });
            orderEdit.setText(label.getOrderValue().toString());
            orderEdit.addKeyDownHandler(new KeyDownHandler() {
              @Override
              public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                  updateOrder(label, orderEdit, context, orderDeck);
                }
              }
            });

            Button orderSave = new Button("save");
            orderSave.addClickHandler(new ClickHandler() {
              public void onClick(ClickEvent event) {
                updateOrder(label, orderEdit, context, orderDeck);
              }
            });

            HorizontalPanel orderEditPanel = new HorizontalPanel();
            
            order.addDoubleClickHandler(new DoubleClickHandler() {
              public void onDoubleClick(DoubleClickEvent event) {
                orderDeck.showWidget(1);
                orderEdit.setFocus(true);
                DeferredCommand.addCommand(new Command() {
                  public void execute() {
                    orderEdit.selectAll();
                  }
                });
              }
            });
            
            orderEditPanel.add(orderSave);
            orderEditPanel.add(orderEdit);
            
            orderEditPanel.setCellWidth(orderSave, "40px");
            
            orderDeck.add(order);
            orderDeck.add(orderEditPanel);
            orderDeck.showWidget(0);
            list.setWidget(row, 1, orderDeck);
          }


          ///// show
          HTML show = new HTML("show");
          show.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              label.setVisible(true);
              LabelRemote.Util.inst.update(label, new AsyncCallback<ILabel>() {
                public void onSuccess(ILabel result) {
                  context.getEventRegistry().handleEvent(SdRegistryEvents.LABEL_CHANGED, null);
                  loadLabels(context);
                }
                public void onFailure(Throwable caught) {
                }
              });
            }
          });
          show.addStyleName(label.getVisible() ? style.active() : style.notactive());
          list.setWidget(row, 2, show);
          
          HTML hide = new HTML("hide");
          hide.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              label.setVisible(false);
              LabelRemote.Util.inst.update(label, new AsyncCallback<ILabel>() {
                public void onSuccess(ILabel result) {
                  context.getEventRegistry().handleEvent(SdRegistryEvents.LABEL_CHANGED, null);
                  loadLabels(context);
                }
                public void onFailure(Throwable caught) {
                }
              });
            }
          });
          hide.addStyleName( (!label.getVisible() ? style.active() : style.notactive()) ); 
          list.setWidget(row, 3, hide);
          
          HTML delete = new HTML("delete");
          delete.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              new ConfirmationDialog(new ICallback() {
                public void doit() {
                  LabelRemote.Util.inst.remove(label, new AsyncCallback<Void>() {
                    public void onSuccess(Void result) {
                      context.getEventRegistry().handleEvent(SdRegistryEvents.LABEL_CHANGED, null);
                      loadLabels(context);
                    }
                    public void onFailure(Throwable caught) {
                      UiNotifier.instance().showError("Label remove failed");
                    }
                  });
                }
                public void canceled() {
                }
              }, "Delete label "+label.getValue()+"?"); 
            }
          });
          delete.addStyleName(style.notactive()); 
          list.setWidget(row, 4, delete);
          ++row;
        }
      }
      
      public void onFailure(Throwable caught) {
      }
    });
  }
}
