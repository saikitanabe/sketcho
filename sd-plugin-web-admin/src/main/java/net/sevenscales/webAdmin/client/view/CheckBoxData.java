package net.sevenscales.webAdmin.client.view;

import net.sevenscales.domain.api.Member;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;

public class CheckBoxData extends CheckBox {

  private Member member;
  private int permission;

  public CheckBoxData(Member member, int permission, ClickListener listener, boolean checked) {
    this.member = member;
    this.permission = permission;
    addClickListener(listener);
    setChecked(checked);
  }
  
  public String getUsername() {
    return member.getUsername();
  }
  
  public Member getMember() {
    return member;
  }
  
  public int getPermission() {
    return permission;
  }

}
