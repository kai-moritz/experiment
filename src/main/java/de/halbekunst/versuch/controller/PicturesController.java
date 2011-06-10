package de.halbekunst.versuch.controller;

import de.halbekunst.fotos.model.Picture;
import de.halbekunst.fotos.model.Tag;
import de.halbekunst.fotos.service.PictureService;
import de.halbekunst.fotos.service.TaggingService;
import de.halbekunst.utils.cachecontrol.Cacheable;
import de.halbekunst.versuch.util.PicturesPage;
import de.halbekunst.versuch.util.PicturesPage.Type;
import de.halbekunst.versuch.util.UpdateTracker;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author kai
 */
@Controller
@Transactional(readOnly=true)
public class PicturesController implements Cacheable {
  private final static Logger log = LoggerFactory.getLogger(PicturesController.class);

  @Autowired PictureService pictureService;
  @Autowired TaggingService taggingService;
  @Autowired Locale locale;
  @Autowired UpdateTracker updateTracker;

  private int maxAge;
  private String serverMaxAge;


  @Autowired
  @Qualifier("maxAge")
  public void setMaxAge(Integer seconds) {
    maxAge = seconds.intValue();
  }

  @Autowired
  @Qualifier("serverMaxAge")
  public void setServerMaxAge(Integer seconds) {
    serverMaxAge = seconds.toString();
  }


  @Override
  public boolean accepts(HttpServletRequest request) {
    return true;
  }

  @Override
  public boolean isGenerateCacheHeaders(HttpServletRequest request) {
    return true;
  }

  @Override
  public int getCacheSeconds(HttpServletRequest request) {
    return maxAge;
  }

  @Override
  public long getLastModified(HttpServletRequest request) {
    return updateTracker.getLastModified();
  }

  @Override
  public String getETag(HttpServletRequest request) {
    return updateTracker.getETag();
  }

  @Override
  public Map<String, String> getCacheControl(HttpServletRequest request) {
    Map cacheControl = new HashMap<String, String>();
    cacheControl.put("public", null); // Darf von shared und non-shared Caches gespeichert werden
    cacheControl.put("s-max-age", serverMaxAge);
    return cacheControl;
  }


  @RequestMapping("/pictures.html")
  @Transactional(readOnly = true)
  public ModelAndView handle(
      HttpServletResponse response,
      @RequestParam(required=false) Integer year,
      @RequestParam(required=false) Integer month,
      @RequestParam(required=false) Integer week,
      @RequestParam(required=false) Integer date,
      @RequestParam(required=false) Date start,
      @RequestParam(required=false) Date end,
      @RequestParam(required=false) Long[] tag,
      @RequestParam(required=false) Integer page,
      @RequestParam(required=false) Integer limit,
      @RequestParam(required=false) String md5sum
      ) throws ParseException, UnsupportedEncodingException, IOException {

    Type type = Type.UNDEFINED;

    if (year != null) {

      Calendar calendar = Calendar.getInstance(locale);
      calendar.set(Calendar.YEAR, year);
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);

      if (month != null) {
        week = null;
        calendar.set(Calendar.MONTH, month - 1);

        if (date != null) {
          type = Type.DAY;
          calendar.set(Calendar.DATE, date);
          start = calendar.getTime();
          end = null;
        }
        else {
          type = Type.MONTH;
          date = null;
          calendar.set(Calendar.DATE, 1);
          start = calendar.getTime();
          calendar.add(Calendar.MONTH, 1);
          calendar.add(Calendar.DATE, -1);
          end = calendar.getTime();
        }
      }
      else {
        month = null;
        date = null;

        if (week != null) {
          type = Type.WEEK;
          calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
          calendar.set(Calendar.WEEK_OF_YEAR, week);
          start = calendar.getTime();
          calendar.add(Calendar.WEEK_OF_YEAR, 1);
          calendar.add(Calendar.DATE, -1);
          end = calendar.getTime();
        }
        else {
          type = Type.YEAR;
          week = null;
          calendar.set(Calendar.MONTH, Calendar.JANUARY);
          calendar.set(Calendar.DATE, 1);
          start = calendar.getTime();
          calendar.set(Calendar.MONTH, Calendar.DECEMBER);
          calendar.set(Calendar.DATE, 31);
          end = calendar.getTime();
        }
      }
    }
    else {
      type = Type.UNDEFINED;
      month = null;
      week = null;
      date = null;
    }

    Set<Tag> tags = new HashSet<Tag>();
    if (tag != null) {
      for (Long id : tag)
        tags.add(taggingService.get(id));
    }

    PicturesPage pageinfo = new PicturesPage(type, year, month, week, date, start, end, tags, page, limit, locale);
    ModelAndView mav;

    if (md5sum != null) {
      Picture picture = pictureService.get(md5sum);
      if (picture == null) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return null;
      }
      pageinfo.setPicture(picture);
      mav = new ModelAndView("TILES/picture");
    }
    else {
      mav = new ModelAndView("TILES/pictures");
    }

    pageinfo.size = pictureService.size(pageinfo);
    if (pageinfo.size > 0)
      pageinfo.pictures = pictureService.search(pageinfo);

    mav.addObject("page", pageinfo);
    return mav;
  }
}
