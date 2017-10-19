package com.example.huangbuqiong.rrtest.service.manager;

import android.content.Context;

import com.example.huangbuqiong.rrtest.service.RetrofitHelper;
import com.example.huangbuqiong.rrtest.service.RetrofitService;
import com.example.huangbuqiong.rrtest.service.entity.Book;
import com.example.huangbuqiong.rrtest.service.entity.Movie;

import rx.Observable;

/**
 * Created by huangbuqiong on 2017/10/16.
 */

public class DataManager {
    private RetrofitService mRetrofitService;

    public DataManager(Context context) {
        this.mRetrofitService = RetrofitHelper.getInstance(context).getServer();
    }

    public Observable<Book> getSearchBooks (String name,String tag,int start,int count) {
        return mRetrofitService.getSearchBook(name,tag,start,count);
    }

    public Observable<Movie> getTopMovie (int start,int count) {
        return mRetrofitService.getTopMovie(start, count);
    }
}
