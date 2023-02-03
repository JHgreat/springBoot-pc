package com.ch.pc.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ch.pc.model.Board;
import com.ch.pc.model.Board_likes;
import com.ch.pc.model.Board_reply;
import com.ch.pc.model.Member1;
import com.ch.pc.model.Reply_likes;
import com.ch.pc.service.BoardService;
import com.ch.pc.service.Board_likesService;
import com.ch.pc.service.Board_replyService;
import com.ch.pc.service.MemberService;
import com.ch.pc.service.PageBean;
import com.ch.pc.service.Reply_likesService;

@Controller
public class BoardController {
	@Autowired
	private BoardService bs;
	@Autowired
	private MemberService ms;
	@Autowired
	private Board_replyService brs;
	@Autowired
	private Board_likesService bls;
	@Autowired
	private Reply_likesService rls;

	
	@RequestMapping("/board/boardList.do")
	public String boardList(Board board, String pageNum, Model model) {
		int  rowPerPage = 10;
		if (pageNum == null || pageNum.equals("")) pageNum="1";
		int currentPage = Integer.parseInt(pageNum);
		int total = bs.getTotal(board);
		int startRow = (currentPage - 1) * rowPerPage + 1;
		int endRow = startRow + rowPerPage - 1;
		board.setStartRow(startRow);
		board.setEndRow(endRow);
		List<Board> list = bs.list(board);
		PageBean pb = new PageBean(currentPage, rowPerPage, total);
		String[] title = {"제목","작성자","내용"};
		String[] Etitle = {"subject","nick_name","content"};
		
		for(Board board1:list) {
			Member1 member1 = ms.selectMno(board1.getMno());
			if(member1 != null) {
				board1.setNickName(member1.getNickName());
			}
		}
		System.out.println(board);
		model.addAttribute("board",board);
		model.addAttribute("pageNum",pageNum);
		model.addAttribute("total",total);
		model.addAttribute("list", list);
		model.addAttribute("pb", pb);		
		model.addAttribute("title", title);
		model.addAttribute("Etitle", Etitle);
		return "/board/boardList";
	}
	@RequestMapping("/board/boardContent.do")
	public String content(Member1 member1, Board board, String pageNum, Model model, HttpSession session) {
		System.out.println(board);
		bs.updateRead_cnt(board);      // 조회수 증가
		Board board2 = bs.select(board);
		board2.setSearchKey(board.getSearchKey());
		board2.setSearchValue(board.getSearchValue());
		Member1 memberSession = (Member1)session.getAttribute("memberSession");

		int mno1 = board2.getMno();
		Member1 member2 = ms.selectMno(mno1);
		String nick_name = member2.getNickName(); 	
		String imgSrc = "";
			int mno = memberSession.getMno();
			int bno = board.getBno();
			Board_likes board_likes = new Board_likes();
			board_likes.setMno(mno);
			board_likes.setBno(bno);
			int result = bls.select(board_likes);
			if(result > 0) {
				imgSrc = "/images/heart.png";
			} else if (result == 0) {
				imgSrc = "/images/empty_heart.png";
			}
			
		model.addAttribute("memberSession", memberSession);
		model.addAttribute("board", board2);
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("imgSrc", imgSrc);
		model.addAttribute("nickName", nick_name);
		return "/board/boardContent";
	}
	@RequestMapping("/board/boardInsertForm.do")
	public String boardInsertForm(Board board, String pageNum, Model model) {	
		model.addAttribute("board", board);		
		model.addAttribute("pageNum", pageNum);
		return "/board/boardInsertForm";
	}
	@RequestMapping("/board/boardInsert.do")
	public String insert(Board board, String pageNum, Model model, HttpSession session) {
		int result = 0;
		Member1 member1 = (Member1)session.getAttribute("memberSession");
		int mno = member1.getMno();
		board.setMno(mno);
		result = bs.insert(board);
		model.addAttribute("result", result);
		model.addAttribute("pageNum", pageNum);
		return "/board/boardInsert";
	}
	@RequestMapping("/board/boardUpdateForm.do")
	public String boardUpdateForm(Board board, String pageNum, Model model) {
		Board board2 = bs.select(board);
		Member1 member1 = ms.selectMno(board2.getMno());
		board2.setNickName(member1.getNickName());
		
		model.addAttribute("board", board2);
		model.addAttribute("pageNum", pageNum);
		return "/board/boardUpdateForm";
	}
	@RequestMapping("/board/boardUpdate.do")
	public String boardUpdate(Board board, String pageNum, Model model) {
		int result = 0;
		result = bs.update(board); 
		
		model.addAttribute("result", result);
		model.addAttribute("pageNum", pageNum);
		return "/board/boardUpdate";
	}
	@RequestMapping("/board/boardDelete.do")
	public String boardDelete(Board board, String pageNum, Model model) {
		int result = 0;
		result = bs.delete(board);
		model.addAttribute("result", result);
		model.addAttribute("pageNum", pageNum);
		return "/board/boardDelete";
	}
	@RequestMapping(value = "/board/board_likes.do", produces = "text/html;charset=utf-8")
	@ResponseBody
	public String board_likes(Board board, Model model, HttpSession session) {
		Member1 member1 = (Member1) session.getAttribute("memberSession");
		int mno = member1.getMno();
		int bno = board.getBno();
		Board_likes board_likes = new Board_likes();
		board_likes.setMno(mno);
		board_likes.setBno(bno);
		int result = bls.select(board_likes);
		String imgSrc = "";
		int likes = 0;
		if(result > 0) {
			bls.delete(board_likes);
			bs.likesMinus(bno);
			imgSrc = "/images/empty_heart.png";
		} else if(result == 0) {
			bls.insert(board_likes);
			bs.likesPlus(bno);
			imgSrc = "/images/heart.png";
		}
		// board 테이블의 현재 likes 조회
		Board board1 = bs.selectOne(bno);	
		likes = board1.getLikes();
		String data = likes+","+imgSrc;

		return data;
	}
	
	@RequestMapping("/board/replyList.do")
	public String replyList(Board_reply board_reply, String pageNum, Model model, HttpSession session) {
		Member1 memberSession = (Member1) session.getAttribute("memberSession");
		
		List<Board_reply> list = brs.list(board_reply);
		for (Board_reply reply : list) {
			Member1 member1 = ms.selectMno(reply.getMno());
			reply.setNickName(member1.getNickName());
			/*-----------댓글 추천유무 구현-----------------*/			
			int mno = memberSession.getMno(); 
			int rno = reply.getRno();
			Reply_likes reply_likes = new Reply_likes();
			reply_likes.setRno(rno);
			reply_likes.setMno(mno);
			int result = rls.select(reply_likes); 
			
			if(result > 0) { // 추천 했으면
				reply.setLikesConfirm(1);
			} 
			else if (result == 0) { // 추천 안했으면 
				reply.setLikesConfirm(0);
			}
			
			/*-----------댓글 추천유무 구현 끝-----------------*/
		}
		
		model.addAttribute("memberSession", memberSession);
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("list", list);
		return "/board/replyList";
	}
	@RequestMapping("/board/rInsert.do")
	public String rInsert(Board_reply board_reply) {
		brs.insert(board_reply);
		return "redirect:/board/replyList.do?pcno="+board_reply.getPcno()+"&bno="+board_reply.getBno();
	}
	@RequestMapping("/board/rUpdate.do")
	public String rUpdate(Board_reply board_reply) {
		brs.update(board_reply);
		return "redirect:/board/replyList.do?pcno="+board_reply.getPcno()+"&bno="+board_reply.getBno();
	}
	@RequestMapping("/board/rDelete.do")
	public String rDelete(Board_reply board_reply) {
		brs.delete(board_reply);
		return "redirect:/board/replyList.do?pcno="+board_reply.getPcno()+"&bno="+board_reply.getBno();
	}
	@RequestMapping("/board/reply_likes.do")
	public String reply_likes(Board_reply board_reply, HttpSession session, Model model, String pageNum) {
		Member1 memberSession = (Member1) session.getAttribute("memberSession");
		int mno = memberSession.getMno();
		int rno = board_reply.getRno();
		Reply_likes reply_likes = new Reply_likes();
		reply_likes.setRno(rno);
		reply_likes.setMno(mno);
		int result = rls.select(reply_likes); 
		

		if(result == 0) { 
			rls.insert(reply_likes);
			brs.likesPlus(rno);
		} else if(result != 0) { 
			rls.delete(reply_likes);
			brs.likesMinus(rno);
		}
		// board_reply 테이블의 현재 likes 조회
		Board_reply board_reply1 = brs.select(rno);
		int pcno = board_reply1.getPcno();
		int bno = board_reply1.getBno();

		/*
		 * model.addAttribute("pcno", pcno); model.addAttribute("bno", bno);
		 * model.addAttribute("rno", rno); model.addAttribute("result", result);
		 * model.addAttribute("pageNum", pageNum);
		 * 필요없는 문구
		 */

		return "redirect:/board/replyList.do?pcno="+pcno+"&bno="+bno;
	}
}
