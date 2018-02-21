package com.example.cosmin.metrorex.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.cosmin.metrorex.R;

import java.util.List;

/**
 * Created by Cosmin on 6/7/2017.
 */

public class AbonamentAdapter extends BaseAdapter{

    private List<Integer> mAbonamentList;
    private Context mContext;

    public AbonamentAdapter(List<Integer> mAbonamentList, Context mContext) {
        this.mAbonamentList =mAbonamentList;
        this.mContext = mContext;
    }
    //get the number of the list
    @Override
    public int getCount() {
        if (mAbonamentList == null)
            return 0;
        else
            return mAbonamentList.size();
    }

    //get the object from a specified position
    @Override
    public Object getItem(int position) {
        if (mAbonamentList == null)
            return null;
        else
            return mAbonamentList.get(position);
    }

    //get the item it from a specified position
    @Override
    public long getItemId(int position) {
        return 0;
    }

    //build the view of the item from a specified position
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        final int currentPosition = position;
        AbonamentAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int layoutId = R.layout.abonament_item;
            view = layoutInflater.inflate(layoutId, parent, false);
            viewHolder = new AbonamentAdapter.ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (AbonamentAdapter.ViewHolder) view.getTag();
        }

        Integer Abonament = (Integer) getItem(currentPosition);
        viewHolder.mAbonamentTextView.setText("Abonament:"+Abonament);

        return view;


    }
    //Design pattern ViewHolder

    class ViewHolder {
        protected TextView mAbonamentTextView;

        public ViewHolder(View view){
            mAbonamentTextView = (TextView) view.findViewById(R.id.tvabonament_adapter);
        }
    }
}
