package com.ch.pc.model;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("fee")
public class Fee {
	private int pcno;
	private int w1000;
	private int w3000;
	private int w5000;
	private int w10000;
	private int w50000;
	private int w100000;
}
