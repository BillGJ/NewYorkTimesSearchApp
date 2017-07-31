package com.ebillson.nytimessearch.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ebillson.nytimessearch.models.Article;
import com.ebillson.nytimessearch.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.ebillson.nytimessearch.R.id.ivImage;

/**
 * Created by Ebillson GJ on 7/27/2017.
 */

public class ArticleArrayAdapter extends ArrayAdapter<Article>  {


    public ArticleArrayAdapter(Context context, List<Article>articles){

        super(context,android.R.layout.simple_list_item_1, articles);


    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get data item for position
        Article article = this.getItem(position);
        //check to see if existing view is being reused
        //not using a recycler view -> inflate the layout

        if (convertView == null){

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
        }
        // find the imageview
        ImageView imageView = (ImageView)convertView.findViewById(ivImage);
        //clear out recycled image from convert view from last time
        imageView.setImageResource(0);

        TextView tvTitle = (TextView)convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(article.getHeadLine());

        //populate the thumbNail image
        //remote download the image in the background

        String thumbnail = article.getThumnNail();




        if(!TextUtils.isEmpty(thumbnail)) {


           // Picasso.with(getContext()).load(thumbnail).into(imageView);
         //   Picasso.with(getContext()).load(thumbnail).into(imageView);
            Picasso.with(getContext()).load(thumbnail).resize(250,250).centerCrop()
                    .into(imageView);
        }


        return  convertView;
    }
}
