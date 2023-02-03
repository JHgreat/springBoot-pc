package com.ch.pc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ch.pc.mapper.Reply_likesDao;
import com.ch.pc.model.Reply_likes;

@Service
public class Reply_likesServiceImpl implements Reply_likesService {
	@Autowired
	private Reply_likesDao rld;

	public int select(Reply_likes reply_likes) {
		return rld.select(reply_likes);
	}
	public void insert(Reply_likes reply_likes) {
		rld.insert(reply_likes);
	}
	public void delete(Reply_likes reply_likes) {
		rld.delete(reply_likes);
	}
}
