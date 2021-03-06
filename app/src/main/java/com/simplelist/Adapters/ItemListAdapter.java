package com.simplelist.Adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.util.LruCache;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.net.Uri;

import com.simplelist.Objects.Item;
import com.simplelist.R;
import com.simplelist.RoundedAvatarDrawable;
import com.simplelist.Views.CustomImageView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Yurii on 26.07.2017.
 */

public class ItemListAdapter extends ArrayAdapter<Item> {
    private Context context;
    private ArrayList<Integer> colors;

    static class ViewHolder {
        TextView title;
        TextView description;
        CustomImageView image;

        String imageStr;
        int color;
    }

    public ItemListAdapter(Context context, ArrayList<Item> items) {
        super(context, 0, items);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Item item = getItem(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item_img, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title_2);
            viewHolder.description = (TextView) convertView.findViewById(R.id.description_2);
            viewHolder.image = (CustomImageView) convertView.findViewById(R.id.image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.image.setImageBitmap(null);
        }

        String title = item.getTitle();
        String description = item.getDescription();
        String image = item.getImage();
        int color = item.getColor();
        //String uuid = item.getUuid();

        //if(title != null && description != null)

        int[] colorsArr = context.getResources().getIntArray(R.array.color_palette);
        colors = new ArrayList<Integer>(colorsArr.length);
        for(int i : colorsArr) colors.add(i);

        if(((ListView)parent).isItemChecked(position))
            convertView.setBackgroundResource(R.drawable.background_activated);
        else
            convertView.setBackgroundColor(adjustAlpha(colors.get(color), 0.4f));

        int titleLength;
        int descLength;
        int display_mode = context.getResources().getConfiguration().orientation;
        if (display_mode == Configuration.ORIENTATION_PORTRAIT) {
            titleLength = 18;
            descLength = 25;
        } else {
            titleLength = 70;
            descLength = 40;
        }
        if (description != null && description.length() > descLength) {
            description = description.substring(0, descLength);
            description = description + "...";
        }
        if (title != null && title.length() > titleLength) {
            title = title.substring(0, titleLength);
            title = title + "...";
        }
        viewHolder.title.setText(title);
        viewHolder.description.setText(description);
        viewHolder.imageStr = image;
        viewHolder.color = color;
        new LoadImageTask().execute(viewHolder); //image load Task

        return convertView;
    }

    private class LoadImageTask extends AsyncTask<ViewHolder, Void, ViewHolder>{
        private RoundedAvatarDrawable bitmap;

        @Override
        protected ViewHolder doInBackground(ViewHolder... params) {
            ViewHolder viewHolder = params[0];
            String image = viewHolder.imageStr;
            int color = viewHolder.color;
            if (image != null && !image.equals("")) {
                File imgFile = new File(image);
                if (imgFile.exists()) {
                    File cacheFile = readImageFromCache(imgFile);
                    if(cacheFile != null)
                        bitmap = new RoundedAvatarDrawable(decodeSampledBitmapFromPath(cacheFile, 70, 70));
                    else {
                        bitmap = new RoundedAvatarDrawable(scaleCenterCrop(decodeSampledBitmapFromPath(imgFile, 70, 70)));
                        writeImageToCache(bitmap, imgFile);
                    }
                } else {
                    bitmap = new RoundedAvatarDrawable(Bitmap.createBitmap(70, 70, Bitmap.Config.RGB_565));
                    bitmap.setColorFilter(colors.get(color), PorterDuff.Mode.ADD);
                }
            } else {
                bitmap = new RoundedAvatarDrawable(Bitmap.createBitmap(70, 70, Bitmap.Config.RGB_565));
                bitmap.setColorFilter(colors.get(color), PorterDuff.Mode.ADD);
                //bitmap.eraseColor(Color.parseColor("#98c639"));
            }
            return viewHolder;
        }

        @Override
        protected void onPostExecute(ViewHolder viewHolder) {
            viewHolder.image.setImageDrawable(bitmap);
        }
    }

    private void writeImageToCache(RoundedAvatarDrawable rad, File file){

        String filename = file.getName();
        //String filename = SystemClock.currentThreadTimeMillis() + ".png";
        //File file = new File(context.getCacheDir(), filename);
        File dir = context.getCacheDir();
        String filepath = dir.getAbsolutePath() + "/" + filename;

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filepath);
            rad.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File readImageFromCache(File file){
        File path = context.getCacheDir();
        //File[] files = path.listFiles();
        File cacheFile = new File(path.getAbsolutePath() + "/" + file.getName());
        if(cacheFile.exists()){
            return cacheFile;
        }
        return null;
    }

    private Bitmap decodeSampledBitmapFromPath(File imgFile, int reqWidth, int reqHeight) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(imgFile), null, options);
            //BitmapFactory.decodeFile(imgFile.getAbsolutePath(),options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(new FileInputStream(imgFile), null, options);
        } catch (FileNotFoundException ex) {
            Bitmap bitmap = Bitmap.createBitmap(70, 70, Bitmap.Config.RGB_565);
            bitmap.eraseColor(Color.parseColor("#98c639"));
            return bitmap;
        }
    }

    private Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth)
                inSampleSize *= 2;
        }
        return inSampleSize;
    }

    private Bitmap scaleCenterCrop(Bitmap bitmap) {
        int sourceWidth = bitmap.getWidth();
        int sourceHeight = bitmap.getHeight();

        if (sourceHeight > sourceWidth){
            //int upper = Math.round((float)1/16 * (float)sourceHeight);
            int y = sourceHeight/2 - sourceWidth/2;
            int upper = y/2;
            return Bitmap.createBitmap(bitmap, 0, y - upper, sourceWidth, sourceWidth);
        } else if(sourceWidth > sourceHeight) {
            //int x = Math.round((float)1/4 * (float)sourceWidth);
            int x = sourceWidth/2 - sourceHeight/2;
            return Bitmap.createBitmap(bitmap, x, 0, sourceHeight, sourceHeight);
        }
        return bitmap;
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }
}

/*
if (image != null && !image.equals("")) {
            File imgFile = new File(image);
            if (imgFile.exists()) {
                //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                //viewHolder.image.setImageBitmap(myBitmap);
                Bitmap bitmap = scaleCenterCrop(decodeSampledBitmapFromPath(imgFile, 70, 70));
                viewHolder.image.setImageDrawable(new RoundedAvatarDrawable(bitmap));
                /*Bitmap backbit = Bitmap.createBitmap(70, 70, Bitmap.Config.RGB_565);
                backbit.eraseColor(context.getResources().getColor(R.color.colorPrimaryDark));
                viewHolder.image.setBackground(new RoundedAvatarDrawable(backbit));
            }
                    } else {
                    //viewHolder.image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    //viewHolder.image.setAlpha((float) 0.5);
                    Bitmap bitmap = Bitmap.createBitmap(70, 70, Bitmap.Config.RGB_565);
                    bitmap.eraseColor(Color.parseColor("#98c639"));
                    //Bitmap bitmap = scaleCenterCrop(decodeSampledBitmapFromResource(context.getResources(), R.drawable.hamster, 70, 70));
                    viewHolder.image.setImageDrawable(new RoundedAvatarDrawable(bitmap));
            /*Bitmap backbit = Bitmap.createBitmap(70, 70, Bitmap.Config.RGB_565);
            backbit.eraseColor(context.getResources().getColor(R.color.colorPrimaryDark));
            viewHolder.image.setBackground(new RoundedAvatarDrawable(backbit));
                    }
 */