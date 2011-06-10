package de.halbekunst.versuch.taglib;

import de.halbekunst.fotos.model.Picture;
import de.halbekunst.fotos.model.Tag;
import de.halbekunst.versuch.util.Page;
import de.halbekunst.versuch.util.PicturesUrl;
import java.util.Date;
import java.util.Set;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import org.springframework.web.util.HtmlUtils;

/**
 *
 * @author kai
 */
public class PicturesLinkTag extends TagSupport {

  private PicturesUrl url = new PicturesUrl();

  private String var;
  private String title;
  private String css;


  @Override
  public int doStartTag() throws JspException {
    try {
      JspWriter out = pageContext.getOut();

      if (var != null) {
        StringBuilder builder = new StringBuilder();
        pageContext.setAttribute(var, builder.toString());
        return SKIP_BODY;
      }

      out.append("<a href=\"");
      url.build(out);
      if (css != null) {
        out.append("\" class=\"");
        out.append(css);
      }
      if (title != null) {
        out.append("\" title=\"");
        out.append(HtmlUtils.htmlEscape(title));
      }
      out.append("\">");

      return EVAL_BODY_INCLUDE;
    }
    catch (Exception e) {
      throw new JspException(e);
    }
  }

  @Override
  public int doEndTag() throws JspException {
    try {
      if (var == null)
        pageContext.getOut().append("</a>");

      return EVAL_PAGE;
    }
    catch (Exception e) {
      throw new JspException(e);
    }
    finally {
      // Variablen zurücksetzen, die bei jedem Aufruf andere Werte haben sollen!
      url.reset();
      var = null;
      title = null;
      css = null;
    }
  }


  public void setInfo(Page info) {
    url.setInfo(info);
  }

  public void setYear(Integer year) {
    url.setYear(year);
  }

  public void setMonth(Integer month) {
    url.setMonth(month);
  }

  public void setWeek(Integer week) {
    url.setWeek(week);
  }

  public void setDate(Integer date) {
    url.setDate(date);
  }

  public void setStart(Date start) {
    url.setStart(start);
  }

  public void setEnd(Date end) {
    url.setEnd(end);
  }

  public void setTags(Set<Tag> tags) {
    url.setTags(tags);
  }

  public void setPicture(Picture picture) {
    url.setPicture(picture);
  }

  public void setPage(Integer page) {
    url.setPage(page);
  }

  public void setLimit(Integer limit) {
    url.setLimit(limit);
  }


  public void setVar(String var) {
    this.var = var;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setCss(String css) {
    this.css = css;
  }
}
