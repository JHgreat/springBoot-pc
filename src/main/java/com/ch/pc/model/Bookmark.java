package com.ch.pc.model;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("bookmark")
public class Bookmark {
	private int mno;
	private int pcno;
	
	// 북마크
	private String pcname;
	private String pcaddr;
	private String pcinfo;
		
}
