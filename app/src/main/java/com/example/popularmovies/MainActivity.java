package com.example.popularmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.popularmovies.databinding.ActivityMainBinding;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.PageMovies;
import com.example.popularmovies.ui.adapters.MovieAdapter;
import com.example.popularmovies.utils.Constants;
import com.example.popularmovies.utils.NetworkUtils;
import com.example.popularmovies.utils.ResultsDisplay;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {

    private ActivityMainBinding binding;
    private MainViewModel mViewModel;

    MovieAdapter mAdapter;
    static MenuItem indicatePage; //item-ul din meniu care imi indica pagina curenta
    static int currentPage = 1; //retin pagina pe care sunt
    static int mSortType = 0; // 0 pentru popularity (default), 1 pt vote_average si 2 pt lista de filme favorite
    static PageMovies mPageMovies; //retin in acest obiect pagina pe care am fost ultima data pentru a evita o cerere noua catre server cand revin inapoi pe aceasta activitate
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mAdapter = new MovieAdapter(this);

        binding.myRecyclerView.setEmptyView(binding.emptyView);
        binding.myRecyclerView.setHasFixedSize(true);

        boolean isLand = getResources().getBoolean(R.bool.isLand);
        if (isLand) {
            binding.myRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
        } else binding.myRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));

        binding.myRecyclerView.setAdapter(mAdapter);

        if (mPageMovies == null && NetworkUtils.isConnected(this))
            getMoviesFromServer(currentPage, Constants.API_KEY, Constants.SORT_BY_POPULARITY);
        else if (mPageMovies != null && NetworkUtils.isConnected(this)) {
            setAdapterMoviesList(mPageMovies.getMoviesList());
        } else if (mPageMovies == null && !NetworkUtils.isConnected(this)) {
            mPageMovies = new PageMovies();
            getFavoriteMoviesFromDb();
            mSortType = 2;
        }
    }

    @Override
    public void onItemClickListener(int viewIndex) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(Constants.MOVIE_KEY, mAdapter.getMovies().get(viewIndex));
        startActivityForResult(intent, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        this.menu = menu;
        inflater.inflate(R.menu.activity_main, menu);
        indicatePage = menu.findItem(R.id.page);
        indicatePage.setTitle(currentPage + "");
        displaySelectedSortedMoviesTypes();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.next_page && currentPage < 1000) {
            currentPage++;
            changeMoviesPage();
        } else if (id == R.id.prev_page && currentPage > 1) {
            currentPage--;
            changeMoviesPage();
        } else if (id == R.id.high_rated_movies) {
            mSortType = 1;
            displaySelectedSortedMoviesTypes();
        } else if (id == R.id.high_popularity) {
            mSortType = 0;
            displaySelectedSortedMoviesTypes();
        } else if (id == R.id.favorite_movies) {
            mSortType = 2;
            displaySelectedSortedMoviesTypes();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getFavoriteMoviesFromDb() {
        mViewModel.getMovies().observe(this, movies -> {

            if (!NetworkUtils.isConnected(this) && mPageMovies == null) {
                mPageMovies = new PageMovies();
            }

            if (mPageMovies != null) {
                mPageMovies.setMoviesList(movies);
                mAdapter.setMovies(mPageMovies.getMoviesList());
            }

            assert movies != null;
            if (movies.size() == 0)
                binding.emptyView.setVisibility(View.VISIBLE);
            else binding.emptyView.setVisibility(View.INVISIBLE);
            binding.loadingSpinner.setVisibility(View.INVISIBLE);
        });
    }

    public void getMoviesFromServer(int page, String apiKey, String sortType) {
        if (NetworkUtils.isConnected(this)) {
            mViewModel.getMoviesFromServer(page, apiKey, sortType).observe(this, checkResultDisplay -> {
                if (checkResultDisplay != null) {
                    switch (checkResultDisplay.state) {
                        case ResultsDisplay.STATE_LOADING:
                            loadingStateUi();
                            break;
                        case ResultsDisplay.STATE_ERROR:
                            Toast.makeText(this, R.string.error_loading_movies, Toast.LENGTH_LONG).show();
                            break;
                        case ResultsDisplay.STATE_SUCCESS:
                            mPageMovies = checkResultDisplay.data;
                            assert mPageMovies != null;
                            setAdapterMoviesList(mPageMovies.getMoviesList());
                            break;
                    }
                }

            });
        } else {
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            binding.emptyView.setVisibility(View.INVISIBLE);
            successStateUi();
        }
    }

    private void changeMoviesPage() {
        indicatePage.setTitle(currentPage + "");
        mPageMovies = null;
        if (mSortType == 0)
            getMoviesFromServer(currentPage, Constants.API_KEY, Constants.SORT_BY_POPULARITY);
        else getMoviesFromServer(currentPage, Constants.API_KEY, Constants.SORT_BY_VOTE);
    }

    private void setAdapterMoviesList(List<Movie> movies) {
        mAdapter.setMovies(movies);
        successStateUi();
    }

    //Pentru a sti ce categorie de filme dorim sa incarcam, cand ne reintoarcem in activitate.
    //Activitatea details a fost lansata si se asteapta un raspuns din partea ei.
    //Aici primim acel raspuns si il gestionam.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                boolean hasBackPressed = data.getBooleanExtra(Constants.HAS_BACK_PRESSED_KEY, false);
                if (hasBackPressed) {
                    displaySelectedSortedMoviesTypes();
                }
            }
        }
    }

    private void displaySelectedSortedMoviesTypes() {
        if (mSortType == 2) {
            hideChangePagesUi();
            getFavoriteMoviesFromDb();
        } else if (NetworkUtils.isConnected(this)) {
            showChangePagesUi();
            changeMoviesPage();
        } else Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
    }

    private void showChangePagesUi() {
        if (menu != null && !menu.getItem(0).isVisible()) {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(true);
        }
    }

    private void hideChangePagesUi() {
        if (menu != null && menu.getItem(0).isVisible()) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
        }
    }

    private void loadingStateUi() {
        binding.loadingSpinner.setVisibility(View.VISIBLE);
    }

    private void successStateUi() {
        binding.loadingSpinner.setVisibility(View.INVISIBLE);
    }
}
