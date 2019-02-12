package com.cloudcollection.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 个人属性设置
 * Created by lizhi on 2017/9/3.
 */
@Entity
public class Config extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private String defaultFavorties;
    @Column(nullable = false)
    private String defaultCollectType;
    @Column(nullable = false)
    private String defaultModel;
    @Column(nullable = false)
    private String createTime;
    @Column(nullable = false)
    private String lastModifyTime;
    @Transient
    private String collectTypeName;
    @Transient
    private String modelName;

    public Config() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDefaultFavorties() {
        return defaultFavorties;
    }

    public void setDefaultFavorties(String defaultFavorties) {
        this.defaultFavorties = defaultFavorties;
    }

    public String getDefaultCollectType() {
        return defaultCollectType;
    }

    public void setDefaultCollectType(String defaultCollectType) {
        this.defaultCollectType = defaultCollectType;
    }

    public String getDefaultModel() {
        return defaultModel;
    }

    public void setDefaultModel(String defaultModel) {
        this.defaultModel = defaultModel;
    }

    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCollectTypeName() {
        return defaultCollectType.equals("private")?"私密":"公开";
    }

    public void setCollectTypeName(String collectTypeName) {
        this.collectTypeName = collectTypeName;
    }

    public String getModelName() {
        return defaultModel.equals("simple")?"简单":"专业";
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}
