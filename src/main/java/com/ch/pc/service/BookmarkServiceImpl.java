package com.ch.pc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ch.pc.mapper.BookmarkDao;
import com.ch.pc.model.Bookmark;

@Service
public class BookmarkServiceImpl implements BookmarkService{
	@Autowired
	private BookmarkDao bd;

	@Override
	public int select(Bookmark bookmark) {
		// TODO Auto-generated method stub
		return bd.select(bookmark);
	}

	@Override
	public void delete(Bookmark bookmark) {
		bd.delete(bookmark);
		
	}

	@Override
	public void insert(Bookmark bookmark) {
		bd.insert(bookmark);
		
	}

	public int getTotalMybookmark(int mno) {
		return bd.getTotalMybookmark(mno);
	}
	public List<Bookmark> mybookmarkList(Bookmark bookmark) {
		return bd.mybookmarkList(bookmark); 
	}
	
}
