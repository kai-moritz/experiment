package de.halbekunst.versuch.dao;

import de.halbekunst.versuch.model.Picture;

/**
 *
 * @author kai
 */
public interface PictureDao {
  public void save(Picture picture);
  public Picture get(Long id);
  public void remove(Picture picture);
  public Picture find(String md5);
}
