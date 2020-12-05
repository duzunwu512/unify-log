package com.gzl.log.web;

import com.gzl.log.entity.RunLog;
import com.gzl.log.service.AsynchQueueLogService;
import com.gzl.log.service.RunLogService;
import com.gzl.log.service.mongo.MongoRunLogService;
import com.gzl.log.util.DateUtil;
import com.gzl.log.util.PageInfo;
import com.gzl.log.vo.RunLogVo;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Api("RunLog 日志")
@RequestMapping("runlog")
public class RunLogController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RunLogService runLogService;

    @Autowired
    private AsynchQueueLogService asynchQueueLogService;

    @RequestMapping(value = "get/{id}", method = RequestMethod.GET)
    public Result get(@PathVariable("id") String id) {
        RunLog runlog = runLogService.findById(id);
        return new Result<>(runlog);
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET)
    public Result findAll() {
        return new Result<>(runLogService.findAll());
    }

    @RequestMapping(value = "find", method = RequestMethod.GET)
    public Result find(String app) {
        return new Result<>(runLogService.findByApp(app));
    }


    @RequestMapping(value = "save", method = RequestMethod.POST)
    public Result save(@RequestBody RunLog runLog) {
        return new Result<>(runLogService.insert(runLog));
    }

    @RequestMapping(value = "saveAll", method = RequestMethod.POST)
    public Result save(@RequestBody List<RunLog> runLogs) {
        asynchQueueLogService.add(runLogs);
        return new Result<>("");
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
    public Result delete(@PathVariable("id") String id) {
        runLogService.delete(id);
        return new Result(CodeConst.SUCCESS.getResultCode(), CodeConst.SUCCESS.getMessage());
    }

    @RequestMapping("/pageRunLogVo")
    @ResponseBody
    public Map<String, Object> pageRunLogVo(RunLogVo runLogVo, Pageable pageable, long skip) {
        logger.info("RunLogVo...skip: {}", skip);
        if(pageable == null){
            pageable = PageRequest.of(0, 20);
        }
        String startDate = runLogVo.getStartDate();
        if(StringUtils.isEmpty(startDate)){
            Calendar nowTime = Calendar.getInstance();
            nowTime.add(Calendar.SECOND, -10);
            //nowTime.set(Calendar.MILLISECOND, 0);

            startDate = DateUtil.format(nowTime.getTime(), DateUtil.LONG_FORMAT);
            runLogVo.setStartDate(startDate);
            logger.info("startDate is null, will set value: {}", startDate);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        PageInfo<RunLog> page = runLogService.page(runLogVo, pageable);
        map.put("page", page);
        map.put("startDate", startDate);

        return map;
    }

    @RequestMapping("/page")
    @ResponseBody
    public Map<String, Object> page(RunLogVo runLogVo, long skip, int size) {
        logger.info("page...skip: {}", skip);
        if(size<1){
            size = 20;
        }
        String startDate = runLogVo.getStartDate();
        if(StringUtils.isEmpty(startDate)){
            Calendar nowTime = Calendar.getInstance();
            nowTime.add(Calendar.SECOND, -10);

            startDate = DateUtil.format(nowTime.getTime(), DateUtil.LONG_FORMAT);
            runLogVo.setStartDate(startDate);
            logger.info("startDate is null, will set value: {}", startDate);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        List<RunLog> datas = runLogService.page(runLogVo, skip, size);
        if(CollectionUtils.isEmpty(datas)){
            datas = new ArrayList<>();
        }
        map.put("datas", datas);
        map.put("startDate", startDate);

        return map;
    }




}
