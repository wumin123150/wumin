package com.wumin.common.security.shiro;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wumin.common.service.impl.JpaServiceImpl;

public class RetryLimitCredentialsMatcher extends HashedCredentialsMatcher {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JpaServiceImpl.class);

      //集群中可能会导致出现验证多过5次的现象，因为AtomicInteger只能保证单节点并发    
      private Cache<String, AtomicInteger> passwordRetryCache;

      public RetryLimitCredentialsMatcher(CacheManager cacheManager) {
          passwordRetryCache = cacheManager.getCache("shiro.passwordRetryCache");
      }

      @Override
      public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
          String username = (String)token.getPrincipal();
          //retry count + 1
          AtomicInteger retryCount = passwordRetryCache.get(username);
          if(null == retryCount) {
              retryCount = new AtomicInteger(0);
              passwordRetryCache.put(username, retryCount);
          }
          if(retryCount.incrementAndGet() > 5) {
              LOGGER.warn("username: " + username + " tried to login more than 5 times in period");
              throw new ExcessiveAttemptsException("username: " + username + " tried to login more than 5 times in period");
          } 
           boolean matches = super.doCredentialsMatch(token, info); 
           if(matches) {
                //clear retry data
                passwordRetryCache.remove(username); 
           } 
           return matches; 
      }
  }
