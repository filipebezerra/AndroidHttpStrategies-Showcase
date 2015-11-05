package com.github.filipebezerra.stackoverflowapi.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.aurae.retrofit.LoganSquareConverterFactory;
import com.github.filipebezerra.stackoverflowapi.R;
import com.github.filipebezerra.stackoverflowapi.adapters.StackOverflowQuestionsAdapter;
import com.github.filipebezerra.stackoverflowapi.network.http.AQueryAsyncCall;
import com.github.filipebezerra.stackoverflowapi.network.http.AsyncCall;
import com.github.filipebezerra.stackoverflowapi.network.http.AsyncTaskCall;
import com.github.filipebezerra.stackoverflowapi.network.http.RetrofitAsyncCall;
import com.github.filipebezerra.stackoverflowapi.network.http.VolleyAsyncTask;
import com.github.filipebezerra.stackoverflowapi.providers.SearchSuggestionsProvider;
import com.github.filipebezerra.stackoverflowapi.stackoverflow.models.Question;
import java.util.List;
import java.util.concurrent.TimeUnit;
import retrofit.Converter;
import retrofit.GsonConverterFactory;
import retrofit.JacksonConverterFactory;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 04/11/2015
 * @since #
 */
public class MainActivity extends AppCompatActivity
        implements AsyncCall.AsyncLifecycle<List<Question>> {

    @Bind(R.id.root_layout) CoordinatorLayout mRootLayout;
    @Bind(R.id.questions) RecyclerView mQuestionsView;

    private @IdRes int mIdStrategySelected = R.id.item_asynctask;
    private @IdRes int mIdConverterSelected = R.id.item_gson;

    private SearchView mSearchView;
    private StackOverflowQuestionsAdapter mQuestionsAdapter;

    private MaterialDialog mProgressDialog;

    private AsyncCall<List<Question>> mAsyncCall;

    private long initialTime;
    private long finalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(ButterKnife.<Toolbar>findById(this, R.id.toolbar));
        setUpView();
    }

    private void setUpView() {
        //TODO: set divider decoration
        mQuestionsView.setHasFixedSize(true);
        mQuestionsView.setLayoutManager(new LinearLayoutManager(this));
        mQuestionsView.setItemAnimator(new DefaultItemAnimator());
        mQuestionsView.setAdapter(mQuestionsAdapter = new StackOverflowQuestionsAdapter());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleSearchIntent(intent);
    }

    private void handleSearchIntent(Intent intent) {
        if (intent != null) {
            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                final String query = intent.getStringExtra(SearchManager.QUERY);
                if (!TextUtils.isEmpty(query)) {
                    executeQuery(query);
                }
            }
        }
    }

    private void executeQuery(String query) {
        mSearchView.setQuery(query, false);
        mSearchView.clearFocus();

        switch (mIdStrategySelected) {
            case R.id.item_asynctask:
                mAsyncCall = new AsyncTaskCall();
                break;
            case R.id.item_aquery:
                mAsyncCall = new AQueryAsyncCall(this);
                break;
            case R.id.item_volley:
                mAsyncCall = new VolleyAsyncTask(getApplicationContext());
                break;
            case R.id.item_retrofit:
                mAsyncCall =  new RetrofitAsyncCall(getRetrofitConverter());
                break;
        }

        if (mAsyncCall != null) {
            //TODO: check network connection before call
            mAsyncCall.execute(query, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        mSearchView = (SearchView) MenuItemCompat.getActionView(
                menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getGroupId() != 0) {
            if (item.getGroupId() == R.id.group_strategy) {
                mIdStrategySelected = item.getItemId();
            } else {
                mIdConverterSelected = item.getItemId();
            }
            if(item.isChecked())
                item.setChecked(false);
            else
                item.setChecked(true);

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null || !mProgressDialog.isShowing()) {
            mProgressDialog = new MaterialDialog.Builder(MainActivity.this).
                    content(R.string.searching_please_wait).
                    progress(true, 0).
                    show();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void saveSearch() {
        if (!TextUtils.isEmpty(mSearchView.getQuery())) {
            final SearchRecentSuggestions provider = new SearchRecentSuggestions(
                    getApplicationContext(), SearchSuggestionsProvider.AUTHORITY,
                    SearchSuggestionsProvider.MODE);
            provider.saveRecentQuery(mSearchView.getQuery().toString(), null);
        }
    }

    private Converter.Factory getRetrofitConverter() {
        switch (mIdConverterSelected) {
            case R.id.item_gson:
                return GsonConverterFactory.create();
            case R.id.item_jackson:
                return JacksonConverterFactory.create();
            case R.id.item_logan_square:
                return LoganSquareConverterFactory.create();
            default:
                return GsonConverterFactory.create();
        }
    }

    private String getHttpStrategyName() {
        switch (mIdStrategySelected) {
            case R.id.item_asynctask:
                return "AsyncTask";
            case R.id.item_aquery:
                return "AQuery";
            case R.id.item_volley:
                return "Volley";
            case R.id.item_retrofit:
                return "Retrofit 2.0";
            default:
                return "AsyncTask";
        }
    }

    private String getJsonConverterName() {
        switch (mIdConverterSelected) {
            case R.id.item_gson:
                return "Gson";
            case R.id.item_jackson:
                return "Jackson";
            case R.id.item_logan_square:
                return "LoganSquare";
            default:
                return "Gson";
        }
    }

    @Override
    public void onBeforeExecute() {
        initialTime = System.currentTimeMillis();
        showProgressDialog();
        //TODO: set loading state
    }

    @Override
    public void onSuccess(List<Question> result) {
        finalTime = System.currentTimeMillis() - initialTime;
        dismissProgressDialog();
        mQuestionsAdapter.swapData(result);
        saveSearch();

        final long duration = TimeUnit.MILLISECONDS.toSeconds(finalTime);
        final String content = String.format(getString(R.string.metrics_explanation),
                duration, getHttpStrategyName(), getJsonConverterName(), result.size());

        new MaterialDialog.Builder(this).
                title(R.string.metrics).
                content(content).
                show();

        //TODO: save the metrics in the local database as history
        //TODO: save the result as the last result state
    }

    @Override
    public void onResultNothing() {
        dismissProgressDialog();

        //TODO: set empty state
    }

    @Override
    public void onFailure(Throwable error) {
        dismissProgressDialog();

        //TODO: set error state
    }
}
