package com.example.samuraitravel.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.samuraitravel.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    // 宿泊施設IDに基づいてレビューを取得するメソッド
	Page<Review> findByHouseId(Integer houseId, Pageable pageable);
	
	List<Review> findByHouseId(Integer houseId);

    // ユーザーIDに基づいてレビューを取得するメソッド
    List<Review> findByUserId(Integer userId);

    // 宿泊施設IDに基づいて、ユーザー情報とともにレビューを取得するクエリ
    @Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.houseId = :houseId")
    List<Review> findReviewsByHouseIdWithUser(Integer houseId);
}