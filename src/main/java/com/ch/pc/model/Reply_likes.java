package com.ch.pc.model;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("reply_likes")
public class Reply_likes {
	private int mno;
	private int rno;
}
