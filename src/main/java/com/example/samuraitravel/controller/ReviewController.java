package com.example.samuraitravel.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.example.samuraitravel.security.UserDetailsImpl;
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
	                                  Model model,
	                                  @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
	    Pageable pageable = PageRequest.of(page, 10);
	    Page<Review> reviewPage = reviewService.getReviewsByHouseIdWithPagination(houseId, pageable);
	    House house = houseService.getHouseById(houseId);

	    model.addAttribute("reviewPage", reviewPage);
	    model.addAttribute("house", house);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", reviewPage.getTotalPages());
	    model.addAttribute("currentUser", userDetailsImpl.getUser()); // 追加

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

//	// クラスの先頭にLoggerを追加
//	private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

	// 新しいレビューを作成する（修正後）
	@PostMapping("/house/{houseId}")
	public String createReview(@PathVariable Integer houseId,
	                           @ModelAttribute @Valid ReviewForm reviewForm,
	                           BindingResult result,
	                           @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
	                           Model model) {
	    if (result.hasErrors()) {
	        House house = houseService.getHouseById(houseId);
	        model.addAttribute("house", house);
	        return "reviews/createReview";
	    }

	    User user = userDetailsImpl.getUser();
//	    logger.info("Logged in user: " + user.getUsername());

//		if (user == null) {
//			logger.error("User not found: " + username);
//			// ログインユーザーが見つからない場合のエラーハンドリング
//			return "redirect:/login"; // もしくはエラーメッセージを表示
//		}

		// ReviewFormからReviewエンティティに変換して保存
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
	public String showEditForm(@PathVariable Integer id, Model model, Principal principal) {
	    Review review = reviewService.getReviewById(id);
	    if (review == null) {
	        return "redirect:/reviews/house"; // レビューが見つからない場合、リダイレクト
	    }
	    if (!review.getUser().getUsername().equals(principal.getName())) {
	        return "redirect:/reviews/house/" + review.getHouse().getId(); // 編集権限がない場合、民宿詳細ページにリダイレクト
	    }
	    model.addAttribute("reviewForm", new ReviewForm(review));
	    model.addAttribute("house", review.getHouse());
	    return "reviews/editReview";
	}

 // レビューを更新する
    @PostMapping("/update/{id}")
    public String updateReview(@PathVariable Integer id, @ModelAttribute @Valid ReviewForm reviewForm, BindingResult result, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("house", reviewService.getReviewById(id).getHouse());
            return "reviews/editReview";
        }
        User user = userDetailsImpl.getUser();
        Review review = reviewService.getReviewById(id);
        if (!review.getUser().getId().equals(user.getId())) {
            return "redirect:/reviews/house/" + review.getHouse().getId();
        }
        reviewService.updateReview(id, reviewForm);
        return "redirect:/reviews/house/" + review.getHouse().getId();
    }

    // レビューを削除する
    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable Integer id, Principal principal) {
        Review review = reviewService.getReviewById(id);
        String username = principal.getName();
        if (!review.getUser().getUsername().equals(username)) {
            return "redirect:/reviews/house/" + review.getHouse().getId();
        }
        reviewService.deleteReview(id);
        return "redirect:/reviews/house/" + review.getHouse().getId();
    }

	// コンストラクタ
	public ReviewController(ReviewService reviewService, HouseService houseService, UserService userService) {
		this.reviewService = reviewService;
		this.houseService = houseService;
		this.userService = userService;
	}
}