package com.quota.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeacherVo implements Serializable {
    String address;
    String age;
    Double money;
}
