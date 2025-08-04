package org.live.streaming.interfaces.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserTagDTO implements Serializable {

    private Long userId;

    private Long tagInfo01;

    private Long tagInfo02;

    private Long tagInfo03;

    private Date createTime;

    private Date updateTime;

}
