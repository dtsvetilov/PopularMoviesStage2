package com.example.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.popularmovies.database.MoviesContract;

public class FavoritesActivity extends AppCompatActivity implements
        FavoritesAdapter.FavoritesAdapterOnClickHandler,
        LoaderCallbacks<Cursor> {

    private FavoritesAdapter mFavoritesAdapter;
    private ProgressBar mLoadingIndicator;

    private static final int FAVORITES_LOADER_ID = 2;

    private static final String[] MAIN_FAVORITES_PROJECTION = {
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.COLUMN_TITLE
    };

    public static final int INDEX_FAVORITES_MOVIE_ID = 0;
    public static final int INDEX_FAVORITES_TITLE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        mFavoritesAdapter = new FavoritesAdapter(this);

        RecyclerView mRecyclerView = findViewById(R.id.favorites_rv);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mFavoritesAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        LoaderCallbacks<Cursor> callback = FavoritesActivity.this;
        getSupportLoaderManager().initLoader(FAVORITES_LOADER_ID, null, callback);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {
        Uri queryUri = MoviesContract.MovieEntry.CONTENT_URI;
        String sortOrder = MoviesContract.MovieEntry.COLUMN_TITLE + " ASC";

        return new CursorLoader(this,
                queryUri,
                MAIN_FAVORITES_PROJECTION,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mFavoritesAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onClick(int movieId) {
        Intent intentToStartDetailActivity = new Intent(this, DetailActivity.class);
        intentToStartDetailActivity.putExtra(DetailActivity.EXTRA_MOVIE_ID, movieId);
        startActivity(intentToStartDetailActivity);
    }
}