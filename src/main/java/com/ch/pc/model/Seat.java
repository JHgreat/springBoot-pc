package com.ch.pc.model;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("seat")
public class Seat {
	private int seatno;
	private String seatposition;
	private int pcno;
	
}
