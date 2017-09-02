package com.simplelist;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.simplelist.Objects.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Yurii on 16.08.2017.
 */

public class ItemsJsonSerializer {
    private Context mContext;
    //private String mFileName;

    public ItemsJsonSerializer(Context c){
        mContext = c;
    }

    public ArrayList<Item> loadItems(String mFileName) throws JSONException, IOException {
        ArrayList<Item> Items = new ArrayList<Item>();
        BufferedReader reader = null;
        try {
            String baseFolder;
            //InputStream in = mContext.openFileInput(mFileName);
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                baseFolder = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            }
            else {
                baseFolder = mContext.getFilesDir().getAbsolutePath();
            }
            File file = new File(baseFolder + "/" + mFileName);
            FileInputStream in = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            for (int i = 0; i < array.length(); i++) {
                JSONObject jObject = array.getJSONObject(i);
                Item item = new Item(jObject);
                Items.add(item);
            }
        } catch (FileNotFoundException e){
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            if(reader != null)
                reader.close();
        }
        return Items;
    }

    public void SaveItems(ArrayList<Item> Items, String mFileName)
            throws JSONException, IOException{
        JSONArray array = new JSONArray();
        for (Item item : Items)
            array.put(item.toJSON());

        Writer writer = null;
        String baseFolder;
        try{
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                baseFolder = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            }
            else {
                baseFolder = mContext.getFilesDir().getAbsolutePath();
            }
            FileOutputStream out = new FileOutputStream(new File(baseFolder + "/" + mFileName));
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        }
        finally {
            if(writer != null)
                writer.close();
        }
    }
}

//OutputStream out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);