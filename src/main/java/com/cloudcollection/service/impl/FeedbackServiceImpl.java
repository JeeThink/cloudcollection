package com.cloudcollection.service.impl;

import com.cloudcollection.domain.Feedback;
import com.cloudcollection.repository.FeedbackRepository;
import com.cloudcollection.service.FeedbackService;
import com.cloudcollection.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by chenzhimin on 2017/2/23.
 */
@Service("feedbackService")
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public void saveFeeddback(Feedback feedback,Long userId) {
        feedback.setUserId(userId == null || userId == 0L ? null : userId);
        feedback.setCreateTime(DateUtils.getCurrentDateTime());
        feedback.setLastModifyTime(DateUtils.getCurrentDateTime());
        feedbackRepository.save(feedback);
    }
}
