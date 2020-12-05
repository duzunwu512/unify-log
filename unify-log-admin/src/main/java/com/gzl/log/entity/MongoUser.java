package com.gzl.log.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;


/**
 * 把今天最好的表现当作明天最新的起点．．～
 * いま 最高の表現 として 明日最新の始発．．～
 * Today the best performance  as tomorrow newest starter!

 *
 * @author : xiaomo
 * github: https://github.com/houko
 * email: xiaomo@xiaomo.info
 * <p>
 * Date: 2016/11/15 15:39
 * Description: 用户实体类
 * Copyright(©) 2015 by xiaomo.
 **/

@Data
@ToString(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class MongoUser {

    @Id
    private long id;

    @ApiModelProperty(value = "登录用户")
    private String email;

    @ApiModelProperty(value = "昵称")
    private String userName;

}
