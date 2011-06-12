package de.halbekunst.versuch.dao.hibernate;

import de.halbekunst.versuch.dao.PictureDao;
import de.halbekunst.versuch.model.Picture;
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
@Repository("pictureDao")
@Lazy(true)
public class HibernatePictureDao extends HibernateDaoSupport implements PictureDao {
  @Autowired SessionFactory sessionFactory;


  @PostConstruct
  public void init() {
    this.setHibernateTemplate(this.createHibernateTemplate(sessionFactory));
  }

  @Override
  public void save(Picture picture) {
    this.getHibernateTemplate().saveOrUpdate(picture);
  }

  @Override
  public Picture get(Long id) {
    return (Picture)this.getHibernateTemplate().get(Picture.class, id);
  }

  @Override
  public void remove(Picture picture) {
    this.getHibernateTemplate().delete(picture);
  }

  @Override
  public Picture find(String md5) {
    return (Picture)
        sessionFactory
          .getCurrentSession()
          .createQuery("FROM Picture WHERE md5sum = :md5")
          .setString("md5", md5)
          .uniqueResult();
  }
}
