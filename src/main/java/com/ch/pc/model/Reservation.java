package com.ch.pc.model;

import java.sql.Date;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("reservation")
public class Reservation {
	private int reserveno;
	private int mno;
	private int pcno;
	private int reservetime;	
	private String starttime;
	private String endtime;
	private String reserveSeatPosition;
	private String expiration;
	private Date regDate;
	
	private int startRow;
	private int endRow;
	
	private String pcname;
}
