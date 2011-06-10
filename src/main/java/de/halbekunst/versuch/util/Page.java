package de.halbekunst.versuch.util;

import de.halbekunst.fotos.dao.PictureQuery;
import de.halbekunst.fotos.model.Picture;
import de.halbekunst.versuch.util.PicturesPage.Type;
import java.util.List;

/**
 *
 * @author kai
 */
public interface Page extends PictureQuery {
  public Type getType();
  public Integer getYear();
  public Integer getMonth();
  public Integer getWeek();
  public Integer getDate();
  public Picture getPicture();
  public Integer getWidth();
  public Integer getHeight();
  public Integer getSize();
  public List<Picture> getPictures();
}
