package com.cloudcollection.domain;

import com.cloudcollection.domain.enums.CollectType;
import com.cloudcollection.domain.enums.IsDelete;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 收藏实体
 * Created by lizhi on 2017/9/3.
 */
@Entity
public class Collect implements Serializable {
    private static final long serialVersionUID =1L;

    @Id
    @GeneratedValue
    private Long id;

    // 所属用户Id
    @Column(nullable = false)
    private  Long userId;

    // 所属收藏夹Id
    @Column(nullable = false)
    private Long favoritesId;

    // 收藏的链接地址
    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true, length = 65535, columnDefinition = "Text")
    private String description;
    @Column(nullable = true)
    private String logoUrl;
    @Column(nullable = true)
    private String charset;
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private CollectType type;
    @Column(nullable = true)
    private String remark;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IsDelete isDelete;
    @Column(nullable = false)
    private String createTime;

    @Column(nullable = false)
    private String lastModifyTime;

    // 分类
    @Column(nullable = true)
    private String category;

    //  收藏时间
    @Transient
    private String collectTime;

    @Transient
    private String newFavorites;

    public Collect() {
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

    public Long getFavoritesId() {
        return favoritesId;
    }

    public void setFavoritesId(Long favoritesId) {
        this.favoritesId = favoritesId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public CollectType getType() {
        return type;
    }

    public void setType(CollectType type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public IsDelete getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(IsDelete isDelete) {
        this.isDelete = isDelete;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }

    public String getNewFavorites() {
        return newFavorites;
    }

    public void setNewFavorites(String newFavorites) {
        this.newFavorites = newFavorites;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
