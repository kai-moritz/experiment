package de.halbekunst.versuch.dao;

import de.halbekunst.versuch.model.AbstractSelection;
import de.halbekunst.versuch.model.SavedSelection;
import java.util.List;

/**
 *
 * @author kai
 */
public interface SelectionDao {
  public void save(AbstractSelection selection);
  public AbstractSelection get(Long id);
  public void remove(AbstractSelection selection);
  public List<SavedSelection> list();
}
