package com.simplelist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.simplelist.Objects.Item;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Yurii on 17.08.2017.
 */

public class BackupActivity extends AppCompatActivity {

    //private static final String EXTRA_ITEMS = "com.simplelist.items";

    private static final int BACKUP_REQUEST = 3;

    private ListView backupList;
    private Button addButton;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> fileNames;
    private File[] files;
    private ItemsJsonSerializer serializer;
    private Intent intent;
    private Intent returnIntent;
    private ArrayList<Item> names;
    private ListDataBaseHelper mHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
        setTitle(R.string.action_backup);

        //intent = getIntent();
        mHelper = new ListDataBaseHelper(getApplicationContext());

        backupList = (ListView) findViewById(R.id.backup_list);
        addButton = (Button) findViewById(R.id.add_backup);
        serializer = new ItemsJsonSerializer(getApplicationContext());

        fileNames = new ArrayList<String>();
        getBackups();

        adapter = new ArrayAdapter<String>(this, R.layout.backup_item, fileNames);
        backupList.setEmptyView(findViewById(R.id.empty_backup_list));
        backupList.setAdapter(adapter);
        backupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    names = new ArrayList<Item>();
                    names.addAll(serializer.loadItems(files[i].getName()));
                    mHelper.deleteAllItems();
                    mHelper.insertItems(names);
                } catch (Exception e){
                    e.printStackTrace();}
                setResult(RESULT_OK);
                finish();
            }
        });
        backupList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        backupList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                actionMode.setTitle(R.string.action_backup);
                int count = backupList.getCheckedItemCount();
                if(count == 0)
                    actionMode.setSubtitle("No backups selected");
                else if(count == 1)
                    actionMode.setSubtitle("1 backup selected");
                else
                    actionMode.setSubtitle(count + " backups selected");
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
                switch (menuItem.getItemId()) {
                    case R.id.action_delete:
                        SparseBooleanArray checkedItems = backupList.getCheckedItemPositions();
                        for (int i = checkedItems.size() - 1; i > -1; i--) {
                            if (checkedItems.valueAt(i)) {
                                //Item item = names.get(checkedItems.keyAt(i));
                                //mHelper.deleteItem(item);
                                //names.remove(item);
                                files[checkedItems.keyAt(i)].delete();
                            }
                        }
                        getBackups();
                        adapter.notifyDataSetChanged();
                        actionMode.finish();
                        return true;
                    default:
                        return true;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) { }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    //names = intent.getParcelableArrayListExtra(EXTRA_ITEMS);
                    String dateName = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(new Date()) + ".json";
                    serializer.SaveItems(mHelper.queryItems(), dateName);
                    getBackups();
                    adapter.notifyDataSetChanged();
                } catch (Exception e){
                    e.printStackTrace();}
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                setResult(RESULT_OK);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getBackups(){
        String baseFolder;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            baseFolder = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        }
        else {
            baseFolder = getFilesDir().getAbsolutePath();
        }
        File dir = new File(baseFolder);
        files = dir.listFiles();
        fileNames.clear();
        for(int i = 0; i < files.length; i++){
            int lastPeriodPos = files[i].getName().lastIndexOf('.');
            if (lastPeriodPos > 0)
                fileNames.add(files[i].getName().substring(0, lastPeriodPos));
        }
    }
}
