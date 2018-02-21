package com.example.cosmin.metrorex.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.cosmin.metrorex.R;

import java.util.List;

/**
 * Created by Cosmin on 5/17/2017.
 */

public class CreditAdapter extends BaseAdapter {
    private List<Integer> mCreditList;
    private Context mContext;

    public CreditAdapter(List<Integer> mCreditList, Context mContext) {
        this.mCreditList = mCreditList;
        this.mContext = mContext;
    }

    //get the number of the list
    @Override
    public int getCount() {
        if (mCreditList == null)
            return 0;
        else
            return mCreditList.size();
    }

    //get the object from a specified position
    @Override
    public Object getItem(int position) {
        if (mCreditList == null)
            return null;
        else
            return mCreditList.get(position);
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
            int layoutId = R.layout.credit_item;
            view = layoutInflater.inflate(layoutId, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        Integer credit = (Integer) getItem(currentPosition);
        viewHolder.mCreditTextView.setText("Credit:"+credit);

        return view;


    }
    //Design pattern ViewHolder

    class ViewHolder {
        protected TextView mCreditTextView;

        public ViewHolder(View view){
            mCreditTextView = (TextView) view.findViewById(R.id.tvcredit_adapter);
        }
    }


}
