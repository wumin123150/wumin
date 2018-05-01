package com.wumin.core.service;

import com.google.common.base.Charsets;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.wumin.common.collection.ListUtil;
import com.wumin.common.dto.ErrorCodeFactory;
import com.wumin.common.exception.BaseException;
import com.wumin.common.persistence.Hibernates;
import com.wumin.common.security.DigestUtil;
import com.wumin.common.service.impl.JpaServiceImpl;
import com.wumin.common.text.EncodeUtil;
import com.wumin.common.web.ThreadContext;
import com.wumin.core.dao.UserDao;
import com.wumin.core.entity.QUser;
import com.wumin.core.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 用户管理业务类.
 *
 * @author walker
 */
@Service
@Transactional
public class UserService extends JpaServiceImpl<User, Long> {
  public static final String HASH_ALGORITHM = "SHA-1";
  public static final int HASH_INTERATIONS = 1024;
  private static final int SALT_SIZE = 8;

  @Autowired
  private UserDao userDao;

  /**
   * 账号忽略大小写
   */
  public User getByLoginName(String loginName) {
    Optional<User> result = userDao.findOne(QUser.user.loginName.equalsIgnoreCase(loginName));
    return result.isPresent() ? result.get() : null;
  }

  public User getByWeixin(String openId) {
    return userDao.findByOpenId(openId);
  }

  /**
   * 按名称查询用户, 并对用户的延迟加载关联进行初始化.
   */
  public User getByLoginNameInitialized(String name) {
    User user = getByLoginName(name);
    if (user != null) {
      Hibernates.initLazyProperty(user.getRoleList());
      Hibernates.initLazyProperty(user.getGroupList());
    }
    return user;
  }

  /**
   * 获取全部用户对象，并在返回前完成LazyLoad属性的初始化。
   */
  public List<User> getAllInitialized() {
    List<User> result = userDao.findAll();
    for (User user : result) {
      Hibernates.initLazyProperty(user.getRoleList());
      Hibernates.initLazyProperty(user.getGroupList());
    }
    return result;
  }

  /**
   * 获取当前用户数量.
   */
  public Long getCount() {
    return userDao.count();
  }

  /**
   * 在保存用户时,发送用户修改通知消息, 由消息接收者异步进行较为耗时的通知邮件发送.
   * <p>
   * 如果企图修改超级用户,取出当前操作员用户,打印其信息然后抛出异常.
   */
  public User save(User user) {
    if(user.getId() == null) {
      user.setRegisterTime(new Date());
      user.setRegisterIp(ThreadContext.getIp());
    }

    if (isSupervisor(user)) {
      LOGGER.warn("操作员{}尝试修改超级管理员", ThreadContext.getUserName());
      throw new BaseException(ErrorCodeFactory.INTERNAL_SERVER_ERROR_CODE, "不能修改超级管理员");
    }

    // 设定安全的密码，生成随机的salt并经过1024次 sha-1 hash
    if (StringUtils.isNotBlank(user.getPlainPassword())) {
      entryptPassword(user);
    }

    return super.save(user);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void start(Long id) {
    User user = get(id);
    user.setStatus(User.STATUS_ACTIVE);
    save(user);
  }

  public void delete(Long id) {
//		userDao.delete(id);
    //停用
    User user = get(id);
    user.setStatus(User.STATUS_TERMINATED);
    save(user);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void bindingWeixin(Long id, String openId) {
    User user = get(id);
    user.setOpenId(openId);
    save(user);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void unbindingWeixin(Long id) {
    User user = get(id);
    user.setOpenId(null);
    save(user);
  }

  public Page<User> getPageByRole(final String searchKey, final Long roleId, Pageable page) {
    QUser qUser = QUser.user;
    List<BooleanExpression> expressions = new ArrayList<>();

    if (StringUtils.isNotEmpty(searchKey)) {
      expressions.add(qUser.loginName.like(StringUtils.trimToEmpty(searchKey))
        .or(qUser.name.like(StringUtils.trimToEmpty(searchKey)))
        .or(qUser.mobile.like(StringUtils.trimToEmpty(searchKey)))
        .or(qUser.email.like(StringUtils.trimToEmpty(searchKey))));
    }

    if (roleId != null) {
      expressions.add(qUser.roleList.any().id.eq(roleId));
    }

    return userDao.findAll(Expressions.allOf(expressions.toArray(new BooleanExpression[expressions.size()])), page);
  }

  public List<User> getListByGroup(final Long groupId) {
    QUser qUser = QUser.user;
    List<BooleanExpression> expressions = new ArrayList<>();
    expressions.add(qUser.groupList.any().id.eq(groupId));
    expressions.add(qUser.status.ne(User.STATUS_TERMINATED));
    return ListUtil.newArrayList(userDao.findAll(Expressions.allOf(expressions.toArray(new BooleanExpression[expressions.size()]))));
//    return userDao.findAll(new Specification<User>() {
//      public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//        List<Predicate> predicates = new ArrayList<Predicate>();
//        predicates.add(cb.equal(root.join("groupList").get(UserGroup.PROP_ID).as(Long.class), groupId));
//        predicates.add(cb.notEqual(root.get("status").as(String.class), User.STATUS_TERMINATED));
//        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
//      }
//    });
  }

  public Page<User> getPageByGroup(final String searchKey, final Long groupId, Pageable page) {
    QUser qUser = QUser.user;
    List<BooleanExpression> expressions = new ArrayList<>();

    if (StringUtils.isNotEmpty(searchKey)) {
      expressions.add(qUser.loginName.like(StringUtils.trimToEmpty(searchKey))
        .or(qUser.name.like(StringUtils.trimToEmpty(searchKey)))
        .or(qUser.mobile.like(StringUtils.trimToEmpty(searchKey)))
        .or(qUser.email.like(StringUtils.trimToEmpty(searchKey))));
    }

    if (groupId != null) {
      expressions.add(qUser.groupList.any().id.eq(groupId));
    }

    return userDao.findAll(Expressions.allOf(expressions.toArray(new BooleanExpression[expressions.size()])), page);
  }

  public boolean existLoginName(Long id, String loginName) {
    User user = getByLoginName(loginName);
    if (user == null || (id != null && id.equals(user.getId()))) {
      return false;
    } else {
      return true;
    }
  }

  public boolean existOpenId(String openId) {
    User user = getByWeixin(openId);
    return user != null;
  }

  public boolean existGroup(final Long groupId) {
    Specification<User> conditions = new Specification<User>() {
      public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.equal(root.join("groupList").get("id").as(Long.class), groupId);
      }
    };
    List<User> users = userDao.findAll(conditions);
    return !users.isEmpty();
  }

  public boolean checkPassword(final Long id, final String password) {
    User user = get(id);
    if (user != null) {
      String hashPassword = EncodeUtil.encodeHex(DigestUtil.sha1(password.getBytes(Charsets.UTF_8), EncodeUtil.decodeHex(user.getSalt()), HASH_INTERATIONS));
      return StringUtils.equalsIgnoreCase(hashPassword, user.getPassword());
    }

    return false;
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void changePassword(Long id, String password) {
    User user = get(id);
    user.setPlainPassword(password);
    entryptPassword(user);

    super.save(user);
    LOGGER.info("change password for: {}", user.getName());
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void changePassword(String username, String password) {
    User user = getByLoginName(username);
    user.setPlainPassword(password);
    entryptPassword(user);

    super.save(user);
    LOGGER.info("change password for: {}", username);
    // TODO:发送邮件提醒用户
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  public void loginSuccess(String loginName, String ip) {
    User user = getByLoginName(loginName);
    user.setLastLoginTime(new Date());
    user.setLastLoginIp(ip);
    user.addLoginCount();

    super.save(user);
  }

  /**
   * 设定安全的密码，生成随机的salt并经过1024次 sha-1 hash
   */
  private void entryptPassword(User user) {
    byte[] salt = DigestUtil.generateSalt(SALT_SIZE);
    user.setSalt(EncodeUtil.encodeHex(salt));

    byte[] hashPassword = DigestUtil.sha1(user.getPlainPassword().getBytes(Charsets.UTF_8), salt, HASH_INTERATIONS);
    user.setPassword(EncodeUtil.encodeHex(hashPassword));
  }

  /**
   * 判断是否超级管理员.
   */
  private boolean isSupervisor(User user) {
    return (user.getId() != null && user.getId() == 1L);
  }

}
