 package de.halbekunst.versuch.controller;

import de.halbekunst.fotos.dao.TagDao;
import de.halbekunst.fotos.service.TaggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author kai
 */
@Controller
@Transactional(readOnly=true)
public class TagBrowserController {

  @Autowired TagDao tagDao;
  @Autowired TaggingService taggingService;


  @RequestMapping("/tags.html")
  public ModelAndView handle() {
    ModelAndView mav = new ModelAndView("TILES/tags");
    mav.addObject("tags", tagDao.get());
    return mav;
  }
}
