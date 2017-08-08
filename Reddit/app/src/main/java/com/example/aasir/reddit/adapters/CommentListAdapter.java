package com.example.aasir.reddit.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aasir.reddit.R;
import com.example.aasir.reddit.model.Post;
import com.example.aasir.reddit.model.comment.Comment;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Aasir on 7/10/2017.
 */
//TODO: Figure out ProgressBar
public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ReactiveCommentViewHolder>{

    private List<Comment> comments;
    private final PublishSubject<Comment> onClickComment = PublishSubject.create();

    private final Observable<List<Comment>> observable;

    public CommentListAdapter(Observable<List<Comment>> observable){
        this.observable = observable;
        this.comments = Collections.emptyList();

        this.observable.observeOn(AndroidSchedulers.mainThread()).subscribe(items -> {
           this.comments = items;
           this.notifyDataSetChanged();
        });
    }

    public Observable<Comment> getCommentClickedObservable(){
        return onClickComment;
    }

    @Override
    public ReactiveCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View postView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout_comment, parent, false);
        ReactiveCommentViewHolder holder = new ReactiveCommentViewHolder(postView);

//        RxView.clicks(postView)
//                .takeUntil(RxView.detaches(parent))
//                .map(aVoid -> holder.getCurrentComment())
//                .subscribe(onClickComment);

        return holder;
    }

    @Override
    public void onBindViewHolder(ReactiveCommentViewHolder reactiveCommentViewHolder, int position) {
        Comment comment = comments.get(position);
        reactiveCommentViewHolder.setCurrentComment(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class ReactiveCommentViewHolder extends RecyclerView.ViewHolder{

        private TextView Comment;
        private TextView Author;
        private TextView Date_updated;
        private Comment currentComment;

        public ReactiveCommentViewHolder(View v) {
            super(v);
            Comment = (TextView) v.findViewById(R.id.comment);
            Author = (TextView) v.findViewById(R.id.commentAuthor);
            Date_updated = (TextView) v.findViewById(R.id.commentUpdated);
        }

        public Comment getCurrentComment() {
            return currentComment;
        }

        public void setCurrentComment(Comment currentComment) {
            this.currentComment = currentComment;

            this.Comment.setText(currentComment.getComment());
            this.Author.setText(currentComment.getAuthor());
            this.Date_updated.setText(currentComment.getUpdated());

        }
    }

}
