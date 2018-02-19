package com.example.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.popularmovies.data.Video;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosAdapterViewHolder> {

    private List<Video> mVideosData;
    final private VideosAdapterOnClickHandler mClickHandler;

    public interface VideosAdapterOnClickHandler {
        void onClick(Video video);
    }

    public VideosAdapter(VideosAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class VideosAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final TextView mTitleTv;

        public VideosAdapterViewHolder(View view) {
            super(view);
            mTitleTv = view.findViewById(R.id.title_tv);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Video video = mVideosData.get(adapterPosition);
            mClickHandler.onClick(video);
        }

        public void bindToData(Video video) {
            mTitleTv.setText(video.getName());
        }
    }

    @Override
    public VideosAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_video, viewGroup, false);
        return new VideosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideosAdapterViewHolder videosAdapterViewHolder, int position) {
        Video video = mVideosData.get(position);
        videosAdapterViewHolder.bindToData(video);
    }

    @Override
    public int getItemCount() {
        if (mVideosData == null)
            return 0;

        return mVideosData.size();
    }

    public void setVideosData(List<Video> videosData) {
        this.mVideosData = videosData;
        notifyDataSetChanged();
    }
}