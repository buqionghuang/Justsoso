package com.example.huangbuqiong.rrtest.service.presenter;

import android.content.Context;
import android.content.Intent;

import com.example.huangbuqiong.rrtest.service.entity.Book;
import com.example.huangbuqiong.rrtest.service.entity.Movie;
import com.example.huangbuqiong.rrtest.service.manager.DataManager;
import com.example.huangbuqiong.rrtest.service.view.BookView;
import com.example.huangbuqiong.rrtest.service.view.View;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by huangbuqiong on 2017/10/16.
 */

public class BookPresenter implements Presenter {

    private DataManager manager;
    private CompositeSubscription compositeSubscription;
    private Context context;
    private BookView mBookView;
    private Book mBook;
    private Movie mMovie;

    public BookPresenter(Context context) {
        this.context = context;
    }
    @Override
    public void onCreate() {
        manager = new DataManager(context);
        compositeSubscription = new CompositeSubscription();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {
        if (compositeSubscription.hasSubscriptions()){
            compositeSubscription.unsubscribe();
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onAttachView(View view) {
        mBookView = (BookView) view;
    }

    @Override
    public void attachIncomingIntent(Intent intent) {

    }

    public void getSearchBooks(String name, String tag, int start, int count) {
        compositeSubscription.add(manager.getSearchBooks(name, tag, start, count)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Book>() {
                    @Override
                    public void onCompleted() {
                        if (mMovie != null) {
                            mBookView.onSuccess(mBook);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mBookView.onError("请求失败！！");
                    }

                    @Override
                    public void onNext(Book book) {
                        mBook = book;
                    }
                }));
    }

    public void getTopMovie(int start, int count) {
        compositeSubscription.add(manager.getTopMovie(start, count)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Movie>() {
                    @Override
                    public void onCompleted() {
                        if (mMovie != null) {
                            mBookView.onSuccess(mMovie);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mBookView.onError("请求失败！！");
                    }

                    @Override
                    public void onNext(Movie movie) {
                        mMovie = movie;
                    }
                }));
    }
}
