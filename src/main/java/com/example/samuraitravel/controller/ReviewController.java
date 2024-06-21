package com.example.samuraitravel.controller;


import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReviewForm;
import com.example.samuraitravel.service.HouseService;
import com.example.samuraitravel.service.ReviewService;
import com.example.samuraitravel.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private HouseService houseService;

	@Autowired
	private UserService userService;

	// 宿泊施設ごとのレビューを取得する
	@GetMapping("/house/{houseId}")
	public String getReviewsByHouseId(@PathVariable Integer houseId,
			@RequestParam(defaultValue = "0") int page,
			Model model) {
		Pageable pageable = PageRequest.of(page, 10);
		Page<Review> reviewPage = reviewService.getReviewsByHouseIdWithPagination(houseId, pageable);
		House house = houseService.getHouseById(houseId);

		model.addAttribute("reviewPage", reviewPage);
		model.addAttribute("house", house);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", reviewPage.getTotalPages());

		return "reviews/list";
	}

	// レビュー作成フォームを表示する
	@GetMapping("/house/{houseId}/newReview")
	public String showCreateForm(@PathVariable Integer houseId, Model model) {
		House house = houseService.getHouseById(houseId);
		ReviewForm reviewForm = new ReviewForm();
		reviewForm.setHouseId(houseId);
		model.addAttribute("reviewForm", reviewForm);
		model.addAttribute("house", house);
		return "reviews/createReview";
	}

	// クラスの先頭にLoggerを追加
	private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

	// 新しいレビューを作成する
	@PostMapping("/house/{houseId}")
	public String createReview(@PathVariable Integer houseId,
			@ModelAttribute @Valid ReviewForm reviewForm,
			BindingResult result,
			Principal principal,
			Model model) {
		if (result.hasErrors()) {
			House house = houseService.getHouseById(houseId);
			model.addAttribute("house", house);
			return "reviews/createReview";
		}

		String username = principal.getName();
		logger.info("Logged in user: " + username);
		User user = userService.findByUsername(username);

//		if (user == null) {
//			logger.error("User not found: " + username);
//			// ログインユーザーが見つからない場合のエラーハンドリング
//			return "redirect:/login"; // もしくはエラーメッセージを表示
//		}

		Review review = new Review();
		review.setHouseId(reviewForm.getHouseId());
		review.setReviewText(reviewForm.getReviewText());
		review.setReviewStar(reviewForm.getReviewStar());
		review.setUser(user);
		reviewService.createReview(review);
		return "redirect:/reviews/house/" + houseId;
	}

	// レビュー編集フォームを表示する
	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable Integer id, Model model) {
		Review review = reviewService.getReviewById(id);
		model.addAttribute("review", review);
		return "editReview";
	}

	// レビューを更新する
	@PostMapping("/update/{id}")
	public String updateReview(@PathVariable Integer id, @ModelAttribute Review updatedReview) {
		reviewService.updateReview(id, updatedReview);
		return "redirect:/reviews/house/" + updatedReview.getHouseId();
	}

	// レビューを削除する
	@PostMapping("/delete/{id}")
	public String deleteReview(@PathVariable Integer id) {
		Review review = reviewService.getReviewById(id);
		Integer houseId = review.getHouseId();
		reviewService.deleteReview(id);
		return "redirect:/reviews/house/" + houseId;
	}

	// コンストラクタ
	public ReviewController(ReviewService reviewService, HouseService houseService, UserService userService) {
		this.reviewService = reviewService;
		this.houseService = houseService;
		this.userService = userService;
	}
}