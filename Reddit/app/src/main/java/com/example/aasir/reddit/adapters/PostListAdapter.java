package com.example.aasir.reddit.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aasir.reddit.R;
import com.example.aasir.reddit.model.Post;
import com.jakewharton.rxbinding2.view.RxView;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Aasir on 7/10/2017.
 */
//TODO: Figure out ProgressBar
public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ReactivePostViewHolder>{

    private List<Post> posts;
    private final PublishSubject<Post> onClickPost = PublishSubject.create();

    private final Observable<List<Post>> observable;

    public PostListAdapter(Observable<List<Post>> observable){
        this.observable = observable;
        this.posts = Collections.emptyList();

        this.observable.observeOn(AndroidSchedulers.mainThread()).subscribe(items -> {
           this.posts = items;
           this.notifyDataSetChanged();
        });
    }

    public Observable<Post> getPostClickedObservable(){
        return onClickPost;
    }

    @Override
    public ReactivePostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View postView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout_main, parent, false);
        ReactivePostViewHolder holder = new ReactivePostViewHolder(postView);

        RxView.clicks(postView)
                .takeUntil(RxView.detaches(parent))
                .map(aVoid -> holder.getCurrentPost())
                .subscribe(onClickPost);

        return holder;
    }

    @Override
    public void onBindViewHolder(ReactivePostViewHolder reactivePostViewHolder, int position) {
        Post post = posts.get(position);
        reactivePostViewHolder.setCurrentPost(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class ReactivePostViewHolder extends RecyclerView.ViewHolder{

        private TextView Title;
        private TextView Author;
        private TextView Date_updated;
        private ImageView ThumbnailImage;
        private Post currentPost;

        public ReactivePostViewHolder(View v) {
            super(v);
            Title = (TextView) v.findViewById(R.id.cardTitle);
            Author = (TextView) v.findViewById(R.id.cardAuthor);
            Date_updated = (TextView) v.findViewById(R.id.cardUpdated);
            ThumbnailImage = (ImageView) v.findViewById(R.id.cardImage);
        }

        public Post getCurrentPost() {
            return currentPost;
        }

        public void setCurrentPost(Post currentPost) {
            this.currentPost = currentPost;

            this.Title.setText(currentPost.getTitle());
            this.Author.setText(currentPost.getAuthor());
            this.Date_updated.setText(currentPost.getDate_updated());

            Context context = this.ThumbnailImage.getContext();
            Picasso.with(context)
                    .load(currentPost.getThumbnailURL())
                    .placeholder(R.drawable.image_failed)
                    .error(R.drawable.image_failed)
                    .into(this.ThumbnailImage);
        }
    }

}
