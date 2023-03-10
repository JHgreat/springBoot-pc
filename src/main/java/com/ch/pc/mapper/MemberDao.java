package com.ch.pc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ch.pc.model.Member1;

@Mapper
public interface MemberDao {

	int insert(Member1 member1);
	
	Member1 select(String id);

	Member1 confirmNick_name(String nickName);

	Member1 confirmEmail(String email);

	Member1 findId(Member1 member1);

	Member1 findPw(Member1 member1);

	int updatePw(Member1 member1);

	int update(Member1 member1);

	Member1 selectMno(int mno);

	//점주
	List<Member1> list(Member1 member1);

	int getTotal(Member1 member1);

	int blackin(int mno);

	int blackout(int mno);

	int memberDelete(int mno);

}
