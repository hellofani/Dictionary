package com.faniapps.dict;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> implements Filterable {
    private ArrayList<Dictionary> mArrayList;
    private ArrayList<Dictionary> mFilteredList;
    Activity _activity;

    public DataAdapter(ArrayList<Dictionary> arrayList, Activity mainActivity) {
        mArrayList = arrayList;
        mFilteredList = arrayList;
        _activity = mainActivity;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.tv_name.setText(mFilteredList.get(i).getTelugu_word());
        viewHolder.tv_version.setText(mFilteredList.get(i).getEnglish_word());
        viewHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("clicked item pos  ",i+"  "+mFilteredList.get(i).getEnglish_word());
                Intent intent =new Intent(_activity,WordDetails.class);
                intent.putExtra("eng",mFilteredList.get(i).getEnglish_word());
                intent.putExtra("tel",mFilteredList.get(i).getTelugu_word());
                _activity.startActivity(intent);
            }
        });
      //  viewHolder.tv_api_level.setText(mFilteredList.get(i).getApi());

    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = mArrayList;
                } else {

                    ArrayList<Dictionary> filteredList = new ArrayList<>();

                    for (Dictionary androidVersion : mArrayList) {

                        if (androidVersion.getEnglish_word().toLowerCase().contains(charString) || androidVersion.getTelugu_word().contains(charString) ) {

                            filteredList.add(androidVersion);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<Dictionary>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_name,tv_version,tv_api_level;
        CardView card;
        public ViewHolder(View view) {
            super(view);
            card =  (CardView)view.findViewById(R.id.cardview);
            tv_name = (TextView)view.findViewById(R.id.tv_name);
            tv_version = (TextView)view.findViewById(R.id.tv_version);
           // tv_api_level = (TextView)view.findViewById(R.id.tv_api_level);
            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = mRecyclerView.getChildAdapterPosition(v);

                }
            });*/
        }
    }

}