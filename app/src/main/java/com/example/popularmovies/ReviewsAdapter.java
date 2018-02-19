package com.example.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.popularmovies.data.Review;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    private List<Review> mReviewsData;

    public ReviewsAdapter() {
    }

    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView mAuthorTv;
        public final TextView mContentTv;

        public ReviewsAdapterViewHolder(View view) {
            super(view);
            mAuthorTv = view.findViewById(R.id.author_tv);
            mContentTv = view.findViewById(R.id.content_tv);
        }

        public void bindToData(Review review) {
            mAuthorTv.setText(review.getAuthor());
            mContentTv.setText(review.getContent());
        }
    }

    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_review, viewGroup, false);
        return new ReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapterViewHolder reviewsAdapterViewHolder, int position) {
        Review review = mReviewsData.get(position);
        reviewsAdapterViewHolder.bindToData(review);
    }

    @Override
    public int getItemCount() {
        if (mReviewsData == null)
            return 0;

        return mReviewsData.size();
    }

    public void setReviewsData(List<Review> reviewsData) {
        this.mReviewsData = reviewsData;
        notifyDataSetChanged();
    }
}