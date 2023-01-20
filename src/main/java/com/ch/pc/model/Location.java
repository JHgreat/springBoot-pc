package com.ch.pc.model;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("location")
public class Location {	
	private String sido;
	private String sigugun;
	private String dong;
}
