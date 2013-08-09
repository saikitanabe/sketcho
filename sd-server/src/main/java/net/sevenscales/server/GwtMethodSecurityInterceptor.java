package net.sevenscales.server;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.acegisecurity.intercept.InterceptorStatusToken;
import org.acegisecurity.intercept.method.aopalliance.MethodSecurityInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ReflectiveMethodInvocation;

import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RPCServletUtils;

public class GwtMethodSecurityInterceptor extends MethodSecurityInterceptor {
  public GwtMethodSecurityInterceptor() {
  }

  public class GwtAdaptMethodInvocation implements MethodInvocation {
    private Method method;
    private ReflectiveMethodInvocation mi;

    public GwtAdaptMethodInvocation(ReflectiveMethodInvocation mi) throws IOException, ServletException {
      this.mi = mi;
      for (Object a : mi.getArguments()) {
        if (a instanceof HttpServletRequest) {
          HttpServletRequest request = (HttpServletRequest)a;
          String payload = RPCServletUtils.readContentAsUtf8(request, true);
          RPCRequest rpcRequest = RPC.decodeRequest(payload);
          this.method = rpcRequest.getMethod();
          TestThreadLocal.setPayload(payload);
          break;
        }
      }
//      
//      RPCServletUtils.readContentAsUtf8(request, true)
//      
//      RPCRequest rpcRequest = RPC.decodeRequest(payload, this.getClass(), this);
//      return RPC.invokeAndEncodeResponse(this, rpcRequest.getMethod(),
//          rpcRequest.getParameters(), rpcRequest.getSerializationPolicy());

    }

    public Method getMethod() {
      return method;
    }

    public Object[] getArguments() {
      return mi.getArguments();
    }

    public AccessibleObject getStaticPart() {
      return mi.getStaticPart();
    }

    public Object getThis() {
      return mi.getThis();
    }

    public Object proceed() throws Throwable {
      return mi.proceed();
    }
    
  }
  
  @Override
  public Object invoke(MethodInvocation mi) throws Throwable {
    Object result = null;

    MethodInvocation tm = mi;
    if (mi instanceof ReflectiveMethodInvocation) {
      tm = new GwtAdaptMethodInvocation((ReflectiveMethodInvocation)mi);
    }

    InterceptorStatusToken token = super.beforeInvocation(tm);

    try {
        result = mi.proceed();
    } finally {
        result = super.afterInvocation(token, result);
    }

    return result;
  }
}
