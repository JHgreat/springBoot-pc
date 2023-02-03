package com.ch.pc.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.ch.pc.model.Reply_likes;

@Mapper
public interface Reply_likesDao {

	int select(Reply_likes reply_likes);

	void insert(Reply_likes reply_likes);

	void delete(Reply_likes reply_likes);

}
