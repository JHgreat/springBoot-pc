package com.ch.pc.model;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("board_likes")
public class Board_likes {
	private int mno;
	private int bno;
}
