package com.example.popularmovies.ui.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.popularmovies.R;
import com.example.popularmovies.databinding.AdapterItemTrailersBinding;
import com.example.popularmovies.model.Video;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private final VideoAdapter.ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void videoOnItemClickListener(String keyToVideo);
    }

    private List<Video> mVideos = new ArrayList<Video>() {
    };

    public VideoAdapter(VideoAdapter.ListItemClickListener listener) {
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_trailers, parent, false);
        return new VideoAdapter.ViewHolder(view);
    }

    public void setVideos(List<Video> videos) {
        mVideos = videos;
        notifyDataSetChanged();
    }

//    public List<Video> getVideos() {
//        return mVideos;
//    }

    @Override
    public int getItemCount() {
        return mVideos != null ? mVideos.size() : 0;
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoAdapter.ViewHolder holder, final int position) {
        Video currentVideo = mVideos.get(position);
        holder.binding.videoName.setText(currentVideo.getName());

        String videoType = currentVideo.getSite() + ", " + currentVideo.getType();
        holder.binding.videoType.setText(videoType);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AdapterItemTrailersBinding binding;

        ViewHolder(View itemView) {
            super(itemView);
            binding = AdapterItemTrailersBinding.bind(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int viewIndex = getAdapterPosition();
            mOnClickListener.videoOnItemClickListener(mVideos.get(viewIndex).getKey());
        }
    }
}
