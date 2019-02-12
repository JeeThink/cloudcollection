package com.cloudcollection.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * 用户实体
 * Created by lizhi on 2017/9/3.
 */
@Entity
public class User  extends BaseEntity implements Serializable{
    private static final long serialVersionUID =1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String userName;
    @Column(nullable = false)
    private String passWord;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String profilePicture;

    @Column(nullable = true, length = 65535, columnDefinition = "Text")
    private String introduction;

    @Column(nullable = false)
    private String createTime;

    @Column(nullable = false)
    private String lastModifyTime;

    // 过期日期
    @Column(nullable = true)
    private String outDate;

    @Column(nullable = true)
    private String validataCode;
    @Column(nullable = true)
    private String backgroundPicture;

    public User(){super();}

    public User(String email, String passWord, String userName){

        super();
        this.userName =userName;
        this.passWord = passWord;
        this.email =email;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassWord() {
        return passWord;
    }
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getIntroduction() {
        return introduction;
    }
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
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
    public String getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    public String getOutDate() {
        return outDate;
    }
    public void setOutDate(String outDate) {
        this.outDate = outDate;
    }
    public String getValidataCode() {
        return validataCode;
    }
    public void setValidataCode(String validataCode) {
        this.validataCode = validataCode;
    }
    public String getBackgroundPicture() {
        return backgroundPicture;
    }
    public void setBackgroundPicture(String backgroundPicture) {
        this.backgroundPicture = backgroundPicture;
    }

}
