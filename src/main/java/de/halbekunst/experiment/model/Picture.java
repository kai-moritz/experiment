package de.halbekunst.experiment.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import org.hibernate.annotations.Index;


/**
 *
 * @author kai
 */
@Entity
@Table(name="pictures")
public class Picture implements Serializable, Comparable<Picture> {
  public final static long serialVersionUID = 1l;

  public enum State { UNSAFED, STAGED, STORED };
  public enum Access { PUBLIC, RESTRICTED, PRIVATE };

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Version
  private Integer version;
  @Column(name="md5",unique=true, nullable=false)
  @Index(name="md5")
  private String md5sum;
  private Integer width, height;
  private String suffix;
  private String mimeType;
  @Temporal(TemporalType.TIMESTAMP)
  private Date created;
  @Temporal(TemporalType.TIMESTAMP)
  private Date modified;
  private String filename;


  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getMd5sum() {
    return md5sum;
  }

  public void setMd5sum(String md5sum) {
    this.md5sum = md5sum;
  }

  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

  public String getSuffix() {
    return suffix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getModified() {
    return modified;
  }

  public void setModified(Date modified) {
    this.modified = modified;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }


  @Override
  public int compareTo(Picture picture) {
    return created.compareTo(picture.created);
  }


  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (!(obj instanceof Picture))
      return false;

    final Picture other = (Picture) obj;

    if (this.getMd5sum() == null)
      return other.getMd5sum() == null;

    return this.getMd5sum().equals(other.getMd5sum());
  }

  @Override
  public int hashCode() {
    return this.getMd5sum() != null ? this.getMd5sum().hashCode() : 0;
  }

  @Override
  public String toString() {
    return getMd5sum();
  }
}
