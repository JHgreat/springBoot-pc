package com.ch.pc.model;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("keyword")
public class Keyword {
	private String print;
	private String duel;
	private String curve;
	private String crt;
	private String atm;
	private String air; 
	private String battery;
	private String game;

}
