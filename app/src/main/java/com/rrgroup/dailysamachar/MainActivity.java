package com.rrgroup.dailysamachar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements NewsAdapter.OnItemClickListener,ScoreAdapter.onScoreBoardClickListener {

    //static string which will be transferred to details activity by using onItemClick void
    public static final String HEADLINE="headline";
    public static final String IMAGE="image";
    public static final String SOURCE_LINK="src_link";
    public static final String SOURCE_NAME="src_name";
    public static final String DESCRIPTION="desc";

    private RecyclerView mRecyclerView;
    private RecyclerView mScoreRecyclerView;
    private NewsAdapter mNewsAdapter;
    private ScoreAdapter mScoreAdapter;
    private ArrayList<NewsItem> mNewsList;
    private ArrayList<NewsItem> mScoreList;
    private RequestQueue mRequestQueue;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    String topic[]={"Nation","Sports"};
    String language[]={"Hindi","English"};
    AutoCompleteTextView topicView;
    AutoCompleteTextView languageView;
    private Handler mHandler;
    private Runnable mRunnable;
    // Save state
    private Parcelable recyclerViewState;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //progress dialog
        progressDialog=new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        //recycler view
        mRecyclerView=findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mScoreRecyclerView=findViewById(R.id.scoreRecyclerView);
        mScoreRecyclerView.setHasFixedSize(true);
        mScoreRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        //swipe refresh
        swipeRefreshLayout=findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //news list
        mNewsList=new ArrayList<>();

        //score list
        mScoreList=new ArrayList<>();

        //request queue
        mRequestQueue= Volley.newRequestQueue(this);

        //drop down
        topicView =findViewById(R.id.autoComplete1);
        languageView =findViewById(R.id.autoComplete2);
        arrayAdapter();

        //run to fetch news and score
        fetchData(makeURL("Nation","english"));
        fetchScore("https://rrjiotvweb.azurewebsites.net/live_score_api/getscore.php","https://rrjiotvweb.azurewebsites.net/live_score_api/score.json");


        //for refreshing score every 5 sec
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                // Call your function here
                mScoreAdapter.notifyDataSetChanged();
                refreshScore("https://rrjiotvweb.azurewebsites.net/live_score_api/getscore.php","https://rrjiotvweb.azurewebsites.net/live_score_api/score.json");

                // Schedule the runnable to run again after 5 seconds
                mHandler.postDelayed(this, 5000);
            }
        };

        // Start the runnable when the activity is created
        mHandler.postDelayed(mRunnable, 5000);
    }

    private String  makeURL(String topic,String language) {
        String url = "https://rrjiotvweb.azurewebsites.net/"+topic+language+".json";
        return url;
    }
    private void fetchNews(String topic, String language) {
        mNewsList.clear();
        fetchData(makeURL(topic,language));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mNewsList.clear();
                fetchData(makeURL(topic,language));
                fetchScore("https://rrjiotvweb.azurewebsites.net/live_score_api/getscore.php","https://rrjiotvweb.azurewebsites.net/live_score_api/score.json");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    private void arrayAdapter() {
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this, R.layout.topic_item,topic);
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, R.layout.language_item, language);
        topicView.setAdapter(arrayAdapter1);
        languageView.setAdapter(arrayAdapter2);

        topicView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                if (item.equals("Nation")) {
                    if(languageView.getText().toString().equals("Hindi")){
                        fetchNews("Nation","hindi");
                    }else if(languageView.getText().toString().equals("English")){
                        fetchNews("Nation","english");
                    }else {
                        fetchNews("Nation","english");
                    }
                }else if (item.equals("Sports")) {
                    if (languageView.getText().toString().equals("Hindi")) {
                        fetchNews("Sports", "hindi");
                    } else if (languageView.getText().toString().equals("English")) {
                        fetchNews("Sports", "english");
                    }else {
                        fetchNews("Sports", "english");
                    }
                }
            }
        });
        languageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();

                if (item.equals("Hindi")) {

                    if(topicView.getText().toString().equals("Nation")){
                        fetchNews("Nation","hindi");
                    }else if(topicView.getText().toString().equals("Sports")){
                        fetchNews("Sports","hindi");
                    }else {
                        fetchNews("Nation","hindi");
                    }
                }else if (item.equals("English")) {
                    if(topicView.getText().toString().equals("Sports")){
                        fetchNews("Sports","english");
                    }else if(topicView.getText().toString().equals("Nation")){
                        fetchNews("Nation","english");
                    }else {
                        fetchNews("Nation","english");
                    }
                }
            }
        });
    }
    private void fetchData(String getUrl){
        JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, getUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONArray jsonArray = response;
                            System.out.println(jsonArray);
                            for(int i=0;i<jsonArray.length();i++) {
                                JSONObject item = jsonArray.getJSONObject(i);

                                String name=item.getString("site_name");
                                String headline = item.getString("title");
                                String names[]=headline.split("-");
                                String mergedString="";
                                for (int j = 0; j < names.length - 1; j++) {
                                    mergedString += names[j];
                                }
                                String description = item.getString("description");
                                String org_article_url = item.getString("url");
                                String g_article_url = item.getString("link");
                                //String date = item.getString("publishedAt");

                                String image = item.getString("image");
                                //String NewsSource = item.getString("source");



                                if(!item.isNull("url") ){
                                    org_article_url=g_article_url;
                                }

                                if(!item.isNull("site_name")){
                                    name=names[names.length-1];
                                }

                                if(!image.equals("None") && !description.equals("None")){
                                    mNewsList.add(new NewsItem(null, mergedString, image, description, org_article_url, name));
                                }

                            }
                            mNewsAdapter=new NewsAdapter(MainActivity.this,mNewsList);
                            mRecyclerView.setAdapter(mNewsAdapter);
                            mNewsAdapter.setOnItemClickListener(MainActivity.this);
                            if(!mNewsList.isEmpty()&&!mScoreList.isEmpty()){
                                progressDialog.dismiss();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            //fetchData();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mRequestQueue.add(request);
    }
    private void fetchScore(String refresh,String getUrl){

        try {
            URL url = new URL(refresh);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            connection.disconnect();
        } catch (IOException e) {
            // Handle exception
        }
        JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, getUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONArray jsonArray = response;
                            System.out.println(jsonArray);
                            for(int i=0;i<jsonArray.length();i++) {
                                JSONObject item = jsonArray.getJSONObject(i);

                                String title=item.getString("title");
                                String update = item.getString("update");
                                String teamone = item.getString("teamone");
                                String teamtwo = item.getString("teamtwo");
                                String current=item.getString("current");

                                if(teamone.equals("Data Not Found")){
                                    teamone=current;
                                }

                                int runs = needed(current);
                                int run1=needed(update);
                                int runNeeded = run1+runs;

                                //for 2nd innings
                                if(teamtwo.equals("Data Not Found") && update.contains("need")){
                                    teamtwo=("Target - "+runNeeded);
                                }//before match
                                else if(update.contains("Starts")){
                                    teamone="Match";
                                    teamtwo="Yet To Start";
                                }//1st innings
                                else if (teamtwo.equals("Data Not Found") && update.contains("opt")){
                                    teamtwo="";
                                }
                                mScoreList.add(new NewsItem(title,update,teamone,teamtwo));
                            }

                            //run every 5sec
                            mScoreAdapter= new ScoreAdapter(MainActivity.this,mScoreList);
                            recyclerViewState = mScoreRecyclerView.getLayoutManager().onSaveInstanceState();
                            mScoreRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                            mScoreRecyclerView.setAdapter(mScoreAdapter);
                            mScoreAdapter.setOnScoreClickListener(MainActivity.this);
                            progressDialog.dismiss();


                        } catch (Exception e) {
                            e.printStackTrace();
                            //fetchData();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mRequestQueue.add(request);
    }
    private void refreshScore(String refresh,String getUrl){
        try {
            URL url = new URL(refresh);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            connection.disconnect();
        } catch (IOException e) {
            // Handle exception
        }

        JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET, getUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONArray jsonArray = response;
                            System.out.println(jsonArray);
                            for(int i=0;i<jsonArray.length();i++) {
                                JSONObject item = jsonArray.getJSONObject(i);

                                String title=item.getString("title");
                                String update = item.getString("update");
                                String teamone = item.getString("teamone");
                                String teamtwo = item.getString("teamtwo");
                                String current=item.getString("current");

                                if(teamone.equals("Data Not Found")){
                                    teamone=current;
                                }



                                int runs = needed(current);
                                int run1=needed(update);
                                int runNeeded = run1+runs;
                                if(teamtwo.equals("Data Not Found") && !update.contains("Starts")){
                                    teamtwo=("Target - "+runNeeded);

                                }else if(update.contains("Starts")){
                                    teamone="Match";
                                    teamtwo="Yet To Start";
                                }
                                mScoreList.set(i,new NewsItem(title,update,teamone,teamtwo));
                            }

                            mScoreAdapter= new ScoreAdapter(MainActivity.this,mScoreList);
                            recyclerViewState = mScoreRecyclerView.getLayoutManager().onSaveInstanceState();
                            mScoreRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                            mScoreRecyclerView.setAdapter(mScoreAdapter);

                            mScoreAdapter.setOnScoreClickListener(MainActivity.this);
                            progressDialog.dismiss();


                        } catch (Exception e) {
                            e.printStackTrace();
                            //fetchData();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mRequestQueue.add(request);
    }
    private int needed(String sentence) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(sentence);

        int[] values = new int[2];
        int index = 0;

        while (matcher.find() && index < 2) {
            values[index] = Integer.parseInt(matcher.group());
            index++;
        }
        int firstValue = values[0];
        return firstValue;
    }
    @Override
    public void onItemClick(int position) {
        Intent detailsIntent=new Intent(this,Details.class);
        NewsItem clickedItem=mNewsList.get(position);

        detailsIntent.putExtra(HEADLINE,clickedItem.getHeadline());
        detailsIntent.putExtra(IMAGE,clickedItem.getImage());
        detailsIntent.putExtra(SOURCE_LINK,clickedItem.getLink());
        detailsIntent.putExtra(SOURCE_NAME,clickedItem.getSrc());
        detailsIntent.putExtra(DESCRIPTION,clickedItem.getDescription());

        startActivity(detailsIntent);
    }
    public void onScoreClick(int position) {
    }
}