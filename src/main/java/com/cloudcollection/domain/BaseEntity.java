package com.cloudcollection.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Created by lizhi on 2017/9/3.
 */
public class BaseEntity implements Serializable {
    private static final  long serialVersionUID =1L;

    @Override
    public String toString() {
        // 使用ToStringBuilder就可以避免暴内存
        return ToStringBuilder.reflectionToString(this);
    }
}
