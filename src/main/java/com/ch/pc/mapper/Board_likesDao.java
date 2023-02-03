package com.ch.pc.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.ch.pc.model.Board_likes;

@Mapper
public interface Board_likesDao {

	int select(Board_likes board_likes);

	void delete(Board_likes board_likes);

	void insert(Board_likes board_likes);

}
