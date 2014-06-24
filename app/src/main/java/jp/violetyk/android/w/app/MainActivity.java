package jp.violetyk.android.w.app;

import android.content.Intent;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;


public class MainActivity extends FragmentActivity {

    // データベースヘルパーの作成
    private DatabaseHelper helper = new DatabaseHelper(this);
    // データベースの宣言
    public static SQLiteDatabase db;

    private static final String noteDir = "/notes";
    private static final String extention = ".md";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = helper.getWritableDatabase();

        ViewPager pager = (ViewPager) findViewById(R.id.pager);

        // PagerTitleStrip のカスタマイズ
        PagerTabStrip strip = (PagerTabStrip) findViewById(R.id.strip);
        strip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        strip.setTextColor(0xff9acd32);
        strip.setTextSpacing(50);
        strip.setNonPrimaryAlpha(0.3f);
        strip.setDrawFullUnderline(true);
        strip.setTabIndicatorColor(0xff9acd32);

        // ViewPager の Adapter
        CustomFragmentPagerAdapter adapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());

        // GridView の Adapter
        ArrayList<App> appList = new ArrayList<App>();
        for (int i = 0; i < 30; i++) {
            App item = new App();
            item.title = "App" + i;
            item.description = "This app is " + i + ".";
            item.company = "Company" + i;
            item.rate = (float) Math.random() * 5;
            item.value = (int) Math.floor((Math.random() * (500 - 80 + 1))) + 80;
            appList.add(item);
        }

        // 各ページアイテム(おすすめアプリ)
        PageItem recommend = new PageItem();
        recommend.title = "Recommend App";
        recommend.fragmentKind = PageItem.RELATIVE;
        adapter.addItem(recommend);

        // 各ページアイテム(人気アプリ)
        PageItem popular = new PageItem();
        popular.title = "Popular App";
        popular.fragmentKind = PageItem.GRID;
        popular.appList = appList;
        adapter.addItem(popular);

        // 各ページアイテム(新着アプリ)
        PageItem newest = new PageItem();
        newest.title = "Newest App";
        newest.fragmentKind = PageItem.GRID;
        newest.appList = appList;
        adapter.addItem(newest);

        pager.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.actionbar_main_menu, menu);

        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // メニューが選択されたときに実行される
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.note_new:
//                String name = ((TextView)findViewById(R.id.editTextName)).getText().toString();
                Intent i = new Intent();
                i.setClassName(this.getPackageName(), this.getPackageName() + ".EditActivity");
                i.putExtra("noteDir", getNoteDir());
                i.putExtra("noteName", createNoteName());
                startActivity(i);
                return true;

            case R.id.action_settings:
                return true;

        }
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.close();
    }

    private String getNoteDir() {
        File dir = new File(this.getFilesDir() + noteDir);
        if (dir.exists() == false) {
            dir.mkdirs();
        }
        return dir + "/";
    }

    private String createNoteName() {
        return String.format("%s%s_%s%s",
                android.text.format.DateFormat.format("yyyyMMddkkmmss", Calendar.getInstance()).toString(),
                random(9),
                Build.DEVICE,
                extention
                );
    }

    private static String random(int length) {
        Random generator = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++){
            sb.append(String.valueOf(generator.nextInt(10)));
        }
        return sb.toString();
    }

}