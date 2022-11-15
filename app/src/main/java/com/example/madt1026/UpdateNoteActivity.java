package com.example.madt1026;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class UpdateNoteActivity extends AppCompatActivity
{
    ArrayList<String> listNoteItems = new ArrayList<>();
    ListView lvnotes;
    ArrayAdapter<String> adapter;
    private int mSelection = 0;
    EditText edBody;
    EditText edTitle;
    Set<String> notesTitlesSet = new HashSet<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);

        this.lvnotes = findViewById(R.id.lvNotes);
        this.adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, this.listNoteItems);
        this.lvnotes.setAdapter(adapter);

        lvnotes.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lvnotes.setSelection(0);
        lvnotes.setItemChecked(0, true);
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
            this.listNoteItems.clear();
//            this.listNoteItems.addAll(savedSet);
            this.listNoteItems.addAll(notesTitlesSet);
            this.adapter.notifyDataSetChanged();
        }

        lvnotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int itemClickedPosition, long mylng) {
                String titleSelected =(String) (lvnotes.getItemAtPosition(itemClickedPosition));
                String noteBody = "";
                noteBody = MainActivity.getBodyFromTitle(savedSet, titleSelected);
//                notesTitlesSet.remove(titleSelected);
                savedSet.remove(MainActivity.composeNoteData(titleSelected,noteBody));
                setContentView(R.layout.modify_note_layout);
                edTitle = findViewById(R.id.edTitle);
                edTitle.setText(adapter.getItem(itemClickedPosition));
                edBody = findViewById(R.id.edBody);
                edBody.setText(noteBody);
//                listNoteItems.remove(pos);


            }
        });
    }

    public void onBtnSaveAndCloseClick(View view) {
        String noteTitle = this.edTitle.getText().toString();
        String noteBody = this.edBody.getText().toString();

        if (!noteTitle.isEmpty()) {
            if (!noteBody.isEmpty()) {

                Date c = Calendar.getInstance().getTime();
                System.out.println("Current time => " + c);

                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                String formattedDate = df.format(c);


                //Current
                SharedPreferences sharedPref = this.getSharedPreferences(Constants.NOTES_FILE, this.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                Set<String> savedSet = sharedPref.getStringSet(Constants.NOTES_ARRAY_KEY, null);
                Set<String> newSet = new HashSet<>();
                if (savedSet != null) {
                    newSet.addAll(savedSet);
                    if (MainActivity.containsTitle(savedSet, noteTitle)) {
                        Toast.makeText(getApplicationContext(),
                                R.string.existing_title,
                                Toast.LENGTH_LONG).show();
                    } else{

                        newSet.add(MainActivity.composeNoteData(noteTitle, noteBody));

                        editor.putString(Constants.NOTE_KEY, noteTitle);
                        editor.putString(Constants.NOTE_KEY_DATE, formattedDate);
                        editor.putStringSet(Constants.NOTES_ARRAY_KEY, newSet);
                        editor.apply();

                        finish();
                    }
                }
            }else
                Toast.makeText(getApplicationContext(),
                        R.string.empty_note_introduced,
                        Toast.LENGTH_LONG).show();
        }else
            Toast.makeText(getApplicationContext(),
                    R.string.empty_title_introduced,
                    Toast.LENGTH_LONG).show();
    }
}
