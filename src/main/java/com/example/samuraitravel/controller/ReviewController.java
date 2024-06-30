package com.example.samuraitravel.controller;

import java.security.Principal;

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
        model.addAttribute("currentUser", userDetailsImpl.getUser());

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

    @GetMapping("/reviews/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model, Principal principal) {
        Review review = reviewService.getReviewById(id);
        if (review == null) {
            return "redirect:/reviews/house";
        }

        User reviewUser = review.getUser();
        if (reviewUser == null || !reviewUser.getUsername().equals(principal.getName())) {
            return "redirect:/reviews/house/" + review.getHouse().getId();
        }

        model.addAttribute("reviewForm", new ReviewForm(review));
        model.addAttribute("house", review.getHouse());
        return "reviews/editReview";
    }

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