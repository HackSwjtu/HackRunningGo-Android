package top.catfish.hackrunninggo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import top.catfish.hackrunninggo.R;
import top.catfish.hackrunninggo.dao.Route;

/***
 * Created by Catfish on 2016/10/24.
 */

public class RouteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    public int selectPos;
    private List<Route> list;
    private LayoutInflater inflater;
    private OnRecyclerViewItemClickListener mRecyclerViewItemClickListener = null;

    public RouteAdapter(Context context, List<Route> list) {
        this.list = list;
        inflater = LayoutInflater.from(context);
        selectPos = -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_listview, parent, false);
        ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder vHolder = (ViewHolder) holder;
        Route route = list.get(position);
        if(null == route)
            return;
        vHolder.tv.setText(route.getName());
        vHolder.view.setTag(String.valueOf(position));
        if (selectPos == position) {
            vHolder.image.setVisibility(View.VISIBLE);
        } else {
            vHolder.image.setVisibility(View.GONE);
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mRecyclerViewItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {
        if (mRecyclerViewItemClickListener != null) {
            mRecyclerViewItemClickListener.onItemClick(v, "123");
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView tv;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.list_item_text);
            view = itemView;
            image = (ImageView) itemView.findViewById(R.id.list_item_check_image);
        }
    }

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, String data);
    }
}
