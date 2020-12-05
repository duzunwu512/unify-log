package com.gzl.log.web;

import com.alibaba.fastjson.JSONObject;
import com.gzl.log.config.MongoConfig;
import com.gzl.log.entity.MongoUser;
import com.gzl.log.service.mongo.MongoUserService;
import com.gzl.log.util.JwtUtil;
import com.gzl.log.util.security.User;
import com.gzl.log.util.security.annotation.AuthToken;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@Api("mongodb測試")
@RequestMapping("mongodb")
@ConditionalOnBean(MongoConfig.class)
public class MongoUserController {

    private final MongoUserService service;

    @Autowired
    private User user;

    @Autowired
    public MongoUserController(MongoUserService service) {
        this.service = service;
    }

    @RequestMapping(value = "get/{id}", method = RequestMethod.GET)
    public Result get(@PathVariable("id") Long id) {
        MongoUser mongoUser = service.findById(id);
        return new Result<>(mongoUser);
    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET)
    public Result findAll() {
        return new Result<>(service.findAll());
    }

    @RequestMapping(value = "find", method = RequestMethod.GET)
    public Result find(String name) {
        return new Result<>(service.findByName(name));
    }


    @RequestMapping(value = "add", method = RequestMethod.POST)
    public Result add(@RequestBody MongoUser user) {
        return new Result<>(service.add(user));
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public Result save(@RequestBody List<MongoUser> users) {
        service.add(users);
        return new Result<>("");
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
    public Result delete(@PathVariable("id") Long id) {
        service.delete(id);
        return new Result(CodeConst.SUCCESS.getResultCode(), CodeConst.SUCCESS.getMessage());
    }


    @PostMapping("/login")
    public Object login(@RequestBody User uservo){
        JSONObject jsonObject=new JSONObject();
        if(uservo==null || StringUtils.isEmpty(uservo.getUserName()) || StringUtils.isEmpty(uservo.getPassword())){
            jsonObject.put("message","登录失败,用户名或密码为空");
            return jsonObject;
        }else  if ( !uservo.getUserName().equals(user.getUserName()) || !uservo.getPassword().equals(user.getPassword())){
            jsonObject.put("message","登录失败,密码错误");
            return jsonObject;
        }else {
            String token = JwtUtil.sign(uservo.getUserName(), uservo.getPassword());
            jsonObject.put("token", token);
            jsonObject.put("user", uservo);
            return jsonObject;
        }
    }

    @AuthToken
    @GetMapping("/getMessage")
    public String getMessage(){
        return "你已通过验证";
    }

}
