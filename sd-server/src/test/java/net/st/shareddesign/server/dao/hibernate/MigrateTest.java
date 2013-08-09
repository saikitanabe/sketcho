package net.st.shareddesign.server.dao.hibernate;

import junit.framework.TestCase;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.server.dao.hibernate.PageDAO;
import net.sevenscales.server.dao.hibernate.ProjectDAO;
import net.sevenscales.server.domain.Content;
import net.sevenscales.server.domain.ContentProperty;
import net.sevenscales.server.domain.DiagramContent;
import net.sevenscales.server.domain.DiagramItem;
import net.sevenscales.server.domain.ListContent;
import net.sevenscales.server.domain.Page;
import net.sevenscales.server.domain.PageOrderedContent;
import net.sevenscales.server.domain.PageProperty;
import net.sevenscales.server.domain.Project;
import net.sevenscales.server.domain.Property;
import net.sevenscales.server.domain.TextContent;
import net.sevenscales.server.domain.TextLineContent;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class MigrateTest extends TestCase {
  private static SessionFactory sessionFactory;
  private static HibernateTemplate hibernateTemplate;
  
  static {
    AnnotationConfiguration config = new AnnotationConfiguration();
    config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect").
      setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver").
      setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/sketcho2").
      setProperty("hibernate.connection.username", "root").
      setProperty("hibernate.connection.password", "").
      setProperty("hibernate.connection.pool_size", "1").
      setProperty("hibernate.connection.autocommit", "false").
//      setProperty("hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider").
      setProperty("hibernate.hbm2ddl.auto", "update").
      setProperty("hibernate.show_sql", "true");

    config.addAnnotatedClass(PageOrderedContent.class);
    config.addAnnotatedClass(Content.class);
    config.addAnnotatedClass(TextContent.class);
    config.addAnnotatedClass(TextLineContent.class);
    config.addAnnotatedClass(ListContent.class);
    config.addAnnotatedClass(Project.class);
    config.addAnnotatedClass(Page.class);
    config.addAnnotatedClass(Property.class);
    config.addAnnotatedClass(PageProperty.class);
    config.addAnnotatedClass(DiagramItem.class);
    config.addAnnotatedClass(DiagramContent.class);
    config.addAnnotatedClass(ContentProperty.class);
    
    sessionFactory = config.buildSessionFactory();
    hibernateTemplate = new HibernateTemplate(sessionFactory);
  }

  private PageDAO pageDAO;
  private ProjectDAO projectDAO;
  private IProject project;
  private Session session;

  @Override
  protected void setUp() throws Exception {
    HibernateTemplate hibernateTemplate =
        new HibernateTemplate(sessionFactory);
    this.session = SessionFactoryUtils.
      getSession(sessionFactory, true);
    TransactionSynchronizationManager.
      bindResource(sessionFactory, 
          new SessionHolder(session));
  
    projectDAO = new ProjectDAO();
    projectDAO.setSessionFactory(sessionFactory);
    projectDAO.setHibernateTemplate(hibernateTemplate);
    
    project = new Project();
    project.setName("TestProject");
    project = (IProject) projectDAO.save(project);
    
    pageDAO = new PageDAO();
    pageDAO.setSessionFactory(sessionFactory);
    pageDAO.setHibernateTemplate(hibernateTemplate);
  }
  
  public void testEmpty() {
    
  }
  
  @Override
  protected void tearDown() throws Exception {
    TransactionSynchronizationManager.unbindResource(sessionFactory);
    SessionFactoryUtils.releaseSession(session, sessionFactory);
  }

}
