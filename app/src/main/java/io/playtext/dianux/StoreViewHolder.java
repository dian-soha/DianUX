package io.playtext.dianux;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class StoreViewHolder extends RecyclerView.ViewHolder{
    public String TAG =  getClass().getSimpleName();
    TextView title, author, language, tags, price, splitter;
    ImageView status;

    StoreViewHolder(View view) {
        super(view);
        status = view.findViewById(R.id.imageView_status);

        title = view.findViewById(R.id.chapter_title);
        author = view.findViewById(R.id.chapter_author);
        language = view.findViewById(R.id.chapter_language);
        tags = view.findViewById(R.id.chapter_tags);
        price = view.findViewById(R.id.chapter_price);
        splitter = view.findViewById(R.id.chapter_splitter);

    }

}
