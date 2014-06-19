package jp.violetyk.android.w.app;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "w.sqlite";
    private static int	DB_VERSION = 1;
    private Context context;

    public DatabaseHelper(Context context) {
        // ストレージ（ローカルファイル）にDBを作成
        // 第二引数がnullならメモリ上に作成する。
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    /**
     * テーブルを作成するメソッド。データ作成時、テーブルが無いときに呼ばれる。
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            execSql(db, "sql/schema.sql");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * データベースをアップデートするときに呼ばれる
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			db.execSQL("DROP TABLE IF EXISTS docs");
//			onCreate(db);
    }

    private void execSql(SQLiteDatabase db, String assetFile) throws IOException {
        AssetManager as = context.getResources().getAssets();
        try {
            String sql = this.readFile(as.open(assetFile));
            db.execSQL(sql);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFile(InputStream is) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is, Charset.defaultCharset()));

            StringBuilder sb = new StringBuilder();
            String str;
            while((str = br.readLine()) != null){
                sb.append(str + "\n");
            }
            return sb.toString();
        } finally {
            if (br != null) br.close();
        }
    }
}