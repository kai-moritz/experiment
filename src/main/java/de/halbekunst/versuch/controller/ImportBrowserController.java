package de.halbekunst.versuch.controller;

import de.halbekunst.fotos.dao.SelectionDao;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
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
@Transactional(readOnly=true)
public class ImportBrowserController {
  private final static Logger log = LoggerFactory.getLogger(ImportBrowserController.class);

  @Autowired SelectionDao selectionDao;


  @RequestMapping("/imports.html")
  public ModelAndView handle(@RequestParam(required=false) Long id) throws ParseException, UnsupportedEncodingException {
    ModelAndView mav;

    if (id == null) {
      mav = new ModelAndView("TILES/imports");
      mav.addObject("imports", selectionDao.listImports());
      return mav;
    }

    mav = new ModelAndView("TILES/import");
    mav.addObject("import", selectionDao.get(id));
    return mav;
  }
}
