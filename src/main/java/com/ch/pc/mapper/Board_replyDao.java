package com.ch.pc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ch.pc.model.Board_reply;
@Mapper
public interface Board_replyDao {

	List<Board_reply> list(Board_reply board_reply);

	void insert(Board_reply board_reply);

	void update(Board_reply board_reply);

	void delete(Board_reply board_reply);

	void likesPlus(int rno);

	void likesMinus(int rno);

	Board_reply select(int rno);

}
