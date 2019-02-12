package com.cloudcollection.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 评论
 * Created by lizhi on 2017/9/3.
 */
@Entity
public class Comment extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
    private Long id;
    // 评论的收藏Id
    @Column(nullable = false)
    private Long collectId;

    // 评论内容
    @Column(nullable = false, length = 65535, columnDefinition = "Text")
    private String content;

    // 发表评论的用户Id
    @Column(nullable = false)
    private Long userId;

    //
    @Column(nullable = true)
    private Long replyUserId;
    @Column(nullable = false)
    private String createTime;

    // 评论时间
    @Transient
    private String commentTime;
    @Transient
    private String userName;
    @Transient
    private String replyUserName;

    // 个人头像
    @Transient
    private String profilePicture;

    public Comment() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCollectId() {
        return collectId;
    }

    public void setCollectId(Long collectId) {
        this.collectId = collectId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(Long replyUserId) {
        this.replyUserId = replyUserId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getReplyUserName() {
        return replyUserName;
    }

    public void setReplyUserName(String replyUserName) {
        this.replyUserName = replyUserName;
    }

}