package com.example.popularmovies.ui.adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies.R;
import com.example.popularmovies.databinding.AdapterItemMovieBinding;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private final ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onItemClickListener(int viewIndex);
    }

    private List<Movie> mMovies = new ArrayList<Movie>() {
    };

    public MovieAdapter(ListItemClickListener listener) {
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_movie, parent, false);
        return new ViewHolder(view);
    }

    public void setMovies(List<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    public List<Movie> getMovies() {
        return mMovies;
    }

    @Override
    public int getItemCount() {
        return mMovies != null ? mMovies.size() : 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Movie currentMovie = mMovies.get(position);

        if (currentMovie.getThumbnail() != null && !currentMovie.getThumbnail().contains(Constants.JPEG))
            Picasso.get().load(Constants.IMAGE_BASE_URL + currentMovie.getThumbnail()).into(holder.binding.thumbnailMovie);
        else if (currentMovie.getThumbnail() == null)
            Picasso.get().load(Constants.NO_PICTURE_AVAILABLE_ICON).into(holder.binding.thumbnailMovie);
        else if (currentMovie.getThumbnail().contains(Constants.JPEG)) {
            Picasso.get().load("file://" + currentMovie.getThumbnail()).config(Bitmap.Config.RGB_565).fit().centerCrop().into(holder.binding.thumbnailMovie);
        }

        if (currentMovie.getVoteAverage() < 10.0)
            holder.binding.voteAverage.setText(currentMovie.getVoteAverage() + "");
        else holder.binding.voteAverage.setText("10");
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        AdapterItemMovieBinding binding;

        ViewHolder(View itemView) {
            super(itemView);
            binding = AdapterItemMovieBinding.bind(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int viewIndex = getAdapterPosition();
            mOnClickListener.onItemClickListener(viewIndex);
        }
    }
}
