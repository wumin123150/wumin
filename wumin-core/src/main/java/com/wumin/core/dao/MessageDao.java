package com.wumin.core.dao;

import com.wumin.common.dao.JpaDao;
import com.wumin.core.entity.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageDao extends JpaDao<Message, String> {

  @Query("SELECT entity FROM Message entity WHERE entity.text.id=:textId")
  List<Message> findByText(@Param("textId") String textId);

}
