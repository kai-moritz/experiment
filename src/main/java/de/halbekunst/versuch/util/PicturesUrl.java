package de.halbekunst.versuch.util;

import de.halbekunst.fotos.model.Picture;
import de.halbekunst.fotos.model.Tag;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author kai
 */
public class PicturesUrl {
  private Page info;

  private Integer year, month, week, date;
  private Date start, end;
  private Set<Tag> tags;
  private Picture picture;
  private Integer page, limit;

  private boolean isSetYear, isSetMonth, isSetWeek, isSetDate, isSetStart, isSetEnd, isSetTags, isSetPage, isSetLimit;

  private String base = "/pictures.html";


  public void setInfo(Page info){
    this.info = info;
    reset();
  }


  public void setYear(Integer year) {
    this.year = year;
    isSetYear = true;
  }

  public void setMonth(Integer month) {
    this.month = month;
    isSetMonth = true;
  }

  public void setWeek(Integer week) {
    this.week = week;
    isSetWeek = true;
  }

  public void setDate(Integer date) {
    this.date = date;
    isSetDate = true;
  }

  public void setStart(Date start) {
    this.start = start;
    isSetStart = true;
  }

  public void setEnd(Date end) {
    this.end = end;
    isSetEnd = true;
  }

  public void setTags(Set<Tag> tags) {
    this.tags = tags;
    isSetTags = true;
  }

  public void setPicture(Picture picture) {
    this.picture = picture;
  }

  public void setPage(Integer page) {
    this.page = page;
    isSetPage = true;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
    isSetLimit = true;
  }


  public void reset() {
    isSetYear = false;
    isSetMonth = false;
    isSetWeek = false;
    isSetDate = false;
    isSetStart = false;
    isSetEnd = false;
    isSetTags = false;
    isSetPage = false;
    isSetLimit = false;
  }


  public Appendable build(Appendable out) throws IOException {
    Integer year = isSetYear ? this.year : info.getYear();
    Integer month = isSetMonth ? this.month : info.getMonth();
    Integer week = isSetWeek ? this.week : info.getWeek();
    Integer date = isSetDate ? this.date : info.getDate();
    Date start = isSetStart ? this.start : info.getStart();
    Date end = isSetEnd ? this.end : info.getEnd();
    Set<Tag> tags = isSetTags ? this.tags : info.getTags();
    Integer page = isSetPage ? this.page : info.getPage();
    Integer limit = isSetLimit ? this.limit : info.getLimit();

    boolean first = true;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    out.append(base);
    if (year != null) {
      first = false;
      out.append("?year=");
      out.append(Integer.toString(year));
      if (month != null) {
        out.append("&amp;month=");
        out.append(Integer.toString(month));
        if (date != null) {
          out.append("&amp;date=");
          out.append(Integer.toString(date));
        }
      }
      else {
        if (week != null) {
          out.append("&amp;week=");
          out.append(Integer.toString(week));
        }
      }
    }
    else {
      if (start != null) {
        first = false;
        out.append("?start=");
        out.append(formatter.format(start));
        if (end != null && !end.equals(start)) {
          out.append("&amp;end=");
          out.append(formatter.format(end));
        }
      }
    }

    if (tags != null) {
      for (Tag tag : tags) {
        out.append(first ? "?tag=" : "&amp;tag=");
        out.append(Long.toString(tag.getId()));
        first = false;
      }
    }

    if (page != null) {
      out.append(first ? "?page=" : "&amp;page=");
      out.append(Integer.toString(page));
    }

    if (limit != null) {
      out.append(first ? "?limit=" : "&amp;limit=");
      out.append(Integer.toString(limit));
    }

    if (picture != null) {
      out.append(first ? "?md5sum=" : "&amp;md5sum=");
      out.append(picture.getMd5sum());
      out.append("#_");
      out.append(picture.getId().toString());
    }

    return out;
  }


  @Override
  public String toString() {
    try {
      StringBuilder builder = new StringBuilder();
      build(builder);
      return builder.toString();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
