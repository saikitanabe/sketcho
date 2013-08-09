package net.sevenscales.server;

import net.sf.hibernate4gwt.core.hibernate.HibernateUtil;
import net.sf.hibernate4gwt.gwt.HibernateRemoteService;

import org.hibernate.Session;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.google.gwt.user.client.rpc.SerializationException;

public abstract class OpenViewHibernateRemoteService extends HibernateRemoteService {
  public OpenViewHibernateRemoteService() {
  }

	public String processCall(String payload) throws SerializationException {
//	  ApplicationContext.getInstance().setThreadLocalRequest(this.getThreadLocalRequest());
//    ApplicationContext.getInstance().setThreadLocalResponse(this.getThreadLocalResponse());
//
	  Session session = SessionFactoryUtils.
      getSession(HibernateUtil.getInstance().getSessionFactory(), true);
	  SessionHolder sessionHolder = new SessionHolder(session);
	  TransactionSynchronizationManager.
      bindResource(HibernateUtil.getInstance().getSessionFactory(), sessionHolder);

	  String result = super.processCall(payload);
		TransactionSynchronizationManager.
      unbindResource(HibernateUtil.getInstance().getSessionFactory());

		SessionFactoryUtils.
      releaseSession(session, HibernateUtil.getInstance().getSessionFactory());

		return result;
	}
}
