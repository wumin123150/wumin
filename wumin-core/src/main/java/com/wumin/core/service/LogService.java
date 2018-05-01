package com.wumin.core.service;

import com.wumin.common.service.impl.JpaServiceImpl;
import com.wumin.common.time.DateFormatUtil;
import com.wumin.common.time.DateUtil;
import com.wumin.core.dao.LogDao;
import com.wumin.core.entity.Log;
import com.wumin.core.entity.QLog;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 日志管理业务类.
 *
 * @author walker
 */
@Service
@Transactional
public class LogService extends JpaServiceImpl<Log, String> {

  @Value("${log.period:30}")
  private int period;

  @Autowired
  private LogDao logDao;

  public void save(Iterable<Log> entities) {
    logDao.saveAll(entities);
  }

  public Page<Log> getPage(final String searchKey, final Date startTime, final Date endTime, Pageable page) {
    QLog qLog = QLog.log;
    List<BooleanExpression> expressions = new ArrayList<>();

    if (StringUtils.isNotEmpty(searchKey)) {
      expressions.add(qLog.username.like(StringUtils.trimToEmpty(searchKey))
        .or(qLog.description.like(StringUtils.trimToEmpty(searchKey)))
        .or(qLog.ip.like(StringUtils.trimToEmpty(searchKey)))
        .or(qLog.url.like(StringUtils.trimToEmpty(searchKey))));
    }

    if (startTime != null && endTime != null) {
      expressions.add(qLog.logTime.between(startTime, endTime));
    } else if (startTime != null) {
      expressions.add(qLog.logTime.goe(startTime));
    } else if (endTime != null) {
      expressions.add(qLog.logTime.loe(endTime));
    }

    return logDao.findAll(Expressions.allOf(expressions.toArray(new BooleanExpression[expressions.size()])), page);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void clear() {
    Date deadline = DateUtil.subDays(DateUtil.today(), period);
    logDao.clear(deadline);
    LOGGER.info("delete entities {}, deadline is {}", entityClazz.getName(), DateFormatUtil.formatShortDate(deadline));
  }

}
