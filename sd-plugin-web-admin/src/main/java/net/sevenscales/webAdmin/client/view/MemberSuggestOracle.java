package net.sevenscales.webAdmin.client.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sevenscales.domain.api.Member;
import net.sevenscales.domain.api.MemberSuggestion;
import net.sevenscales.serverAPI.remote.AdminRemote;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle;

public class MemberSuggestOracle extends SuggestOracle {
  private MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
  private List<Member> members;

  @Override
  public void requestSuggestions(Request request, Callback callback) {
//    AdminRemote.Util.inst.findAll(request, new MemberSuggestCallback(request, callback));
  }
  
  private class MemberSuggestCallback implements AsyncCallback<Response>{
    private Request request;
    private Callback callback;
    
    public MemberSuggestCallback(Request request, Callback callback) {
      this.request = request;
      this.callback = callback;
    }
    public void onSuccess(Response result) {
      oracle.clear();
      for (MemberSuggestion s : (Collection<MemberSuggestion>) result.getSuggestions()) {
        if (members != null && !members.contains(s.getMember())) {
          oracle.add(s.getReplacementString());
        }
      }
      oracle.requestSuggestions(request, callback);
//      callback.onSuggestionsReady(request, result);
    }
    public void onFailure(Throwable caught) {
      oracle.clear();
      Response res = new Response();
      res.setSuggestions(new ArrayList<Suggestion>());
      callback.onSuggestionsReady(request, res);
    }
  }
  
  @Override
  public boolean isDisplayStringHTML() {
    return true;
  }

  public List<Member> getMembers() {
    return members;
  }
  public void setMembers(List<Member> members) {
    this.members = members;
  }

}
