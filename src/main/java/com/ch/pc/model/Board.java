package com.ch.pc.model;
import java.sql.Date;

import org.apache.ibatis.type.Alias;

import lombok.Data;
@Data
@Alias("board")
public class Board {
	private int bno;
	private String subject;
	private String content;
	private Date regDate;
	private int readCnt;
	private int likes;
	private String del;
	private int mno;
	private int pcno;
	// 게시판에 보여주기 위해
	private String nickName;
	//검색
	private int startRow;
	private int endRow;
	private String searchKey;
	private String searchValue;
}