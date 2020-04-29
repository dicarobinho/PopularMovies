package com.example.popularmovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.popularmovies.data.AppDataBase;
import com.example.popularmovies.data.DataBaseOperations;
import com.example.popularmovies.databinding.ActivityDetailsBinding;
import com.example.popularmovies.databinding.RecyclerviewVideosReviewsBinding;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.PageReviews;
import com.example.popularmovies.model.PageVideos;
import com.example.popularmovies.ui.adapters.ReviewAdapter;
import com.example.popularmovies.ui.adapters.VideoAdapter;
import com.example.popularmovies.utils.Constants;
import com.example.popularmovies.utils.ImagesOperations;
import com.example.popularmovies.utils.NetworkUtils;
import com.example.popularmovies.utils.ResultsDisplay;
import com.example.popularmovies.utils.SharedPreferenceUtils;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity implements ReviewAdapter.ListItemClickListener, VideoAdapter.ListItemClickListener {
    ReviewAdapter rAdaper;
    VideoAdapter vAdapter;
    private DetailsActivityViewModel mViewModel;
    LinearLayout recyclerViewParent;

    ActivityDetailsBinding binding;
    RecyclerviewVideosReviewsBinding binding2;
    static AppDataBase mDb;

    static ViewTreeObserver.OnGlobalLayoutListener viewTreeObserver;

    //referinta catre meniul din activitate
    private Menu menu;

    //obiectul cu filmul primit de la MainAcitvity
    static Movie selectedMovie;

    //facem o copie a obiectului primit de la MainActivity (din cauza unor erori...)
    static Movie realCurrentMovie;

    // 0 daca nu suntem siguri ca filmul exista in DB, 1 daca suntem siguri si nu merita sa mai facem verificare in DB, 2 daca suntem siguri ca nu exista in DB
    static int markedAsFavorite = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerViewParent = findViewById(R.id.recycler_view_parent);
        binding2 = RecyclerviewVideosReviewsBinding.bind(recyclerViewParent);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewModel = new ViewModelProvider(this).get(DetailsActivityViewModel.class);

        mDb = AppDataBase.getInstance(getApplicationContext());
        if (getIntent() != null && getIntent().hasExtra(Constants.MOVIE_KEY))
            selectedMovie = (Movie) getIntent().getSerializableExtra(Constants.MOVIE_KEY);

        realCurrentMovie = getRealMovieObject();

        getSupportActionBar().setTitle(realCurrentMovie.getOriginalTitle());
        setUiContent();

        rAdaper = new ReviewAdapter(this);
        vAdapter = new VideoAdapter(this);

        binding2.recyclerViewDetails.setHasFixedSize(false);
        binding2.recyclerViewDetails.setNestedScrollingEnabled(false);
        binding2.recyclerViewDetails.setLayoutManager(new LinearLayoutManager(DetailsActivity.this));

        int typeAdapter = SharedPreferenceUtils.getIdFromSharedPreference(this, Constants.SELECTED_ADAPTER_TYPE_KEY);
        if (typeAdapter != -1) {
            if (typeAdapter == 1) {
                setupSelectedAdapter(2);
                setupSelectedAdapter(typeAdapter);
            } else if (typeAdapter == 2) {
                setupSelectedAdapter(1);
                setupSelectedAdapter(typeAdapter);
            }
        } else {
            setupSelectedAdapter(1);
            setupSelectedAdapter(2);
        }
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        this.menu = menu;
        inflater.inflate(R.menu.activity_details, menu);
        if (markedAsFavorite == 0)
            checkMovieIfExistsInDb(realCurrentMovie.getId());
        else if (markedAsFavorite == 1)
            markAsFavorite();
        else markAsNoFavorite();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.mark_as_favorite) {
            deleteMovieFromDataBase(realCurrentMovie, getApplicationContext());
            ImagesOperations.deleteImageFile(this, realCurrentMovie.getThumbnail());
            markAsNoFavorite();
        } else if (id == R.id.mark_as_no_favorite) {
            ImagesOperations.urlToBitmap(this, Constants.IMAGE_BASE_URL + realCurrentMovie.getThumbnail());
            realCurrentMovie.setThumbnail(ImagesOperations.finalImagePath);
            saveMovieToDataBase(realCurrentMovie, getApplicationContext());
            markAsFavorite();
        }
        return super.onOptionsItemSelected(item);
    }
    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    public void getVideosFromServer(String id, String apiKey) {
        if (NetworkUtils.isConnected(this)) {
            mViewModel.getVideosFromServer(id, apiKey).observe(this, checkResultDisplay -> {
                if (checkResultDisplay != null) {
                    switch (checkResultDisplay.state) {
                        case ResultsDisplay.STATE_LOADING:
                            loadStateUi();
                            break;
                        case ResultsDisplay.STATE_ERROR:
                            Toast.makeText(this, R.string.error_loading_videos, Toast.LENGTH_LONG).show();
                            break;
                        case ResultsDisplay.STATE_SUCCESS:
                            PageVideos page = checkResultDisplay.data;
                            assert page != null;
                            vAdapter.setVideos(page.getVideosList());
                            successStateUi();
                            if (vAdapter.getItemCount() == 0) {
                                binding2.emptyView.setVisibility(View.VISIBLE);
                                binding2.emptyView.setText(R.string.no_videos_to_display);
                            }
                            break;
                    }
                }
            });
        } else {
            binding2.emptyView.setVisibility(View.VISIBLE);
            binding2.emptyView.setText(R.string.no_internet_connection);
            successStateUi();
        }
    }

    public void getReviewsFromServer(String id, int pageNumber, String apiKey) {
        if (NetworkUtils.isConnected(this)) {
            mViewModel.getReviewsFromServer(id, pageNumber, apiKey).observe(this, checkResultDisplay -> {
                if (checkResultDisplay != null) {
                    switch (checkResultDisplay.state) {
                        case ResultsDisplay.STATE_LOADING:
                            loadStateUi();
                            break;
                        case ResultsDisplay.STATE_ERROR:
                            Toast.makeText(this, R.string.error_loading_reviews, Toast.LENGTH_LONG).show();
                            break;
                        case ResultsDisplay.STATE_SUCCESS:
                            PageReviews page = checkResultDisplay.data;
                            assert page != null;
                            rAdaper.setReviews(page.getReviewList());
                            successStateUi();
                            if (rAdaper.getItemCount() == 0) {
                                binding2.emptyView.setVisibility(View.VISIBLE);
                                binding2.emptyView.setText(R.string.no_reviews_to_display);
                            }
                            break;
                    }
                }
            });
        } else {
            binding2.emptyView.setVisibility(View.VISIBLE);
            binding2.emptyView.setText(R.string.no_internet_connection);
            successStateUi();
        }
    }
    //----------------------------------------------------------------------------------------------

    private void setUiContent() {
        binding.durationMovie.setText(R.string.min_120);
        assert realCurrentMovie != null;
        if (!realCurrentMovie.getOverview().equals(""))
            binding.overview.setText(realCurrentMovie.getOverview());
        else binding.overview.setText(R.string.no_description);

        if (realCurrentMovie != null && realCurrentMovie.getDate() != null && !realCurrentMovie.getDate().equals("")) {
            String[] tempDate = realCurrentMovie.getDate().split("-");
            binding.releaseYear.setText(tempDate[0]);
        } else binding.releaseYear.setText(R.string.not_specified);

        String voteAverage = realCurrentMovie.getVoteAverage() + "" + "/10";
        binding.voteAverage.setText(voteAverage);

        if (realCurrentMovie.getThumbnail() != null && !realCurrentMovie.getThumbnail().contains(Constants.JPEG))
            Picasso.get().load(Constants.IMAGE_BASE_URL + realCurrentMovie.getThumbnail()).into(binding.thumbnailMovie);
        else if (realCurrentMovie.getThumbnail() == null)
            Picasso.get().load(Constants.NO_PICTURE_AVAILABLE_ICON).into(binding.thumbnailMovie);
        else if (realCurrentMovie.getThumbnail().contains(Constants.JPEG)) {
            Picasso.get().load("file://" + realCurrentMovie.getThumbnail()).config(Bitmap.Config.RGB_565).fit().centerCrop().into(binding.thumbnailMovie);
        }
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void reviewOnItemClickListener(String urlToReview) {
        Uri uri = Uri.parse(urlToReview);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
        else Toast.makeText(this, R.string.no_app_to_do_this_action, Toast.LENGTH_LONG).show();
    }

    @Override
    public void videoOnItemClickListener(String keyToVideo) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(Constants.BASE_YOUTUBE_URL)
                .appendPath("watch")
                .appendQueryParameter("v", keyToVideo);
        String urlToVideo = builder.build().toString();

        Uri uri = Uri.parse(urlToVideo);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
        else Toast.makeText(this, R.string.no_app_to_do_this_action, Toast.LENGTH_LONG).show();
    }
    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    private void checkMovieIfExistsInDb(String id) {
        mViewModel.getMovieById(id).observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                mViewModel.getMovieById(id).removeObserver(this);
                if (movie != null) {
                    markAsFavorite();
                }
            }
        });
    }

    private void setupSelectedAdapter(int adapterType) {
        if (adapterType == 1) {
            binding2.indicateRecyclerType.setText(R.string.trailers);
            binding2.recyclerViewDetails.setAdapter(vAdapter);
            getVideosFromServer(realCurrentMovie.getId(), Constants.API_KEY);
        } else {
            binding2.indicateRecyclerType.setText(R.string.reviews);
            binding2.recyclerViewDetails.setAdapter(rAdaper);
            getReviewsFromServer(realCurrentMovie.getId(), 1, Constants.API_KEY);
        }
    }

    @Override
    public void onBackPressed() {
        markedAsFavorite = 0;
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.HAS_BACK_PRESSED_KEY, true);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void displayVideosInRecycler(View view) {
        loadStateUi();
        SharedPreferenceUtils.saveToSharedPreference(this, 1, Constants.SELECTED_ADAPTER_TYPE_KEY);
        if (viewTreeObserver != null){
            binding.nestedScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(viewTreeObserver);
            viewTreeObserver = null;
        }
        setupSelectedAdapter(1);
    }

    public void displayReviewsInRecycler(View view) {
        loadStateUi();
        SharedPreferenceUtils.saveToSharedPreference(this, 2, Constants.SELECTED_ADAPTER_TYPE_KEY);
        if (viewTreeObserver != null){
            binding.nestedScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(viewTreeObserver);
            viewTreeObserver = null;
        }
        setupSelectedAdapter(2);
    }

    private void markAsFavorite() {
        menu.getItem(1).setVisible(true);
        menu.getItem(0).setVisible(false);
        markedAsFavorite = 1;
    }

    private void markAsNoFavorite() {
        menu.getItem(1).setVisible(false);
        menu.getItem(0).setVisible(true);
        markedAsFavorite = 2;
    }

    private void saveMovieToDataBase(Movie movie, Context context) {
        DataBaseOperations.saveMovieToDataBase(movie, context);
    }

    private void deleteMovieFromDataBase(Movie movie, Context context) {
        DataBaseOperations.deleteMovie(movie, context);
    }

    private Movie getRealMovieObject() {
        return new Movie(selectedMovie.getId(),
                selectedMovie.getOriginalTitle(),
                selectedMovie.getThumbnail(),
                selectedMovie.getVoteAverage(),
                selectedMovie.getOverview(),
                selectedMovie.getDate(),
                selectedMovie.getPopularity());
    }

    private void loadStateUi() {
        binding2.loadingSpinner.setVisibility(View.VISIBLE);
        binding2.emptyView.setVisibility(View.INVISIBLE);
    }

    private void successStateUi() {
        binding2.loadingSpinner.setVisibility(View.INVISIBLE);
    }

    //-----------------------------Salvare/restaurare pozitie ScrollView---------------------------------------
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(Constants.NESTED_SCROLL_VIEW_POSITION_KEY, new int[]{binding.nestedScrollView.getScrollX(), binding.nestedScrollView.getScrollY()});
    }

    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray(Constants.NESTED_SCROLL_VIEW_POSITION_KEY);
        if (position != null) {
            binding.nestedScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    binding.nestedScrollView.post(() ->
                            binding.nestedScrollView.scrollTo(position[0], position[1]));

                    viewTreeObserver = this;
                }
            });


        }
    }
    //-----------------------------Salvare/restaurare pozitie ScrollView---------------------------------------
}
