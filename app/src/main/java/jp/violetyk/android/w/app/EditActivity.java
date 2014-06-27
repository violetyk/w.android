package jp.violetyk.android.w.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EditActivity extends Activity {

    // データベースヘルパーの作成
    private DatabaseHelper helper = new DatabaseHelper(this);
    // データベースの宣言
    public static SQLiteDatabase db;

    private String noteDir;
    private String noteName;
    private ArrayList<String> tags;

    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks;


    private static final String NOTITLE = "No Title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        db = helper.getWritableDatabase();

        // インテントからファイルパスをもらう
        Intent intent = getIntent();
        this.noteDir = intent.getStringExtra("noteDir");
        this.noteName = intent.getStringExtra("noteName");

        this.editNote();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.actionbar_edit_menu, menu);

        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.note_save:
                if (!this.saveNote()) {
                    Toast.makeText(this, "ファイルの書き込みに失敗しました", Toast.LENGTH_SHORT).show();
                }
                finish();
                return true;

            case R.id.note_cancel:
                finish();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }


    private void editNote() {
        // テキストを読み込んでセット
        StringBuilder sb = new StringBuilder();
        File f = new File(this.noteDir + this.noteName);
        if (f.exists()) {

            try {
                FileReader fr = new FileReader(f.getPath());
                BufferedReader br = new BufferedReader(fr);

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                fr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String text = sb.toString();
        // 裏でタグデータを持っておく
        this.tags = getTagsFromText(text);
        ((EditText)findViewById(R.id.edittext)).setText(text);
    }

    private boolean saveNote() {

        try {
            File file = new File(this.noteDir + this.noteName);
            FileWriter filewriter = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(filewriter);
            PrintWriter pw = new PrintWriter(bw);
            String text = ((EditText)findViewById(R.id.edittext)).getText().toString();
            pw.write(text);
            pw.close();

            ArrayList<String> oldTags = this.tags;
            ArrayList<String> newTags = getTagsFromText(text);

            if (helper.saveNote(db, this.noteName, getTitleFromText(text), newTags, oldTags)) {
                this.tags = newTags;
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getTitleFromText(String text) {
        String title = new String("");
        String[] lines = text.split("\\r?\\n");
        if (lines[0] != null) {
            title = lines[0];
        }
        if (title.trim().length() == 0) {
            return NOTITLE;
        }
        return title;
    }

    private ArrayList<String> getTagsFromText(String text) {
        ArrayList<String> tags = new ArrayList<String>();

        String[] lines = text.split("\\r?\\n");
        if (lines.length >= 2 && lines[1] != null) {
            String[] t = lines[1].split("(?<=\\])");
            Pattern pattern = Pattern.compile("\\[(.+)\\]");
            for (int i = 0; i < t.length; i++ ) {
                Matcher matcher = pattern.matcher(t[i]);
                while(matcher.find()){
                    tags.add(matcher.group(1));
                }
            }

        }
        return tags;
    }

}
