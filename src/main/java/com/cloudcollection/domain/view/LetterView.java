package com.cloudcollection.domain.view;

import com.cloudcollection.domain.enums.LetterType;

/**
 * Created by DingYS on 2017/3/8.
 */
public interface LetterView {
    Long getId();
    Long getSendUserId();
    String getSendUserName();
    String getProfilePicture();
    String getContent();
    Long getCreateTime();
    Long getPid();
    String getType();
}
