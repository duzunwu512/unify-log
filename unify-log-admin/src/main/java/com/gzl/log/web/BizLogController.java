package com.gzl.log.web;

import com.gzl.log.entity.BizLog;
import com.gzl.log.service.BizLogService;
import com.gzl.log.service.mongo.MongoBizLogService;
import com.gzl.log.util.PageInfo;
import com.gzl.log.vo.BizLogVo;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api("BizLog 日志")
@RequestMapping("bizlog")
public class BizLogController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BizLogService bizLogService;

    @RequestMapping(value = "get/{id}", method = RequestMethod.GET)
    public Result get(@PathVariable("id") String id) {
        if(!StringUtils.isEmpty(id)){
            id = id.trim();
        }
        BizLog mongoUser = bizLogService.findById(id);
        return new Result<>(mongoUser);
    }

    @RequestMapping(value = "find", method = RequestMethod.GET)
    public Result findByBizId(String bizId) {
        if(!StringUtils.isEmpty(bizId)){
            bizId = bizId.trim();
        }
        return new Result<>(bizLogService.findByBizId(bizId));
    }


    @RequestMapping(value = "save", method = RequestMethod.POST)
    public Result save(@RequestBody BizLog runLog) {
        return new Result<>(bizLogService.insert(runLog));
    }

    @RequestMapping(value = "saveAll", method = RequestMethod.POST)
    public Result save(@RequestBody List<BizLog> runLogs) {
        bizLogService.insert(runLogs);
        return new Result<>("");
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.POST)
    public Result delete(@PathVariable("id") String id) {
        if(!StringUtils.isEmpty(id)){
            id = id.trim();
            String ids[] = id.split(",");
            bizLogService.delete(ids);
        }
        //bizLogService.delete(id);
        return new Result(CodeConst.SUCCESS.getResultCode(), CodeConst.SUCCESS.getMessage());
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public Result update(@RequestBody BizLogVo bizLogVo) {
        logger.info("update:{}....................");
        boolean b = bizLogService.update(bizLogVo);
        return new Result<>(b);
    }

    @RequestMapping("/page")
    @ResponseBody
    public PageInfo<BizLog> page(BizLogVo bizLog, Pageable pageable) {
        if(pageable == null){
            pageable = PageRequest.of(0, 10);
        }

        PageInfo<BizLog> page = bizLogService.page(bizLog, pageable);

        return page;
    }
}
