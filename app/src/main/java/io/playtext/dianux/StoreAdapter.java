package io.playtext.dianux;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.io.FilenameUtils;

import java.util.List;


class StoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public static String TAG = "StoreAdapter";

    public Context context;
    private List<Store> list;


    StoreAdapter(Context context, List<Store> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.card_item_store_activity, parent, false);
        viewHolder = new StoreViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Store store = list.get(holder.getAdapterPosition());

        StoreViewHolder storeViewHolder = (StoreViewHolder) holder;

        storeViewHolder.title.setText(FilenameUtils.removeExtension(store.getTitle()));
        storeViewHolder.author.setText(store.getAuthor());
        storeViewHolder.language.setText(store.getLanguage());
        storeViewHolder.tags.setText(store.getTags());
        storeViewHolder.price.setText("Free");
        storeViewHolder.splitter.setText(store.getSplitter());


        if(store.getStatus() == 0){
            storeViewHolder.status.setVisibility(View.GONE);
        } else if (store.getStatus() == 1){
            storeViewHolder.status.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return (list == null || list.isEmpty()) ? 0 : list.size() ;
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
