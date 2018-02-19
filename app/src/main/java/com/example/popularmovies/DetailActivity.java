package com.example.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.popularmovies.data.IUIAppendableObject;
import com.example.popularmovies.data.MovieDetails;
import com.example.popularmovies.data.MovieExtended;
import com.example.popularmovies.data.ReviewsResponse;
import com.example.popularmovies.data.Video;
import com.example.popularmovies.data.VideosResponse;
import com.example.popularmovies.database.MoviesContract;
import com.example.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieExtended> {

    public static final String EXTRA_MOVIE_ID = "extra_movie_id";
    private static final int DEFAULT_MOVIE_ID = -1;

    private static final String LOADER_MOVIE_ID = "loader_movie_id";

    private static final String[] MOVIE_PROJECTION = {
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.COLUMN_TITLE
    };

    private LinearLayout mContentContainerLl;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private VideosAdapter mVideosAdapter;
    private ReviewsAdapter mReviewsAdapter;

    private ImageButton mFavoriteBtn;

    private boolean mIsMovieFavorite;
    private MovieExtended mMovieData;

    private static final int MOVIE_DETAILS_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
            return;
        }

        int movieId = intent.getIntExtra(EXTRA_MOVIE_ID, DEFAULT_MOVIE_ID);
        if (movieId == DEFAULT_MOVIE_ID) {
            closeOnError();
            return;
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        mContentContainerLl = findViewById(R.id.content_container_ll);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        mFavoriteBtn = findViewById(R.id.favorite_btn);

        mVideosAdapter = new VideosAdapter(this::processVideoClick);
        mReviewsAdapter = new ReviewsAdapter();

        RecyclerView mVideosRv = findViewById(R.id.videos_rv);
        mVideosRv.setHasFixedSize(true);
        mVideosRv.setAdapter(mVideosAdapter);

        LinearLayoutManager videosLinearLayoutManager = new LinearLayoutManager(this);
        videosLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mVideosRv.setLayoutManager(videosLinearLayoutManager);

        RecyclerView mReviewsRv = findViewById(R.id.reviews_rv);
        mReviewsRv.setHasFixedSize(true);
        mReviewsRv.setAdapter(mReviewsAdapter);

        LinearLayoutManager reviewsLinearLayoutManager = new LinearLayoutManager(this);
        reviewsLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mReviewsRv.setLayoutManager(reviewsLinearLayoutManager);

        Bundle loaderBundle = new Bundle();
        loaderBundle.putInt(LOADER_MOVIE_ID, movieId);
        getSupportLoaderManager().initLoader(MOVIE_DETAILS_LOADER_ID, loaderBundle, DetailActivity.this);
    }

    @Override
    public Loader<MovieExtended> onCreateLoader(int id, final Bundle loaderArgs) {
        final int movieId = loaderArgs.getInt(LOADER_MOVIE_ID, -1);
        if (movieId == DEFAULT_MOVIE_ID)
            return null;

        return new AsyncTaskLoader<MovieExtended>(this) {
            MovieExtended mMoviesData;

            @Override
            protected void onStartLoading() {
                if (mMoviesData != null) {
                    deliverResult(mMoviesData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public MovieExtended loadInBackground() {
                MovieExtended movieExtended = null;
                try {
                    String apiKey = getString(R.string.tmdb_api_key);
                    MovieDetails movieDetails = NetworkUtils.movieDetails(apiKey, movieId);
                    VideosResponse videosResponse = NetworkUtils.videos(apiKey, movieId);
                    ReviewsResponse reviewsResponse = NetworkUtils.review(apiKey, movieId);

                    movieExtended = new MovieExtended(movieDetails, videosResponse.getResults(), reviewsResponse.getResults());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return movieExtended;
            }

            @Override
            public void deliverResult(MovieExtended data) {
                mMoviesData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieExtended> loader, MovieExtended data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mMovieData = data;
        if (data == null) {
            mVideosAdapter.setVideosData(null);
            mReviewsAdapter.setReviewsData(null);
            showErrorMessage();
        } else {
            populateUI(data);
            populateFavoriteState(data);

            mVideosAdapter.setVideosData(data.getVideos());
            mReviewsAdapter.setReviewsData(data.getReviews());
        }
    }

    @Override
    public void onLoaderReset(Loader<MovieExtended> loader) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
    }

    private void showErrorMessage() {
        mContentContainerLl.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.GONE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void populateUI(MovieExtended movieExtended) {
        mContentContainerLl.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.GONE);

        TextView titleTv = findViewById(R.id.title_tv);
        TextView genresTv = findViewById(R.id.genres_tv);
        TextView overviewTv = findViewById(R.id.overview_tv);
        TextView releaseDateTv = findViewById(R.id.release_date_tv);
        TextView runtimeTv = findViewById(R.id.runtime_tv);
        TextView statusTv = findViewById(R.id.status_tv);
        TextView voteAverageTv = findViewById(R.id.vote_average_tv);

        ImageView posterIv = findViewById(R.id.poster_iv);

        MovieDetails movieDetails = movieExtended.getMovieDetails();

        fillTextViewWithValueOrDataUnavailable(titleTv, movieDetails.getTitle());
        fillTextViewWithValueOrDataUnavailable(overviewTv, movieDetails.getOverview());
        fillTextViewWithValueOrDataUnavailable(releaseDateTv, movieDetails.getReleaseDate());
        fillTextViewWithValueOrDataUnavailable(runtimeTv, String.valueOf(movieDetails.getRuntime()));
        fillTextViewWithValueOrDataUnavailable(statusTv, movieDetails.getStatus());
        fillTextViewWithValueOrDataUnavailable(voteAverageTv, String.valueOf(movieDetails.getVoteAverage()));

        fillTextViewWithListData(genresTv, movieDetails.getGenres());

        Uri moviePosterUri = NetworkUtils.buildMovieImageUri(movieDetails.getPosterPath());
        Picasso.with(DetailActivity.this)
                .load(moviePosterUri)
                .into(posterIv);
    }

    private void fillTextViewWithValueOrDataUnavailable(TextView textView, String value) {
        if (value == null || value.isEmpty()) {
            textView.setText(R.string.data_unavailable);
            return;
        }

        textView.setText(value);
    }

    private <T extends IUIAppendableObject> void fillTextViewWithListData(TextView textView, List<T> data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < data.size(); i++) {
            String value = data.get(i).getUIAppendableValue();
            stringBuilder.append(value);

            if (i < data.size() - 1)
                stringBuilder.append(", ");
        }

        String valueToDisplay = stringBuilder.toString();
        fillTextViewWithValueOrDataUnavailable(textView, valueToDisplay);
    }

    private void populateFavoriteState(MovieExtended data) {
        Uri uri = MoviesContract.MovieEntry.buildUri(data.getMovieDetails().getId());
        Cursor cursor = getContentResolver().query(
                uri,
                MOVIE_PROJECTION,
                MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(data.getMovieDetails().getId())},
                null);

        mIsMovieFavorite = cursor != null && !cursor.isAfterLast();

        if (mIsMovieFavorite)
            mFavoriteBtn.setImageResource(R.drawable.ic_star_black_24dp);
        else
            mFavoriteBtn.setImageResource(R.drawable.ic_star_border_black_24dp);

        mFavoriteBtn.setOnClickListener(v -> processFavoriteStateButtonClick());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_refresh) {
            int movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, DEFAULT_MOVIE_ID);
            Bundle loaderBundle = new Bundle();
            loaderBundle.putInt(LOADER_MOVIE_ID, movieId);

            mLoadingIndicator.setVisibility(View.VISIBLE);
            getSupportLoaderManager().restartLoader(MOVIE_DETAILS_LOADER_ID, loaderBundle, this);
            return true;
        }

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void processVideoClick(Video video) {
        if (video.getId() == null || video.getId().isEmpty()) {
            Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + video.getId()));
        startActivity(webIntent);
    }

    private void processFavoriteStateButtonClick() {
        Uri uri = MoviesContract.MovieEntry.CONTENT_URI;
        if (mIsMovieFavorite) {
            int rowsAffected = getContentResolver().delete(
                    uri,
                    MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{String.valueOf(mMovieData.getMovieDetails().getId())});

            if (rowsAffected == 0) {
                Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
                return;
            }

            mIsMovieFavorite = false;
            mFavoriteBtn.setImageResource(R.drawable.ic_star_border_black_24dp);
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, mMovieData.getMovieDetails().getId());
            contentValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, mMovieData.getMovieDetails().getTitle());

            Uri newRecordUri = getContentResolver().insert(
                    uri,
                    contentValues);

            if (newRecordUri == null) {
                Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
                return;
            }

            mIsMovieFavorite = true;
            mFavoriteBtn.setImageResource(R.drawable.ic_star_black_24dp);
        }
    }
}