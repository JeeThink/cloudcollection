package com.cloudcollection.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.cloudcollection.domain.view.CollectSummary;

public interface NoticeService {
	
	public void saveNotice(String collectId,String type,Long userId,String operId);
	
	public List<CollectSummary> getNoticeCollects(String type, Long userId, Pageable pageable);

}
