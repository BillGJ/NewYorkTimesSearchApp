package com.ebillson.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.ebillson.nytimessearch.Article;
import com.ebillson.nytimessearch.ArticleArrayAdapter;
import com.ebillson.nytimessearch.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    EditText etQuery;
    GridView gvResults;
    Button btnSearch;


    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //references for the views
        setupViews();
    }


    public void setupViews(){

        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView)findViewById(R.id.gvResults);
        btnSearch = (Button)findViewById(R.id.btnSearch);

        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);


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


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //This gets the text typed in the EditText
    public void onArticleSearch(View view) {


        String query = etQuery.getText().toString();

        adapter.clear();

        //Toast.makeText(this, "Searching for " + query, Toast.LENGTH_LONG).show();


        /////////////////////////////////////written without internet///////////////////
        AsyncHttpClient client = new AsyncHttpClient();

        String url =  "http://api.nytimes.com/svc/search/v2/articlesearch.json";


        RequestParams params = new RequestParams();
        params.put("api-key", "fbdad548ee694e36a5eebf8d30986158");
        params.put("page", 0);
        params.put("q",query);

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
}
