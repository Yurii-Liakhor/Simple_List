package com.simplelist;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.simplelist.Adapters.ColorGridAdapter;
import com.simplelist.Objects.Item;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Yurii on 28.06.2017.
 */

public class EditActivity extends AppCompatActivity {

    private static final String EXTRA_ITEM = "com.simplelist.item";
    private static final String EXTRA_NUMB = "com.simplelist.item_numb";

    private static final int RESULT_DELETED = 3;
    private static final int REQUEST_IMAGE = 1;

    private static final int REQUEST_TAKE_PHOTO = 2;

    private static final String STATE_ITEM = "ITEM";
    private static final String STATE_OLD_ITEM = "OLD_ITEM";
    //private static final String STATE_IMAGE = "IMAGE";
    //private static final String STATE_COLLAPSING = "COLLAPSING";

    private static final int DEFAULT_COLOR = 3;

    private Intent intent;
    private Intent returnIntent;
    private String title;
    private String description;
    private String image;
    private int color;
    //private List<String> oldImages;
    //private String oldImage;
    private Item item;
    private Item oldItem;
    private int numb;
    private ArrayList<Integer> colors;

    private EditText editText;
    private EditText editDescription;
    private ImageView editImage;
    private TextView charsLeft;
    private FloatingActionButton fabDone;
    private FloatingActionButton fabCam;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private Menu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();
        returnIntent = new Intent();
        item = intent.getParcelableExtra(EXTRA_ITEM);

        if(savedInstanceState != null){
            //image = savedInstanceState.getString(STATE_IMAGE);
            item = savedInstanceState.getParcelable(STATE_ITEM);
            oldItem = savedInstanceState.getParcelable(STATE_OLD_ITEM);
            image = item.getImage();
            color = item.getColor();
        } else {
            if(intent.hasExtra(EXTRA_ITEM)){
                oldItem = new Item(item);
                image = item.getImage();
                color = item.getColor();
            } else{
                color = DEFAULT_COLOR;
            }
        }
        setCustomTheme(color);
        setContentView(R.layout.activity_edit);

        editText = (EditText)findViewById(R.id.editText);
        editDescription = (EditText)findViewById(R.id.editDescription);
        charsLeft = (TextView)findViewById(R.id.charters_left);
        fabDone = (FloatingActionButton)findViewById(R.id.fabDone);
        fabCam = (FloatingActionButton)findViewById(R.id.camera);
        editImage = (ImageView)findViewById(R.id.edit_image);
        appBarLayout =  (AppBarLayout) findViewById(R.id.appbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(intent.hasExtra(EXTRA_NUMB))
            numb = intent.getExtras().getInt(EXTRA_NUMB);

        if(intent.hasExtra(EXTRA_ITEM)){ ///????
            title = item.getTitle();
            description = item.getDescription();
            color = item.getColor();

            editText.setText(title);
            editDescription.setText(description);
            //setTitle(name);
            if(title != null){
                if(!title.equals(""))
                    collapsingToolbar.setTitle(title);
                else
                    collapsingToolbar.setTitle("Title");

                editText.setSelection(title.length());
            } else
                collapsingToolbar.setTitle("Title");

            if(description != null)
                charsLeft.setText(String.valueOf(getResources().getInteger(R.integer.description_size) - description.length()));
            else
                charsLeft.setText(String.valueOf(getResources().getInteger(R.integer.description_size)));

            fabDone.setImageResource(R.drawable.ic_edit);
        } else{
            collapsingToolbar.setTitle("Title");
            if(item == null){
                item = new Item();
                editText.requestFocus();
                this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
            charsLeft.setText(String.valueOf(getResources().getInteger(R.integer.description_size)));
        }

        if(image != null){
            File imgFile = new File(image);
            Bitmap bitmap = decodeSampledBitmapFromPath(imgFile);
            editImage.setImageBitmap(bitmap);
        }

        int[] colorsArr = this.getResources().getIntArray(R.array.color_palette);
        colors = new ArrayList<Integer>(colorsArr.length);
        for(int color : colorsArr) colors.add(color);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                title = charSequence.toString();
                collapsingToolbar.setTitle(title);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() == 0)
                    collapsingToolbar.setTitle("Title");
            }
        });

        editDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                description = charSequence.toString();
                charsLeft.setText(String.valueOf(getResources().getInteger(R.integer.description_size) - charSequence.length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {
               // charsLeft.setText(String.valueOf(editable.length()));
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, REQUEST_IMAGE);
            }
        });

        editImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showImagePopupMenu(view);
                return true;
            }
        });

        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItem();
            }
        });

        fabCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putString(STATE_IMAGE, image);
        item.setImage(image);
        item.setColor(color);
        outState.putParcelable(STATE_ITEM, item);
        outState.putParcelable(STATE_OLD_ITEM, oldItem);
    }

    private void saveItem(){
        //String title = editText.getText().toString();
        //String description = editDescription.getText().toString();
        String uuid = null;
        if(intent.hasExtra(EXTRA_NUMB)) {
            uuid = item.getUuid();
        }
        item = new Item(uuid, title, description, image, color);
        returnIntent.putExtra(EXTRA_ITEM, item);
        returnIntent.putExtra(EXTRA_NUMB, numb);
        setResult(Activity.RESULT_OK, returnIntent);

        finish();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI;
                //if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT){
                    photoURI = Uri.fromFile(photoFile);
                /*} else {
                    photoURI = FileProvider.getUriForFile(this,
                            "com.simplelist.fileprovider",
                            photoFile);
                }*/
                //image = photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        this.image = image.getAbsolutePath();
        return image;
    }

    private void deleteImageFiles(List<String> uriStrs){/////////////////////////
        File imgFile;
        for(int i = 0; i < uriStrs.size(); i++){
            imgFile = new File(uriStrs.get(i));
            imgFile.delete();
        }
    }

    @Override
    public void onBackPressed() {
        item.setTitle(title);
        item.setDescription(description);
        item.setImage(image);
        item.setColor(color);

        //item = new Item(item.getUuid(), title, description, item.getTimestamp(), image);
        //int i = oldItem.hashCode();
        //int j = item.hashCode();
        if(!item.equals(oldItem)){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage(R.string.back_button_alert_message);
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    saveItem();
                }
            });

            alertDialog.show();
        } else{
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_IMAGE){
                Uri selectedImageUri = data.getData();
                image = getPath(selectedImageUri);
                if(image != null){
                    File imgFile = new File(image);
                    Bitmap bitmap = decodeSampledBitmapFromPath(imgFile);
                    editImage.setImageBitmap(bitmap);
                }
            }
            else if(requestCode == REQUEST_TAKE_PHOTO){
                if(image != null){
                    File imgFile = new File(image);
                    Bitmap bitmap = decodeSampledBitmapFromPath(imgFile);
                    editImage.setImageBitmap(bitmap);
                }
            }
        }
    }

    public String getPath(Uri uri) {
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return uri.getPath();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        this.menu = menu;

        if(!intent.hasExtra(EXTRA_NUMB)) {
            MenuItem item = menu.findItem(R.id.action_delete);
            item.setVisible(false);
        }
        setMenuIcoColor(color);

        return true;
    }

    private void setMenuIcoColor(int color){
        MenuItem item = menu.findItem(R.id.action_color);
        RoundedAvatarDrawable bitmap = new RoundedAvatarDrawable(Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565));
        bitmap.setColorFilter(colors.get(color), PorterDuff.Mode.ADD);
        item.setIcon(bitmap);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colors.get(color)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                //finish();
                return true;
            case R.id.action_color:
                colorAlert();
                return true;
            case R.id.action_delete:
                deleteAlert();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public int getNewHeight(int oldWidth, int oldHeight, int newWidth){
        return Math.round((float)(oldHeight/(float)(oldWidth/newWidth)));
    }

    public Bitmap decodeSampledBitmapFromPath(File imgFile) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            BitmapFactory.decodeStream(new FileInputStream(imgFile), null, options);
            //String kek = imgFile.getAbsoluteFile().toString();
            //BitmapFactory.decodeFile(kek, options);

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = getNewHeight(options.outWidth, options.outHeight, width);
            options.inSampleSize = calculateInSampleSize(options, width, height);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(new FileInputStream(imgFile), null, options);
        } catch (Exception ex) {
            String str = ex.toString();
            Toast.makeText(this, str, Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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

    private void showImagePopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.image_popupmenu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete_image:
                        editImage.setImageBitmap(null);
                        image = null;
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    private void deleteAlert(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(R.string.delete_button_alert_message);
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(EXTRA_NUMB, intent.getExtras().getInt(EXTRA_NUMB));
                returnIntent.putExtra(EXTRA_ITEM, item);
                setResult(RESULT_DELETED, returnIntent);
                finish();
            }
        });

        alertDialog.show();
    }

    private void colorAlert(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.ColorsDialog);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_color, null);

        ColorGridAdapter adapter = new ColorGridAdapter(this, colors); //R.layout.grid_color_item, R.id.color_item,
        GridView gridView = (GridView) dialogView.findViewById(R.id.color_grid);
        gridView.setAdapter(adapter);

        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                color = i;
                item.setColor(color);
                //setMenuIcoColor(color);
                alertDialog.cancel();
                recreate();
            }
        });
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    private void setCustomTheme(int color){
        switch(color){
            case 0:
                setTheme(R.style.OverlayPrimaryColorRed);
                break;
            case 1:
                setTheme(R.style.OverlayPrimaryColorOrange);
                break;
            case 2:
                setTheme(R.style.OverlayPrimaryColorYellow);
                break;
            case 3:
                setTheme(R.style.OverlayPrimaryColorGreen);
                break;
            case 4:
                setTheme(R.style.OverlayPrimaryColorBlue);
                break;
            case 5:
                setTheme(R.style.OverlayPrimaryColorPurple);
                break;
            case 6:
                setTheme(R.style.OverlayPrimaryColorBlack);
                break;
            case 7:
                setTheme(R.style.OverlayPrimaryColorBrown);
                break;
            case 8:
                setTheme(R.style.OverlayPrimaryColorGrey);
                break;
        }
    }
}

/*
returnIntent.putExtra(EXTRA_TEXT, editText.getText().toString());
        returnIntent.putExtra(EXTRA_DESCRIPTION, editDescription.getText().toString());
        returnIntent.putExtra(EXTRA_IMAGE, image);
        if(intent.hasExtra(EXTRA_NUMB)){
            int i = intent.getExtras().getInt(EXTRA_NUMB);
            String uuid = intent.getStringExtra(EXTRA_UUID);
            returnIntent.putExtra(EXTRA_NUMB, i);
            returnIntent.putExtra(EXTRA_UUID, uuid);
        }*/


/*
if(intent.hasExtra(EXTRA_TEXT)){
            name = intent.getStringExtra(EXTRA_TEXT);
            description = intent.getStringExtra(EXTRA_DESCRIPTION);

            if(savedInstanceState != null)
                image = savedInstanceState.getString(STATE_IMAGE);
            else
                image = intent.getStringExtra(EXTRA_IMAGE);
 */
