package net.st.shareddesign.server.dao.hibernate;

import java.util.List;

import junit.framework.TestCase;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.dto.SdServerEception;
import net.sevenscales.server.dao.hibernate.PageDAO;
import net.sevenscales.server.dao.hibernate.ProjectDAO;
import net.sevenscales.server.domain.DiagramContent;
import net.sevenscales.server.domain.Page;
import net.sevenscales.server.domain.PageOrderedContent;
import net.sf.hibernate4gwt.core.HibernateBeanManager;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.thoughtworks.xstream.XStream;

public class ContentXmlMigrate extends TestCase {
  private static SessionFactory sessionFactory;
  private static HibernateTemplate hibernateTemplate;
  private XStream xstream = new XStream();
  private static HibernateBeanManager hibernateManager;
  
  static {
//    AnnotationConfiguration config = new AnnotationConfiguration();
//    config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect").
//    setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver").
//    setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/sketcho").
//    setProperty("hibernate.connection.username", "root").
//    setProperty("hibernate.connection.password", "").
//    setProperty("hibernate.connection.pool_size", "1").
//    setProperty("hibernate.connection.autocommit", "false").
//    setProperty("hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider").
//    setProperty("hibernate.hbm2ddl.auto", "update").
//    setProperty("hibernate.show_sql", "true");
////
//    config.addAnnotatedClass(PageOrderedContent.class);
//    config.addAnnotatedClass(Content.class);
//    config.addAnnotatedClass(TextContent.class);
//    config.addAnnotatedClass(TextLineContent.class);
//    config.addAnnotatedClass(ListContent.class);
//    config.addAnnotatedClass(LinkContent.class);
//    config.addAnnotatedClass(Project.class);
//    config.addAnnotatedClass(Page.class);
//    config.addAnnotatedClass(Property.class);
//    config.addAnnotatedClass(PageProperty.class);
//    config.addAnnotatedClass(DiagramItem.class);
//    config.addAnnotatedClass(DiagramContent.class);
//    config.addAnnotatedClass(ContentProperty.class);
//    
//    config.addFile("/Users/saikitanabe/Documents/workspace/sd-server/target/classes/applicationContext-database.xml");
    
    GenericApplicationContext springContext = new GenericApplicationContext();
    XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(springContext);
    Resource[] resources = new Resource[]{
        new ClassPathResource("applicationContext.xml"),
        new ClassPathResource("applicationContext-GWT.xml"),
        new ClassPathResource("applicationContext-acegi-security.xml"), 
        new ClassPathResource("applicationContext-common-authorization.xml") 
        };
    xmlReader.loadBeanDefinitions(resources);
    springContext.refresh();

    sessionFactory = (SessionFactory) springContext.getBean("sessionFactory");
//    sessionFactory = config.buildSessionFactory();
    hibernateTemplate = new HibernateTemplate(sessionFactory);
    hibernateManager = (HibernateBeanManager) springContext.getBean("hibernateBeanManager");
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
  
//    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken
//      ("testitollo", "pass");

//    SecurityContextHolder.getContext().setAuthentication(authRequest);
  }
  
  @Override
  protected void tearDown() throws Exception {
//    SecurityContextHolder.getContext().setAuthentication(null);

    TransactionSynchronizationManager.unbindResource(sessionFactory);
    SessionFactoryUtils.releaseSession(session, sessionFactory);
  }

  public void testConvertValueToXmlProperty() throws SdServerEception {
//    ContentDAO dao = new ContentDAO();
//    dao.setSessionFactory(sessionFactory);
//    dao.setHibernateTemplate(hibernateTemplate);
//    
//    for (IContent c : dao.findAll()) {
//      System.out.println(c);
//    }
//    hibernateTemplate.find("from Project p");
//    Project p = (Project) hibernateTemplate.get(Project.class, new Long(1));
//    System.out.println(p.getName());

//    List<Page> pages = hibernateTemplate.find("from "+Page.class.getName()+" p");
    List<Page> pages = hibernateTemplate.find("from page p");
    for (Page p : pages) {
      for (PageOrderedContent poc : p.getContentItems()) {
//        if (poc.getContent() instanceof DiagramContent) {
//          DiagramContent dc = (DiagramContent) poc.getContent();
//          System.out.println(xstream.toXML(hibernateManager.clone(dc)));
//        }
      }
    }

//    List<Content> contents = hibernateTemplate.find("from "+Content.class.getName()+" p");
//    
//    for (Content c : contents) {
//      if (c instanceof DiagramContent) {
//        DiagramContent dc = (DiagramContent) c;
//        System.out.println(xstream.toXML(hibernateManager.clone(dc.getDiagramItems())));
////        System.out.println();
//      }
      
//      if (c.getName() != null && c.getName().length() > 0) {
//        ContentProperty cp = new ContentProperty();
//        cp.setType(ITemplate.BOOLEAN);
//        cp.setValue(Boolean.FALSE.toString());
//        c.getProperties().put(ContentPropertyDTO.DELETABLE, cp);
//        hibernateTemplate.save(cp);
//        hibernateTemplate.update(c);
//        System.out.println(c);
//      }
//    }
    hibernateTemplate.flush();
    hibernateTemplate.clear();
  }
}
