package server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import server.exception.CommonException;

import javax.servlet.http.HttpServletRequest;



@Controller
public class AppController {

		@RequestMapping(value = "/login.form", method = RequestMethod.GET)
		public String homeForm(@RequestHeader(value = "referer",defaultValue = "") String backUrl, ModelMap model, HttpServletRequest request) throws CommonException {

			return "login";

		}
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String voidForm(@RequestHeader(value = "referer",defaultValue = "") String backUrl, ModelMap model, HttpServletRequest request) throws CommonException {

		return "login";

	}

	
	


}