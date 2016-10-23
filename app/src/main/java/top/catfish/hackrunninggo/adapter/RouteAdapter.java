package top.catfish.hackrunninggo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import java.util.List;

import top.catfish.hackrunninggo.R;
import top.catfish.hackrunninggo.dao.Route;

/***
 * Created by Catfish on 2016/10/24.
 */

public class RouteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Route> list;
    private LayoutInflater inflater;
    public RouteAdapter(Context context,List<Route> list){
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_listview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder vHolder = (ViewHolder)holder;
        vHolder.tv.setText(list.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tv;
        public CheckBox cb;
        public ViewHolder(View itemView) {
            super(itemView);
            tv = (TextView)itemView.findViewById(R.id.list_item_text);
            cb = (CheckBox)itemView.findViewById(R.id.list_item_checkBox);
        }
    }
}
