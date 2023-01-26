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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ch.pc.model.Bookmark;
import com.ch.pc.model.Fee;
import com.ch.pc.model.Member1;
import com.ch.pc.model.Pc;
import com.ch.pc.model.Pcimage;
import com.ch.pc.model.Seat;
import com.ch.pc.service.BookmarkService;
import com.ch.pc.service.PcService;
@Controller
public class PcController {
	@Autowired
	private PcService ps;
	
	@Autowired
	private BookmarkService bs;
	
	
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
	
	@RequestMapping(value = "/pc/bookmark.do", produces = "text/html;charset=utf-8")
	@ResponseBody
	public String bookmark(int pcno, Model model, HttpSession session) {
		
		Member1 memberSession = (Member1) session.getAttribute("memberSession");
		int mno = memberSession.getMno();
		Bookmark bookmark = new Bookmark();
		bookmark.setMno(mno);
		bookmark.setPcno(pcno);
		int result = bs.select(bookmark);
		
		String imgSrc = "";
		if (result > 0) { // 북마크한 약품이면
			bs.delete(bookmark); // bookmark 테이블에서 데이터 삭제
			imgSrc = "/images/bookmark_off.png";

		} else if (result == 0) { // 북마크한 약품이 아니면
			bs.insert(bookmark); // bookmark 테이블에 데이터 추가
			imgSrc = "/images/bookmark_on.png";
		}
		return imgSrc;
	}
	
	@RequestMapping("/pc/mypcUpdateForm.do")
	public String mypcUpdateForm(HttpSession session, Model model) {
		Member1 memberSession = (Member1)session.getAttribute("memberSession");
		int mno = memberSession.getMno();
		Pc pc = ps.selectMno(mno);
		model.addAttribute("mno", mno);
		model.addAttribute("pc", pc);
		return "/pc/mypcUpdateForm";
	}
	@RequestMapping("/pc/mypcUpdate.do")
	public String mypcUpdate(HttpSession session, Model model, Pc pc, MultipartHttpServletRequest mhr) throws IOException {
		int result = 0;
		String real="src/main/resources/static/images";
		// 파일 여러개를 저장할 list만들고 pcimage를 생성해서 하나씩 저장
		List<MultipartFile> list = mhr.getFiles("pcimage");
		// list의 사진을 한장씩 뽑아서 images에 저장
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
		result = ps.updatePc(pc);
		ps.deletePcimage(images);
		ps.insertPcimage(images);
		model.addAttribute("result", result);
		return "/pc/mypcUpdate";
	}
	
	@RequestMapping("/pc/seatInsertForm.do")
	public String seatInsertForm(Model model, HttpSession session) {
		Member1 memberSession = (Member1) session.getAttribute("memberSession");
		Pc pc = ps.selectMno(memberSession.getMno());
		model.addAttribute("pcno", pc.getPcno());
		model.addAttribute("pc", pc);
		return "/pc/seatInsertForm";
	}
	
	@RequestMapping("/pc/seatInsert.do")
	public String seatInsert(Pc pc, Seat seat, Fee fee, Model model) {
		int result = 0;
		int pcno = seat.getPcno();
		fee.setPcno(pc.getPcno());
		int seatform = ps.updateSeatform(pc);
		Seat s1 = ps.selectSeat(pcno);
		if (seat.getSeatposition() == null || fee.getW1000() == 0 || fee.getW3000() == 0 || fee.getW5000() == 0 || fee.getW10000() == 0 || fee.getW50000() == 0 || fee.getW100000() == 0) {
			result = 0;
		} else if(s1 == null){			
			result = ps.insertSeat(seat);
			ps.feeInsert(fee);
		} else {
			result = ps.updateSeat(seat);
		}
		
		model.addAttribute("result", result);
		model.addAttribute("pcno", pcno);
		return "/pc/seatInsert";
	}
	
	@RequestMapping("/pc/feeUpdateForm.do")
	public String feeUpdate(Model model, HttpSession session) {
		Member1 member = (Member1)session.getAttribute("memberSession");
		Pc pc = ps.selectMno(member.getMno());
	    int pcno = pc.getPcno();    
		Fee fee = ps.selectFee(pcno);
		model.addAttribute("pc",pc);
		model.addAttribute("pcno",pcno);
		model.addAttribute("fee", fee);
		return "/pc/feeUpdateForm";
	}
	
	@RequestMapping("/pc/feeUpdate.do")
	public String feeUpdate(Fee fee, Model model, HttpSession session) {
		Member1 member = (Member1)session.getAttribute("memberSession"); 
		Pc pc = ps.selectMno(member.getMno());
	    int pcno = pc.getPcno();
	    Fee f1 = ps.selectFee(pcno);
	    fee.setPcno(pcno);
	    int result = 0;
	    
	    if(f1 == null) {
	    	result = ps.feeInsert(fee);
	    } else {	    	
	    	result = ps.feeUpdate(fee);
	    }
		model.addAttribute("pcno",pcno);
		model.addAttribute("result", result);
		return "/pc/feeUpdate";
	}
	
	@RequestMapping("/pc/seatUpdateForm.do")
	public String seatForm(int pcno, Model model) {
		Pc pc = ps.select(pcno);
		Seat seat = ps.selectSeat(pcno);
		model.addAttribute("seat", seat);
		model.addAttribute("pcno", pcno);
		model.addAttribute("pc", pc);
		return "/pc/seatUpdateForm";
	}
	@RequestMapping("/pc/seatUpdate.do")
	public String seatSetting(Pc pc, Seat seat, Model model) {
		int result = 0;
		int pcno = seat.getPcno();
		int seatform = ps.updateSeatform(pc);
		Seat s1 = ps.selectSeat(pcno);
		System.out.println(seat);
		if (seat.getSeatposition() == null) {
			result = 0;
		} else if(s1 == null){			
			result = ps.insertSeat(seat);
		} else {
			result = ps.updateSeat(seat);
		}
		model.addAttribute("result", result);
		model.addAttribute("pcno", pcno);
		return "/pc/seatUpdate";
	}
	
	
}