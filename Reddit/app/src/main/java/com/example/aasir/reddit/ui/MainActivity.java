package com.example.aasir.reddit.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aasir.reddit.App;
import com.example.aasir.reddit.R;
import com.example.aasir.reddit.RedditAPI;
import com.example.aasir.reddit.adapters.PostListAdapter;
import com.example.aasir.reddit.model.ParseXML;
import com.example.aasir.reddit.model.Post;
import com.example.aasir.reddit.model.RedditFeed;
import com.example.aasir.reddit.model.entry.Entry;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.btnRefresh) Button btnRefresh;
    @BindView(R.id.subName) EditText mFeedName;
    private String currentFeed = "all"; //Default subreddit

    private RecyclerView mRecyclerView;
    private PostListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Inject
    RedditAPI redditAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Injecting the Main Activity
        ((App)getApplication()).getPostComponent().inject(this);

        Logger.addLogAdapter(new AndroidLogAdapter());

        initToolbar();
        initView();
        loadRedditFeed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_navigation, menu);
        return true;
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(item -> {
            Log.d(TAG, "onMenuClicked: " + item);
            switch (item.getItemId()){
                case R.id.navLogin:
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    break;
            }
            return false;
        });
    }

    /**
     * Initializing the Main Activity View
     */
    private void initView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(MainActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        btnRefresh.setOnClickListener(view -> {
            String feedName = mFeedName.getText().toString();
            if(!feedName.equals("")){
                currentFeed = feedName;
                loadRedditFeed();
            }
        });
    }

    /**
     * Creating the Retrofit RedditAPI instance and running the feed
     */
    private void loadRedditFeed(){
        redditAPI.getFeed(currentFeed)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError);
    }

    private void handleResponse(RedditFeed redditFeed){
        List<Entry> entries = redditFeed.getEntries();
        List<Post> mPosts = new ArrayList<>();
        for(int i = 0; i < entries.size(); i++) {
            ParseXML aTag = new ParseXML("<a href=", entries.get(i).getContent());
            List<String> postContent = aTag.parse();

            ParseXML imgTag = new ParseXML("<img src=", entries.get(i).getContent());
            String imgLink;

            try {
                imgLink = imgTag.parse().get(0);
            } catch (Exception e){
                imgLink = null;
                Logger.e(e.getMessage());
            }

            try{
                mPosts.add(new Post(entries.get(i).getTitle(),
                        entries.get(i).getAuthor().getName(),
                        entries.get(i).getUpdated(),
                        postContent.get(0),
                        imgLink,
                        entries.get(i).getId()));
            } catch (NullPointerException e){
                mPosts.add(new Post(entries.get(i).getTitle(),
                        "None",
                        entries.get(i).getUpdated(),
                        postContent.get(0),
                        imgLink,
                        entries.get(i).getId()));
                Log.e(TAG, "handleResponse: NullPointerException: " + e.getMessage());
            }
        }

        Observable<List<Post>> observable = Observable.fromArray(mPosts);
        mAdapter = new PostListAdapter(observable);
        mAdapter.getPostClickedObservable()
                .subscribe(post -> {
                    Log.d(TAG, "onItemClick: " + post);
                    Intent intent = new Intent(MainActivity.this, CommentsActivity.class);
                    intent.putExtra(getResources().getString(R.string.post_author), post.getAuthor());
                    intent.putExtra(getResources().getString(R.string.post_title), post.getTitle());
                    intent.putExtra(getResources().getString(R.string.post_url), post.getPostURl());
                    intent.putExtra(getResources().getString(R.string.post_thumbnail), post.getThumbnailURL());
                    intent.putExtra(getResources().getString(R.string.post_updated), post.getDate_updated());
                    intent.putExtra(getResources().getString(R.string.post_id), post.getId());
                    startActivity(intent);
                });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void handleError(Throwable error){
        Logger.e("onFailure: Unable to retrieve RSS: " + error.getMessage());
        Toast.makeText(MainActivity.this, "An Error Occurred", Toast.LENGTH_LONG).show();
    }

}