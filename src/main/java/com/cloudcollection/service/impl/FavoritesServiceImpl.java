package com.cloudcollection.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloudcollection.domain.Favorites;
import com.cloudcollection.repository.FavoritesRepository;
import com.cloudcollection.service.FavoritesService;
import com.cloudcollection.utils.DateUtils;

@Service("favoritesService")
public class FavoritesServiceImpl implements FavoritesService{
	
	@Autowired
	private FavoritesRepository favoritesRepository;
	
	/**
	 * 保存
	 * @param userId
	 * @param count
	 * @param name
	 * @return
	 */
	public Favorites saveFavorites(Long userId,Long count,String name){
		Favorites favorites = new Favorites();
		favorites.setName(name);
		favorites.setUserId(userId);
		favorites.setCount(count);
		favorites.setCreateTime(DateUtils.getCurrentDateTime());
		favorites.setLastModifyTime(DateUtils.getCurrentDateTime());
		favoritesRepository.save(favorites);
		return favorites;
	}

}
