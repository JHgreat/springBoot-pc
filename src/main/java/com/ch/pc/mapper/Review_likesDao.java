package com.ch.pc.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.ch.pc.model.Review_likes;

@Mapper
public interface Review_likesDao {

	int select(Review_likes review_likes);

	void insert(Review_likes review_likes);

	void delete(Review_likes review_likes);

}
