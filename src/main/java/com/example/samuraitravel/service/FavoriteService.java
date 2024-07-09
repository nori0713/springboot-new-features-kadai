package com.example.samuraitravel.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.FavoriteRepository;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    public Page<House> getFavoriteHouses(User user, Pageable pageable) {
        Page<Favorite> favoritePage = favoriteRepository.findByUser(user, pageable);
        List<House> houses = favoritePage.stream().map(Favorite::getHouse).collect(Collectors.toList());
        return new PageImpl<>(houses, pageable, favoritePage.getTotalElements());
    }

    public void addFavorite(User user, House house) {
        if (!isFavorite(user, house)) {
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setHouse(house);
            favoriteRepository.save(favorite);
        }
    }

    public void removeFavorite(User user, House house) {
        favoriteRepository.findUniqueFavorite(user, house).ifPresent(favoriteRepository::delete);
    }

    public boolean isFavorite(User user, House house) {
        return favoriteRepository.findUniqueFavorite(user, house).isPresent();
    }

    public Page<Favorite> getFavorites(User user, Pageable pageable) {
        return favoriteRepository.findByUser(user, pageable);
    }
}