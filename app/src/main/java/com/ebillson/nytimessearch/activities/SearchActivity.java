package com.ebillson.nytimessearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.ebillson.nytimessearch.R;
import com.ebillson.nytimessearch.adapters.ArticleArrayAdapter;
import com.ebillson.nytimessearch.models.Article;
import com.ebillson.nytimessearch.models.SearchSettings;
import com.ebillson.nytimessearch.utils.EndlessScrollListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    //EditText etQuery;
    GridView gvResults;
    //Button btnSearch;


    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    SearchSettings settings;

    String searchQuery;
    int searchPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);
// Get access to the custom title view
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        //references for the views

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);



        setupSearchParameters();
        setupViews();

        //verifying if there is network
        isNetworkAvailable();
        //verifying if device is online
        isOnline();
    }





    public void setupSearchParameters() {
        // initialize settings with default values
        settings = new SearchSettings();
    }

    public void setupViews(){

       // etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView)findViewById(R.id.gvResults);
       // btnSearch = (Button)findViewById(R.id.btnSearch);

        articles = new ArrayList<Article>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);
        searchPage = 0;



        //hook up listener for grid click

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //create an intent to display the article

                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);


                // get the article to display

                Article article = articles.get(position);

                //pass in that article into intent

                i.putExtra("article",article);

                //launch the actvity
                startActivity(i);
            }
        });


        // Attach the listener to the AdapterView onCreate

        gvResults.setOnScrollListener(new EndlessScrollListener(5, 0) {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView

                searchPage = page;
                searchArticles(false);
                //Toast.makeText(SearchActivity.this, "LoadMore "+searchPage+" - "+totalItemsCount, Toast.LENGTH_LONG).show();
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);


        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               searchQuery = query;
               searchArticles(true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_filter){

            showSettings();

            return  true;

        }

        return super.onOptionsItemSelected(item);
    }


    //This gets the text typed in the EditText
    //public void onArticleSearch(View view) {

    public void searchArticles(final Boolean clear) {
        //String query = etQuery.getText().toString();

        //adapter.clear();

        //Toast.makeText(this, "Searching for " + query, Toast.LENGTH_LONG).show();


        /////////////////////////////////////written without internet///////////////////
        AsyncHttpClient client = new AsyncHttpClient();

        String url =  "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("api-key", "fbdad548ee694e36a5eebf8d30986158");
        params.put("page", searchPage);
        params.put("q",searchQuery);

        // if settings begin date has been set apply begin_date
        if(settings.getBeginDate() != null && settings.getBeginDate().getCalendar() != null) {
            params.put("begin_date", settings.formatBeginDate());
        }

        // if settings has sort oder apply sort
        if(settings.getSortOrder() != SearchSettings.Sort.none) {
            params.put("sort", settings.getSortOrder().name());
        }

        // if settings filters contains at least one filter, apply filter
        if(settings.getFilters().size() > 0) {
            params.put("fq", settings.generateNewsDeskFiltersOR());
        }

        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
               ///////////
                JSONArray articleJSONResults = null;

                try{
                    articleJSONResults = response.getJSONObject("response").getJSONArray("docs");
                    //////////
                    adapter.addAll(Article.fromJSONArray(articleJSONResults));
                    adapter.notifyDataSetChanged();
                   Log.d("DEBUG", articles.toString());

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    ////////////////////////////////


    public void showSettings() {
        Intent i = new Intent(this, SettingsActivity.class);
        i.putExtra("settings", settings);
        startActivityForResult(i, 100);
    }

    // Handle the result once the activity returns a result
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100) {
            if(resultCode == RESULT_OK) {
                settings = (SearchSettings) data.getSerializableExtra("settings");
                //Toast.makeText(this, "sort passed: "+ settings.getSortOrder(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

}
