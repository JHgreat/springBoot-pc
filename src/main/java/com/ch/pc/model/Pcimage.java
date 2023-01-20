package com.ch.pc.model;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("pcImage")
public class Pcimage {
		private int imageno;
		private int pcno;
		private String imagename;
}
