package com.ch.pc.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ch.pc.model.Bookmark;
import com.ch.pc.model.Fee;
import com.ch.pc.model.Member1;
import com.ch.pc.model.Pc;
import com.ch.pc.model.Reservation;
import com.ch.pc.service.BookmarkService;
import com.ch.pc.service.MemberService;
import com.ch.pc.service.PageBean;
import com.ch.pc.service.PcService;
import com.ch.pc.service.ReservationService;


@Controller
public class MemberController {
	
	@Autowired 
	private MemberService ms;
	
	@Autowired
	private PcService ps;
	
	@Autowired
	private BookmarkService bs;
	
	@Autowired
	private ReservationService rs;
	
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
		return "/fragments/header";
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
	
	@RequestMapping("/member/passChkForm.do")
	public String passChkForm(Model model, HttpSession session) {
		 Member1 memberSession = (Member1)session.getAttribute("memberSession");      
		 Pc pc = ps.selectMno(memberSession.getMno());
	     if(pc != null) {
		     int pcno = pc.getPcno();
		     Fee f1 = ps.selectFee(pcno);
	    	 model.addAttribute("pc",pc);
	    	 model.addAttribute("f1", f1);
	     }	  	     
	     model.addAttribute("memberSession", memberSession);
		return "/member/passChkForm";
	}
	
	@RequestMapping("/member/passChk.do")
	public String passChk(Member1 member1, HttpSession session, Model model) {
		Member1 member2 = (Member1)session.getAttribute("memberSession");
		int result = 0;
		if (member2.getPassword().equals(member1.getPassword())) {
			result = 1;
		}
		model.addAttribute("result", result);
		return "/member/passChk";
	}
	
	@RequestMapping("/member/updateForm.do")
	public String updateForm(Model model, HttpSession session) {
	      Member1 member = (Member1)session.getAttribute("memberSession");
	      Pc pc = ps.selectMno(member.getMno());
		     if(pc != null) {
			     int pcno = pc.getPcno();
			     Fee f1 = ps.selectFee(pcno);
		    	 model.addAttribute("pc",pc);
		    	 model.addAttribute("f1", f1);
		     }	  	     
	      model.addAttribute("member", member);
	      return "/member/updateForm";
	   }
	@RequestMapping(value = "/member/confirmNick_name2.do", produces = "text/html;charset=utf-8") 
	@ResponseBody
	public String confirmNick_name2(String nickName, HttpSession session) {
		Member1 member = (Member1)session.getAttribute("memberSession");
		String nick_name1 = member.getNickName();
		String msg = "";
		Member1 member1 = ms.confirmNick_name(nickName); 
		if (member1 == null) msg = "1";
		else if (member1.getNickName().equals(nick_name1)) msg = "1";
		else msg = "0";
		return msg;
	}
	@RequestMapping(value = "/member/confirmEmail2.do", produces = "text/html;charset=utf-8") 
	@ResponseBody
	public String confirmEmail2(String email, HttpSession session) {
		Member1 member = (Member1)session.getAttribute("memberSession");
		String email1 = member.getEmail();
		String msg = "";
		Member1 member1 = ms.confirmEmail(email); 
		if (member1 == null) msg = "1";
		else if (member1.getEmail().equals(email1)) msg = "1";
		else msg = "0";
		return msg;
	}
	@RequestMapping("/member/update.do")
	public String update(Member1 member1, Model model, HttpSession session) throws IOException {	
		int result = 0;
		// form에서 입력한 member1데이터를 가져와서 member2 객체에 대입하여 아이디가 존재하는지 확인
		System.out.println(member1);
		System.out.println(member1.getId());
		
		Member1 member2 = ms.select(member1.getId());
		if (member2 == null) {
			result = -1;	// 회원정보 없음
		} else {
			String fileName = member1.getFile().getOriginalFilename();
			if(fileName != null && !fileName.equals("")) { // 값이 새로 들어오면
				member1.setProfile(fileName);
				String real = "src/main/resources/static/images";
				FileOutputStream fos = new FileOutputStream(new File(real+"/"+fileName));
				fos.write(member1.getFile().getBytes());
				fos.close();
			}
			result = ms.update(member1);
			Member1 member3 = ms.select(member1.getId());
			
			Pc pc = ps.selectMno(member3.getMno());
			if(pc == null) {
				member3.setPermitConfirm(-1); // 가맹신청을 아직 안했으면 -1
			}else if(pc.getPermit().equals("y")) {	
				member3.setPermitConfirm(1);	// 가맹승인이 되면 1
			}else if(pc.getPermit().equals("n")) {	
				member3.setPermitConfirm(0);	// 가맹승인이 아직이면 0
			}	
			
			session.setAttribute("memberSession", member3);// header출력용
		}
		model.addAttribute("result", result);
		return "member/update";
	}
	
	@RequestMapping("/member/mybookmark.do")
	public String mybookmark(Bookmark bookmark, Model model, HttpSession session, String pageNum) {
		Member1 memberSession = (Member1)session.getAttribute("memberSession");
		 Pc pc = ps.selectMno(memberSession.getMno());
	     if(pc != null) {
		     int pcno = pc.getPcno();
		     Fee f1 = ps.selectFee(pcno);
	    	 model.addAttribute("pc",pc);
	    	 model.addAttribute("f1", f1);
	     }	  	     
		int mno = memberSession.getMno();
		int  rowPerPage = 4;
		if (pageNum == null || pageNum.equals("")) pageNum="1";
		int currentPage = Integer.parseInt(pageNum); 
		int startRow = (currentPage - 1) * rowPerPage + 1;
		int endRow = startRow + rowPerPage - 1;	
		int total = bs.getTotalMybookmark(mno);
		bookmark.setMno(mno);
		bookmark.setStartRow(startRow);
		bookmark.setEndRow(endRow);
		List<Bookmark> list = bs.mybookmarkList(bookmark);
		System.out.println(list);
		PageBean pb = new PageBean(currentPage, rowPerPage, total);
		
		model.addAttribute("pb", pb);
		model.addAttribute("list", list);
		model.addAttribute("total", total);
		return "/member/mybookmark";
	}
	
	@RequestMapping("/member/reserveList.do")
	public String reserveList(Reservation reservation, HttpSession session, Model model, String pageNum) {
		Member1 memberSession = (Member1)session.getAttribute("memberSession");
		 Pc pc = ps.selectMno(memberSession.getMno());
	     if(pc != null) {
		     int pcno = pc.getPcno();
		     Fee f1 = ps.selectFee(pcno);
	    	 model.addAttribute("pc",pc);
	    	 model.addAttribute("f1", f1);
	     }	  	     
		int mno = memberSession.getMno();
		List<Reservation> nList = rs.nList(mno);
		List<Reservation> yList = rs.yList(mno);
		model.addAttribute("mno", mno);
		model.addAttribute("nList", nList);
		model.addAttribute("yList", yList);
		model.addAttribute("pageNum", pageNum);
		
		return "/member/reserveList";
	}
	
	@RequestMapping("/member/findIdForm.do")
	public String findIdForm() {
		return "/member/findIdForm";
	}
	@RequestMapping("/member/findId.do")
	public String findIdResult(Member1 member1, Model model) {
		int result = 0;
		Member1 member2 = ms.findId(member1);
		if (member2 != null) {
			result = 1;
			model.addAttribute("member", member2);
		} else {
			result = -1;
		}
		model.addAttribute("result", result);
		return "member/findId";
	}
	@RequestMapping("/member/findPwForm.do")
	public String findPasswordForm() {
		return "/member/findPwForm";
	}
	// 비밀번호 찾기
	@RequestMapping("/member/findPw.do")
	public String findPw(Member1 member1, Model model) {
		int result = 0;
		Member1 member2 = ms.findPw(member1);
		if (member2 != null) {
			result = 1;
			model.addAttribute("member", member2);
		} else {
			result = -1;
		}
		model.addAttribute("result", result);
		return "/member/findPw";
	}
	

}