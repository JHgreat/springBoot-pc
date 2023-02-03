package com.ch.pc.service;

import java.util.List;

import com.ch.pc.model.Board;

public interface BoardService {

	List<Board> list(Board board);

	int getTotal(Board board);

	Board select(Board board);

	void updateRead_cnt(Board board);

	int update(Board board);

	int insert(Board board);

	int delete(Board board);

	void likesMinus(int bno);

	void likesPlus(int bno);

	Board selectOne(int bno);

}
