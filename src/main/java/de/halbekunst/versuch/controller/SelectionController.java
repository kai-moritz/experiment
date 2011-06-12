package de.halbekunst.versuch.controller;

import de.halbekunst.versuch.dao.PictureDao;
import de.halbekunst.versuch.dao.SelectionDao;
import de.halbekunst.versuch.model.SavedSelection;
import java.util.Date;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SelectionController {
  private final static Logger log = LoggerFactory.getLogger(SelectionController.class);

  public final String COOKIE_PATH = "/selection/";
  public final String COOKIE_NAME = "selection";
  public final int COOKIE_MAX_AGE = Integer.MAX_VALUE;
  public final String VIEW = "selection";

  @Autowired PictureDao pictureDao;
  @Autowired SelectionDao selectionDao;


  @Transactional(readOnly=true)
  @RequestMapping("/selection/show.html")
  ModelAndView show(HttpServletRequest request, HttpServletResponse response) {
    Cookie[] cookies = request.getCookies();
    SavedSelection selection = null;
    if (cookies != null) {
      for (Cookie received : cookies) {
        if (COOKIE_NAME.equals(received.getName())) {
          received.setPath(COOKIE_PATH);
          String value = received.getValue();
          try {
            selection = (SavedSelection) selectionDao.get(Long.parseLong(value));
          }
          catch (NullPointerException e) {
            log.info("Die gemerkte Auswahl {} existiert nicht (mehr)!", value);
          }
          break;
        }
      }
    }

    ModelAndView mav = new ModelAndView(VIEW);
    mav.addObject("selection", selection);
    return mav;
  }

  @Transactional(readOnly=false)
  @RequestMapping("/selection/add.html")
  ModelAndView addPicture(HttpServletRequest request, HttpServletResponse response, @RequestParam Long id) {
    Cookie[] cookies = request.getCookies();
    SavedSelection selection = null;
    Cookie cookie = new Cookie(COOKIE_NAME, "");
    cookie.setPath(COOKIE_PATH);
    cookie.setMaxAge(COOKIE_MAX_AGE);
    if (cookies != null) {
      for (Cookie received : cookies) {
        if (COOKIE_NAME.equals(received.getName())) {
          received.setPath(COOKIE_PATH);
          String value = received.getValue();
          cookie = received;
          try {
            selection = (SavedSelection) selectionDao.get(Long.parseLong(value));
          }
          catch (NullPointerException e) {
            log.info("Die gemerkte Auswahl {} existiert nicht (mehr)!", value);
          }
          break;
        }
      }
    }

    if (selection == null) {
      selection = new SavedSelection();
      selection.setName((new Date()).toString());
      selectionDao.save(selection);
      cookie.setValue(selection.getId().toString());
    }

    selection.getPictures().add(pictureDao.get(id));
    response.addCookie(cookie);

    ModelAndView mav = new ModelAndView(VIEW);
    mav.addObject("selection", selection);
    return mav;
  }
}
