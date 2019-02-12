package com.cloudcollection.service.impl;

import com.cloudcollection.domain.Config;
import com.cloudcollection.repository.ConfigRepository;
import com.cloudcollection.service.ConfigService;
import com.cloudcollection.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("configService")
public class ConfigServiceImpl implements ConfigService{
	
	@Autowired
	private ConfigRepository configRepository;
	
	/**
	 * 保存属性设置
	 * @param userId
	 * @param favoritesId
	 * @return
	 */
	public Config saveConfig(Long userId,String favoritesId){
		Config config = new Config();
		config.setUserId(userId);
		config.setDefaultModel("simple");
		config.setDefaultFavorties(favoritesId);
		config.setDefaultCollectType("public");
		config.setCreateTime(DateUtils.getCurrentDateTime());
		config.setLastModifyTime(DateUtils.getCurrentDateTime());
		configRepository.save(config);
		return config;
	}
	
	/**
	 * 属性修改
	 * @param id
	 * @param type
	 */
	public void updateConfig(Long id ,String type,String defaultFavorites){
		Config config = configRepository.findOne(id);
		String value="";
		if("defaultCollectType".equals(type)){
			if("public".equals(config.getDefaultCollectType())){
				value = "private";
			}else{
				value = "public";
			}
			configRepository.updateCollectTypeById(id, value, DateUtils.getCurrentTime());
		}else if("defaultModel".equals(type)){
			if("simple".equals(config.getDefaultModel())){
				value = "major";
			}else{
				value="simple";
			}
			configRepository.updateModelTypeById(id, value, DateUtils.getCurrentTime());
		}else if("defaultFavorites".equals(type)){
			configRepository.updateFavoritesById(id, defaultFavorites, DateUtils.getCurrentTime());
		}
		
	}

}
