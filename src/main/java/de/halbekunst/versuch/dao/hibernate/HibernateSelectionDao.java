package de.halbekunst.versuch.dao.hibernate;

import de.halbekunst.versuch.dao.SelectionDao;
import de.halbekunst.versuch.model.AbstractSelection;
import de.halbekunst.versuch.model.SavedSelection;
import java.util.List;
import javax.annotation.Resource;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

/**
 *
 * @author kai
 */
@Repository
@Lazy(true)
public class HibernateSelectionDao implements SelectionDao {
  @Resource SessionFactory sessionFactory;


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
