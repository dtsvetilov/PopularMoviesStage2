package com.example.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.popularmovies.data.Movie;
import com.example.popularmovies.data.MoviesPreferences;
import com.example.popularmovies.data.MoviesResponse;
import com.example.popularmovies.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity implements
        MoviesAdapter.MoviesAdapterOnClickHandler,
        LoaderCallbacks<MoviesResponse>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private static final int MOVIES_LOADER_ID = 0;
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        mMoviesAdapter = new MoviesAdapter(this);

        mRecyclerView = findViewById(R.id.movies_rv);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mMoviesAdapter);
        updateRecyclerViewGridLayout();

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        LoaderCallbacks<MoviesResponse> callback = MainActivity.this;
        getSupportLoaderManager().initLoader(MOVIES_LOADER_ID, null, callback);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    private void updateRecyclerViewGridLayout() {
        int numberOfColumns;
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            numberOfColumns = 3;
        } else {
            numberOfColumns = 5;
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns);
        mRecyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public Loader<MoviesResponse> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<MoviesResponse>(this) {
            MoviesResponse mMoviesData;
            boolean forSortOrderPopular;

            @Override
            protected void onStartLoading() {
                boolean sortOrderPopular = MoviesPreferences.sorOrderPopular(MainActivity.this);
                if (mMoviesData != null && forSortOrderPopular == sortOrderPopular) {
                    deliverResult(mMoviesData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }

            }

            @Override
            public MoviesResponse loadInBackground() {
                boolean sortOrderPopular = MoviesPreferences.sorOrderPopular(MainActivity.this);

                MoviesResponse moviesResponse = null;
                try {
                    String apiKey = getString(R.string.tmdb_api_key);

                    if (sortOrderPopular)
                        moviesResponse = NetworkUtils.popular(apiKey);
                    else
                        moviesResponse = NetworkUtils.topRated(apiKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return moviesResponse;
            }

            @Override
            public void deliverResult(MoviesResponse data) {
                forSortOrderPopular = MoviesPreferences.sorOrderPopular(MainActivity.this);
                mMoviesData = data;
                super.deliverResult(data);

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MoviesResponse> loader, MoviesResponse data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (data == null) {
            mMoviesAdapter.setMoviesData(null);
            showErrorMessage();
        } else {
            mMoviesAdapter.setMoviesData(data.getResults());
            showMoviesDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<MoviesResponse> loader) {
    }

    private void invalidateData() {
        mMoviesAdapter.setMoviesData(null);
    }

    @Override
    public void onClick(Movie movie) {
        Intent intentToStartDetailActivity = new Intent(this, DetailActivity.class);
        intentToStartDetailActivity.putExtra(DetailActivity.EXTRA_MOVIE_ID, movie.getId());
        startActivity(intentToStartDetailActivity);
    }

    private void showMoviesDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            invalidateData();
            getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
            return true;
        }

        if (id == R.id.action_favorites) {
            Intent startFavoritesActivity = new Intent(this, FavoritesActivity.class);
            startActivity(startFavoritesActivity);
            return true;
        }

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        PREFERENCES_HAVE_BEEN_UPDATED = true;
    }
}