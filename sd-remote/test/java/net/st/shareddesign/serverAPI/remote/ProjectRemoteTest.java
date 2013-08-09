package net.st.shareddesign.serverAPI.remote;
import net.st.shareddesign.domain.api.IProject;
import net.st.shareddesign.domain.dto.ProjectDTO;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class ProjectRemoteTest extends GWTTestCase {
  private IProject project;

  @Override
  public String getModuleName() {
    return "net.st.shareddesign.serverAPI.ServerAPITest";
  }
  
  @Override
  protected void gwtSetUp() throws Exception {
  }
  
  public void test1() {
    ProjectDTO p = new ProjectDTO();
    p.setName("test-project");
    ProjectRemote.Util.inst.save(p, new AsyncCallback<IProject>(){
      public void onSuccess(IProject result) {
        project = result;
        finishTest();
      };
      public void onFailure(Throwable caught) {};
    });
    
    delayTestFinish(500);

//    PageDTO p = new PageDTO();
//    p
//    ProjectRemote.Util.inst.save(page, async);
  }

}
