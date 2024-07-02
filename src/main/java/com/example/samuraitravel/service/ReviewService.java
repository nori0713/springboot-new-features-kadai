package com.example.samuraitravel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.form.ReviewForm;
import com.example.samuraitravel.repository.ReviewRepository;

@Service
public class ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;

	@Transactional(readOnly = true)
	public Page<Review> getReviewsByHouseIdWithPagination(Integer houseId, Pageable pageable) {
		Page<Review> reviews = reviewRepository.findByHouseId(houseId, pageable);
		reviews.forEach(review -> {
			review.getHouse().getName(); // Force fetch house
		});
		return reviews;
	}

	public List<Review> getReviewsByHouseId(Integer houseId) {
		return reviewRepository.findByHouseId(houseId);
	}

	public List<Review> getReviewByUserId(Integer userId) {
		return reviewRepository.findByUserId(userId);
	}

	public Review getReviewById(Integer id) {
        return reviewRepository.findById(id).orElse(null);
    }

	@Transactional
	public void updateReview(Integer id, ReviewForm reviewForm) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            review.setReviewText(reviewForm.getReviewText());
            review.setReviewStar(reviewForm.getReviewStar());
            reviewRepository.save(review);
        }
    }

	@Transactional
	public void deleteReview(Integer id) {
		reviewRepository.deleteById(id);
	}

	@Transactional
	public void createReview(Review review) {
		reviewRepository.save(review);
	}
}