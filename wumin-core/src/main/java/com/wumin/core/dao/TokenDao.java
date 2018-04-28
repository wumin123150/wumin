package com.wumin.core.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.wumin.common.dao.JpaDao;
import com.wumin.core.entity.Token;

public interface TokenDao extends JpaDao<Token, String> {

	@Modifying
	@Transactional
	@Query("UPDATE Token entity SET entity.status=" + Token.STATUS_DISABLE + " WHERE entity.user.id=?1")
	void disable(Long userId);
	
	@Query("FROM Token entity WHERE entity.status=" + Token.STATUS_ENABLE + " AND entity.user.id=?1")
	List<Token> findByEnable(Long userId);

}
