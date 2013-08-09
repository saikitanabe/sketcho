package net.st.shareddesign.server.dao.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;
import net.sevenscales.domain.api.IContent;
import net.sevenscales.domain.api.IPage;
import net.sevenscales.domain.api.IPageOrderedContent;
import net.sevenscales.domain.api.IProject;
import net.sevenscales.domain.dto.ContentPropertyDTO;
import net.sevenscales.domain.dto.PagePropertyDTO;
import net.sevenscales.domain.dto.SdServerEception;
import net.sevenscales.server.dao.hibernate.PageDAO;
import net.sevenscales.server.dao.hibernate.ProjectDAO;
import net.sevenscales.server.domain.Content;
import net.sevenscales.server.domain.ContentProperty;
import net.sevenscales.server.domain.DiagramContent;
import net.sevenscales.server.domain.DiagramItem;
import net.sevenscales.server.domain.LinkContent;
import net.sevenscales.server.domain.ListContent;
import net.sevenscales.server.domain.Page;
import net.sevenscales.server.domain.PageOrderedContent;
import net.sevenscales.server.domain.PageProperty;
import net.sevenscales.server.domain.Project;
import net.sevenscales.server.domain.Property;
import net.sevenscales.server.domain.SketchTemplate;
import net.sevenscales.server.domain.TextContent;
import net.sevenscales.server.domain.TextLineContent;

import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

public class PageDAOTest extends TestCase {
  private static SessionFactory sessionFactory;
  private static HibernateTemplate hibernateTemplate;
  
  static {
    AnnotationConfiguration config = new AnnotationConfiguration();
    config.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect").
      setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver").
      setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:baseball").
      setProperty("hibernate.connection.username", "sa").
      setProperty("hibernate.connection.password", "").
      setProperty("hibernate.connection.pool_size", "1").
      setProperty("hibernate.connection.autocommit", "true").
      setProperty("hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider").
      setProperty("hibernate.hbm2ddl.auto", "create-drop").
      setProperty("hibernate.show_sql", "true");

    config.addAnnotatedClass(PageOrderedContent.class);
    config.addAnnotatedClass(Content.class);
    config.addAnnotatedClass(TextContent.class);
    config.addAnnotatedClass(TextLineContent.class);
    config.addAnnotatedClass(ListContent.class);
    config.addAnnotatedClass(LinkContent.class);
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
    
    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken
      ("testitollo", "pass");

//    Authentication authResult = authenticationManager.authenticate(authRequest);
    SecurityContextHolder.getContext().setAuthentication(authRequest);

//    Authentication a = SecurityContextHolder.getContext().setAuthentication()
  }
  
  @Override
  protected void tearDown() throws Exception {
    SecurityContextHolder.getContext().setAuthentication(null);

    TransactionSynchronizationManager.unbindResource(sessionFactory);
    SessionFactoryUtils.releaseSession(session, sessionFactory);
  }

  public void testCreate() throws SdServerEception {
    Page page = new Page();
    page.setName("funda");
    page.setProject(project);
    
    pageDAO.addPage(page, project.getDashboard());
    
    List<IPage> pages = pageDAO.findAll();
    
    // dashboard and funda
    assertEquals(2, pages.size());
    
    for (IPage p : pages) {
      if (p.getParent() != null) {
        assertEquals("funda", p.getName());
        assertEquals(p.getParent(), project.getDashboard());
      } else {
        assertEquals("Dashboard", p.getName());
      }
    }
  }
  
  public void testMove() throws SdServerEception {
    IPage bapu = new Page();
    bapu.setName("babu");
    bapu.setProject(project);
    bapu = pageDAO.addPage(bapu, project.getDashboard());
    
    IPage raapu = new Page();
    raapu.setName("raapu");
    raapu.setProject(project);
    raapu = pageDAO.addPage(raapu, project.getDashboard());
    
    IPage pobe = new Page();
    pobe.setName("pobe");
    pobe.setProject(project);
    pobe = pageDAO.addPage(pobe, raapu);
    
    hibernateTemplate.flush();
    hibernateTemplate.clear();

    // Dashboard
    //    babu
    //    raapu
    //      pobe
    
    raapu = pageDAO.open(raapu.getId());
    assertEquals(1, raapu.getSubpages().size());

    // Dashboard
    //    raapu
    //      babu
    //      pobe
    pageDAO.move(bapu.getId(), 1, raapu.getId());

    hibernateTemplate.flush();
    hibernateTemplate.clear();
    
    List<IPage> pages = pageDAO.findAll(project.getId());
    assertEquals(4, pages.size());
    
    for (IPage p : pages) {
      if (!p.getName().equals("Dashboard")) {
        System.out.println(p.getName());
        assertNotNull(p.getParent());
      }
    }
    
    raapu = pageDAO.open(raapu.getId());
    assertEquals(2, raapu.getSubpages().size());
    
    for (IPage sp : (Set<IPage>) raapu.getSubpages()) {
      System.out.println(sp.getName() + sp.getOrderValue());
      assertNotNull(sp.getParent());
    }
  }

  public void testMove2() throws SdServerEception {
    IPage first = new Page();
    first.setName("first");
    first.setProject(project);
    first = pageDAO.addPage(first, project.getDashboard());
    
    IPage second = new Page();
    second.setName("second");
    second.setProject(project);
    second = pageDAO.addPage(second, project.getDashboard());
    
    IPage third = new Page();
    third.setName("third");
    third.setProject(project);
    third = pageDAO.addPage(third, project.getDashboard());

    IPage fourth = new Page();
    fourth.setName("fourth");
    fourth.setProject(project);
    fourth = pageDAO.addPage(fourth, project.getDashboard());

    IPage last = new Page();
    last.setName("last");
    last.setProject(project);
    last = pageDAO.addPage(last, project.getDashboard());

    hibernateTemplate.flush();
    hibernateTemplate.clear();

    // Dashboard
    //    first
    //    second
    //    third
    //    fourth
    //    last
    
    IPage dashboard = pageDAO.open(project.getDashboard().getId());
    assertEquals(5, dashboard.getSubpages().size());

    // Dashboard
    //    first
    //    fourth
    //    third
    //    second
    //    last
    pageDAO.move(fourth.getId(), 2, dashboard.getId());

    hibernateTemplate.flush();
    hibernateTemplate.clear();
    
    List<Page> all = hibernateTemplate.find("from page p");
    for (Page p : all) {
      if (!p.getName().equals("Dashboard")) {
        Assert.notNull(p.getParent());
      }
    }

    dashboard = pageDAO.open(project.getDashboard().getId());
    assertEquals(5, dashboard.getSubpages().size());
    
    
//    List<IPage> pages = pageDAO.findAll(project.getId());
//    assertEquals(4, pages.size());
//    
//    for (IPage p : pages) {
//      if (!p.getName().equals("Dashboard")) {
//        System.out.println(p.getName());
//        assertNotNull(p.getParent());
//      }
//    }
//    
//    second = pageDAO.open(second.getId());
//    assertEquals(2, second.getSubpages().size());
//    
//    for (IPage sp : (Set<IPage>) second.getSubpages()) {
//      System.out.println(sp.getName() + sp.getOrderValue());
//      assertNotNull(sp.getParent());
//    }
  }

  public void testMoveContent() {
    IPage page = project.getDashboard();
    TextContent tc = new TextContent();
    tc.setText("ekateksti");
    
    TextContent tc2 = new TextContent();
    tc2.setText("tokateksti");
    
    PageOrderedContent poc1 = new PageOrderedContent();
    poc1.setContent(tc);
    poc1.setOrderValue(1);
    poc1.setPage(page);

    PageOrderedContent poc2 = new PageOrderedContent();
    poc2.setContent(tc2);
    poc2.setOrderValue(2);
    poc2.setPage(page);

    pageDAO.addContent(poc1);
    pageDAO.addContent(poc2);
    
    pageDAO.moveContent(poc1, 2);
    assertEquals(new Integer(2), poc1.getOrderValue());
    
    hibernateTemplate.flush();
    hibernateTemplate.clear();

    page = pageDAO.open(page.getId());
    
    Integer i = 1;
    for (IPageOrderedContent poc : (Set<IPageOrderedContent>)page.getContentItems()) {
      assertEquals(i++, poc.getOrderValue());
    }
    
    IPageOrderedContent tmp = (IPageOrderedContent) page.getContentItems().toArray()[1];
    assertEquals(new Integer(2), tmp.getOrderValue());
    tmp = pageDAO.moveContent(tmp, 1);
    assertEquals(new Integer(1), tmp.getOrderValue());
  }
  
  public void testCreatePage() {
    Page page = new Page();
    page.setName("funda");
    page.setProject(project);

    Page p = pageDAO.createPage(new SketchTemplate(), project.getId());
    hibernateTemplate.flush();
    hibernateTemplate.clear();
    
    p = (Page) pageDAO.open(p.getId());
    
    Map<String,IContent> contents = new HashMap<String, IContent>();
    for (PageOrderedContent c : (Set<PageOrderedContent>) p.getContentItems()) {
      System.out.println(c.getContent().getName());
      contents.put(c.getContent().getName(), c.getContent());
    }
    
    PageProperty pp = p.getProperties().get(PagePropertyDTO.NAME_VISIBLE);
    assertEquals("false", pp.getValue());
    assertEquals("Boolean", pp.getType());
    
    ContentProperty tmp = (ContentProperty) contents.get("Description").getProperties().get(ContentPropertyDTO.NAME_AS_TITLE);
    assertEquals(Boolean.TRUE.toString(), tmp.getValue());
  }
  
  /**
   * testing cascade DELETE_ORPHAN, but currently not null constraint is ignored
   */
  public void testUpdateContent() {
    IPage page = project.getDashboard();
    
    DiagramContent dc = new DiagramContent();
    dc.setDiagramItems(new TreeSet<DiagramItem>());
    
    PageOrderedContent poc1 = new PageOrderedContent();
    poc1.setContent(dc);
    poc1.setOrderValue(1);
    poc1.setPage(page);

    pageDAO.addContent(poc1);
//    hibernateTemplate.flush();
//    hibernateTemplate.clear();
    
    DiagramItem di = new DiagramItem();
//    di.type = "classitem";
    di.setText("jees");
//    di.setShape(shapetext);
    di.setType("classitem");
//    di.setDiagramContent(dc);
    dc.addItem(di);
    
    pageDAO.updateContent(poc1);
    
    hibernateTemplate.flush();
    hibernateTemplate.clear();
//    hibernateTemplate.evict(poc1);
    
//    page = pageDAO.open(page.getId());
    Object[] contents = page.getContentItems().toArray();
    dc = (DiagramContent) ((PageOrderedContent) contents[1]).getContent();
    dc.getDiagramItems().clear();
    
    pageDAO.updateContent(poc1);
    hibernateTemplate.flush();
    hibernateTemplate.clear();

//    Object[] items = dc.getDiagramItems().toArray();
//    items[0];
  }

}
