package com.example.samuraitravel.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table (name = "review")
@Data

public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "house_id", insertable = false, updatable = false)
    private Integer houseId; // 重複するカラムに insertable と updatable を false に設定
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id", nullable = false)
    private House accommodation; // フィールド名を変更
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
	
	@Column(name = "review_text")
	private String reviewText;
	
	@Column(name = "review_star")
	private Integer reviewStar;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;
	
	@Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;
	
	@Override
	public String toString() {
	    return "Review{" +
	            "id=" + id +
	            ", user=" + (user != null ? user.getUsername() : "null") +
	            ", reviewText='" + reviewText + '\'' +
	            ", reviewStar=" + reviewStar +
	            ", createdAt=" + createdAt +
	            ", updatedAt=" + updatedAt +
	            '}';
	}
	
	// ゲッターとセッター
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public House getHouse() {
        return accommodation;
    }

    public void setHouse(House house) {
        this.accommodation = house;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
