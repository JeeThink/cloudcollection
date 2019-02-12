package com.cloudcollection.service;

import com.cloudcollection.domain.Favorites;

public interface FavoritesService {
	
	public Favorites saveFavorites(Long userId,Long count,String name);

}
