package de.halbekunst.versuch.controller;

import de.halbekunst.versuch.dao.SelectionDao;
import de.halbekunst.versuch.model.SavedSelection;
import de.halbekunst.versuch.model.Selection;
import java.io.IOException;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Transactional
public class ExperimentController {
  private final static Logger log = LoggerFactory.getLogger(ExperimentController.class);

  @Resource SelectionDao dao;




  @RequestMapping("/experiment.html")
  @Transactional
  public ModelAndView handle(HttpServletResponse response, @RequestParam(required=false) Long clone, @RequestParam(required=false) Long show) throws IOException {
    ModelAndView mav = new ModelAndView("experiment");

    if (clone != null) {
      Selection selection = dao.get(clone);
      SavedSelection saved = new SavedSelection();
      saved.setName("Kopie von " + selection.toString());
      saved.setPictures(new ArrayList(selection.getPictures()));
      dao.save(saved);
      mav.addObject("selection", saved);
    }

    if (show != null) {
      mav.addObject("selection", dao.get(show));
    }

    mav.addObject("selections", dao.list());
    return mav;
  }
}
