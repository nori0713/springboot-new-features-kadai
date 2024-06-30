package com.example.samuraitravel.form;

import com.example.samuraitravel.entity.Review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewForm {
    
	private Integer id;
	
	@NotNull
	private Integer houseId;

	@NotBlank
	private String reviewText;

	@NotNull
	@Min(1)
	@Max(5)
	private Integer reviewStar;

    public ReviewForm() {}

    public ReviewForm(Review review) {
    	this.id = review.getId();
        this.houseId = review.getHouse().getId();
        this.reviewText = review.getReviewText();
        this.reviewStar = review.getReviewStar();
    }

    // ゲッターとセッター
    public Integer getHouseId() {
        return houseId;
    }

    public void setHouseId(Integer houseId) {
        this.houseId = houseId;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Integer getReviewStar() {
        return reviewStar;
    }

    public void setReviewStar(Integer reviewStar) {
        this.reviewStar = reviewStar;
    }
}