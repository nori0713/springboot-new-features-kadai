package com.example.samuraitravel.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReviewForm {
    
    @NotNull
    private Integer houseId;

    @NotBlank
    private String reviewText;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer reviewStar;

    // Getters and Setters
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