package jp.violetyk.android.w.app;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
            execSqlFromFile(db, "sql/schema.sql");
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

    public ArrayList<Note> findMruNotes(SQLiteDatabase db, int limit) {
        ArrayList<Note> noteList = new ArrayList<Note>();

        try {
            String[] args = {String.valueOf(limit)};
            Cursor c = db.rawQuery("SELECT path, title FROM notes ORDER BY modified DESC LIMIT ?;", args);
            while (c.moveToNext()) {
                Note note = new Note();
                note.path = c.getString(c.getColumnIndex("path"));
                note.title = c.getString(c.getColumnIndex("title"));
                noteList.add(note);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return noteList;
    }

    public ArrayList<Tag> findAllTags(SQLiteDatabase db) {
        ArrayList<Tag> tagList = new ArrayList<Tag>();

        try {
            Cursor c = db.rawQuery("SELECT name, note_count FROM tags ORDER BY name ASC;", null);

            while (c.moveToNext()) {
                Tag tag = new Tag();
                tag.name = c.getString(c.getColumnIndex("name"));
                tag.note_count = c.getString(c.getColumnIndex("note_count"));
                tagList.add(tag);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tagList;
    }

    public ArrayList<String> findMruTags(SQLiteDatabase db, int limit) {
        ArrayList<String> tags = new ArrayList<String>();

        try {
            String[] args = {String.valueOf(limit)};
            Cursor c = db.rawQuery("SELECT name FROM tags ORDER BY modified DESC LIMIT ?;", args);

            while (c.moveToNext()) {
                tags.add(c.getString(c.getColumnIndex("name")));
            }

       } catch (Exception e) {
            e.printStackTrace();
       }

       return tags;
    }

    public ArrayList<Note> findMruNotesByTag(SQLiteDatabase db, String tag, int limit) {
        ArrayList<Note> noteList = new ArrayList<Note>();

        ArrayList<String> note_paths = new ArrayList<String>();

        try {
            String[] args1 = {tag, String.valueOf(limit)};
            Cursor c1 = db.rawQuery("SELECT note_path FROM search_data WHERE tags MATCH ? LIMIT ?;", args1);
            while (c1.moveToNext()) {
                note_paths.add(c1.getString(c1.getColumnIndex("note_path")));
            }

            if (note_paths.size() > 0) {
                // 配列の連結
                String[] args2 = new String[note_paths.size() + 1];
                String[] array1 = note_paths.toArray(new String[0]);
                String[] array2 = {String.valueOf(limit)};
                System.arraycopy(array1, 0, args2, 0, array1.length);
                System.arraycopy(array2, 0, args2, array1.length, array2.length);

//                TextUtils.join(",", note_paths.toArray()) +  String.valueOf(limit);

                Cursor c2 = db.rawQuery("SELECT path, title FROM notes WHERE path IN (" + makePlaceholders(note_paths.size()) + ") ORDER BY modified DESC LIMIT ?;", args2);
                while (c2.moveToNext()) {
                    Note note = new Note();
                    note.path = c2.getString(c2.getColumnIndex("path"));
                    note.title = c2.getString(c2.getColumnIndex("title"));
                    noteList.add(note);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return noteList;
    }



    public boolean saveNote(SQLiteDatabase db, String path, String title, ArrayList<String> newTags, ArrayList<String> oldTags) {

        boolean isSuccess = false;
        db.beginTransaction();
        try {

            // UPSERT note
            SQLiteStatement stmt1 = db.compileStatement("INSERT OR IGNORE INTO notes(path, title) VALUES(?, ?);");
            stmt1.bindString(1, path);
            stmt1.bindString(2, title);
            stmt1.executeInsert();

            SQLiteStatement stmt2 = db.compileStatement("UPDATE notes SET title = ?, modified = DATETIME('now', 'localtime') WHERE path = ?;");
            stmt2.bindString(2, path);
            stmt2.bindString(1, title);
            stmt2.executeUpdateDelete();

            // このノートの検索データを再生成する
            SQLiteStatement stmt3 = db.compileStatement("DELETE FROM search_data WHERE note_path = ?;");
            stmt3.bindString(1, path);
            stmt3.executeUpdateDelete();

            SQLiteStatement stmt4 = db.compileStatement("INSERT INTO search_data(note_path, tags) VALUES(?, ?);");
            stmt4.bindString(1, path);

            StringBuilder sb = new StringBuilder();
            for (String newTag : newTags) {
                sb.append(newTag + " ");
            }
            stmt4.bindString(2, sb.toString());
            stmt4.executeUpdateDelete();

            db.setTransactionSuccessful();
            isSuccess = true;
        } catch (Exception e) {
            isSuccess = false;
        } finally {
            db.endTransaction();

            // タグ情報を更新する
            ArrayList<String> allTags = new ArrayList<String>();
            allTags.addAll(newTags);
            allTags.addAll(oldTags);
            updateTags(db, allTags);
        }

        return isSuccess;
    }

    public boolean updateTags(SQLiteDatabase db, ArrayList<String> tags) {

        // HashSetに入れてユニークにする
        Set<String> set = new HashSet<String>();
        set.addAll(tags);

        boolean isSuccess = false;
        db.beginTransaction();
        try {
            SQLiteStatement stmt = db.compileStatement("INSERT OR REPLACE INTO tags(name, note_count) VALUES(?, (SELECT count(note_path) FROM search_data WHERE tags MATCH ?));");
            for(String tag : tags) {
                stmt.clearBindings();
                stmt.bindString(1, tag);
                stmt.bindString(2, tag);
                stmt.executeInsert();
            }
            // カウントが0のタグを削除
            db.execSQL("DELETE FROM tags WHERE note_count = 0;");

            db.setTransactionSuccessful();
            isSuccess = true;
        } catch (Exception e) {
            isSuccess = false;
        } finally {
            db.endTransaction();
        }

       return isSuccess;



    }

    private void execSqlFromFile(SQLiteDatabase db, String assetFile) throws IOException {
        AssetManager as = context.getResources().getAssets();
        try {
            String sql = this.readFile(as.open(assetFile));
            Log.d("sql",sql);
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

    private String makePlaceholders(int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }
}