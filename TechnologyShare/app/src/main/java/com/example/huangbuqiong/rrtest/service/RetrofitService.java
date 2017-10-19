package com.example.huangbuqiong.rrtest.service;


import com.example.huangbuqiong.rrtest.service.entity.Book;
import com.example.huangbuqiong.rrtest.service.entity.Movie;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * URL：https://api.douban.com/v2/book/search?q=金瓶梅&tag=&start=0&count=1
 * Created by huangbuqiong on 2017/10/16.
 */

public interface RetrofitService {
    @GET ("book/search")
    Observable<Book> getSearchBook (@Query("q") String name,
                                    @Query("tag") String tag,
                                    @Query("start") int start,
                                    @Query("count") int count);

    @FormUrlEncoded
    @POST ("movie/top250")
    Observable<Movie> getTopMovie (@Field("start") int start,
                                   @Field("count") int count);
}
