package com.cloudcollection.web;

import com.cloudcollection.common.aop.LoggerManage;
import com.cloudcollection.domain.Favorites;
import com.cloudcollection.domain.User;
import com.cloudcollection.domain.enums.CollectType;
import com.cloudcollection.domain.enums.FollowStatus;
import com.cloudcollection.domain.enums.IsDelete;
import com.cloudcollection.domain.view.CollectSummary;
import com.cloudcollection.domain.view.LetterSummary;
import com.cloudcollection.repository.*;
import com.cloudcollection.service.CollectService;
import com.cloudcollection.service.LetterService;
import com.cloudcollection.service.LookRecordService;
import com.cloudcollection.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController extends BaseController{
	
	@Autowired
	private CollectService collectService;
	@Autowired
	private FavoritesRepository favoritesRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CollectRepository collectRepository;
	@Autowired
	private FollowRepository followRepository;
	@Autowired
	private NoticeService noticeService;
	@Autowired
	private LookRecordService lookRecordService;
	@Autowired
	private LetterService letterService;
	@Autowired
	private NoticeRepository noticeRepository;


	@RequestMapping(value="/standard/{type}/{userId}")
	@LoggerManage(description="文章列表standard")
	public String standard(Model model,@RequestParam(value = "page", defaultValue = "0") Integer page,
	        @RequestParam(value = "size", defaultValue = "15") Integer size,@PathVariable("type") String type,@PathVariable("userId") Long userId) {
		Sort sort = new Sort(Direction.DESC, "id");
	    Pageable pageable = new PageRequest(page, size, sort);
		model.addAttribute("type", type);
		Favorites favorites = new Favorites();
		if(!"my".equals(type)&&!"explore".equals(type) && !"garbage".equals(type)){
			try {
				favorites = favoritesRepository.findOne(Long.parseLong(type));
				favorites.setPublicCount(collectRepository.countByFavoritesIdAndTypeAndIsDelete(favorites.getId(), CollectType.PUBLIC,IsDelete.No));
			} catch (Exception e) {
				logger.error("获取收藏夹异常：",e);
			}
		}
		List<CollectSummary> collects = null;
	    if(null != userId && 0 != userId && userId.longValue() != getUserId().longValue()){
			User user = userRepository.findOne(userId);
			model.addAttribute("otherPeople", user);
			collects =collectService.getCollects("otherpublic",userId, pageable,favorites.getId(),null);
		}else{
			collects =collectService.getCollects(type,getUserId(), pageable,null,null);
		}
		model.addAttribute("collects", collects);
		model.addAttribute("favorites", favorites);
		model.addAttribute("userId", getUserId());
		model.addAttribute("size", collects.size());
		logger.info("standard end :"+ getUserId());
		return "collect/standard";
	}
	
	
	@RequestMapping(value="/simple/{type}/{userId}")
	@LoggerManage(description="文章列表simple")
	public String simple(Model model,@RequestParam(value = "page", defaultValue = "0") Integer page,
	        @RequestParam(value = "size", defaultValue = "20") Integer size,@PathVariable("type") String type,
	        @PathVariable("userId") Long userId) {
		Sort sort = new Sort(Direction.DESC, "id");
	    Pageable pageable = new PageRequest(page, size, sort);
		model.addAttribute("type", type);
		Favorites favorites = new Favorites();
		if(!"my".equals(type)&&!"explore".equals(type) && !"garbage".equals(type)){
			try {
				favorites = favoritesRepository.findOne(Long.parseLong(type));
				favorites.setPublicCount(collectRepository.countByFavoritesIdAndTypeAndIsDelete(favorites.getId(), CollectType.PUBLIC, IsDelete.No));
			} catch (Exception e) {
				logger.error("获取收藏夹异常：",e);
			}
		}
		List<CollectSummary> collects = null;
	    if(null != userId && 0 != userId && userId.longValue() != getUserId().longValue()){
			User user = userRepository.findOne(userId);
			model.addAttribute("otherPeople", user);
			collects =collectService.getCollects("otherpublic",userId, pageable,favorites.getId(),null);
		}else{
			collects =collectService.getCollects(type,getUserId(), pageable,null,null);
		}
		model.addAttribute("collects", collects);
		model.addAttribute("favorites", favorites);
		model.addAttribute("userId", getUserId());
		model.addAttribute("size", collects.size());
		logger.info("simple end :"+ getUserId());
		return "collect/simple";
	}
	
	/**
	 * 个人首页
	 * @param model
	 * @param userId
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping(value="/user/{userId}/{favoritesId}")
	@LoggerManage(description="个人首页")
	public String userPageShow(Model model,@PathVariable("userId") Long userId,@PathVariable("favoritesId") Long favoritesId,@RequestParam(value = "page", defaultValue = "0") Integer page,
	        @RequestParam(value = "size", defaultValue = "15") Integer size){
		User user = userRepository.findOne(userId);
		Long collectCount = 0l;
		Sort sort = new Sort(Direction.DESC, "id");
	    Pageable pageable = new PageRequest(page, size, sort);
	    List<CollectSummary> collects = null;
	    Integer isFollow = 0;
		if(getUserId().longValue() == userId.longValue()){
			model.addAttribute("myself",IsDelete.YES.toString());
			collectCount = collectRepository.countByUserIdAndIsDelete(userId,IsDelete.No);
			if(0 == favoritesId){
				collects =collectService.getCollects("myself", userId, pageable,null,null);
			}else{
				collects =collectService.getCollects(String.valueOf(favoritesId), userId, pageable,0l,null);
			}
		}else{
			model.addAttribute("myself",IsDelete.No.toString());
			collectCount = collectRepository.countByUserIdAndTypeAndIsDelete(userId, CollectType.PUBLIC, IsDelete.No);
			if(favoritesId == 0){
				collects =collectService.getCollects("others", userId, pageable,null,getUserId());
			}else{
				collects = collectService.getCollects("otherpublic", userId, pageable, favoritesId,getUserId());
			}
			isFollow = followRepository.countByUserIdAndFollowIdAndStatus(getUserId(), userId, FollowStatus.FOLLOW);
		}
		Integer follow = followRepository.countByUserIdAndStatus(userId, FollowStatus.FOLLOW);
		Integer followed = followRepository.countByFollowIdAndStatus(userId, FollowStatus.FOLLOW);
		List<Favorites> favoritesList = favoritesRepository.findByUserId(userId);
		List<String> followUser = followRepository.findFollowUserByUserId(userId);
		List<String> followedUser = followRepository.findFollowedUserByFollowId(userId);
		model.addAttribute("collectCount",collectCount);
		model.addAttribute("follow",follow);
		model.addAttribute("followed",followed);
		model.addAttribute("user",user);
		model.addAttribute("collects", collects);
		model.addAttribute("favoritesList",favoritesList);
		model.addAttribute("followUser",followUser);
		model.addAttribute("followedUser",followedUser);
		model.addAttribute("isFollow",isFollow);
		User userTemp = null;
		User currentUser = getUser();
		if(this.getUser()==null){
			userTemp = new User();
			userTemp.setId(0L);
		}
		model.addAttribute("loginUser",currentUser == null ? userTemp : currentUser);
		return "user";
	}
	
	
		/**
		 * 个人首页内容替换
		 * @param model
		 * @param userId
		 * @param page
		 * @param size
		 * @return
		 */
		@RequestMapping(value="/usercontent/{userId}/{favoritesId}")
		@LoggerManage(description="个人首页内容替换")
		public String userContentShow(Model model,@PathVariable("userId") Long userId,@PathVariable("favoritesId") Long favoritesId,@RequestParam(value = "page", defaultValue = "0") Integer page,
		        @RequestParam(value = "size", defaultValue = "15") Integer size){
			User user = userRepository.findOne(userId);
			Long collectCount = 0l;
			Sort sort = new Sort(Direction.DESC, "id");
		    Pageable pageable = new PageRequest(page, size, sort);
		    List<CollectSummary> collects = null;
			if(getUserId().longValue() == userId.longValue()){
				model.addAttribute("myself",IsDelete.YES.toString());
				collectCount = collectRepository.countByUserIdAndIsDelete(userId, IsDelete.No);
				if(0 == favoritesId){
					collects =collectService.getCollects("myself", userId, pageable,null,null);
				}else{
					collects =collectService.getCollects(String.valueOf(favoritesId), userId, pageable,0l,null);
				}
			}else{
				model.addAttribute("myself",IsDelete.No.toString());
				collectCount = collectRepository.countByUserIdAndTypeAndIsDelete(userId, CollectType.PUBLIC, IsDelete.No);
				if(favoritesId == 0){
					collects =collectService.getCollects("others", userId, pageable,null,getUserId());
				}else{
					collects = collectService.getCollects("otherpublic", userId, pageable, favoritesId,getUserId());
				}
			}
			List<Favorites> favoritesList = favoritesRepository.findByUserId(userId);
			model.addAttribute("collectCount",collectCount);
			model.addAttribute("user",user);
			model.addAttribute("collects", collects);
			model.addAttribute("favoritesList",favoritesList);
			model.addAttribute("favoritesId", favoritesId);
			model.addAttribute("loginUser",getUser());
			return "fragments/usercontent";
		}
	
	
	/**
	 * 搜索
	 * @author neo
	 * @date 2016年8月25日
	 * @param model
	 * @param page
	 * @param size
	 * @param key
	 * @return
	 */
	@RequestMapping(value="/search/{key}")
	@LoggerManage(description="搜索")
	public String search(Model model,@RequestParam(value = "page", defaultValue = "0") Integer page,
	        @RequestParam(value = "size", defaultValue = "20") Integer size, @PathVariable("key") String key) {
		Sort sort = new Sort(Direction.DESC, "id");
	    Pageable pageable = new PageRequest(page, size, sort);
	    List<CollectSummary> myCollects=collectService.searchMy(getUserId(),key ,pageable);
	    List<CollectSummary> otherCollects=collectService.searchOther(getUserId(), key, pageable);
		model.addAttribute("myCollects", myCollects);
		model.addAttribute("otherCollects", otherCollects);
		model.addAttribute("userId", getUserId());
		
		model.addAttribute("mysize", myCollects.size());
		model.addAttribute("othersize", otherCollects.size());
		model.addAttribute("key", key);

		logger.info("search end :"+ getUserId());
		return "collect/search";
	}
	
	/**
	 * 消息通知@我的
	 * @param model
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping(value="/notice/atMe")
	public String atMe(Model model,@RequestParam(value = "page", defaultValue = "0") Integer page,
	        @RequestParam(value = "size", defaultValue = "15") Integer size) {
		Sort sort = new Sort(Direction.DESC, "id");
	    Pageable pageable = new PageRequest(page, size, sort);
	    List<CollectSummary> collects=noticeService.getNoticeCollects("at", getUserId(), pageable);
		model.addAttribute("collects", collects);
		noticeRepository.updateReadedByUserId("read",getUserId(),"at");
		logger.info("at end :"+ getUserId());
		return "notice/atme";
	}
	
	/**
	 * 消息通知评论我的
	 * @param model
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping(value="/notice/commentMe")
	public String commentMe(Model model,@RequestParam(value = "page", defaultValue = "0") Integer page,
	        @RequestParam(value = "size", defaultValue = "15") Integer size) {
		Sort sort = new Sort(Direction.DESC, "id");
	    Pageable pageable = new PageRequest(page, size, sort);
	    List<CollectSummary> collects=noticeService.getNoticeCollects("comment", getUserId(), pageable);
		model.addAttribute("collects", collects);
		noticeRepository.updateReadedByUserId("read",getUserId(),"comment");
		logger.info("at end :"+ getUserId());
		return "notice/commentme";
	}
	
	/**
	 * 消息通知赞我的
	 * @param model
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping(value="/notice/praiseMe")
	public String praiseMe(Model model,@RequestParam(value = "page", defaultValue = "0") Integer page,
	        @RequestParam(value = "size", defaultValue = "15") Integer size) {
		Sort sort = new Sort(Direction.DESC, "id");
	    Pageable pageable = new PageRequest(page, size, sort);
	    List<CollectSummary> collects=noticeService.getNoticeCollects("praise", getUserId(), pageable);
		model.addAttribute("collects", collects);
		noticeRepository.updateReadedByUserId("read",getUserId(),"praise");
		logger.info("praiseMe end :"+ getUserId());
		return "notice/praiseme";
	}

	/**
	 * 浏览记录 标准显示 added by chenzhimin
	 * @param model
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping(value="/lookRecord/standard/{type}/{userId}")
	@LoggerManage(description="浏览记录lookRecord")
	public String getLookRecordStandard(Model model,@RequestParam(value = "page", defaultValue = "0") Integer page,
						     @RequestParam(value = "size", defaultValue = "15") Integer size,
							 @PathVariable("type") String type,@PathVariable("userId") Long userId) {

		Sort sort = new Sort(Direction.DESC, "lastModifyTime");
		Pageable pageable = new PageRequest(page, size, sort);
		model.addAttribute("type", "lookRecord");
		Favorites favorites = new Favorites();

		List<CollectSummary> collects = null;
		User user = userRepository.findOne(userId);
		model.addAttribute("otherPeople", user);
		collects =lookRecordService.getLookRecords(this.getUserId(),pageable);

		model.addAttribute("collects", collects);
		model.addAttribute("favorites", favorites);
		model.addAttribute("userId", getUserId());
		model.addAttribute("size", collects.size());
		logger.info("LookRecord end :"+ getUserId());
		return "lookRecord/standard";
	}

	/**
	 * 浏览记录 简单显示 added by chenzhimin
	 * @param model
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping(value="/lookRecord/simple/{type}/{userId}")
	@LoggerManage(description="浏览记录lookRecord")
	public String getLookRecordSimple(Model model,@RequestParam(value = "page", defaultValue = "0") Integer page,
								@RequestParam(value = "size", defaultValue = "20") Integer size,
								@PathVariable("type") String type,@PathVariable("userId") Long userId) {

		Sort sort = new Sort(Direction.DESC, "lastModifyTime");
		Pageable pageable = new PageRequest(page, size, sort);
		model.addAttribute("type", "lookRecord");
		Favorites favorites = new Favorites();

		List<CollectSummary> collects = null;
		User user = userRepository.findOne(userId);
		model.addAttribute("otherPeople", user);
		collects =lookRecordService.getLookRecords(this.getUserId(),pageable);

		model.addAttribute("collects", collects);
		model.addAttribute("favorites", favorites);
		model.addAttribute("userId", getUserId());
		model.addAttribute("size", collects.size());
		logger.info("LookRecord end :"+ getUserId());
		return "lookRecord/simple";
	}

	@RequestMapping("/letter/letterMe")
	@LoggerManage(description = "私信我的页面展示")
	public String letterMe(Model model, @RequestParam(value = "page", defaultValue = "0") Integer page,
						   @RequestParam(value = "size", defaultValue = "15") Integer size){
		Sort sort = new Sort(Sort.Direction.DESC, "id");
		Pageable pageable = new PageRequest(page, size, sort);
		List<LetterSummary> letterList = letterService.findLetter(getUserId(),pageable);
		model.addAttribute("letterList",letterList);
		noticeRepository.updateReadedByUserId("read",getUserId(),"letter");
		return "notice/letterme";
	}
	
}