package com.ch.pc.model;

import java.sql.Date;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("board_reply")
public class Board_reply {
	private int rno;
	private String content;
	private Date reg_date;
	private int likes;
	private String del;
	private int mno;
	private int bno;
	private int pcno;
	
	private String nick_name;
	
	private int likesConfirm;
}
