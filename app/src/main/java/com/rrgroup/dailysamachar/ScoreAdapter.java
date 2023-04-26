package com.rrgroup.dailysamachar;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private Context mContext;
    private ArrayList<NewsItem> mScoreList;
    private onScoreBoardClickListener mListener;

    public ScoreAdapter(Context context, ArrayList<NewsItem> NewsList) {
        mContext=context;
        mScoreList=NewsList;
    }

    public void setOnScoreClickListener(onScoreBoardClickListener listener) {
        mListener=listener;
    }


    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public interface onScoreBoardClickListener{
        void onScoreClick(int position);
    }


    @NonNull
    @Override
    public ScoreAdapter.ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.score_card,parent,false);
        return new ScoreViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        NewsItem currentItem = mScoreList.get(position);
        String title = currentItem.getTitle();
        String team1 =currentItem.getTeamOne();
        String team2=currentItem.getTeamTwo();
        String details = currentItem.getUpdate();

        holder.onScoreClick.setOnClickListener(view->{
            if(mListener!= null){
                if (position!=RecyclerView.NO_POSITION){
                    mListener.onScoreClick(position);
                }
            }
        });



        holder.matchTitle.setText(title);
        holder.team1.setText(team1);
        holder.team2.setText(team2);
        holder.results.setText(details);


    }

    @Override
    public int getItemCount() {
        return mScoreList.size();
    }

    public class ScoreViewHolder extends RecyclerView.ViewHolder{
        public TextView matchTitle;
        public TextView team1,team2,results;
        CardView onScoreClick;

        public ScoreViewHolder(@NonNull View itemView){
            super(itemView);
            matchTitle=itemView.findViewById(R.id.matchTitle);
            team1=itemView.findViewById(R.id.team1);
            team2=itemView.findViewById(R.id.team2);
            results=itemView.findViewById(R.id.results);
            onScoreClick=itemView.findViewById(R.id.scoreClick);
        }
    }
}
