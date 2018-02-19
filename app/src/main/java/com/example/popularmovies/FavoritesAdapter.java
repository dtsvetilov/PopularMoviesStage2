package com.example.popularmovies;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesAdapterViewHolder> {

    private Cursor mCursor;
    final private FavoritesAdapterOnClickHandler mClickHandler;

    public interface FavoritesAdapterOnClickHandler {
        void onClick(int movieId);
    }

    public FavoritesAdapter(FavoritesAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class FavoritesAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final TextView mTitleTv;

        private int movieId;

        public FavoritesAdapterViewHolder(View view) {
            super(view);
            mTitleTv = view.findViewById(R.id.title_tv);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickHandler.onClick(movieId);
        }

        private void bindToData(int position) {
            mCursor.moveToPosition(position);

            movieId = mCursor.getInt(FavoritesActivity.INDEX_FAVORITES_MOVIE_ID);
            String movieTitle = mCursor.getString(FavoritesActivity.INDEX_FAVORITES_TITLE);
            mTitleTv.setText(movieTitle);
        }
    }

    @Override
    public FavoritesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_favorite, viewGroup, false);
        return new FavoritesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoritesAdapterViewHolder favoritesAdapterViewHolder, int position) {
        favoritesAdapterViewHolder.bindToData(position);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null)
            return 0;

        return mCursor.getCount();
    }

    public void setCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }
}