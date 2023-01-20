package com.ch.pc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ch.pc.model.Bookmark;
@Mapper
public interface BookmarkDao {

	int select(Bookmark bookmark);

	void delete(Bookmark bookmark);

	void insert(Bookmark bookmark);

	int getTotalMybookmark(int mno);

	List<Bookmark> mybookmarkList(Bookmark bookmark);

}
