package com.ch.pc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ch.pc.model.Member1;
import com.ch.pc.model.Pc;
import com.ch.pc.service.MemberService;
import com.ch.pc.service.PageBean;
import com.ch.pc.service.PcService;
@Controller
public class AdminController {
	@Autowired
	private PcService ps;
	@Autowired
	private MemberService ms;
	
	@RequestMapping("/admin/pcList.do")
	public String pcList(Pc pc, Model model, String pageNum) {
		int  rowPerPage = 10;
		if (pageNum == null || pageNum.equals("")) pageNum="1";
		int currentPage = Integer.parseInt(pageNum);
		int total = ps.getTotal(pc);
		int startRow = (currentPage - 1) * rowPerPage + 1;
		int endRow = startRow + rowPerPage - 1;
		pc.setStartRow(startRow);
		pc.setEndRow(endRow);
		List<Pc> pcList = ps.list(pc);
		PageBean pb = new PageBean(currentPage, rowPerPage, total);
		String[] title = {"pc방 이름","위치","정보"};
		String[] Etitle = {"pcname","pcaddr","pcinfo"};
		
		model.addAttribute("total", total);
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("pcList", pcList);
		model.addAttribute("pb", pb);
		model.addAttribute("title", title);
		model.addAttribute("Etitle", Etitle);
		
		return "/admin/pcList";
	}
	@RequestMapping("/admin/pcPermit.do")
	public String pcPermit(Pc pc, Model model, String pageNum) {
		int result = 0;
		result = ps.permit(pc.getPcno());
		model.addAttribute("result", result);
		model.addAttribute("pageNum", pageNum);
		return "/admin/pcPermit";
	}
	@RequestMapping("/admin/memberList.do")
	public String memberList(Member1 member1, String pageNum, Model model) {
		int  rowPerPage = 10;
		if (pageNum == null || pageNum.equals("")) pageNum="1";
		int currentPage = Integer.parseInt(pageNum);
		int total = ms.getTotal(member1);
		int startRow = (currentPage - 1) * rowPerPage + 1;
		int endRow = startRow + rowPerPage - 1;
		member1.setStartRow(startRow);
		member1.setEndRow(endRow);
		PageBean pb = new PageBean(currentPage, rowPerPage, total);
		List<Member1> memberList = ms.list(member1);
		String[] title = {"구별","아이디","이름","닉네임"};
		String[] Etitle = {"identity","id","name","nick_name"};
		
		System.out.println(pb);
		
		model.addAttribute("total", total);
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("memberList", memberList);
		model.addAttribute("pb", pb);
		model.addAttribute("title", title);
		model.addAttribute("Etitle", Etitle);
		
		return "/admin/memberList";
	}
	
	@RequestMapping("/admin/blackin.do")
	public String blackin(Member1 member1, String pageNum, Model model) {
		int result = 0;
		result = ms.blackin(member1.getMno());
		model.addAttribute("result", result);
		model.addAttribute("pageNum", pageNum);
		return "/admin/blackin";
	}
	@RequestMapping("/admin/blackout.do")
	public String blackout(Member1 member1, String pageNum, Model model) {
		int result = 0;
		result = ms.blackout(member1.getMno());
		model.addAttribute("result", result);
		model.addAttribute("pageNum", pageNum);
		return "/admin/blackout";
	}
	@RequestMapping("/admin/memberDelete.do")
	public String memberDelete(Member1 member1, String pageNum, Model model) {
		int result = 0;
		result = ms.memberDelete(member1.getMno());
		model.addAttribute("result", result);
		model.addAttribute("pageNum", pageNum);
		return "/admin/memberDelete";
	}
	
}
