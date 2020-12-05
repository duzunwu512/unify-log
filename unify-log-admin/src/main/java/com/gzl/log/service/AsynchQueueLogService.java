package com.gzl.log.service;

import com.gzl.log.entity.RunLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class AsynchQueueLogService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RunLogService runLogService;

    private LinkedBlockingQueue<RunLog> entityQueue = new LinkedBlockingQueue<RunLog>();
    private Thread saveEntityThread;
    private volatile boolean toStop = false;

    @PostConstruct
    public void after(){
        saveEntityThread = new Thread(new Runnable() {

            @Override
            public void run() {
                logger.info("开启线程异步处理日志请求...");
                // normal callback
                List<RunLog> logs = null;
                while(!toStop){
                    try {
                        RunLog log = entityQueue.take();
                        if (log != null) {

                            // callback list param
                            logs = new ArrayList<RunLog>();
                            int drainToNum = entityQueue.drainTo(logs);
                            logs.add(log);
                            logger.info("处理｛｝个请求..."+logs.size(), logs.size());
                            runLogService.insert(logs);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }

                // last callback
                try {
                    logs = new ArrayList<RunLog>();
                    logger.info("兜底处理{}个请求...", logs.size());
                    int drainToNum = entityQueue.drainTo(logs);
                    if (!CollectionUtils.isEmpty(logs)) {
                        runLogService.insert(logs);;
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
        saveEntityThread.setDaemon(true);
        saveEntityThread.start();
    }

    public void add(RunLog optLog) {
        entityQueue.add(optLog);
    }

    public void add(Iterable<RunLog> optLogs) {
        //optLogDao.insert(optLogs);
        long s = System.currentTimeMillis();
        logger.info("insert.....{}...=================");
        optLogs.forEach(log->{
            logger.info("insert.....{}...", log.toString());
            entityQueue.add(log);
        });
        System.out.println("insert..use time...{}..."+ (System.currentTimeMillis()-s));
    }

}
