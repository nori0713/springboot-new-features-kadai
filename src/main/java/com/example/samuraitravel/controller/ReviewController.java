package com.example.samuraitravel.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

	private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

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

	    boolean hasReviewed = false;
	    if (userDetailsImpl != null) {
	        model.addAttribute("currentUser", userDetailsImpl.getUser());
	        hasReviewed = reviewService.hasUserReviewedHouse(userDetailsImpl.getUser().getId(), houseId.longValue());
	    } else {
	        model.addAttribute("currentUser", null);
	    }

	    model.addAttribute("hasReviewed", hasReviewed);

	    return "reviews/list";
	}

	@GetMapping("/house/{houseId}/newReview")
	public String showCreateForm(@PathVariable Integer houseId, Model model) {
		House house = houseService.getHouseById(houseId);
		ReviewForm reviewForm = new ReviewForm();
		reviewForm.setHouseId(houseId);
		model.addAttribute("reviewForm", reviewForm);
		model.addAttribute("house", house);
		return "reviews/createReview";
	}

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

		Review review = new Review();
		review.setHouse(houseService.getHouseById(houseId));
		review.setReviewText(reviewForm.getReviewText());
		review.setReviewStar(reviewForm.getReviewStar());
		review.setUser(user);
		reviewService.createReview(review);
		return "redirect:/reviews/house/" + houseId;
	}

	@GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        if (userDetailsImpl == null) {
            return "redirect:/login"; // ログインページにリダイレクト
        }

        Review review = reviewService.getReviewById(id);
        if (review == null) {
            return "redirect:/reviews/house"; // レビューが見つからない場合、リダイレクト
        }

        User currentUser = userDetailsImpl.getUser();
        if (currentUser == null || review.getUser() == null || !review.getUser().getId().equals(currentUser.getId())) {
            return "redirect:/reviews/house/" + review.getHouse().getId(); // 編集権限がない場合、民宿詳細ページにリダイレクト
        }

        ReviewForm reviewForm = new ReviewForm(review);
        reviewForm.setHouseId(review.getHouse().getId()); // houseIdを設定する

        model.addAttribute("reviewForm", reviewForm);
        model.addAttribute("house", review.getHouse());
        return "reviews/editReview";
    }

    @PostMapping("/update/{id}")
    public String updateReview(@PathVariable Integer id, 
                               @ModelAttribute @Valid ReviewForm reviewForm,
                               BindingResult result, 
                               @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
                               Model model) {

        logger.info("Entering updateReview method with id: {}", id);

        if (result.hasErrors()) {
            logger.error("Validation errors: {}", result.getAllErrors());
            model.addAttribute("house", reviewService.getReviewById(id).getHouse());
            return "reviews/editReview";
        }

        User user = userDetailsImpl.getUser();
        Review review = reviewService.getReviewById(id);

        if (review == null) {
            logger.error("Review not found with id: {}", id);
            return "redirect:/reviews/house"; // 適切なリダイレクト先に変更してください
        }

        if (!review.getUser().getId().equals(user.getId())) {
            logger.warn("User does not have permission to update this review");
            return "redirect:/reviews/house/" + review.getHouse().getId();
        }

        logger.info("Updating review with id: {}", id);
        reviewService.updateReview(id, reviewForm);
        logger.info("Successfully updated review with id: {}", id);
        return "redirect:/reviews/house/" + review.getHouse().getId();
    }

	@GetMapping("/update/test/{id}")
	public String testUpdate(@PathVariable Integer id, Model model) {
		model.addAttribute("message", "Test successful for ID: " + id);
		logger.info("Test update method hit for ID: " + id);
		return "test"; // Ensure "test.html" exists in the templates folder
	}

	@GetMapping("/test")
	public String test(Model model) {
		model.addAttribute("message", "Test successful");
		return "test"; // test.htmlというテンプレートファイルを用意
	}

	@ControllerAdvice
	public class GlobalExceptionHandler {

		@ExceptionHandler(Exception.class)
		public String handleException(Exception e, Model model) {
			model.addAttribute("errorMessage", e.getMessage());
			return "error";
		}
	}

	@PostMapping("/delete/{id}")
	public String deleteReview(@PathVariable Integer id, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		User user = userDetailsImpl.getUser();
		Review review = reviewService.getReviewById(id);
		if (review.getUser().getId().equals(user.getId())) {
			reviewService.deleteReview(id);
		}
		return "redirect:/reviews/house/" + review.getHouse().getId();
	}
}