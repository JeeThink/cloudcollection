package com.cloudcollection.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloudcollection.common.aop.LoggerManage;
import com.cloudcollection.domain.Follow;
import com.cloudcollection.domain.enums.FollowStatus;
import com.cloudcollection.domain.enums.ExceptionMsg;
import com.cloudcollection.domain.result.Response;
import com.cloudcollection.repository.FollowRepository;
import com.cloudcollection.utils.DateUtils;

@RestController
@RequestMapping("/follow")
public class FollowController extends BaseController{
	
	@Autowired
	private FollowRepository followRepository;
	
	/**
	 * 关注&取消关注
	 * @return
	 */
	@RequestMapping("/changeFollowStatus")
	@LoggerManage(description="关注&取消关注")
	public Response changeFollowStatus(String status,Long userId){
		try {
			FollowStatus followStatus = FollowStatus.FOLLOW;
			if(!"follow".equals(status)){
				followStatus = FollowStatus.UNFOLLOW;
			}
			Follow follow = followRepository.findByUserIdAndFollowId(getUserId(), userId);
			if(null != follow){
				followRepository.updateStatusById(followStatus, DateUtils.getCurrentDateTime(), follow.getId());
			}else{
				follow = new Follow();
				follow.setFollowId(userId);
				follow.setUserId(getUserId());
				follow.setStatus(followStatus);
				follow.setCreateTime(DateUtils.getCurrentDateTime());
				follow.setLastModifyTime(DateUtils.getCurrentDateTime());
				followRepository.save(follow);
			}
		} catch (Exception e) {
			logger.error("关注&取消关注异常：",e);
			return result(ExceptionMsg.FAILED);
		}
		return result();
	}

}
