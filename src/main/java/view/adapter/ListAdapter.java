package view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import gsrini.movienow.view.R;
import model.constant.Constants;
import model.vo.ListVO;

/**
 * Created by geethasrini on 4/7/15.
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> implements Constants {

    // creates an instance of a row and saves all necessary info for it
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

       // TextView image;
        TextView listItem1;
        TextView listItem2;

        public ViewHolder(View view) {
            super(view);
           // this.image = (TextView) view.findViewById(R.id.list_image);
            this.listItem1 = (TextView) view.findViewById(R.id.list_item1);
            this.listItem2 = (TextView) view.findViewById(R.id.list_item2);

            view.setOnClickListener(this);
        }

        public void onClick(View v) {

            Intent intent = new Intent(context, view.activity.WebView.class);

            TextView text = (TextView) v.findViewById(R.id.list_item1);
            String name = (String) text.getText();
            name.replaceAll(" ", "_");
            Log.d(TAG, "Name: " + name);

            String url = WIKI_URL + name;
            intent.putExtra("url", url);
            context.startActivity(intent);

        }
    }

    private final Context context;
    private List<ListVO> list = Collections.emptyList();

    private String TAG = this.getClass().getSimpleName();

    // the context is needed to inflate views in getView()
    public ListAdapter(Context context, List<ListVO> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final ListVO listVO =  list.get(position);

        //reset values
        holder.listItem1.setText("");
        holder.listItem2.setText("");

        if(listVO != null) {

//            if(listVO.getStr1() != null) {
//
//                if (listVO.getStr1().equalsIgnoreCase("rotten")) {
//
//                    // critic score considered bad
////                    Picasso.with(context)
////                            .load(R.drawable.rotten)
////                            .resize(30, 20)
////                            .into(holder.image);
//                } else {
//                    // critic score considered good
////                    Picasso.with(context)
////                            .load(R.drawable.fresh)
////                            .resize(30, 20)
////                            .into(holder.image);
//                }
//            }
//            else {
//                //holder.image.setVisibility(View.GONE);  // hide image (make the view gone)
//            }

            if(listVO.getStr2() != null && listVO.getStr3() != null) {
                holder.listItem1.setText(listVO.getStr2());
                holder.listItem2.setText(listVO.getStr3());
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.more_info_row, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}

