package com.simplelist.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplelist.Objects.SortItem;
import com.simplelist.R;
import com.simplelist.RoundedAvatarDrawable;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Yurii on 07.09.2017.
 */

public class ColorGridAdapter extends ArrayAdapter<Integer>{

    private Context context;
    private RoundedAvatarDrawable bitmap;

    static class ViewHolder {
        ImageView imageView;
    }

    public ColorGridAdapter(Context context, ArrayList<Integer> colors){ //, int layout_id, int item_id
        super(context, 0, colors); //layout_id, item_id,
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        int color = getItem(position);

        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.grid_color_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.color_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        bitmap = new RoundedAvatarDrawable(Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565));
        bitmap.setColorFilter(color, PorterDuff.Mode.ADD);
        viewHolder.imageView.setImageDrawable(bitmap);

        return convertView;
    }

}
