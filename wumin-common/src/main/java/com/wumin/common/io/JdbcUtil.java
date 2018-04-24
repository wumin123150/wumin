package com.wumin.common.io;

import com.wumin.common.web.ApplicationCleanListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC工具类
 */
public class JdbcUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(JdbcUtil.class);

  // 定义数据库的链接
  private Connection conn;
  // 定义sql语句的执行对象
  private PreparedStatement pstmt;
  // 定义查询返回的结果集合
  private ResultSet rs;

  // 初始化
  public JdbcUtil(String driver, String url, String username, String password) {
    try {
      Class.forName(driver);
      conn = DriverManager.getConnection(url, username, password);
      LOGGER.debug("数据库连接成功");
    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.error("数据库连接失败：", e);
    }
  }

  // 更新数据
  public boolean updateByParams(String sql, List params) throws SQLException {
    // 影响行数
    int result = -1;
    pstmt = conn.prepareStatement(sql);
    int index = 1;
    // 填充sql语句中的占位符
    if (null != params && !params.isEmpty()) {
      for (int i = 0; i < params.size(); i++) {
        pstmt.setObject(index++, params.get(i));
      }
    }
    result = pstmt.executeUpdate();
    return result > 0 ? true : false;
  }

  // 查询多条记录
  public List<Map> selectByParams(String sql, List params) throws SQLException {
    List<Map> list = new ArrayList<>();
    int index = 1;
    pstmt = conn.prepareStatement(sql);
    if (null != params && !params.isEmpty()) {
      for (int i = 0; i < params.size(); i++) {
        pstmt.setObject(index++, params.get(i));
      }
    }
    rs = pstmt.executeQuery();
    ResultSetMetaData metaData = rs.getMetaData();
    int colLen = metaData.getColumnCount();
    while (rs.next()) {
      Map map = new HashMap();
      for (int i = 0; i < colLen; i++) {
        String colName = metaData.getColumnName(i + 1);
        Object colValue = rs.getObject(colName);
        if (null == colValue) {
          colValue = "";
        }
        map.put(colName, colValue);
      }
      list.add(map);
    }
    return list;
  }

  // 释放连接
  public void release() {
    try {
      if (null != rs)
        rs.close();
      if (null != pstmt)
        pstmt.close();
      if (null != conn)
        conn.close();
      LOGGER.debug("释放数据库连接成功");
    } catch (SQLException e) {
      e.printStackTrace();
      LOGGER.error("释放数据库连接失败：", e);
    }
  }

}
