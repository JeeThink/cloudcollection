package com.cloudcollection.web;

import com.cloudcollection.common.aop.LoggerManage;
import com.cloudcollection.domain.Config;
import com.cloudcollection.domain.Favorites;
import com.cloudcollection.domain.enums.CollectType;
import com.cloudcollection.domain.enums.ExceptionMsg;
import com.cloudcollection.domain.enums.IsDelete;
import com.cloudcollection.domain.result.Response;
import com.cloudcollection.domain.result.ResponseData;
import com.cloudcollection.repository.CollectRepository;
import com.cloudcollection.repository.ConfigRepository;
import com.cloudcollection.repository.FavoritesRepository;
import com.cloudcollection.service.FavoritesService;
import com.cloudcollection.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoritesController extends BaseController{
	
	@Autowired
	private FavoritesRepository favoritesRepository;
	@Resource
	private FavoritesService favoritesService;
	@Autowired
	private CollectRepository collectRepository;
	@Autowired
	private ConfigRepository configRespository;
	
	/**
	 * 添加
	 * @param name
	 * @return
	 */
	@RequestMapping(value="/add",method=RequestMethod.POST)
	@LoggerManage(description="新建收藏夹")
	public Response addFavorites(String name){
		if(StringUtils.isNotBlank(name)){
			Favorites favorites = favoritesRepository.findByUserIdAndName(getUserId(), name);
			if(null != favorites){
				logger.info("收藏夹名称已被创建");
				return result(ExceptionMsg.FavoritesNameUsed);
			}else{
				try {
					favoritesService.saveFavorites(getUserId(), 0l, name);
				} catch (Exception e) {
					logger.error("异常：",e);
					return result(ExceptionMsg.FAILED);
				}
			}
		}else{
			logger.info("收藏夹名称为空");
			return result(ExceptionMsg.FavoritesNameIsNull);
		}
		return result();
	}
	
	/**
	 * 创建导入收藏夹
	 * @return
	 */
	@RequestMapping(value="/addImportFavorites",method=RequestMethod.POST)
	@LoggerManage(description="创建导入收藏夹")
	public ResponseData addImportFavorites(){
		Favorites favorites = favoritesRepository.findByUserIdAndName(getUserId(), "导入自浏览器");
		if(null == favorites){
			try {
				favorites = favoritesService.saveFavorites(getUserId(), 0l, "导入自浏览器");
			} catch (Exception e) {
				logger.error("异常：",e);				
			}
		}
		return new ResponseData(ExceptionMsg.SUCCESS, favorites.getId());
	}
	
	/**
	 * 修改
	 * @param favoritesName
	 * @param favoritesId
	 * @return
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	@LoggerManage(description="修改收藏夹")
	public Response updateFavorites(String favoritesName,Long favoritesId){
		if(StringUtils.isNotBlank(favoritesName)&& null != favoritesId){
			Favorites fav = favoritesRepository.findById(favoritesId).orElse(new Favorites());
			if(null != fav && getUserId().longValue() == fav.getUserId().longValue()){
				Favorites favorites = favoritesRepository.findByUserIdAndName(getUserId(), favoritesName);
				if(null != favorites){
					logger.info("收藏夹名称已被创建");
					return result(ExceptionMsg.FavoritesNameUsed);
				}else{
					try {
						favoritesRepository.updateNameById(favoritesId, DateUtils.getCurrentTime(), favoritesName);
					} catch (Exception e) {
						logger.error("修改收藏夹名称异常：",e);
					}
				}
			}
		}else{
			logger.info("参数错误name:" + favoritesName +"----" + "id:" + favoritesId);
			return result(ExceptionMsg.FAILED);
		}
		return result();
	}
	
	/**
	 * 删除
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/del",method=RequestMethod.POST)
	@LoggerManage(description="删除收藏夹")
	public Response delFavorites(Long id){
		if(null == id){
			return result(ExceptionMsg.FAILED);
		}
		try {
			Favorites fav = favoritesRepository.findById(id).orElse(new Favorites());
			if(null != fav && getUserId().longValue() == fav.getUserId().longValue()){
				favoritesRepository.deleteById(id);
				// 删除该收藏夹下文章
				collectRepository.deleteByFavoritesId(id);
				Config config = configRespository.findByUserIdAndDefaultFavorties(getUserId(),String.valueOf(id));
				if(null != config){
					// 默认收藏夹被删除，设置“未读列表”为默认收藏夹
					Favorites favorites = favoritesRepository.findByUserIdAndName(getUserId(), "未读列表");
					if(null != favorites){
						configRespository.updateFavoritesById(config.getId(), String.valueOf(favorites.getId()), DateUtils.getCurrentTime());
					}
				}
			}
			
		} catch (Exception e) {
			logger.error("删除异常：",e);
		}
		return result();
	}
	
	/**
	 * 获取收藏夹
	 * @return
	 */
	@RequestMapping(value = "/getFavorites/{userId}", method = RequestMethod.POST)
	@LoggerManage(description="获取收藏夹")
	public List<Favorites> getFavorites(@PathVariable("userId") Long userId) {
		List<Favorites> favorites = null;
		try {
			Long id = getUserId();
			if(null != userId && 0 != userId){
				id = userId;
			}
			favorites = favoritesRepository.findByUserIdOrderByIdDesc(id);
			if(!getUserId().equals(userId)){
				for(Favorites favorites2 : favorites){
					favorites2.setPublicCount(collectRepository.countByFavoritesIdAndTypeAndIsDelete(favorites2.getId(), CollectType.PUBLIC,IsDelete.No));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("getFavorites failed, ", e);
		}
		logger.info("getFavorites end favorites ==" );
		return favorites;
	}

}
