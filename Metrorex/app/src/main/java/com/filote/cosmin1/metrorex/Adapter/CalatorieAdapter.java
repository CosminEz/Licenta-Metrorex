package com.filote.cosmin1.metrorex.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.filote.cosmin1.metrorex.R;

import java.util.List;

/**
 * Created by Cosmin on 5/18/2017.
 */

public class CalatorieAdapter extends BaseAdapter {

    private List<Integer> mCalatorieList;
    private Context mContext;

    public CalatorieAdapter(List<Integer> mCalatorieList, Context mContext) {
        this.mCalatorieList = mCalatorieList;
        this.mContext = mContext;
    }

    //get the number of the list
    @Override
    public int getCount() {
        if (mCalatorieList == null)
            return 0;
        else
            return mCalatorieList.size();
    }

    //get the object from a specified position
    @Override
    public Object getItem(int position) {
        if (mCalatorieList == null)
            return null;
        else
            return mCalatorieList.get(position);
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
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int layoutId = R.layout.calatorie_item;
            view = layoutInflater.inflate(layoutId, parent, false);
            viewHolder = new CalatorieAdapter.ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (CalatorieAdapter.ViewHolder) view.getTag();
        }

        Integer Calatorie = (Integer) getItem(currentPosition);
        viewHolder.mCalatorieTextView.setText("Calatorii:" + Calatorie);
        viewHolder.mCalatoriePretTextView.setText(2*Calatorie +" credite");

        return view;


    }
    //Design pattern ViewHolder

    class ViewHolder {
        protected TextView mCalatorieTextView;
        protected TextView mCalatoriePretTextView;

        public ViewHolder(View view) {
            mCalatorieTextView = (TextView) view.findViewById(R.id.tvcalatorie_adapter);
            mCalatoriePretTextView = (TextView) view.findViewById(R.id.tvcalatorii_pret_adapter);
        }
    }

}
