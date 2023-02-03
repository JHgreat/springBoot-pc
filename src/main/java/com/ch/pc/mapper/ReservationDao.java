package com.ch.pc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ch.pc.model.Reservation;

@Mapper
public interface ReservationDao {

	int getTotal(int mno);

	List<Reservation> nList(int mno);
	
	List<Reservation> yList(int mno);

}
