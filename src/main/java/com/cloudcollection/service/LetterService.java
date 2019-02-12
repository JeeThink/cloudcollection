package com.cloudcollection.service;

import com.cloudcollection.domain.Letter;
import com.cloudcollection.domain.view.LetterSummary;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by DingYS on 2017/3/8.
 */
public interface LetterService {

    public void sendLetter(Letter letter);

    public List<LetterSummary> findLetter(Long userId, Pageable pageable);
}
