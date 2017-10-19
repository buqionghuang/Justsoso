package com.example.huangbuqiong.rrtest;

import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.huangbuqiong.rrtest.fragment.MovieFragment;
import com.example.huangbuqiong.rrtest.service.entity.Book;
import com.example.huangbuqiong.rrtest.service.entity.Movie;
import com.example.huangbuqiong.rrtest.service.presenter.BookPresenter;
import com.example.huangbuqiong.rrtest.service.view.BookView;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private BookPresenter mBookPresenter = new BookPresenter(this);
    private Context mContext;
    private String mDataType;
    private ProgressBar mPorgress;
    private MovieFragment mFragment;

    private BookView mBookView = new BookView() {
        @Override
        public void onSuccess(Object obj) {
            if (mDataType.equals("电影")) {
                mFragment.setData((Movie)obj);
//                Toast.makeText(mContext, ((Movie)obj).getSubjects().toString(), Toast.LENGTH_SHORT).show();
            } else if (mDataType.equals("书籍")) {
                Toast.makeTgiext(mContext, ((Book)obj).getBooks().toString(), Toast.LENGTH_SHORT).show();
            }
            mPorgress.setVisibility(View.GONE);
        }

        @Override
        public void onError(String result) {
            Toast.makeText(MainActivity.this,result, Toast.LENGTH_SHORT).show();
            mPorgress.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        mContext = getApplicationContext();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mPorgress = (ProgressBar) findViewById(R.id.progress);
        mFragment = new MovieFragment();
        NavigationView navigation = (NavigationView)findViewById(R.id.navigation_view);
        navigation.setItemIconTintList(null);
        navigation.getHeaderView(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "SUPER MARIO!!!", Toast.LENGTH_SHORT).show();
            }
        });
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getTitle().equals("电影")) {
                    FragmentTransaction tt = getFragmentManager().beginTransaction();
                    tt.add(R.id.container, mFragment);
                    tt.commit();
                    mBookPresenter.getTopMovie(0, 50);
                } else if (item.getTitle().equals("书籍")) {
                    mBookPresenter.getSearchBooks("金瓶梅", null, 0, 1);
                }
                mPorgress.setVisibility(View.VISIBLE);
                mDataType = item.getTitle().toString();
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        mBookPresenter.onCreate();
        mBookPresenter.onAttachView(mBookView);

//        mBookClick = (Button) findViewById(R.id.book);
//        mMovieClick = (Button) findViewById(R.id.movie);
//        mBookClick.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                /**
//                 * retrofit
//                 */
//                Retrofit retrofit = new Retrofit.Builder()
//                        .baseUrl("https://api.douban.com/v2/")
//                        .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
//                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                        .build();
//                RetrofitService service = retrofit.create(RetrofitService.class);
//                Call<Book> call = service.getSearchBook("金瓶梅", null, 0, 1);
//                call.enqueue(new Callback<Book>() {
//                    @Override
//                    public void onResponse(Call<Book> call, Response<Book> response) {
//                        mResult.setText(response.body()+ "");
//                    }
//
//                    @Override
//                    public void onFailure(Call<Book> call, Throwable t) {
//
//                    }
//                });
                /**
                 * retrofit + rxJava
                 */
//                Observable<Book> observable = service.getSearchBook("金瓶梅", null, 0, 1);
//                observable.subscribeOn(Schedulers.io())//请求数据的事件发生在IO线程
//                          .observeOn(AndroidSchedulers.mainThread())
//                          .subscribe(new Observer<Book>() {
//                              @Override
//                              public void onCompleted() {
//                                    //所有事件都完成，可以做些操作。。。
//                              }
//
//                              @Override
//                              public void onError(Throwable e) {
//                                  e.printStackTrace(); //请求过程中发生错误
//                              }
//
//                              @Override
//                              public void onNext(Book book) {
//                                  mResult.setText(book.getBooks().get(0).getTitle());
//                              }
//                          });
//                /**
//                 * 封装后的retrofit + rxJava + OkHttp
//                 */
//                mBookPresenter.getSearchBooks("金瓶梅", null, 0, 1);
//            }
//        });

//        mMovieClick.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mBookPresenter.getSearchBooks("金瓶梅", null, 0, 1);
//            }
//        });
    }

}
