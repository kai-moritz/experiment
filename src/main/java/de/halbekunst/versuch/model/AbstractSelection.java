package de.halbekunst.versuch.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 *
 * @author kai
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.CHAR)
@DiscriminatorValue("X")
@Table(name="selections")
public abstract class AbstractSelection implements Selection, Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @ManyToMany
  private List<Picture> pictures;


  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public List<Picture> getPictures() {
    return pictures;
  }

  public void setPictures(List<Picture> pictures) {
    this.pictures = pictures;
  }

  @Override
  public abstract int hashCode();
  @Override
  public abstract boolean equals(Object other);
  @Override
  public abstract String toString();
}
