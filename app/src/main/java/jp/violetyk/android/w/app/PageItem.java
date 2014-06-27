package jp.violetyk.android.w.app;

import java.util.ArrayList;

/**
 * ページのアイテム
 */
public class PageItem {
    // GridViewで表示するページ
    public static final int FRAGMENT_TYPE_GRID = 0;

    // Recommendレイアウトで表示するページ
    public static final int FRAGMENT_TYPE_RECOMMEND = 1;

    // ListViewで表示するページ
    public static final int FRAGMENT_TYPE_LIST = 2;

    // ページ名
    public String title;

    // ページを表示するときのフラグメントタイプ
    public int fragmentType;

    // ページのアイテムのリスト
    public ArrayList<Note> noteList;
}