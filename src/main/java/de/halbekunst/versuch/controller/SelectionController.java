package de.halbekunst.versuch.controller;

import de.halbekunst.versuch.dao.SelectionDao;
import de.halbekunst.versuch.model.SavedSelection;
import de.halbekunst.versuch.model.Selection;
import javax.servlet.http.HttpServletResponse;
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

  public final String COOKIE_PATH = "/selection/";
  public final String COOKIE_NAME = "selection";
  public final int COOKIE_MAX_AGE = Integer.MAX_VALUE;
  public final String VIEW = "selection";

  @Autowired SelectionDao selectionDao;


  @RequestMapping("/selection/add.html")
  @Transactional
  public ModelAndView addPicture(HttpServletResponse response, @RequestParam Long id) {
    ModelAndView mav = new ModelAndView(VIEW);
    if (id != null) {
      Selection selection = selectionDao.get(id);
      SavedSelection saved = new SavedSelection();
      saved.setName("Kopie von " + selection.toString());
      selectionDao.save(saved);
      saved.getPictures().addAll(selection.getPictures());
      mav.addObject("selection", saved);
    }
    return mav;
  }
}
