package com.example.popularmovies;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.popularmovies.data.Movie;
import com.example.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private List<Movie> mMoviesData;
    final private MoviesAdapterOnClickHandler mClickHandler;

    public interface MoviesAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MoviesAdapter(MoviesAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final ImageView mPosterIv;
        public final TextView mTitleTv;

        public MoviesAdapterViewHolder(View view) {
            super(view);
            mPosterIv = view.findViewById(R.id.poster_iv);
            mTitleTv = view.findViewById(R.id.title_tv);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movie = mMoviesData.get(adapterPosition);
            mClickHandler.onClick(movie);
        }

        public void bindToData(Movie movie) {
            mTitleTv.setText(movie.getTitle());

            Uri moviePosterUri = NetworkUtils.buildMovieImageUri(movie.getPosterPath());
            Picasso.with(mPosterIv.getContext())
                    .load(moviePosterUri)
                    .into(mPosterIv);
        }
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_item_movie, viewGroup, false);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder moviesAdapterViewHolder, int position) {
        Movie movie = mMoviesData.get(position);
        moviesAdapterViewHolder.bindToData(movie);
    }

    @Override
    public int getItemCount() {
        if (mMoviesData == null)
            return 0;

        return mMoviesData.size();
    }

    public void setMoviesData(List<Movie> moviesData) {
        this.mMoviesData = moviesData;
        notifyDataSetChanged();
    }
}