package com.ch.pc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ch.pc.model.Board;
@Mapper
public interface BoardDao {

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
