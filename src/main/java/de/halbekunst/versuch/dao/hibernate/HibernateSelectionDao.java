package de.halbekunst.versuch.dao.hibernate;

import de.halbekunst.versuch.dao.SelectionDao;
import de.halbekunst.versuch.model.AbstractSelection;
import de.halbekunst.versuch.model.SavedSelection;
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
public class HibernateSelectionDao extends HibernateDaoSupport implements SelectionDao {
  @Autowired SessionFactory sessionFactory;


  @PostConstruct
  public void init() {
    this.setHibernateTemplate(this.createHibernateTemplate(sessionFactory));
  }


  @Override
  public void save(AbstractSelection selection) {
    sessionFactory.getCurrentSession().saveOrUpdate(selection);
  }

  @Override
  public AbstractSelection get(Long id) {
    return (AbstractSelection) sessionFactory.getCurrentSession().get(AbstractSelection.class, id);
  }

  @Override
  public void remove(AbstractSelection selection) {
    sessionFactory.getCurrentSession().delete(selection);
  }

  @Override
  public List<SavedSelection> list() {
    return (List<SavedSelection>)sessionFactory.getCurrentSession().createQuery("FROM SavedSelection").list();
  }
}
