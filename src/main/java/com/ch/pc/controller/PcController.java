package com.ch.pc.controller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ch.pc.model.Member1;
import com.ch.pc.model.Pc;
import com.ch.pc.model.Pcimage;
import com.ch.pc.service.PcService;
@Controller
public class PcController {
	@Autowired
	private PcService ps;
	
	
	@RequestMapping("/pc/registerForm.do")
	public String registerForm() {
		return "/pc/registerForm";
	}
	
	@RequestMapping("/pc/register.do")
	public String register(Pc pc, Model model, HttpSession session, MultipartHttpServletRequest mr)throws IOException {
		int result;
		Member1 memberSession = (Member1)session.getAttribute("memberSession");
		
		pc.setPcno(ps.givePcno()); // 일련번호 부여
		List<Pc> pcbnm = ps.selectPcbnm(pc.getPcbusinessnum()); // 중복된 사업자번호 등록 방지
		List<Pc> pcpno = ps.selectPcpno(pc.getPcpno()); // 중복된 전화번호 등록 방지
		String real = "src/main/resources/static/images";
		if (pcbnm.isEmpty() && pcpno.isEmpty() ) {			
			List<MultipartFile> list = mr.getFiles("pcimage");
			List<Pcimage> images = new ArrayList<Pcimage>();
			for (MultipartFile mf : list) {
				Pcimage pi = new Pcimage();
				String fileName = mf.getOriginalFilename();
				pi.setPcno(pc.getPcno()); 
				pi.setImagename(fileName);
				images.add(pi);
				FileOutputStream fos = new FileOutputStream(new File(real+"/"+fileName));
				fos.write(mf.getBytes()); 
				fos.close();
				pc.setImagename(fileName);
			}
			pc.setMno(memberSession.getMno());
			result = ps.insertPc(pc);
			ps.insertPcimage(images);
			// 가맹점 문의 후 바로 헤더에서 승인이 안된걸 확인하기 위해
			pc = ps.selectMno(memberSession.getMno());
			if(pc == null) {
				memberSession.setPermitConfirm(-1);
			}else if(pc.getPermit().equals("y")) {
				memberSession.setPermitConfirm(1);
			}else if(pc.getPermit().equals("n")) {
				memberSession.setPermitConfirm(0);
			}
			session.setAttribute("memberSession", memberSession);
			
		} else result = 0;

		model.addAttribute("result", result);

		return "/pc/register";
	}
	
	
}