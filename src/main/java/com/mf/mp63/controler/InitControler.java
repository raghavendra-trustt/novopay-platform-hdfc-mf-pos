package com.mf.mp63.controler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class InitControler {
	public String loadHome()
	{
	  return "index.html";
	}

	@RequestMapping("/main_page")
	public ModelAndView loadHomePage()
	{
		return new ModelAndView("main_page.html");

	}
}
