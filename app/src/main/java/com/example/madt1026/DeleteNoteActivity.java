package com.example.madt1026;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DeleteNoteActivity extends AppCompatActivity
{

    ArrayList<String> listNoteItems = new ArrayList<>();
    ListView remove_notes_lv;
    ArrayAdapter<String> adapter;
    private int mSelection = 0;
    Set<String> tempArraySet;
    Set<String> notesTitlesSet = new HashSet<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_note);
        tempArraySet = new HashSet<>();


        this.remove_notes_lv = findViewById(R.id.remove_notes_lv);
        this.adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, this.listNoteItems);
        this.remove_notes_lv.setAdapter(adapter);

        remove_notes_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        remove_notes_lv.setSelection(0);
        remove_notes_lv.setItemChecked(0, true);
//        remove_notes_lv.setOnItemClickListener(adapter);


    }

    @Override
    protected void onStart() {
        super.onStart();

        //Current
        SharedPreferences sharedPref = this.getSharedPreferences(Constants.NOTES_FILE, this.MODE_PRIVATE);
        Set<String> savedSet = sharedPref.getStringSet(Constants.NOTES_ARRAY_KEY, null);

        MainActivity.getFromSet(savedSet,notesTitlesSet,0);


        if(savedSet != null) {
            tempArraySet.addAll(savedSet);
            this.listNoteItems.clear();
            this.listNoteItems.addAll(notesTitlesSet);

            this.adapter.notifyDataSetChanged();
        }

        remove_notes_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int itemClickedPosition, long mylng) {
                String selectedTitleFromList =(String) (remove_notes_lv.getItemAtPosition(itemClickedPosition));

                SharedPreferences.Editor editor = sharedPref.edit();
                AlertDialog.Builder adb=new AlertDialog.Builder(DeleteNoteActivity.this);
                adb.setMessage("Are you sure you want to delete: " + selectedTitleFromList);
                final int positionToRemove = itemClickedPosition;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String noteBody = MainActivity.getBodyFromTitle(savedSet,selectedTitleFromList);
                        String composedNote = MainActivity.composeNoteData(selectedTitleFromList, noteBody);
                        tempArraySet.remove(composedNote);
                        listNoteItems.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                    }});
                adb.show();
            }
        });
    }


    public void onSaveRemovesButtonClicked (View view) {
        SharedPreferences sharedPref = this.getSharedPreferences(Constants.NOTES_FILE, this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        Set<String> savedSet = sharedPref.getStringSet(Constants.NOTES_ARRAY_KEY, null);
        Set<String> newSet = new HashSet<>();
        if(tempArraySet != null) {
            newSet.addAll(tempArraySet);
            editor.remove(Constants.NOTES_ARRAY_KEY).apply();
            editor.putStringSet(Constants.NOTES_ARRAY_KEY, newSet).apply();
        }

        String lastSavedNoteTitle = sharedPref.getString(Constants.NOTE_KEY, "n/a");

        //Remove sharedPreferences for last note if it has been deleted
        if (!MainActivity.containsTitle(newSet, lastSavedNoteTitle))
        {
            editor.remove(Constants.NOTE_KEY).apply();
            editor.remove(Constants.NOTE_KEY_DATE).apply();
        }
        finish();
    }

    public void onDiscardRemovesButtonClicked (View view)
    {
        finish();
    }



}

