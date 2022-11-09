package com.quota.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserVo implements Serializable {
    public String name;
    public String address;
}
