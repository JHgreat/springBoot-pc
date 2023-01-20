package com.ch.pc.model;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("review_likes")
public class Review_likes {
	private int mno;
	private int rno;
}
