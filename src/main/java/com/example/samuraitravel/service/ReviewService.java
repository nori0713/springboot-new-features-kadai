package com.example.samuraitravel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.repository.ReviewRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    // 宿泊施設IDに基づいてレビューを取得するメソッド（ページネーション対応）
    public Page<Review> getReviewsByHouseIdWithPagination(Integer houseId, Pageable pageable) {
        return reviewRepository.findByHouseId(houseId, pageable);
    }

    // 宿泊施設IDに基づいてレビューを取得するメソッド（ページネーションなし）
    public List<Review> getReviewsByHouseId(Integer houseId) {
        return reviewRepository.findByHouseId(houseId);
    }

    // ユーザーIDに基づいてレビューを取得するメソッド
    public List<Review> getReviewByUserId(Integer userId) {
        return reviewRepository.findByUserId(userId);
    }

    // レビューIDに基づいて特定のレビューを取得するメソッド
    public Review getReviewById(Integer id) {
        Optional<Review> review = reviewRepository.findById(id);
        return review.orElse(null);
    }

    // レビューを更新するメソッド
    @Transactional
    public void updateReview(Integer id, Review updatedReview) {
        Optional<Review> existingReview = reviewRepository.findById(id);
        if (existingReview.isPresent()) {
            Review review = existingReview.get();
            review.setReviewText(updatedReview.getReviewText());
            review.setReviewStar(updatedReview.getReviewStar());
            reviewRepository.save(review);
        }
    }

    // レビューを削除するメソッド
    @Transactional
    public void deleteReview(Integer id) {
        reviewRepository.deleteById(id);
    }

    // 新しいレビューを作成するメソッド
    @Transactional
    public void createReview(Review review) {
        reviewRepository.save(review);
    }
}