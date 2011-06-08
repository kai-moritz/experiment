package de.halbekunst.versuch.controller;

import de.halbekunst.versuch.dao.SelectionDao;
import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
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
  @Transactional(readOnly = true)
  public ModelAndView handle(HttpServletResponse response) throws IOException {
    ModelAndView mav = new ModelAndView("experiment");

    mav.addObject("selections", dao.list());
    return mav;
  }
}