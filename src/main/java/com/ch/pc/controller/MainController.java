package com.ch.pc.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ch.pc.model.Bookmark;
import com.ch.pc.model.Keyword;
import com.ch.pc.model.Location;
import com.ch.pc.model.Member1;
import com.ch.pc.model.Pc;
import com.ch.pc.model.Pcimage;
import com.ch.pc.service.BookmarkService;
import com.ch.pc.service.PcService;
import com.ch.pc.service.ReviewService;

@Controller
public class MainController {
	
	@Autowired
	private ReviewService rs;
	
	@Autowired
	private PcService ps;
	
	@Autowired
	private BookmarkService bs;
	
	@RequestMapping("/main/main.do")
	public String main(Location location, Model model, Keyword keyword) {

		if (location.getSido() == null || location.getSido().equals("")) {
			location.setSido("경기도");
			location.setSigugun("김포시");
			location.setDong("풍무동");
		}

		List<Pc> listsearch = ps.searchpc(keyword);

		model.addAttribute("listsearch", listsearch);
		model.addAttribute("location", location);
		return "/main/main";
	}
	
	@RequestMapping("/main/pcDetailForm.do")
	public String pcDetailForm(int pcno, Model model, HttpSession session) {
		Pc pc = ps.select(pcno);
		List<Pcimage> list = ps.listPhoto(pcno);
		double avgRating = rs.avgRating(pcno);
		
		//이 아이디가 북마크 했는지 안했는지 구분
		String imgSrc = "";
		Member1 memberSession = (Member1) session.getAttribute("memberSession");
		if (memberSession != null) {
			int mno = memberSession.getMno(); 
			Bookmark bookmark = new Bookmark();
			bookmark.setMno(mno);
			bookmark.setPcno(pcno);
			int bookmarkOnOff = bs.select(bookmark);
			if (bookmarkOnOff > 0) { // 북마크 한 pc방이면이면
				imgSrc = "/images/bookmark_on.png";

			} else if (bookmarkOnOff == 0) { // 북마크 한 pc방이 아니면
				imgSrc = "/images/bookmark_off.png";
			}
		}
		model.addAttribute("avgRating", avgRating);
		model.addAttribute("imgSrc", imgSrc);
		model.addAttribute("pc", pc);
		model.addAttribute("list", list);
		return "/main/pcDetailForm";
	}
	@RequestMapping("/main/sessionChk.do")
	public String sessionChk() {
		return "/main/sessionChk";
	}

}