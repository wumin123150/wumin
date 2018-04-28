package com.wumin.core.web;

import com.wumin.core.entity.Log;
import com.wumin.core.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class LogQueue {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogQueue.class);

  //队列大小
  public static final int QUEUE_MAX_SIZE = 10000;
  //阻塞队列
  private BlockingQueue<Log> queue = new ArrayBlockingQueue<>(QUEUE_MAX_SIZE);
  public static final int THREAD_POOL_SIZE = 1;
  public static final int LOG_INTERVAL = 10000;

  @Autowired
  LogService logService;
  ExecutorService threadPoolExecutor;

  private static LogQueue logQueue = new LogQueue();

  private LogQueue(){}

  public static LogQueue getInstance() {
    return logQueue;
  }

  public boolean push(Log log) {
    if (size() == QUEUE_MAX_SIZE) {
      LOGGER.warn(this.getClass().getName() + " queue is full!");
    } else {
        return queue.offer(log);
    }
    return false;
  }

  public Log poll() {
      return queue.poll();
  }

  public int size() {
    return queue.size();
  }

  @PostConstruct
  public void startThread() {
    threadPoolExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    for(int i = 0; i < THREAD_POOL_SIZE; i++) {
      threadPoolExecutor.submit(new PollLog(logService));
    }
  }

  @PreDestroy
  public void destroy() {
    threadPoolExecutor.shutdown();

    boolean run = true;
    List<Log> batch = new ArrayList<>();
    while (run) {
      try {
        Log log = queue.poll();
        if(log != null){
          batch.add(log);
        }
        if (batch.size() >= 1000 || queue.isEmpty()) {
          if (batch.size() > 0) {
            logService.save(batch);
            batch.clear();
          }
        }
        if (queue.isEmpty())
          run = false;
      } catch (Exception ex) {
//        if(errorTime < 4){
//          queue.addAll(batch);
//          errorTime ++;
//        } else {
//          errorTime = 0;
//        }
        batch.clear();
        LOGGER.error("store log error!", ex);
      }
    }
  }

  class PollLog implements Runnable {
    LogService logService;
    int errorTime;

    public PollLog(LogService logService) {
      this.logService = logService;
    }

    @Override
    public void run() {
      long lastTime = System.currentTimeMillis();
      List<Log> batch = new ArrayList<>();
      while (true) {
        try {
          Log log = LogQueue.getInstance().poll();
          if(log != null){
            batch.add(log);
          }
          if (batch.size() >= 1000 || (System.currentTimeMillis() - lastTime) >= LOG_INTERVAL) {
            if (batch.size() > 0) {
              logService.save(batch);
              batch.clear();
              lastTime = System.currentTimeMillis();
            }
          }
          if (queue.isEmpty())
            Thread.sleep(5000L);
        } catch (Exception ex) {
          if(errorTime < 4){
            queue.addAll(batch);
            errorTime ++;
          } else {
            errorTime = 0;
          }
          batch.clear();
          lastTime = System.currentTimeMillis();
          LOGGER.error("store log error!", ex);
        }
      }
    }
  }

}
