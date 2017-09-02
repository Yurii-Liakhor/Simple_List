package com.simplelist.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplelist.Objects.SortItem;
import com.simplelist.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Yurii on 01.08.2017.
 */

public class SortListAdapter extends ArrayAdapter<SortItem> {

    private Context context;

    static class ViewHolder {

        @Bind(R.id.sort_img) ImageView imageView;
        @Bind(R.id.sort_text) TextView textView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public SortListAdapter(Context context, ArrayList<SortItem> items){
        super(context, 0, items);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SortItem item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.sort_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.setImageResource(item.getImageRes());
        viewHolder.textView.setText(item.getTextRes());
        // Return the completed to render on screen
        return convertView;
    }
}
