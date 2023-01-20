package com.ch.pc.service;

import java.util.List;

import com.ch.pc.model.Bookmark;

public interface BookmarkService {

	int select(Bookmark bookmark); //model에 집어넣음

	void delete(Bookmark bookmark); //model에 집어넣음

	void insert(Bookmark bookmark); //model에 집어넣음

	int getTotalMybookmark(int mno);

	List<Bookmark> mybookmarkList(Bookmark bookmark); //model에 집어넣음

}
