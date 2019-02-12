package com.cloudcollection.repository;

import com.cloudcollection.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by chenzhimin on 2017/2/23.
 */
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
