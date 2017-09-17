package com.simplelist;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.simplelist.Adapters.ItemListAdapter;
import com.simplelist.Adapters.SortListAdapter;
import com.simplelist.Objects.Item;
import com.simplelist.Objects.SortItem;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String STATE_IS_TITLE_ASC = "IS_TITLE_ASC";
    private static final String STATE_IS_DATE_ASC = "IS_DATE_ASC";
    private static final String STATE_CURRENT_SORT = "CURRENT_SORT";
    private static final String STATE_NAMES = "NAMES";

    private static final String EXTRA_ITEM = "com.simplelist.item";
    private static final String EXTRA_NUMB = "com.simplelist.item_numb";
    private static final int EDIT_REQUEST = 1;
    private static final int ADD_REQUEST = 2;
    private static final int RESULT_DELETED = 3;
    private static final int BACKUP_REQUEST = 3;

    private static final String COLUMN_LIST_ITEM_NAME = "item_name";
    private static final String COLUMN_LIST_ITEM_DATE = "item_date";
    private static final String SORT_BY_ASC = COLUMN_LIST_ITEM_NAME + " " +  "ASC";
    private static final String SORT_BY_DESC = COLUMN_LIST_ITEM_NAME + " " +  "DESC";
    private static final String SORT_BY_DATE_ASC = COLUMN_LIST_ITEM_DATE + " " + "ASC";
    private static final String SORT_BY_DATE_DESC = COLUMN_LIST_ITEM_DATE + " " + "DESC";

    private Boolean IS_TITLE_ASC = false;
    private Boolean IS_DATE_ASC = true;
    private String CURRENT_SORT = "";

    private ArrayList<Item> names = new ArrayList<Item>();
    //ArrayAdapter<Item> adapter;
    private ItemListAdapter listAdapter;

    private ListView listView;
    private ListDataBaseHelper mHelper;
    private FloatingActionButton fabAdd;
    private Toolbar toolbar;

    private Context context;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putParcelableArrayList(STATE_NAME_LIST, names);
        outState.putBoolean(STATE_IS_TITLE_ASC, IS_TITLE_ASC);
        outState.putBoolean(STATE_IS_DATE_ASC, IS_DATE_ASC);
        outState.putString(STATE_CURRENT_SORT, CURRENT_SORT);
        outState.putParcelableArrayList(STATE_NAMES, names);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        listView = (ListView) findViewById(R.id.sampleList);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);

        mHelper = new ListDataBaseHelper(context);

        if(savedInstanceState != null){
            IS_TITLE_ASC = savedInstanceState.getBoolean(STATE_IS_TITLE_ASC);
            IS_DATE_ASC = savedInstanceState.getBoolean(STATE_IS_DATE_ASC);
            CURRENT_SORT = savedInstanceState.getString(STATE_CURRENT_SORT);
            names = savedInstanceState.getParcelableArrayList(STATE_NAMES);
        }
        else{
            CURRENT_SORT = SORT_BY_DATE_DESC;
            names = mHelper.queryItems(CURRENT_SORT);
        }

        listView.setEmptyView(findViewById(R.id.first_image));
        listAdapter = new ItemListAdapter(this, names);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, EditActivity.class);
                Item item = names.get(i);
                intent.putExtra(EXTRA_ITEM, item);
                intent.putExtra(EXTRA_NUMB, i);
                startActivityForResult(intent, EDIT_REQUEST);
            }
        });


        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                actionMode.setTitle(R.string.app_name);
                int count = listView.getCheckedItemCount();
                if(count == 0)
                    actionMode.setSubtitle("No items selected");
                else if(count == 1)
                    actionMode.setSubtitle("1 item selected");
                else
                    actionMode.setSubtitle(count + " items selected");
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.edit_menu, menu);
                MenuItem item = menu.findItem(R.id.action_color);
                item.setVisible(false);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_delete:
                        SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
                        for(int i = checkedItems.size() - 1; i > -1; i--) {
                            if (checkedItems.valueAt(i)) {
                                Item item = names.get(checkedItems.keyAt(i));
                                mHelper.deleteItem(item);
                                names.remove(item);
                            }
                        }
                        listAdapter.notifyDataSetChanged();
                        actionMode.finish();
                        return true;
                    default:
                        return true;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {}
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditActivity.class);
                startActivityForResult(intent, ADD_REQUEST);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_sort:
                //region Sort Alert
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alert_sort_message, null);
                ListView sortList = (ListView) dialogView.findViewById(R.id.sort_list);

                dialogBuilder.setView(dialogView);
                //dialogBuilder.setTitle(R.string.action_sort);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                ArrayList<SortItem> itemsArr = new ArrayList<SortItem>();
                itemsArr.add(new SortItem(R.drawable.ic_sort_by_alpha_black, R.string.alphabet_sort));
                itemsArr.add(new SortItem(R.drawable.ic_sort_by_time_black, R.string.date_sort));

                SortListAdapter sortListAdapter = new SortListAdapter(this, itemsArr);
                sortList.setAdapter(sortListAdapter);
                sortList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        switch (i){
                            case 0:
                                if(IS_TITLE_ASC) {
                                    names.clear();
                                    names.addAll(mHelper.queryItems(SORT_BY_DESC));
                                    CURRENT_SORT = SORT_BY_DESC;
                                }
                                else {
                                    names.clear();
                                    names.addAll(mHelper.queryItems(SORT_BY_ASC));
                                    CURRENT_SORT = SORT_BY_ASC;
                                }
                                //IS_DATE_ASC = true;
                                IS_TITLE_ASC = !IS_TITLE_ASC;
                                listAdapter.notifyDataSetChanged();
                                alertDialog.cancel();
                                break;
                            case 1:
                                if(IS_DATE_ASC) {
                                    names.clear();
                                    names.addAll(mHelper.queryItems(SORT_BY_DATE_DESC));
                                    CURRENT_SORT = SORT_BY_DATE_DESC;
                                }
                                else {
                                    names.clear();
                                    names.addAll(mHelper.queryItems(SORT_BY_DATE_ASC));
                                    CURRENT_SORT = SORT_BY_DATE_ASC;
                                }
                                //IS_TITLE_ASC = false;
                                IS_DATE_ASC = !IS_DATE_ASC;
                                listAdapter.notifyDataSetChanged();
                                alertDialog.cancel();
                                break;
                            default:
                                break;
                        }
                    }
                });
                return true;
            //endregion
            case R.id.action_backup:
                //backupAlert();
                Intent intent = new Intent(this, BackupActivity.class);
                //intent.putParcelableArrayListExtra(EXTRA_ITEMS, names);
                startActivityForResult(intent, BACKUP_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REQUEST) {
            if (resultCode == RESULT_OK) {
                int i = data.getExtras().getInt(EXTRA_NUMB);
                Item item = data.getParcelableExtra(EXTRA_ITEM);
                mHelper.updateItem(item);
                names.set(i, item);
                listAdapter.notifyDataSetChanged();
            }
            else if(resultCode == RESULT_DELETED){
                int i = data.getExtras().getInt(EXTRA_NUMB);
                Item item = data.getParcelableExtra(EXTRA_ITEM);
                mHelper.deleteItem(item);
                names.remove(i);
                listAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == ADD_REQUEST) {
            if (resultCode == RESULT_OK) {
                Item item = data.getParcelableExtra(EXTRA_ITEM);
                String uuid = UUID.randomUUID().toString();
                String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(new Date());
                Timestamp timestamp = Timestamp.valueOf(date);
                item.setUuid(uuid);
                item.setTimestamp(timestamp);
                mHelper.insertItem(item);
                names.add(0, item);
                listAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == BACKUP_REQUEST){
            //if(resultCode == RESULT_OK){
                names.clear();
                names.addAll(mHelper.queryItems(CURRENT_SORT));
                listAdapter.notifyDataSetChanged();
            //}
        }
    }

}

/*private void backupAlert(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_backup, null);
        final ListView backupList = (ListView) dialogView.findViewById(R.id.backup_list);
        Button addButton = (Button) dialogView.findViewById(R.id.add_backup);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        String baseFolder;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            baseFolder = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        }
        else {
            baseFolder = getFilesDir().getAbsolutePath();
        }
        File dir = new File(baseFolder);
        final File[] files = dir.listFiles();
        ArrayList<String> fileNames = new ArrayList<String>();
        for(int i = 0; i < files.length; i++){
            int lastPeriodPos = files[i].getName().lastIndexOf('.');
            if (lastPeriodPos > 0)
                fileNames.add(files[i].getName().substring(0, lastPeriodPos));
        }

        final ItemsJsonSerializer serializer = new ItemsJsonSerializer(context);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileNames);
        backupList.setEmptyView(findViewById(R.id.empty_backup_list));
        backupList.setAdapter(adapter);
        backupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    names.clear();
                    names.addAll(serializer.loadItems(files[i].getName()));
                    mHelper.deleteAllItems();
                    mHelper.insertItems(names);
                    listAdapter.notifyDataSetChanged();
                } catch (Exception e){
                    e.printStackTrace();}
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    serializer.SaveItems(mHelper.queryItems());
                    adapter.notifyDataSetChanged();
                } catch (Exception e){
                    e.printStackTrace();}
            }
        });
    }*/

/*
String str = data.getExtras().getString(EXTRA_TEXT);
                String description = data.getExtras().getString(EXTRA_DESCRIPTION);
                String image = data.getExtras().getString(EXTRA_IMAGE);
 */

/*String str = data.getExtras().getString(EXTRA_TEXT);
                String uuid = data.getExtras().getString(EXTRA_UUID);
                String description = data.getExtras().getString(EXTRA_DESCRIPTION);
                String image = data.getExtras().getString(EXTRA_IMAGE);
                Item item = new Item(uuid, str, description, image);*/


/*intent.putExtra(EXTRA_TEXT, names.get(i).getTitle());
                intent.putExtra(EXTRA_DESCRIPTION, names.get(i).getDescription());
                intent.putExtra(EXTRA_NUMB, i);
                intent.putExtra(EXTRA_UUID, names.get(i).getUuid());
                intent.putExtra(EXTRA_IMAGE, names.get(i).getImage());
                startActivityForResult(intent, EDIT_REQUEST);*/

/*File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

            String destinationPath = storageDir + "/" +files[i].getName();
            File destination = new File(destinationPath);
            try {
                FileUtils.copyFile(files[i], destination);
            }
            catch (IOException e){
                e.printStackTrace();
            }*/


/*
        Integer sortItems[] = {
                R.drawable.ic_sort_by_alpha,
                R.drawable.ic_sort_by_date
        };

        MenuItem item = menu.findItem(R.id.sort_spinner);

        sortSpinner = (Spinner) MenuItemCompat.getActionView(item);
        spinnerAdapter = new SpinnerAdapter(this, sortItems);

        sortSpinner.setAdapter(spinnerAdapter);*/

//new ArrayAdapter<Item>(this, android.R.layout.simple_list_item_2, names);

/*
if(savedInstanceState != null){
        names = savedInstanceState.getStringArrayList(STATE_NAME_LIST);
        } else{
        for(int i = 0; true; i++){
        Item item = mHelper.queryItems(i);
        if(item == null){
        break;
        }
        names.add(i, item.getName());
        }

        }*/

/*names.add("qwerty");
            names.add("vova");
            names.add("interstelar");
            names.add("pentagon");
            names.add("poroshenko");
            names.add("roshen");
            names.add("dokinth");
            names.add("writer");
            names.add("Ukraine");
            names.add("long read");
            names.add("supper Mario");
            names.add("Didko");
            names.add("Zrada");
            names.add("Rock&Roll");
            names.add("Zradoyoby");
            names.add("Napiy");*/
