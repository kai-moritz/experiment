package de.halbekunst.experiment.dao;

import de.halbekunst.experiment.model.Selection;
import java.util.List;
import javax.annotation.PostConstruct;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

/**
 *
 * @author kai
 */
@Repository
@Lazy(true)
public class SelectionDao extends HibernateDaoSupport {
  @Autowired SessionFactory sessionFactory;


  @PostConstruct
  public void init() {
    this.setHibernateTemplate(this.createHibernateTemplate(sessionFactory));
  }


  public void save(Selection selection) {
    this.getHibernateTemplate().saveOrUpdate(selection);
  }

  public Selection get(Long id) {
    return (Selection) this.getHibernateTemplate().get(Selection.class, id);
  }

  public List<Selection> list() {
    return (List<Selection>)sessionFactory.getCurrentSession().createQuery("FROM Selection").list();
  }
}
