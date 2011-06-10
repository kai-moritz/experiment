package de.halbekunst.versuch.taglib;

import de.halbekunst.fotos.dao.PictureDao;
import de.halbekunst.fotos.dao.PictureQuery;
import de.halbekunst.fotos.model.Tag;
import de.halbekunst.versuch.util.PicturesPage;
import de.halbekunst.versuch.util.PicturesPage.Type;
import de.halbekunst.versuch.util.PicturesUrl;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author kai
 */
@Configurable
public class CalendarTag extends TagSupport {
  private final static Logger log = LoggerFactory.getLogger(CalendarTag.class);

  @Autowired private PictureDao dao;

  private PicturesUrl url;

  private PicturesPage info;
  private Locale locale;

  private CalendarPictureQuery query;
  private Date start, end;
  private String[] months;
  private String[] days;
  private DateFormat monthFormat;
  private DateFormat dayFormat;


  public CalendarTag() {
    url = new PicturesUrl();
    query = new CalendarPictureQuery();
    months = new String[12];
    days = new String[7];
  }


  @Override
  public int doEndTag() throws JspException {
    try {
      JspWriter out = pageContext.getOut();

      Calendar calendar = Calendar.getInstance(locale);

      /** Datum des anzuzeigenden Tages/Monats bestimmen */
      if (info.getStart() != null) {
        calendar.setTime(info.getStart());
      }
      else {
        if (info.getPicture() != null) {
          /** Start-Datum auf das Datum des aktuellen Bildes setzen */
          calendar.setTime(info.getPicture().getCreated());
        }
        else {
          if (info.getPictures() != null) {
            /** Start-Datum aus den gefundenen Bildern bestimmen */
            calendar.setTime(info.getPictures().get(0).getCreated());
          }
        }
      }

      int year = info.getYear() == null ? calendar.get(Calendar.YEAR) : info.getYear();
      int month = info.getMonth() == null ? calendar.get(Calendar.MONTH) : info.getMonth() - 1;
      int week = info.getWeek() == null ? calendar.get(Calendar.WEEK_OF_YEAR) : info.getWeek();
      int date = info.getDate() == null ? calendar.get(Calendar.DATE) : info.getDate();

      url.setInfo(info);


      out.append("<div id=\"calendar\"><h2><a href=\"");
      calendar.add(Calendar.MONTH, -1);
      url.setYear(calendar.get(Calendar.YEAR));
      url.setMonth(calendar.get(Calendar.MONTH) +1);
      url.setDate(null);
      url.build(out);
      out.append("\">&lt;</a> ");
      calendar.add(Calendar.MONTH, 1);
      switch (info.getType()) {
        case MONTH:
          out.append(months[month]);
          break;
        default:
          out.append("<a href=\"");
          url.setYear(calendar.get(Calendar.YEAR));
          url.setMonth(calendar.get(Calendar.MONTH) +1);
          url.build(out);
          out.append("\">");
          out.append(months[month]);
          out.append("</a>");
      }
      calendar.add(Calendar.MONTH, 1);
      out.append(" <a href=\"");
      url.setYear(calendar.get(Calendar.YEAR));
      url.setMonth(calendar.get(Calendar.MONTH) +1);
      url.build(out);
      out.append("\">&gt;</a></h2><table><thead><tr><th>KW</th>"); // << TODO: message.bundle
      calendar.add(Calendar.MONTH, -1);

      for (int i=0; i<7; i++) {
        out.append("<th>");
        out.append(days[i]);
        out.append("</th>");
      }
      out.append("</tr></thead><tbody");
      if (info.getType() == Type.MONTH)
        out.append(" class=\"current\">");
      else
        out.append(">");

      calendar.set(Calendar.DATE, 1);
      calendar.add(Calendar.DATE, (-1) * ((calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek() + 7) % 7));

      do {
        start = calendar.getTime();
        calendar.add(Calendar.DATE, 6);
        end = calendar.getTime();
        long count = dao.size(query);
        calendar.add(Calendar.DATE, -6);
        int w = calendar.get(Calendar.WEEK_OF_YEAR);
        if (w == week && info.getType() == Type.WEEK) {
          out.append("<tr class=\"current\"><th>");
          out.append(Integer.toString(w));
          out.append("</th>");
        }
        else {
          if (count < 1) {
            out.append("<tr><th>");
            out.append(Integer.toString(w));
            out.append("</th>");
          }
          else {
            out.append("<tr><th><a href=\"");
            url.setYear(year);
            url.setMonth(null);
            url.setWeek(w);
            url.setDate(null);
            url.build(out);
            out.append("\">");
            out.append(Integer.toString(w));
            out.append("</a></th>");
          }
        }
        for (int i=0; i<7; i++) {
          int d = calendar.get(Calendar.DATE);
          int y = calendar.get(Calendar.YEAR);
          int m = calendar.get(Calendar.MONTH);
          List<String> classes = new LinkedList<String>();
          if (m != month)
            classes.add("nim");
          if (d == date && m == month && info.getType() == Type.DAY)
            classes.add("current");
          if (classes.isEmpty()) {
            out.append("<td>");
          }
          else {
            out.append("<td class=\"");
            Iterator<String> it = classes.iterator();
            while (it.hasNext()) {
              out.append(it.next());
              if (it.hasNext())
                out.append(' ');
            }
            out.append("\">");
          }
          start = end = calendar.getTime();
          count = dao.size(query);
          if (count > 0 && (d != date || info.getType() != Type.DAY)) {
            out.append("<a href=\"");
            url.setYear(y);
            url.setMonth(calendar.get(Calendar.MONTH) + 1);
            url.setDate(d);
            url.build(out);
            out.append("\">");
            out.append(Integer.toString(d));
            out.append("</a>");
          }
          else {
            out.append(Integer.toString(d));
          }
          out.append("</td>");
          calendar.add(Calendar.DATE, 1);
        }
        out.append("</tr>");
      }
      while (calendar.get(Calendar.MONTH) == month);
      out.append("</tbody></table></div>");

      return EVAL_PAGE;
    }
    catch (Exception e) {
      log.error(e.toString());
      throw new Error(e);
    }
    finally {
      info = null;
    }
  }


  public void setInfo(PicturesPage info) {
    this.info = info;
  }


  @Autowired
  public void setLocale(Locale locale) {
    this.locale = locale;

    monthFormat = new SimpleDateFormat("MMMM", locale);
    dayFormat = new SimpleDateFormat("EE", locale);

    Calendar calendar = Calendar.getInstance(locale);
    for (int i=0; i<12; i++) {
      calendar.set(Calendar.MONTH, i);
      months[i] = monthFormat.format(calendar.getTime());
    }

    calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
    for (int i = 0; i < 7; i++) {
      days[i] = dayFormat.format(calendar.getTime());
      calendar.add(Calendar.DATE, 1);
    }
  }


  private class CalendarPictureQuery implements PictureQuery {

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
      return info.getTags();
    }

    @Override
    public Integer getPage() {
      return null;
    }

    @Override
    public Integer getLimit() {
      return null;
    }
  }
}
