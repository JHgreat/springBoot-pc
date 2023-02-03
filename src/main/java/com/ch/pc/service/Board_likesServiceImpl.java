package com.ch.pc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ch.pc.mapper.Board_likesDao;
import com.ch.pc.model.Board_likes;

@Service
public class Board_likesServiceImpl implements Board_likesService {
	@Autowired
	private Board_likesDao bld;

	public int select(Board_likes board_likes) {
		return bld.select(board_likes);
	}
	public void delete(Board_likes board_likes) {
		bld.delete(board_likes);
	}
	public void insert(Board_likes board_likes) {
		bld.insert(board_likes);
	}
}
