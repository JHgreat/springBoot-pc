package com.ch.pc.service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
public class SessionChk implements HandlerInterceptor {
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession();
		if (session == null || session.getAttribute("memberSession") == null ||
				session.getAttribute("memberSession").equals("")) {
			response.sendRedirect("/main/sessionChk.do");
			return false;
		}
		return true;
	}
}