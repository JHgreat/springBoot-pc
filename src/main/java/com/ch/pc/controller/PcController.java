package com.ch.pc.controller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
import com.ch.pc.model.Reservation;
import com.ch.pc.model.Seat;
import com.ch.pc.service.BookmarkService;
import com.ch.pc.service.PcService;
import com.ch.pc.service.ReviewService;
@Controller
public class PcController {
	@Autowired
	private PcService ps;
	
	@Autowired
	private BookmarkService bs;
	
	@Autowired
	private ReviewService rs;
	
	
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
	@RequestMapping("/pc/pcMainForm.do")
	public String pcMainForm(int pcno, Model model, HttpSession session, String pageNum) {
		String imgSrc = "";
		Member1 memberSession = (Member1) session.getAttribute("memberSession");
		if(pageNum == null || pageNum.equals("")) {
			pageNum = "1";
		}
		if (memberSession != null) {
			int mno = memberSession.getMno();
			Bookmark bookmark = new Bookmark();
			bookmark.setMno(mno);
			bookmark.setPcno(pcno);
			int bookmarkConfirm = bs.select(bookmark);
			if (bookmarkConfirm > 0) { // 북마크 한 pc방이면
				imgSrc = "/pc/resources/images/bookmark_on.png";

			} else if (bookmarkConfirm == 0) { // 북마크 한 pc방이 아니면
				imgSrc = "/pc/resources/images/bookmark_off.png";
			}
		}
		String id = memberSession.getId();
		Pc pc = ps.select(pcno);
		double avgRating = rs.avgRating(pcno);

		List<Pcimage> photolist = ps.listPhoto(pcno);
		//필요없는구문
		pc.setSearchKey(pc.getSearchKey());
		pc.setSearchValue(pc.getSearchValue());		
		String slist = ps.listSeat(pcno);
		String[] seatlists = null;
		if(slist != null) {
			seatlists = slist.split(",");
			}
		//필요없는 구문
		Fee fee = ps.selectFee(pcno);
		session.setAttribute("pcnoSession", pcno);
		
		model.addAttribute("avgRating", avgRating);
		model.addAttribute("imgSrc", imgSrc);
		model.addAttribute("fee", fee);
		model.addAttribute("seatlists", Arrays.toString(seatlists));
		model.addAttribute("pc", pc);
		model.addAttribute("photolist", photolist);
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("id", id);
		return "/pc/pcMainForm";
	}
	@RequestMapping("/pc/reservationForm.do")
	public String reservationForm(int pcno, Model model, HttpSession session) {
		session.setAttribute("pcnoSession", pcno);
		String slist = ps.listSeat(pcno);
		String[] seatlists = null;
		if (slist != null) {
			seatlists = slist.split(",");
		}

		Calendar now_time = Calendar.getInstance();
		
		// now_time을 현재시간 +1로 설정, 현재 시간 정보가져오기
		now_time.set(Calendar.HOUR_OF_DAY, now_time.get(Calendar.HOUR_OF_DAY) + 1);

		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		SimpleDateFormat sdf2 = new SimpleDateFormat("mm");
		Date now_time1 = now_time.getTime();
		String min = sdf2.format(now_time1);
		String hours = sdf.format(now_time1);
		int hoursI = Integer.parseInt(hours);
		int minI = Integer.parseInt(min);
		
		if (minI < 30) {
			hoursI -= 1;
			minI = 30;
		} else {
			minI = 0;
		}
		// 예약된 자리 정보 가져오기
		List<Reservation> reservelist = ps.reserveList(pcno);
		
		String allReserveSeatPosition = "";
		LocalDate nowDay = LocalDate.now();
		int currentDay = nowDay.getDayOfMonth();	//오늘 일
		
		int StartTimeToMin = currentDay * 1440 + hoursI*60 + minI;
		
		for (Reservation reservation : reservelist) {
			String startTime = reservation.getStarttime();
			
			String starthour = startTime.substring(0, 2);
			String startmin = startTime.substring(3, 5);
			
			Date regdate = reservation.getRegDate(); 
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
			String startday = simpleDateFormat.format(regdate);
			
			int reservetime = reservation.getReservetime();
			
			int startHour =Integer.parseInt(starthour);
			int startMin =Integer.parseInt(startmin);
			int startDay = Integer.parseInt(startday);
			
			int endTimeToMin = startDay*1440 + startHour*60 + startMin + reservetime;
			
			if(endTimeToMin>StartTimeToMin) { //DB에 있는 끝나는 시각이 현재 시간보다 크다면 StartTimeToMin = 현재 시간
			allReserveSeatPosition += reservation.getReserveSeatPosition() + ",";
			}
		}
		
		String[] rlists = null;
		if (allReserveSeatPosition != null) {
			rlists = allReserveSeatPosition.split(",");
		}

		Pc pc = ps.select(pcno);
		Fee fee = ps.selectFee(pcno);
		System.out.println(fee);
		model.addAttribute("now_hour", hoursI);
		model.addAttribute("now_min", minI);
		model.addAttribute("fee", fee);
		model.addAttribute("seatlists", Arrays.toString(seatlists));
		model.addAttribute("rlists", Arrays.toString(rlists));
		model.addAttribute("pc", pc);
		return "/pc/reservationForm";
	}

	@RequestMapping("/pc/reservation.do")
	public String reservation(Reservation reservation, Model model, HttpSession session) {
		System.out.println(reservation);
		int pcno = (Integer) session.getAttribute("pcnoSession");
		Member1 member1 = (Member1) session.getAttribute("memberSession");
		reservation.setPcno(pcno);
		reservation.setMno(member1.getMno());
		int result = 0;
		LocalTime now = LocalTime.now();
		int currentmin = now.getMinute();
		int currentHour = now.getHour();		
		String startTime = reservation.getStarttime();
		String hour = startTime.substring(0, 2);
		String min = startTime.substring(3, 5);
		int ihour = Integer.parseInt(hour);
		int imin = Integer.parseInt(min);
		int hourToMin = ihour*60;
		int currentHourToMin = currentHour*60;
		
		if(hourToMin + imin <= currentHourToMin+currentmin) {
			result = -1;
		}else {
			result = ps.insertReservation(reservation);
		}
		
		
		model.addAttribute("result", result);
		return "/pc/reservation";
	}
	@RequestMapping(value = "/pc/starttimeChange.do", produces = "text/html;charset=utf-8") 
	@ResponseBody
	public String starttimeChange(String changestarttime, HttpSession session) {
		LocalDate nowDay = LocalDate.now();
		
		String changehour = changestarttime.substring(0, 2);
		String changemin = changestarttime.substring(3, 5);
		
		int changeHour = Integer.parseInt(changehour);
		int changeMin = Integer.parseInt(changemin);
		int changeDay = nowDay.getDayOfMonth();	//오늘 일
		
		int changeStartTimeToMin = changeDay * 1440 + changeHour*60 + changeMin;
		
		int pcno = (Integer) session.getAttribute("pcnoSession");		 
		List<Reservation> reservelist = ps.reserveList(pcno);
		String allReserveSeatPosition = "";
		
		for (Reservation reservation : reservelist) {
			String startTime = reservation.getStarttime();
			
			String starthour = startTime.substring(0, 2);
			String startmin = startTime.substring(3, 5);
			
			Date regdate = reservation.getRegDate(); 
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
			String startday = simpleDateFormat.format(regdate);
			
			int reservetime = reservation.getReservetime();
			
			int startHour =Integer.parseInt(starthour);
			int startMin =Integer.parseInt(startmin);
			int startDay = Integer.parseInt(startday);
			
			int endTimeToMin = startDay*1440 + startHour*60 + startMin + reservetime;
			
			if(endTimeToMin>changeStartTimeToMin) { //DB에 있는 끝나는 시각이 바꾼 시간보다 크다면
			allReserveSeatPosition += reservation.getReserveSeatPosition() + ",";
			}
		}
		
		String[] lists = null;
		if (allReserveSeatPosition != null) {
			lists = allReserveSeatPosition.split(",");
		}
		
		
		String rlists = Arrays.toString(lists);
		
		return rlists;
	}
	
	
}