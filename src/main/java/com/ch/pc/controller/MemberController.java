package com.ch.pc.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ch.pc.model.Member1;
import com.ch.pc.model.Pc;
import com.ch.pc.service.MemberService;
import com.ch.pc.service.PcService;


@Controller
public class MemberController {
	
	@Autowired 
	private MemberService ms;
	
	@Autowired
	private PcService ps;
	
	@RequestMapping("/member/loginForm.do")
	public String loginForm() {
		return "/member/loginForm";
	}
	
	@RequestMapping("/member/login.do")
	public String login(Member1 member1, HttpSession session, Model model) {
		Member1 member2 = ms.select(member1.getId());
		int result = 0; // 암호가 일치하지 않는 경우
		if (member2 == null || member2.getDel().equals("y")) {
			result = -1; // 없거나 관리자가 삭제한 아이디
		}
		else if (member2.getBlacklist().equals("y   ")) {
			result = -2; // blacklist에 추가된 아이디
		}
		else if (member2.getPassword().equals(member1.getPassword())) {
			result = 1;
			//pc방이 승인이 됐는지 안됐는지 확인을 위해
			Pc pc = ps.selectMno(member2.getMno());
			if(pc == null) {
				member2.setPermitConfirm(-1); // 가맹신청을 아직 안했으면 -1
			}else if(pc.getPermit().equals("y")) {	
				member2.setPermitConfirm(1);	// 가맹승인이 되면 1
			}else if(pc.getPermit().equals("n")) {	
				member2.setPermitConfirm(0);	// 가맹승인이 아직이면 0
			}	
			
			session.setAttribute("memberSession", member2);// header출력용		
			System.out.println(member2);
		}
		model.addAttribute("result", result);
		return "/member/login";
	}

	@RequestMapping("/fragments/header.do")
	public String header() {
		return "/fragments/header2";
	}

	@RequestMapping("/fragments/footer.do")
	public String footer() {
		return "/fragments/footer";
	}

	@RequestMapping("/member/joinForm.do")
	public String joinForm() {
		return "/member/joinForm";
	}
	
	@RequestMapping("/member/join.do")
	public String join(Member1 member1, Model model, HttpSession session) throws IOException {
		int result = 0;
		// form에서 입력한 member1데이터를 가져와서 member2 객체에 대입하여 아이디가 존재하는지 확인
		Member1 member2 = ms.select(member1.getId());
		if (member2 == null) {
			String fileName = member1.getFile().getOriginalFilename();
			String real = "src/main/resources/static/images";
			FileOutputStream fos=new FileOutputStream(new File(real+"/"+fileName));
			fos.write(member1.getFile().getBytes());
			fos.close();
			member1.setProfile(fileName);
			result = ms.insert(member1);
		} else { 
			result = -1; // 아이디 중복
		}
		model.addAttribute("result", result);
		return "/member/join";
	}
	
	@RequestMapping(value = "/member/confirmId.do", produces = "text/html;charset=utf-8")
	@ResponseBody
	public String confirmId(String id) {
		String msg = "";
		Member1 member1 = ms.select(id); 
		if (member1 == null) msg = "1";
		else msg = "0";;
		return msg;
	}
	
	@RequestMapping(value = "/member/confirmNick_name.do", produces = "text/html;charset=utf-8") 
	@ResponseBody
	public String confirmNick_name(String nickName) {
		String msg = "";
		Member1 member1 = ms.confirmNick_name(nickName); 
		if (member1 == null) msg = "1";
		else msg = "0";
		return msg;
	}
	
	@RequestMapping(value = "/member/confirmEmail.do", produces = "text/html;charset=utf-8") 
	@ResponseBody
	public String confirmEmail(String email) {
		String msg = "";
		Member1 member1 = ms.confirmEmail(email); 
		if (member1 == null) msg = "1";
		else msg = "0";
		return msg;
	}
	
	@RequestMapping("/member/logout.do")
	public String logout(HttpSession session) {
		session.invalidate();
		return "/member/logout";
	}
	

}