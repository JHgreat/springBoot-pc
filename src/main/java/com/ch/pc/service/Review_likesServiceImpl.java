package com.ch.pc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ch.pc.mapper.Review_likesDao;
import com.ch.pc.model.Review_likes;

@Service
public class Review_likesServiceImpl implements Review_likesService {
	@Autowired
	private Review_likesDao rld;

	public int select(Review_likes review_likes) {
		return rld.select(review_likes);
	}
	public void insert(Review_likes review_likes) {
		rld.insert(review_likes);
	}
	public void delete(Review_likes review_likes) {
		rld.delete(review_likes);
	}
}
