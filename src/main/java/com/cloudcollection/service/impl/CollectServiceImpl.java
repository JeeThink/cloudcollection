package com.cloudcollection.service.impl;

import com.cloudcollection.cache.CacheService;
import com.cloudcollection.domain.Collect;
import com.cloudcollection.domain.Favorites;
import com.cloudcollection.domain.Praise;
import com.cloudcollection.domain.User;
import com.cloudcollection.domain.enums.CollectType;
import com.cloudcollection.domain.enums.IsDelete;
import com.cloudcollection.domain.view.CollectSummary;
import com.cloudcollection.domain.view.CollectView;
import com.cloudcollection.repository.*;
import com.cloudcollection.service.CollectService;
import com.cloudcollection.service.FavoritesService;
import com.cloudcollection.service.NoticeService;
import com.cloudcollection.utils.DateUtils;
import com.cloudcollection.utils.HtmlUtil;
import com.cloudcollection.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("collectService")
public class CollectServiceImpl extends CacheService implements CollectService {
	protected Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private CollectRepository collectRepository;
	@Autowired
	private FavoritesRepository favoritesRepository;
	@Autowired
	private FavoritesService favoritesService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private NoticeService noticeService;
	@Autowired
	private PraiseRepository praiseRepository;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private FollowRepository followRepository;

	/**
	 * 展示收藏列表
	 * @author neo
	 * @date 2016年8月24日
	 * @param type
	 * @param userId
	 * @param pageable
	 * @return
	 */
	@Override
	public List<CollectSummary> getCollects(String type, Long userId, Pageable pageable,Long favoritesId,Long specUserId) {
		// TODO Auto-generated method stub
		Page<CollectView> views = null;
		if ("my".equals(type)) {
			List<Long> userIds=followRepository.findMyFollowIdByUserId(userId);
			if(userIds==null || userIds.size()==0){
				views = collectRepository.findViewByUserId(userId, pageable);
			}else{
				views = collectRepository.findViewByUserIdAndFollows(userId, userIds, pageable);
			}
		}else if("myself".equals(type)){
			views = collectRepository.findViewByUserId(userId, pageable);
		} else if ("explore".equals(type)) {
			views = collectRepository.findExploreView(userId,pageable);
		} else if("others".equals(type)){
			views = collectRepository.findViewByUserIdAndType(userId, pageable, CollectType.PUBLIC);
			if(null != specUserId){
				userId = specUserId;
			}
		} else if("otherpublic".equals(type)){
			views = collectRepository.findViewByUserIdAndTypeAndFavoritesId(userId, pageable, CollectType.PUBLIC, favoritesId);
			if(null != specUserId){
				userId = specUserId;
			}
		} else if("garbage".equals(type)){
			views = collectRepository.findViewByUserIdAndIsDelete(userId, pageable);
		}else {
			views = collectRepository.findViewByFavoritesId(Long.parseLong(type), pageable);
		}
		return convertCollect(views,userId);
	}

	
	/**
	 * @author neo
	 * @date 2016年8月25日
	 * @param key
	 * @param userId
	 * @return
	 */
	@Override
	public List<CollectSummary> searchMy(Long userId,String key,Pageable pageable) {
		// TODO Auto-generated method stub
		Page<CollectView> views = collectRepository.searchMyByKey(userId,"%"+key+"%",pageable);
		return convertCollect(views,userId);
	}
	
	
	/**
	 * @author neo
	 * @date 2016年8月26日
	 * @param userId
	 * @param key
	 * @param pageable
	 * @return
	 */
	@Override
	public List<CollectSummary> searchOther(Long userId,String key,Pageable pageable) {
		// TODO Auto-generated method stub
		Page<CollectView> views = collectRepository.searchOtherByKey(userId, "%"+key+"%", pageable);
		return convertCollect(views,userId);
	}

	/**
	 * @author neo
	 * @date 2016年8月11日
	 * @param
	 * @return
	 */
	private List<CollectSummary> convertCollect(Page<CollectView> views,Long userId) {
		List<CollectSummary> summarys=new ArrayList<CollectSummary>();
		for (CollectView view : views) {
			CollectSummary summary=new CollectSummary(view);
			summary.setPraiseCount(praiseRepository.countByCollectId(view.getId()));
			summary.setCommentCount(commentRepository.countByCollectId(view.getId()));
			Praise praise=praiseRepository.findByUserIdAndCollectId(userId, view.getId());
			if(praise!=null){
				summary.setPraise(true);
			}else{
				summary.setPraise(false);
			}
			summary.setCollectTime(DateUtils.getTimeFormatText(view.getCreateTime()));
			summarys.add(summary);
		}
		return summarys;
	}

	/**
	 * 收藏文章
	 * @param collect
	 * @param
	 */
	@Transactional
	public void saveCollect(Collect collect) {
		if(StringUtils.isNotBlank(collect.getNewFavorites())){
			collect.setFavoritesId(createfavorites(collect.getNewFavorites(), collect.getUserId()));
		}else{
			favoritesRepository.increaseCountById(collect.getFavoritesId(), DateUtils.getCurrentTime());
		}
		if (collect.getType()==null) {
			collect.setType(CollectType.PUBLIC);
		}else{
			collect.setType(CollectType.PRIVATE);
		}
		if(StringUtils.isBlank(collect.getDescription())){
			collect.setDescription(collect.getTitle());
		}
		collect.setIsDelete(IsDelete.No);
		collect.setCreateTime(DateUtils.getCurrentDateTime());
		collect.setLastModifyTime(DateUtils.getCurrentDateTime());
		collectRepository.save(collect);
		noticeFriends(collect);
	}
	
	/**
	 *  修改文章
	 * @author neo
	 * @date 2016年8月24日
	 * @param newCollect
	 */
	@Transactional
	public void updateCollect(Collect newCollect) {
		Collect collect=collectRepository.findById(newCollect.getId()).orElse(new Collect());
		if(StringUtils.isNotBlank(newCollect.getNewFavorites())){
			collect.setFavoritesId(createfavorites(newCollect.getNewFavorites(), collect.getUserId()));
		}else if(!collect.getFavoritesId().equals(newCollect.getFavoritesId()) && !IsDelete.YES.equals(collect.getIsDelete())){
			favoritesRepository.reduceCountById(collect.getFavoritesId(), DateUtils.getCurrentTime());
			favoritesRepository.increaseCountById(newCollect.getFavoritesId(), DateUtils.getCurrentTime());
			collect.setFavoritesId(newCollect.getFavoritesId());
		}
		if(IsDelete.YES.equals(collect.getIsDelete())){
			collect.setIsDelete(IsDelete.No);
			if(StringUtils.isBlank(newCollect.getNewFavorites())){
				favoritesRepository.increaseCountById(newCollect.getFavoritesId(), DateUtils.getCurrentTime());
				collect.setFavoritesId(newCollect.getFavoritesId());
			}
		}
		if (newCollect.getType()==null) {
			collect.setType(CollectType.PUBLIC);
		}else{
		  collect.setType(CollectType.PRIVATE);		}
		collect.setTitle(newCollect.getTitle());
		collect.setDescription(newCollect.getDescription());
		collect.setLogoUrl(newCollect.getLogoUrl());
		collect.setRemark(newCollect.getRemark());
		collect.setLastModifyTime(DateUtils.getCurrentDateTime());
		collectRepository.save(collect);
		noticeFriends(collect);
	}
	
	
	/**
	 * 收藏别人的文章
	 * @author lz
	 * @date 2016年8月31日
	 * @param collect
	 * @param
	 */
	@Transactional
	public void otherCollect(Collect collect) {
		Collect other=collectRepository.findById(collect.getId()).orElse(new Collect());
		//收藏别人文章默认给点赞
		collectLike(collect.getUserId(),other.getId());
		if(StringUtils.isNotBlank(collect.getNewFavorites())){
			collect.setFavoritesId(createfavorites(collect.getNewFavorites(), collect.getUserId()));
		}else{
			favoritesRepository.increaseCountById(collect.getFavoritesId(), DateUtils.getCurrentTime());
		}
		collect.setId(null);
		collect.setIsDelete(IsDelete.No);
		if (collect.getType()==null) {
			collect.setType(CollectType.PUBLIC);
		}else{
			collect.setType(CollectType.PRIVATE);
		}
		if(StringUtils.isBlank(collect.getDescription())){
			collect.setDescription(collect.getTitle());
		}
		collect.setUrl(other.getUrl());
		collect.setLastModifyTime(DateUtils.getCurrentDateTime());
		collect.setCreateTime(DateUtils.getCurrentDateTime());
		collectRepository.save(collect);
		noticeFriends(collect);
	}
	
	/**
	 * 验证是否重复收藏
	 * @param collect
	 * @param
	 * @return
	 */
	public boolean checkCollect(Collect collect){
		if(StringUtils.isNotBlank(collect.getNewFavorites())){
			// url+favoritesId+userId
			Favorites favorites = favoritesRepository.findByUserIdAndName(collect.getUserId(), collect.getNewFavorites());
			if(null == favorites){
				return true;
			}else{
				List<Collect> list = collectRepository.findByFavoritesIdAndUrlAndUserIdAndIsDelete(favorites.getId(), collect.getUrl(), collect.getUserId(), IsDelete.No);
				if(null != list && list.size() > 0){
					return false;
				}else{
					return true;
				}
			}
		}else{
			if(collect.getId() != null){
				Collect c = collectRepository.findById(collect.getId()).orElse(new Collect());
				if(c.getFavoritesId().longValue() == collect.getFavoritesId().longValue()){
					return true;
				}else{
					List<Collect> list = collectRepository.findByFavoritesIdAndUrlAndUserIdAndIsDelete(collect.getFavoritesId(), collect.getUrl(), collect.getUserId(),IsDelete.No);
					if(null != list && list.size() > 0){
						return false;
					}else{
						return true;
					}
				}
			}else{
				List<Collect> list = collectRepository.findByFavoritesIdAndUrlAndUserIdAndIsDelete(collect.getFavoritesId(), collect.getUrl(), collect.getUserId(),IsDelete.No);
				if(null != list && list.size() > 0){
					return false;
				}else{
					return true;
				}
			}
		}
	}
	
	/**
	 * 导入收藏文章
	 */
	public void importHtml(Map<String, String> map,Long favoritesId,Long userId,String type){
		for(Map.Entry<String, String> entry : map.entrySet()){
			try {
				Map<String, String> result = HtmlUtil.getCollectFromUrl(entry.getKey());
				Collect collect = new Collect();
				collect.setCharset(result.get("charset"));
				if(StringUtils.isBlank(result.get("title"))){
					collect.setTitle(entry.getValue());
				}else{
					collect.setTitle(result.get("title"));
				}
				if(StringUtils.isBlank(result.get("description"))){
					collect.setDescription(collect.getTitle());
				}else{
					collect.setDescription(result.get("description"));
				}
				collect.setRemark(entry.getValue());
				collect.setFavoritesId(favoritesId);
				collect.setIsDelete(IsDelete.No);
				collect.setLogoUrl(getMap(entry.getKey()));
				if(CollectType.PRIVATE.toString().equals(type)){
					collect.setType(CollectType.PRIVATE);
				}else{
					collect.setType(CollectType.PUBLIC);
				}
				collect.setUrl(entry.getKey());
				collect.setUserId(userId);
				collect.setCreateTime(DateUtils.getCurrentDateTime());
				collect.setLastModifyTime(DateUtils.getCurrentDateTime());
				List<Collect> list = collectRepository.findByFavoritesIdAndUrlAndUserIdAndIsDelete(favoritesId, entry.getKey(), userId,IsDelete.No);
				if(null != list && list.size() > 0){
					logger.info("收藏夹：" + favoritesId + "中已经存在：" + entry.getKey() + "这个文章，不在进行导入操作");
					continue;
				}
				collectRepository.save(collect);
				favoritesRepository.increaseCountById(favoritesId, DateUtils.getCurrentTime());
			} catch (Exception e) {
				logger.error("导入存储异常：",e);
			}
		}
		
	}
	
	/**
	 * 导出到html文件
	 * @param favoritesId
	 */
	public StringBuilder exportToHtml(Long favoritesId){
		try {
			Favorites favorites = favoritesRepository.findById(favoritesId).orElse(new Favorites());
			StringBuilder sb = new StringBuilder();
			List<Collect> collects = collectRepository.findByFavoritesIdAndIsDelete(favoritesId,IsDelete.No);
			StringBuilder sbc = new StringBuilder();
			for(Collect collect : collects){
				sbc.append("<DT><A HREF=\""+collect.getUrl()+"\" TARGET=\"_blank\">"+collect.getTitle()+"</A></DT>");
			}
			sb.append("<DL><P></P><DT><H3>"+favorites.getName()+"</H3><DL><P></P>"+sbc+"</DL></DT></DL>");
			return sb;
		} catch (Exception e) {
			logger.error("异常：",e);
		}
		return null;
	}

	
	/**
	 * @author neo
	 * @date 2016年9月8日
	 * @param fname
	 * @param userId
	 * @return
	 */
	private Long  createfavorites(String fname,Long userId){
		Favorites favorites = favoritesRepository.findByUserIdAndName(userId, fname);
		if (null == favorites) {
			favorites = favoritesService.saveFavorites(userId, 1l,fname);
		} else {
			favoritesRepository.increaseCountById(favorites.getId(),DateUtils.getCurrentTime());
		}
		return favorites.getId();
	
	}
	
	
	
	/**
	 * @通知好友
	 * @author neo
	 * @date 2016年8月24日
	 * @param collect
	 */
	private void noticeFriends(Collect collect){
		if (StringUtils.isNotBlank(collect.getRemark())&& collect.getRemark().indexOf("@") > -1) {
			List<String> atUsers = StringUtil.getAtUser(collect.getRemark());
			for (String str : atUsers) {
				logger.info("用户名：" + str);
				User user = userRepository.findByUserName(str);
				if (null != user) {
					// 保存消息通知
					noticeService.saveNotice(String.valueOf(collect.getId()),"at", user.getId(), null);
				} else {
					logger.info("为找到匹配：" + str + "的用户.");
				}
			}
		}
	}


	@Override
	public void like(Long userId,long id) {
		Praise praise=praiseRepository.findByUserIdAndCollectId(userId, id);
		if(praise==null){
			Praise newPraise=new Praise();
			newPraise.setUserId(userId);
			newPraise.setCollectId(id);
			newPraise.setCreateTime(DateUtils.getCurrentDateTime());
			praiseRepository.save(newPraise);
			// 保存消息通知
			Collect collect = collectRepository.findById(id).orElse(new Collect());
			if(null != collect){
				noticeService.saveNotice(String.valueOf(id), "praise", collect.getUserId(), String.valueOf(newPraise.getId()));
			}
		}else if(praise.getUserId().equals(userId)){
			praiseRepository.deleteById(praise.getId());
		}
	}

	/**
	 * 收藏文章默认点赞
	 * @param userId
	 * @param id
	 */
	public void collectLike(Long userId,long id) {
		Praise praise=praiseRepository.findByUserIdAndCollectId(userId, id);
		if(praise==null){
			Praise newPraise=new Praise();
			newPraise.setUserId(userId);
			newPraise.setCollectId(id);
			newPraise.setCreateTime(DateUtils.getCurrentDateTime());
			praiseRepository.save(newPraise);
			// 保存消息通知
			Collect collect = collectRepository.findById(id).orElse(new Collect());
			if(null != collect){
				noticeService.saveNotice(String.valueOf(id), "praise", collect.getUserId(), String.valueOf(newPraise.getId()));
			}
		}
	}

}