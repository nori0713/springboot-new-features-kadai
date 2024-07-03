package com.example.samuraitravel.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.form.ReservationInputForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.HouseService;
import com.example.samuraitravel.service.ReviewService;

@Controller
@RequestMapping("/houses")
public class HouseController {
    private final HouseRepository houseRepository;
    private final ReviewService reviewService;
    private final HouseService houseService;
    
    @Autowired
    public HouseController(HouseRepository houseRepository, ReviewService reviewService, HouseService houseService) {
        this.houseRepository = houseRepository;
        this.reviewService = reviewService;
        this.houseService = houseService;
    }

    @GetMapping("/{id}")
    public String showHouse(@PathVariable Integer id, Model model, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        House house = houseService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid house Id:" + id));
        model.addAttribute("house", house);

        List<Review> reviews = reviewService.findReviewsByHouseId(id);
        model.addAttribute("reviews", reviews);

        // reservationInputFormをモデルに追加
        model.addAttribute("reservationInputForm", new ReservationInputForm());

        // ログインユーザーのレビューの存在を確認
        if (userDetailsImpl != null) {
        	Integer userId = userDetailsImpl.getId(); // ユーザーIDを取得
            boolean hasReviewed = reviewService.hasUserReviewedHouse(userId, id);
            model.addAttribute("hasReviewed", hasReviewed);
        } else {
            model.addAttribute("hasReviewed", false);
        }

        return "houses/show";
    }

    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
                        @RequestParam(name = "area", required = false) String area,
                        @RequestParam(name = "price", required = false) Integer price,
                        @RequestParam(name = "order", required = false) String order,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
                        Model model) {
        Page<House> housePage;

        if (keyword != null && !keyword.isEmpty()) {
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findByNameLikeOrAddressLikeOrderByPriceAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
            } else {
                housePage = houseRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
            }
        } else if (area != null && !area.isEmpty()) {
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findByAddressLikeOrderByPriceAsc("%" + area + "%", pageable);
            } else {
                housePage = houseRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
            }
        } else if (price != null) {
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
            } else {
                housePage = houseRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
            }
        } else {
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findAllByOrderByPriceAsc(pageable);
            } else {
                housePage = houseRepository.findAllByOrderByCreatedAtDesc(pageable);
            }
        }

        model.addAttribute("housePage", housePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("area", area);
        model.addAttribute("price", price);
        model.addAttribute("order", order);

        return "houses/index";
    }
}