package de.halbekunst.versuch.controller;

import de.halbekunst.versuch.dao.SelectionDao;
import de.halbekunst.versuch.model.SavedSelection;
import de.halbekunst.versuch.model.Selection;
import java.io.IOException;
import java.util.ArrayList;
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
public class ExperimentController {
  private final static Logger log = LoggerFactory.getLogger(ExperimentController.class);

  @Autowired SelectionDao selectionDao;




  @RequestMapping("/experiment.html")
  @Transactional
  public ModelAndView handle(HttpServletResponse response, @RequestParam(required=false) Long clone, @RequestParam(required=false) Long show) throws IOException {
    ModelAndView mav = new ModelAndView("experiment");

    if (clone != null) {
      Selection selection = selectionDao.get(clone);
      SavedSelection saved = new SavedSelection();
      saved.setName("Kopie von " + selection.toString());
      selectionDao.save(saved);
      saved.getPictures().addAll(selection.getPictures());
      mav.addObject("selection", saved);
    }

    if (show != null) {
      mav.addObject("selection", selectionDao.get(show));
    }

    mav.addObject("selections", selectionDao.list());
    return mav;
  }
}
