package com.ad_victoriam.libtex.user.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.models.Review;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private final FragmentActivity activity;
    private FirebaseUser currentUser;

    private final List<Review> reviews;

    public ReviewsAdapter(FragmentActivity activity, List<Review> reviews) {
        this.activity = activity;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewsAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_review, parent, false);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return new ReviewsAdapter.ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapter.ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.ratingBarUser.setProgress((int) (review.getRating() * 2));
        if (review.getDescription().isEmpty()) {
            holder.tUserReviewDescription.setVisibility(View.GONE);
        } else {
            holder.tUserReviewDescription.setText(review.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardViewReview;
        MaterialRatingBar ratingBarUser;
        TextView tUserReviewDescription;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewReview = itemView.findViewById(R.id.cardViewReview);
            ratingBarUser = itemView.findViewById(R.id.ratingBarUser);
            tUserReviewDescription = itemView.findViewById(R.id.tUserReviewDescription);
        }
    }
}
