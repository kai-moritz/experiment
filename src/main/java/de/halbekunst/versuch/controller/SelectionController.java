package de.halbekunst.versuch.controller;

import de.halbekunst.versuch.dao.PictureDao;
import de.halbekunst.versuch.dao.SelectionDao;
import de.halbekunst.versuch.model.Picture;
import de.halbekunst.versuch.model.SavedSelection;
import de.halbekunst.versuch.model.Selection;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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


  @Transactional(readOnly=false)
  @RequestMapping("/selection/clone.html")
  ModelAndView clone(HttpServletRequest request, HttpServletResponse response, @RequestParam Long id) {
    ModelAndView mav = new ModelAndView(VIEW);
    CookieBackedSelection selection = new CookieBackedSelection(selectionDao.get(id));
    response.addCookie(selection.toCookie());
    mav.addObject("selection", selection);
    return mav;
  }

  @Transactional(readOnly=true)
  @RequestMapping("/selection/show.html")
  ModelAndView show(HttpServletRequest request, HttpServletResponse response) {
    ModelAndView mav = new ModelAndView(VIEW);
    mav.addObject("selection", new CookieBackedSelection(request.getCookies()));
    return mav;
  }

  @Transactional(readOnly=false)
  @RequestMapping("/selection/sort.html")
  ModelAndView sort(HttpServletRequest request, HttpServletResponse response) {
    CookieBackedSelection selection = new CookieBackedSelection(request.getCookies());
    Collections.sort(selection.getPictures());
    response.addCookie(selection.toCookie());
    ModelAndView mav = new ModelAndView(VIEW);
    mav.addObject("selection", new CookieBackedSelection(request.getCookies()));
    return mav;
  }

  @Transactional(readOnly=false)
  @RequestMapping("/selection/add.html")
  ModelAndView addPicture(HttpServletRequest request, HttpServletResponse response, @RequestParam Long id) {
    CookieBackedSelection selection = new CookieBackedSelection(request.getCookies());
    selection.addPicture(id);
    response.addCookie(selection.toCookie());
    ModelAndView mav = new ModelAndView(VIEW);
    mav.addObject("selection", selection);
    return mav;
  }

  @Transactional(readOnly=false)
  @RequestMapping("/selection/remove.html")
  ModelAndView removePicture(HttpServletRequest request, HttpServletResponse response, @RequestParam Long id) {
    CookieBackedSelection selection = new CookieBackedSelection(request.getCookies());
    selection.removePicture(id);
    response.addCookie(selection.toCookie());
    ModelAndView mav = new ModelAndView(VIEW);
    mav.addObject("selection", selection);
    return mav;
  }

  @Transactional(readOnly=false)
  @RequestMapping("/selection/move/up.html")
  ModelAndView moveUp(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=true) Long id) {
    CookieBackedSelection selection = new CookieBackedSelection(request.getCookies());
    List<Picture> pictures = selection.getPictures();
    Picture picture = pictureDao.get(id);
    int pos = pictures.indexOf(picture);
    if (pos+1 < pictures.size()) {
      pictures.remove(pos);
      pictures.add(pos+1, picture);
    }
    response.addCookie(selection.toCookie());
    ModelAndView mav = new ModelAndView(VIEW);
    mav.addObject("selection", selection);
    return mav;
  }

  @Transactional(readOnly=false)
  @RequestMapping("/selection/move/down.html")
  ModelAndView moveDown(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=true) Long id) {
    CookieBackedSelection selection = new CookieBackedSelection(request.getCookies());
    List<Picture> pictures = selection.getPictures();
    Picture picture = pictureDao.get(id);
    int pos = pictures.indexOf(picture);
    if (pos > 0) {
      pictures.remove(pos);
      pictures.add(pos-1, picture);
    }
    response.addCookie(selection.toCookie());
    ModelAndView mav = new ModelAndView(VIEW);
    mav.addObject("selection", selection);
    return mav;
  }


  class CookieBackedSelection implements Selection, Serializable {
    private Long id = null;
    private String name;
    private List<Picture> pictures;
    private SavedSelection selection;
    private Cookie cookie;


    CookieBackedSelection(Selection original) {
      cookie = new Cookie(COOKIE_NAME,"");
      cookie.setPath(COOKIE_PATH);
      cookie.setMaxAge(COOKIE_MAX_AGE);
      name = "Kopie von " + original.toString();
      if (true) {
        selection = new SavedSelection();
        selection.setName(name);
        selection.getPictures().addAll(original.getPictures());
        pictures = selection.getPictures();
      }
      else {
        pictures = new ArrayList<Picture>(original.getPictures());
      }
    }

    CookieBackedSelection(Cookie[] cookies) {
      try {
        if (cookies != null) {
          for (Cookie received : cookies) {
            if (COOKIE_NAME.equals(received.getName())) {
              cookie = received;
              cookie.setPath(COOKIE_PATH);
              String value = cookie.getValue();
              if (false) {
                name = "Gemerkte Bilder";
                pictures = new LinkedList<Picture>();
                int start = 1;
                int end = 0;
                do {
                  do end++; while (end < value.length() && value.charAt(end) != '-');
                  if (start == end)
                    return;
                  addPicture(Long.parseLong(value.substring(start, end)));
                  start = end + 1;
                }
                while (true);
              }
              else {
                try {
                  selection = (SavedSelection) selectionDao.get(Long.parseLong(value));
                  name = selection.getName();
                  pictures = selection.getPictures();
                  return;
                }
                catch (NullPointerException e) {
                  log.info("Die gemerkte Auswahl {} existiert nicht (mehr)!", value);
                  break;
                }
              }
            }
          }
        }
      }
      catch (NumberFormatException e) {
        log.info("Ignoriere invaliden Cookie \"{}\": {}", cookie.getValue(), e);
      }


      if (true) {
        name = "Unbenannt";
        selection = new SavedSelection();
        selection.setName(name);
        pictures = selection.getPictures();
      }
      else {
        name = "Gemerkte Bilder";
        pictures = new LinkedList<Picture>();
      }

      cookie = new Cookie(COOKIE_NAME,"");
      cookie.setPath(COOKIE_PATH);
      cookie.setMaxAge(COOKIE_MAX_AGE);
    }


    @Override
    public Long getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    @Override
    public List<Picture> getPictures() {
      return pictures;
    }

    public final void addPicture(Long id) {
      Picture picture = pictureDao.get(id);
      if (picture == null)
        return;
      if (pictures.contains(picture))
        return;
      pictures.add(picture);
    }

    public void removePicture(Long id) {
      pictures.remove(pictureDao.get(id));
    }

    public Cookie toCookie() {
      StringBuilder builder = new StringBuilder();
      if (selection == null) {
        Iterator<Picture> iterator = pictures.iterator();
        while (iterator.hasNext()) {
          builder.append('-');
          builder.append(iterator.next().getId());
        }
      }
      else {
        selection.setName(name);
        if (selection.getId() == null)
          selectionDao.save(selection);
        builder.append(selection.getId());
      }
      cookie.setValue(builder.toString());
      return cookie;
    }


    @Override
    public String toString() {
      return name;
    }
  }
}
