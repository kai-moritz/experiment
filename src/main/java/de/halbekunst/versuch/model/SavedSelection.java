package de.halbekunst.versuch.model;

import java.util.LinkedList;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author kai
 */
@Entity
@DiscriminatorValue("s")
public class SavedSelection extends AbstractSelection {

  private String name;


  public SavedSelection() {
    setPictures(new LinkedList<Picture>());
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  @Override
  public boolean equals(Object other) {
    if (other == this)
      return true;
    if (other == null)
      return false;

    if (!(other instanceof SavedSelection))
      return false;

    final SavedSelection selection = (SavedSelection) other;
    return name.equals(selection.name);
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 97 * hash + name.hashCode();
    return hash;
  }

  @Override
  public String toString() {
    return name;
  }
}
