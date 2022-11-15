package com.example.madt1026;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> listNoteItems = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ListView lvNotes;
    Set<String> notesTitlesSet = new HashSet<>();
    TextView noteTitleTV;
    TextView noteBodyTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_frame_layout);

        this.lvNotes = findViewById(R.id.lvNotes);
        this.adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, this.listNoteItems);
        this.lvNotes.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notes_options_menu, menu);
        //inflater.inflate(R.menu.secondary_options_menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        findViewById(R.id.main_screen_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.note_content_layout).setVisibility(View.INVISIBLE);

        //Current
        SharedPreferences sharedPref = this.getSharedPreferences(Constants.NOTES_FILE, this.MODE_PRIVATE);
        String lastSavedNote = sharedPref.getString(Constants.NOTE_KEY, "NA");
        String lastSavedNoteDate = sharedPref.getString(Constants.NOTE_KEY_DATE, "1900-01-01");
        Set<String> savedSet = sharedPref.getStringSet(Constants.NOTES_ARRAY_KEY, null);

        getFromSet(savedSet,notesTitlesSet,0);

        if(savedSet != null) {
            this.listNoteItems.clear();
//            this.listNoteItems.addAll(savedSet);
            this.listNoteItems.addAll(notesTitlesSet);
            this.adapter.notifyDataSetChanged();
        }


        lvNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int itemClickedPosition, long mylng) {
                String titleSelected =(String) (lvNotes.getItemAtPosition(itemClickedPosition));
                String noteBody = "";
                noteBody = MainActivity.getBodyFromTitle(savedSet, titleSelected);


                findViewById(R.id.main_screen_layout).setVisibility(View.INVISIBLE);
                findViewById(R.id.note_content_layout).setVisibility(View.VISIBLE);

                noteTitleTV = findViewById(R.id.titleField);
                noteTitleTV.setText(adapter.getItem(itemClickedPosition));
                noteBodyTV = findViewById(R.id.bodyField);
                noteBodyTV.setText(noteBody);
//                Toast.makeText(MainActivity.this, noteBody, Toast.LENGTH_LONG).show();
            }
        });

        Snackbar.make(lvNotes, String.format("%s: %s", getString(R.string.msg_last_saved_note), lastSavedNote), Snackbar.LENGTH_LONG).show();
        Toast.makeText(this, lastSavedNoteDate, Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_note:
                Intent addNote = new Intent(this, AddNoteActivity.class);
                try {
                    startActivity(addNote);
                }catch (RuntimeException e){
                    e.printStackTrace();
                }
                return true;
            case R.id.remove_note:
                Intent remNote = new Intent(this, DeleteNoteActivity.class);
                try {
                    startActivity(remNote);
                }catch (RuntimeException e){
                    e.printStackTrace();
                }
                return true;
            case R.id.update_note:
                Intent updateNote = new Intent(this, UpdateNoteActivity.class);
                try {
                    startActivity(updateNote);
                }catch (RuntimeException e){
                    e.printStackTrace();
                }
                return true;
            case R.id.dunno:
                Toast.makeText(getApplicationContext(), R.string.msg_dunno_clicked, Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onAddNoteButtonClicked (View view)
    {
        Intent addNote = new Intent(this, AddNoteActivity.class);
        try {
            startActivity(addNote);
        }catch (RuntimeException e){
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Separates the title from the note text.
     * @pre The given Set<String> items has to be formatted as follows: title;text
     * @param set composed Set<String>
     * @param tr how to transform the set. 0 for obtaining titles. 1 for obtaining content
     * @return Set<String> with the specified data
     */
    public static Set<String> getFromSet (Set<String> set, Set<String> newSet, int tr)
    {
        if (tr != 0 && tr != 1 || set == null)
            return null;

        newSet.clear();
        Iterator it;
        it = set.iterator();
        String currentData;
        ArrayList<String> titlesArray = new ArrayList<>();
        while (it.hasNext())
        {
            currentData =(String) it.next();
            currentData = currentData.split(";")[tr];
            titlesArray.add(currentData);
//            newSet.add(currentData);
        }
        newSet.addAll(titlesArray);
        return newSet;

    }

    public static String composeNoteData (String title, String body)
    {
        return (title + ';' + body);
    }

    public static String getTitleFromSavedNote (String note)
    {
        return note.split(";")[0];
    }
    public static String getBodyFromSavedNote (String note)
    {
        return note.split(";")[1];
    }
    public static String getBodyFromTitle(Set<String> savedSet, String noteTitle)
    {
        if (savedSet == null || noteTitle.isEmpty()) return "";
        Iterator it;
        it = savedSet.iterator();
        String currentNote;
        String[] currentNoteSplited;
        String currentTitle;

        Boolean end = false;
        String toRet = "";
        while (it.hasNext() && !end)
        {
            currentNote = (String)it.next();
//            toRet++;
            currentNoteSplited = currentNote.split(";");
            currentTitle = currentNoteSplited[0];
            if (currentTitle.equals(noteTitle))
            {
                if (currentNoteSplited.length < 2) return toRet;
                else {
                    toRet = currentNoteSplited[1];
                    end = true;
                }
            }

        }
        return toRet;
    }
    public static boolean containsTitle (Set<String> set, String title)
    {
        Set<String> titleSet = new HashSet<>();
        getFromSet(set,titleSet,0);
        return titleSet.contains(title);
    }

    public void onBtnBackClick (View view) {
        findViewById(R.id.main_screen_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.note_content_layout).setVisibility(View.INVISIBLE);
    }
}

