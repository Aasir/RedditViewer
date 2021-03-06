package com.example.aasir.reddit.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aasir.reddit.R;
import com.example.aasir.reddit.RedditAPI;
import com.example.aasir.reddit.adapters.CommentListAdapter;
import com.example.aasir.reddit.model.ParseXML;
import com.example.aasir.reddit.model.RedditFeed;
import com.example.aasir.reddit.model.comment.CheckComment;
import com.example.aasir.reddit.model.comment.Comment;
import com.example.aasir.reddit.model.entry.Entry;
import com.example.aasir.reddit.utils.URLs;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by Aasir on 7/17/2017.
 */

public class CommentsActivity extends AppCompatActivity {
    private static final String TAG = "CommentsActivity";

    private static String postURL;
    private static String postThumbnailURL;
    private static String postTitle;
    private static String postAuthor;
    private static String postUpdated;
    private static String postId;
    private static String currentPostURL;

    private String username;
    private String modhash;
    private String cookie;

    private RedditAPI commentFeedAPI;

    private RecyclerView mRecyclerView;
    private CommentListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Comment> comments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        initToolbar();
        initView();
        initPost();
        createRedditCommentAPI();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(TAG, "onPostResume");
        loadSessionParams();
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
                    Intent intent = new Intent(CommentsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    break;
            }
            return false;
        });
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(item -> {
            Log.d(TAG, "onMenuClicked: " + item);
            switch (item.getItemId()){
                case R.id.navLogin:
                    Intent intent = new Intent(CommentsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    break;
            }
            return false;
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.commentRecView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(CommentsActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void initPost(){
        Intent incomingIntent = getIntent();
        postAuthor = incomingIntent.getStringExtra(getResources().getString(R.string.post_author));
        postTitle = incomingIntent.getStringExtra(getResources().getString(R.string.post_title));
        postThumbnailURL = incomingIntent.getStringExtra(getResources().getString(R.string.post_thumbnail));
        postUpdated = incomingIntent.getStringExtra(getResources().getString(R.string.post_updated));
        postId = incomingIntent.getStringExtra(getResources().getString(R.string.post_id));
        postURL = incomingIntent.getStringExtra(getResources().getString(R.string.post_url));

        Log.d(TAG, "initPost Before Split: " + postURL);
        try{
            String[] splitURL = postURL.split(URLs.BASE_URL);
            currentPostURL = splitURL[1];
        }catch (ArrayIndexOutOfBoundsException e){
            Log.e(TAG, "initPost: ArrayIndexOutOfBoundException:" + e.getMessage());
        }

        TextView title = (TextView) findViewById(R.id.postTitle);
        TextView author = (TextView) findViewById(R.id.postAuthor);
        TextView updated = (TextView) findViewById(R.id.postUpdated);
        ImageView thumbnail = (ImageView) findViewById(R.id.postThumbnail);
        Button btnReply = (Button) findViewById(R.id.btnPostReply);

        title.setText(postTitle);
        author.setText(postAuthor);
        updated.setText(postUpdated);

        Picasso.with(getApplicationContext())
                .load(postThumbnailURL)
                .placeholder(R.drawable.image_failed)
                .error(R.drawable.image_failed)
                .into(thumbnail);


        btnReply.setOnClickListener(v -> {
            Log.d(TAG, "Reply Button Clicked: ");
            postCommentDialog(postId);
        });

        thumbnail.setOnClickListener(v -> {
            Log.d(TAG, "onClick: Image URL Clicked");
            Intent intent = new Intent(CommentsActivity.this, WebViewActivity.class);
            intent.putExtra("url", postURL);
            startActivity(intent);
        });
    }


    private void postCommentDialog(String postID){
        final Dialog dialog = new Dialog(CommentsActivity.this);
        dialog.setContentView(R.layout.dialog_comment_add);

        int weight = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);

        dialog.getWindow().setLayout(weight, height);
        dialog.show();

        Button btnPostComment = (Button) dialog.findViewById(R.id.btnPostComment);
        final EditText comment = (EditText) dialog.findViewById(R.id.dialogComment);

        btnPostComment.setOnClickListener(v -> {
            Log.d(TAG, "postCommentDialog: ");

            //Retrofit for Commenting
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URLs.COMMENT_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RedditAPI redditAPI = retrofit.create(RedditAPI.class);
            HashMap<String, String> headerMap = new HashMap<>();
            headerMap.put("User-Agent", username);
            headerMap.put("X-Modhash", modhash);
            headerMap.put("cookie", "reddit_session=" + cookie);

            Call<CheckComment> call = redditAPI.submitComment(headerMap, "comment", postID, comment.getText().toString());
            call.enqueue(new Callback<CheckComment>() {
                @Override
                public void onResponse(Call<CheckComment> call, Response<CheckComment> response) {
                    try{
                        Log.d(TAG, "onResponse: " + response.toString());
                        String commentSuccess = response.body().getSuccess();

                        if (commentSuccess.equals("true")){
                            dialog.dismiss();
                            Toast.makeText(CommentsActivity.this, "Post Successful", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(CommentsActivity.this, "An Error Occurred. Did you Login?", Toast.LENGTH_SHORT).show();
                        }

                    }catch (NullPointerException e){
                        Log.e(TAG, "onResponse: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<CheckComment> call, Throwable t) {
                    Log.e(TAG, "onFailure" + t.getMessage());
                }
            });

        });


    }


    private void createRedditCommentAPI(){
        if (commentFeedAPI == null){
            commentFeedAPI = new Retrofit.Builder()
                    .baseUrl(URLs.BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .build()
                    .create(RedditAPI.class);
        }

        commentFeedAPI.getFeed(currentPostURL)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError);
    }


    private void handleResponse(RedditFeed redditFeed){
        List<Entry> entries = redditFeed.getEntries();
        Log.d(TAG, "handleResponse");

        comments = new ArrayList<>();
        for(int i = 1; i < entries.size(); i++) {
            ParseXML comment = new ParseXML("<div class=\"md\"><p>", entries.get(i).getContent(), "</p>");
            List<String> commentContent = comment.parse();

            try{
                comments.add(new Comment(
                        commentContent.get(0),
                        entries.get(i).getAuthor().getName(),
                        entries.get(i).getUpdated(),
                        entries.get(i).getId()
                ));
            } catch (IndexOutOfBoundsException e){
                comments.add(new Comment(
                        "Error reading comment",
                        "Error",
                        "Error",
                        "Error"
                ));
                Log.e(TAG, "handleResponse: NullPointerException: " + e.getMessage());
            } catch (NullPointerException e){
                comments.add(new Comment(
                        commentContent.get(0),
                        "None",
                        entries.get(i).getUpdated(),
                        entries.get(i).getId()
                ));
                Log.e(TAG, "handleResponse: NullPointerException: " + e.getMessage());
            }
        }

            Observable<List<Comment>> observable = Observable.fromArray(comments);
            mAdapter = new CommentListAdapter(observable);

        mAdapter.getCommentClickedObservable()
                .subscribe(comment -> {
                    postCommentDialog(comment.getId());
                });

        mRecyclerView.setAdapter(mAdapter);
    }

    private void handleError(Throwable error){
        Logger.e("onFailure: Unable to retrieve RSS: " + error.getMessage());
        Toast.makeText(CommentsActivity.this, "An Error Occurred", Toast.LENGTH_LONG).show();
    }

    /**
     * Loading the session params if user is logged in.
     */
    private void loadSessionParams(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CommentsActivity.this);

        username = preferences.getString("@string/session_username", "");
        modhash = preferences.getString("@string/session_modhash", "");
        cookie = preferences.getString("@string/session_cookie", "");

        Log.d(TAG, "saveSessionParams: Session Variables: \n" +
                "username: " + username + "\n" +
                "modhash: " + modhash + "\n" +
                "cookie: " + cookie + "\n");

    }


}
