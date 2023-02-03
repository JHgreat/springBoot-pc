package com.ch.pc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ch.pc.mapper.BoardDao;
import com.ch.pc.model.Board;

@Service
public class BoardServiceImpl implements BoardService {
	@Autowired
	private BoardDao bd;

	public List<Board> list(Board board) {
		return bd.list(board);
	}
	public int getTotal(Board board) {
		return bd.getTotal(board);
	}
	public Board select(Board board) {
		return bd.select(board);
	}
	public void updateRead_cnt(Board board) {
		bd.updateRead_cnt(board);
	}
	public int update(Board board) {
		return bd.update(board);
	}
	public int insert(Board board) {
		return bd.insert(board);
	}
	public int delete(Board board) {
		return bd.delete(board);
	}
	public void likesMinus(int bno) {
		bd.likesMinus(bno);
	}
	public void likesPlus(int bno) {
		bd.likesPlus(bno);
	}
	public Board selectOne(int bno) {
		return bd.selectOne(bno);
	}
}
