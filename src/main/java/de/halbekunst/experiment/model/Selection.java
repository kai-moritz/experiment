package de.halbekunst.experiment.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 *
 * @author kai
 */
@Entity
@Table(name="selections")
public class Selection implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String name;
  @ManyToMany
  private List<Picture> pictures;


  public Selection() {
    pictures = new LinkedList<Picture>();
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Picture> getPictures() {
    return pictures;
  }

  public void setPictures(List<Picture> pictures) {
    this.pictures = pictures;
  }


  @Override
  public boolean equals(Object other) {
    if (other == this)
      return true;
    if (other == null)
      return false;

    if (!(other instanceof Selection))
      return false;

    final Selection selection = (Selection) other;
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
