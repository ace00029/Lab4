package com.example.madt1026;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class AddNoteActivity extends AppCompatActivity {

    EditText edBody;
    EditText edTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        this.edBody = findViewById(R.id.edBody);
        this.edTitle = findViewById(R.id.edTitle);
    }

    //About SharedPreferences: //https://developer.android.com/training/data-storage/shared-preferences
    public void onBtnSaveAndCloseClick(View view) {
        String noteBody = this.edBody.getText().toString();
        String noteTitle = this.edTitle.getText().toString();

        if (!noteTitle.isEmpty() ){
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
                    if (MainActivity.containsTitle(savedSet,noteTitle)) {
                        Toast.makeText(getApplicationContext(),
                                R.string.existing_title,
                                Toast.LENGTH_LONG).show();
                    }
                    else {
                        newSet.add(MainActivity.composeNoteData(noteTitle, noteBody));

                        editor.putString(Constants.NOTE_KEY, noteTitle);
                        editor.putString(Constants.NOTE_KEY_DATE, formattedDate);
                        editor.putStringSet(Constants.NOTES_ARRAY_KEY, newSet);
                        editor.apply();

                        finish();
                    }
                }
            } else
                Toast.makeText(getApplicationContext(),
                        R.string.empty_note_introduced,
                        Toast.LENGTH_LONG).show();
        }else
            Toast.makeText(getApplicationContext(),
                    R.string.empty_title_introduced,
                    Toast.LENGTH_LONG).show();
    }


}