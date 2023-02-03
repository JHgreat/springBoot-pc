package com.ch.pc.model;

import java.sql.Date;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("review")
public class Review {
	private int rno;
	private int pcno;
	private int mno;
	private String content;
	private Date regDate;
	private int likes;
	private int rating;
	private String del;
	
	private String nickName;
	
	private int likesConfirm;
	
	private int startRow;
	private int endRow;
}
