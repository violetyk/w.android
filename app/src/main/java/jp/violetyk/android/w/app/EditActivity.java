package jp.violetyk.android.w.app;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;


public class EditActivity extends Activity {

    // データベースヘルパーの作成
    private DatabaseHelper helper = new DatabaseHelper(this);
    // データベースの宣言
    public static SQLiteDatabase db;

    private String notePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        db = helper.getWritableDatabase();

        // インテントからファイルパスをもらう
        Intent intent = getIntent();
        this.notePath = intent.getStringExtra("notePath");
//        Toast.makeText(this, notePath, Toast.LENGTH_LONG).show();

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
        File f = new File(this.notePath);
        if (f.exists()) {

            try {
                FileReader fr = new FileReader(f.getPath());
                BufferedReader br = new BufferedReader(fr);

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                fr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ((EditText)findViewById(R.id.edittext)).setText(sb.toString());
    }

    private boolean saveNote() {

        try {
            File file = new File(this.notePath);
            FileWriter filewriter = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(filewriter);
            PrintWriter pw = new PrintWriter(bw);
            pw.write(((EditText)findViewById(R.id.edittext)).getText().toString());

            pw.close();
            return true;
            // DB にタイトルとタグを保存
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
