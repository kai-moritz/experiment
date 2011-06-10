package de.halbekunst.versuch.util;

import de.halbekunst.fotos.model.Picture;
import de.halbekunst.fotos.model.Tag;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 *
 * @author kai
 */
public class PicturesPage implements Page {
  public enum Type { UNDEFINED, YEAR, MONTH, WEEK, DAY };

  private final Type type;
  private final Integer year, month, week, date;
  private final Date start, end;
  private final Set<Tag> tags;
  private final Integer page, limit;

  private Picture picture;
  private Integer width, height;

  public Integer size;
  public List<Picture> pictures;


  public PicturesPage(Type type, Integer year, Integer month, Integer week, Integer date, Date start, Date end, Set<Tag> tags, Integer page, Integer limit, Locale locale) {
    this.type = type;
    this.year = year;
    this.month = month;
    this.week = week;
    this.date = date;
    this.start = start;
    this.end = end;
    this.tags = tags;
    this.page = page;
    this.limit = limit;
  }

  public PicturesPage(Picture picture) {
    type = Type.UNDEFINED;
    year = null;
    month = null;
    week = null;
    date = null;
    this.start = null;
    this.end = null;
    this.tags = null;
    this.page = null;
    this.limit = null;

    this.setPicture(picture);
  }


  @Override
  public Type getType() {
    return type;
  }

  @Override
  public Integer getYear() {
    return year;
  }

  @Override
  public Integer getMonth() {
    return month;
  }

  @Override
  public Integer getWeek() {
    return week;
  }

  @Override
  public Integer getDate() {
    return date;
  }

  @Override
  public Date getStart() {
    return start;
  }

  @Override
  public Date getEnd() {
    return end;
  }

  @Override
  public Set<Tag> getTags() {
    return tags;
  }

  @Override
  public Integer getPage() {
    return page;
  }

  @Override
  public Integer getLimit() {
    return limit;
  }


  public final void setPicture(Picture picture) {
    this.picture = picture;
    width = picture.getWidth();
    height = picture.getHeight();
    double scale = 800d/(double)(width > height ? width : height);
    width = (int)Math.round((double)width*scale);
    height = (int)Math.round((double)height*scale);
  }

  @Override
  public Picture getPicture() {
    return picture;
  }

  @Override
  public Integer getWidth() {
    return width;
  }

  @Override
  public Integer getHeight() {
    return height;
  }


  @Override
  public Integer getSize() {
    return size;
  }

  @Override
  public List<Picture> getPictures() {
    return pictures;
  }


  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy");
    builder.append("year=");
    builder.append(year);
    builder.append(", month=");
    builder.append(month);
    builder.append(", week=");
    builder.append(week);
    builder.append(", date=");
    builder.append(date);
    builder.append(", start=");
    builder.append(start == null ? "null" : formatter.format(start));
    builder.append(", end=");
    builder.append(end == null ? "null" : formatter.format(end));
    builder.append(", tags=");
    builder.append(tags);
    builder.append(", picture=");
    builder.append(picture == null ? "null" : picture.getId());
    builder.append(", page=");
    builder.append(page);
    builder.append(", limit=");
    builder.append(limit);
    return builder.toString();
  }
}
