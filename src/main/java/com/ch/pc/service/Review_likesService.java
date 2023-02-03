package com.ch.pc.service;

import com.ch.pc.model.Review_likes;

public interface Review_likesService {

	int select(Review_likes review_likes);

	void insert(Review_likes review_likes);

	void delete(Review_likes review_likes);

}
