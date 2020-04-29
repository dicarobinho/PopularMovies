package com.example.popularmovies.ui.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.popularmovies.R;
import com.example.popularmovies.databinding.AdapterItemReviewsBinding;
import com.example.popularmovies.model.Review;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final ReviewAdapter.ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void reviewOnItemClickListener(String urlToReview);
    }

    private List<Review> mReviews = new ArrayList<Review>() {
    };

    public ReviewAdapter(ReviewAdapter.ListItemClickListener listener) {
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_reviews, parent, false);
        return new ReviewAdapter.ViewHolder(view);
    }

    public void setReviews(List<Review> reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }

//    public List<Review> getReviews() {
//        return mReviews;
//    }

    @Override
    public int getItemCount() {
        return mReviews != null ? mReviews.size() : 0;
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewAdapter.ViewHolder holder, final int position) {
        Review currentReview = mReviews.get(position);
        holder.binding.author.setText(currentReview.getAuthor());
        holder.binding.content.setText(currentReview.getContent());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AdapterItemReviewsBinding binding;

        ViewHolder(View itemView) {
            super(itemView);
            binding = AdapterItemReviewsBinding.bind(itemView);
            binding.readMoreIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int viewIndex = getAdapterPosition();
            mOnClickListener.reviewOnItemClickListener(mReviews.get(viewIndex).getUrl());
        }
    }
}

