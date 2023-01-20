package com.ch.pc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ch.pc.model.Review;
@Mapper
public interface ReviewDao {

	List<Review> list(Review review);

	void insert(Review review);

	void update(Review review);

	void delete(Review review);

	void likesPlus(int rno);

	void likesMinus(int rno);

	Review select(int rno);

	int getTotal(Review review);

	double avgRating(int pcno);

}
