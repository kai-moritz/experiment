package de.halbekunst.experiment.controller;

import de.halbekunst.experiment.dao.SelectionDao;
import de.halbekunst.experiment.model.Selection;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author kai
 */
@Controller
public class ExperimentController {
  @Autowired SelectionDao selectionDao;


  @RequestMapping("/right.html")
  @Transactional
  public ModelAndView right(HttpServletResponse response, @RequestParam(required=false) Long clone, @RequestParam(required=false) Long show) {
    ModelAndView mav = new ModelAndView("experiment");

    if (clone != null) {
      Selection selection = selectionDao.get(clone);
      Selection saved = new Selection();
      saved.setName("Copy of " + selection.toString());
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

  @RequestMapping("/wrong.html")
  @Transactional
  ModelAndView wrong(HttpServletResponse response, @RequestParam(required=false) Long clone, @RequestParam(required=false) Long show) {
    ModelAndView mav = new ModelAndView("experiment");

    if (clone != null) {
      Selection selection = selectionDao.get(clone);
      Selection saved = new Selection();
      saved.setName("Copy of " + selection.toString());
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
