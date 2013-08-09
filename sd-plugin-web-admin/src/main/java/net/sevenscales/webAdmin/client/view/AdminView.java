package net.sevenscales.webAdmin.client.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sevenscales.appFrame.api.IContributor;
import net.sevenscales.appFrame.impl.ActionFactory;
import net.sevenscales.appFrame.impl.DynamicParams;
import net.sevenscales.appFrame.impl.IController;
import net.sevenscales.appFrame.impl.ITilesEngine;
import net.sevenscales.appFrame.impl.View;
import net.sevenscales.domain.api.Member;
import net.sevenscales.plugin.api.Context;
import net.sevenscales.plugin.api.PermissionUtil;
import net.sevenscales.plugin.constants.ActionId;
import net.sevenscales.plugin.constants.RequestId;
import net.sevenscales.plugin.constants.RequestValue;
import net.sevenscales.plugin.constants.Styles;
import net.sevenscales.plugin.constants.TileId;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class AdminView extends View<Context> {
	private HTML title;
	private VerticalPanel commandArea;
  private FlexTable table = new FlexTable();
  private VerticalPanel content = new VerticalPanel();
  private VerticalPanel body = new VerticalPanel();
//  private SuggestBox addMemberValue;
  private TextBox addMemberValue;
  private HorizontalPanel hierarchy;
  private HorizontalPanel buttons;
  private MemberConfigurationObserver memberConfigurationObserver;
  
  public interface MemberConfigurationObserver {
    public void addMember(String username);
    public void setPermission(Member member, int permission, boolean enabled);
    public void remove(Member member);
  }
	
//	public AdminView(IController controller, MemberConfigurationObserver addMemberObserver, 
//	    MemberSuggestOracle oracle) {
  public AdminView(IController controller, MemberConfigurationObserver addMemberObserver) {
		super(controller);
		this.memberConfigurationObserver = addMemberObserver;
		
		// title
		title = new HTML("Admin");		
		title.setStyleName("title-text");
		
    // layout code
    commandArea = new VerticalPanel();
    commandArea.setStyleName(Styles.COMMAND_AREA);

    hierarchy =  new HorizontalPanel();
    hierarchy.setStyleName(Styles.PAGE_LINK_HIERARCHY);

    buttons = new HorizontalPanel();
    buttons.addStyleName(Styles.COMMAND_AREA_BUTTONS);

    commandArea.add(hierarchy);
//    commandArea.add(buttons);
    
    // back link
    Map<Object, Object> requests = new HashMap<Object, Object>();
    requests.put(RequestId.CONTROLLER, RequestValue.PROJECTS_CONTROLLER);
    hierarchy.add(ActionFactory
      .createLinkAction("&laquo; back to projects", requests)
      .getWidget());    

    buttons.add(ActionFactory
        .createButtonAction("Add User", ActionId.ADD_USER, controller)
        .getWidget());
    buttons.add(ActionFactory
        .createButtonAction("Delete User", ActionId.DELETE_USER, controller)
        .getWidget());
    
//    addMemberValue = new SuggestBox(oracle);
//    addMemberValue.addSelectionHandler(new SelectionHandler<Suggestion>() {
//		public void onSelection(SelectionEvent<Suggestion> event) {
//	        AdminView.this.memberConfigurationObserver.addMember(
//	                event.getSelectedItem().getReplacementString());
//		}
//	});
    
    addMemberValue = new TextBox();
    addMemberValue.addKeyDownHandler(new KeyDownHandler() {
      @Override
      public void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
          AdminView.this.memberConfigurationObserver.addMember(addMemberValue.getText());
        }
      }
    });
    
    Button addMemberButton = new Button("add");
    addMemberButton.addClickHandler(new ClickHandler() {
		public void onClick(ClickEvent event) {
	        AdminView.this.memberConfigurationObserver.addMember(addMemberValue.getText());
		}
	});
    
    HorizontalPanel addMember = new HorizontalPanel();
    addMember.add(addMemberValue);
    addMember.add(addMemberButton);
    
    body.add(addMember);
    body.add(table);
    
    content.add(body);
    content.setSpacing(20);
	}

	public void activate(ITilesEngine tilesEngine, 
						 DynamicParams params, IContributor contributor) {
	  title.setHTML(controller.getContext().getProject().getName()+ " - Admin");
		tilesEngine.setTile(TileId.TITLE, title);
    tilesEngine.setTile(TileId.COMMAND_AREA, commandArea);

    table.clear();

    table.setHTML(0, 0, "<center>*</center>");
    table.setHTML(0, 1, "Member");
    table.setHTML(0, 2, "Edit");
    table.setHTML(0, 3, "Delete");

    table.getRowFormatter().setStyleName(0, "admin-users-header");

    tilesEngine.setTile(TileId.CONTENT, content);   
	}
	
	public void setUsers(List<Member> users) {
	  table.clear();
	  int row = 1;
	  table.getColumnFormatter().setStyleName(1, "admin-users-user-column");
	  final int EDIT_PERMISSION = 2;
    final int CREATE_RIGHT = 4;
    final int DELETE_PERMISSION = 8;

    ClickListener c = new ClickListener() {
      public void onClick(Widget sender) {
        if (sender instanceof CheckBoxData) {
          CheckBoxData cbd = (CheckBoxData) sender;
          memberConfigurationObserver.setPermission(cbd.getMember(), cbd.getPermission(), cbd.isChecked());
        }
      }
    };

    for (Member member : users) {
      final Member m = member;
      Button remove = new Button("del");
      remove.addClickListener(new ClickListener() {
        public void onClick(Widget sender) {
          memberConfigurationObserver.remove(m);
        }
      });
      
      table.setWidget(row, 0, remove);
      table.setWidget(row, 1, new HTML(member.getUsername()));
      
      boolean editEnabled = PermissionUtil.hasPermission(member.getPermissions(), EDIT_PERMISSION);
      table.setWidget(row, 2, new CheckBoxData(member, EDIT_PERMISSION, c, editEnabled));
      
      boolean deleteEnabled = PermissionUtil.hasPermission(member.getPermissions(), DELETE_PERMISSION);
      table.setWidget(row, 3, new CheckBoxData(member, DELETE_PERMISSION, c, deleteEnabled));
      ++row;
    }
	}

  public List getSelectedUsers() {
//    return listContent.getSelectedItems();
    return null;
  }

  public void updateMember(Member result) {
    for (int i = 1; i < table.getRowCount(); ++i) {
      HTML h = (HTML) table.getWidget(i, 1);
      if (h.getHTML().equals(result.getUsername())) {
        CheckBoxData cbdEdit = (CheckBoxData) table.getWidget(i, 2);
        CheckBoxData cbdDelete = (CheckBoxData) table.getWidget(i, 3);
        boolean editEnabled = PermissionUtil.hasPermission(result.getPermissions(), cbdEdit.getPermission());
        boolean deleteEnabled = PermissionUtil.hasPermission(result.getPermissions(), cbdDelete.getPermission());
        cbdEdit.setChecked(editEnabled);
        cbdDelete.setChecked(deleteEnabled);
        break;
      }
    }
  }

}
