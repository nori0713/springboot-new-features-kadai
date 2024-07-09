package com.example.samuraitravel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.FavoriteService;
import com.example.samuraitravel.service.HouseService;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private HouseService houseService;

    @GetMapping("/add/{houseId}")
    public String addFavorite(@PathVariable Integer houseId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        House house = houseService.getHouseById(houseId);
        favoriteService.addFavorite(user, house);
        return "redirect:/houses/" + houseId;
    }

    @GetMapping("/remove/{houseId}")
    public String removeFavorite(@PathVariable Integer houseId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        House house = houseService.getHouseById(houseId);
        favoriteService.removeFavorite(user, house);
        return "redirect:/houses/" + houseId;
    }

    @GetMapping
    public String getFavorites(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model,
                               @PageableDefault(size = 10, sort = "id", direction = Direction.ASC) Pageable pageable) {
        User user = userDetails.getUser();
        Page<Favorite> favoritePage = favoriteService.getFavorites(user, pageable);
        model.addAttribute("favoritePage", favoritePage);
        return "favorites/list";
    }

    @GetMapping("/houses/{houseId}")
    public String showHouse(@PathVariable Integer houseId, @AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        User user = userDetails.getUser();
        House house = houseService.getHouseById(houseId);
        boolean isFavorite = favoriteService.isFavorite(user, house);
        model.addAttribute("house", house);
        model.addAttribute("isFavorite", isFavorite);
        return "houses/show";
    }
}