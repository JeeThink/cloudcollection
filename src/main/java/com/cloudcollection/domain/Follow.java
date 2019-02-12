package com.cloudcollection.domain;

import com.cloudcollection.domain.enums.FollowStatus;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 关注
 * Created by lizhi on 2017/9/3.
 */
@Entity
public class Follow extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private Long followId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FollowStatus status;
    @Column(nullable = false)
    private String createTime;
    @Column(nullable = false)
    private String lastModifyTime;
    @Transient
    private String name;

    public Follow() {
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

    public Long getFollowId() {
        return followId;
    }

    public void setFollowId(Long followId) {
        this.followId = followId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public FollowStatus getStatus() {
        return status;
    }

    public void setStatus(FollowStatus status) {
        this.status = status;
    }

    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
