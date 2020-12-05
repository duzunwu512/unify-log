package com.gzl.log.web;

import com.alibaba.fastjson.JSONObject;
import com.gzl.log.util.JwtUtil;
import com.gzl.log.util.security.User;
import com.gzl.log.util.security.annotation.AuthToken;
import com.gzl.log.vo.UserVo;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Api("mongodb測試")
@RequestMapping("user")
public class UserController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private User user;

    @PostMapping("/login")
    public Object login(@RequestBody UserVo uservo){
        logger.info("login:::user:{}", uservo);
        JSONObject jsonObject=new JSONObject();
        if(uservo==null || StringUtils.isEmpty(uservo.getUserName()) || StringUtils.isEmpty(uservo.getPassword())){
            jsonObject.put("message","登录失败,用户名或密码为空");
            jsonObject.put("rslt", false);
            return jsonObject;
        }else  if ( !uservo.getUserName().equals(user.getUserName()) || !uservo.getPassword().equals(user.getPassword())){
            jsonObject.put("message","登录失败,密码错误");
            jsonObject.put("rslt", false);
            return jsonObject;
        }else {
            String token = JwtUtil.sign(uservo.getUserName(), uservo.getPassword());
            uservo.setToken(token);
            uservo.setPassword("");
            jsonObject.put("user", uservo);
            jsonObject.put("rslt", true);
            return jsonObject;
        }
    }

    @PostMapping("/logout")
    public Object logout(){
        logger.info("logout:::");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rslt", true);
        return map;
    }

    @AuthToken
    @GetMapping("/getMessage")
    public String getMessage(){
        return "你已通过验证";
    }

}
