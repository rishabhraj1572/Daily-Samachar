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

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context mContext;
    private ArrayList<NewsItem> mNewsList;
    private OnItemClickListener mListener;

    public NewsAdapter(Context context, ArrayList<NewsItem> NewsList) {
        mContext=context;
        mNewsList=NewsList;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void  setOnItemClickListener(OnItemClickListener listener){
        mListener= listener;
    }

    @NonNull
    @Override
    public NewsAdapter.NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.each_item,parent,false);
        return new NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem currentItem = mNewsList.get(position);
        String imgUrl = currentItem.getImage();
        String date =currentItem.getDate();
        String headline=currentItem.getHeadline();
        String src = currentItem.getSrc();

        holder.clickedNewsItem.setOnClickListener(view->{
            if(mListener!= null){
                if (position!=RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }
        });

//        String inputDateStr = date;
//        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        try {
//            Date inputDate = inputDateFormat.parse(inputDateStr);
//            SimpleDateFormat outputDateFormat = new SimpleDateFormat("d'th' MMMM, yyyy");
//            String outputDateStr = outputDateFormat.format(inputDate);
//            //System.out.println(outputDateStr); // Output: 5th April, 2023
//            //holder.mDate.setText(outputDateStr+" - "+headline);
//            String fullText = outputDateStr + " - " + headline;
//            SpannableStringBuilder sb = new SpannableStringBuilder(fullText);
//            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
//            sb.setSpan(boldSpan, 0, outputDateStr.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//            holder.mDate.setText(sb);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        //String src= headline.split("-")[src.length()-1];

        holder.mDate.setText(headline);
        holder.source.setText(src);


        Picasso.get()
                .load(imgUrl)
                .fit()
                //.transform(transformation)
                .into(holder.mImage);
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImage;
        public TextView mDate,mHeadline;
        public CardView clickedNewsItem;
        public TextView source;

        public NewsViewHolder(@NonNull View itemView){
            super(itemView);
            source=itemView.findViewById(R.id.source);
            mImage=itemView.findViewById(R.id.image);
            mDate=itemView.findViewById(R.id.details);
            mHeadline=itemView.findViewById(R.id.details);
            clickedNewsItem=itemView.findViewById(R.id.recyclerViewItem);
        }
    }
}
