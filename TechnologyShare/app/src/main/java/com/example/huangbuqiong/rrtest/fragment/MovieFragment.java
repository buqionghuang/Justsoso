package com.example.huangbuqiong.rrtest.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.huangbuqiong.rrtest.R;
import com.example.huangbuqiong.rrtest.service.entity.Movie;

/**
 * Created by huangbuqiong on 2017/10/17.
 */

public class MovieFragment extends Fragment {
    private RecyclerView mMovieList;
    private Movie mMovie;
    private Adapter mAdapter;
    private Context mContext;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movie_fragment,null);
        mMovieList = (RecyclerView) view.findViewById(R.id.list);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mMovieList.setLayoutManager(manager);
        mMovieList.addItemDecoration(new SpaceItemDecoration(5));
        mAdapter = new Adapter();
        mMovieList.setAdapter(mAdapter);
        return view;
    }

    public void setData (Movie movie) {
        mMovie = movie;
        mAdapter.notifyDataSetChanged();
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder>{
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_movie, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Movie.SubjectsBean sub = mMovie.getSubjects().get(position);
            holder.name.setText(sub.getTitle());
            holder.average.setText(sub.getRating().getAverage()+"");
            holder.director.setText(sub.getDirectors().get(0).getName());
            holder.type.setText(sub.getSubtype());
            Glide.with(mContext).load(sub.getImages().getSmall()).into(holder.img);
        }

        @Override
        public int getItemCount() {
            if (mMovie != null) {
                return mMovie.getSubjects().size();
            }
            return 0;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView name;
        private TextView type;
        private TextView average;
        private TextView director;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            type = (TextView) itemView.findViewById(R.id.type);
            average = (TextView) itemView.findViewById(R.id.average);
            director = (TextView) itemView.findViewById(R.id.director);
            img = (ImageView) itemView.findViewById(R.id.img);
        }
    }

    class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        int mSpace;
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = mSpace;
            outRect.right = mSpace;
            outRect.bottom = mSpace;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = mSpace;
            }
        }

        public SpaceItemDecoration(int space) {
            this.mSpace = space;
        }
    }
}
