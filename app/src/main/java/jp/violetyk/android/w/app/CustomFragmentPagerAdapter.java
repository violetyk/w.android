package jp.violetyk.android.w.app;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by kagaya on 2014/06/04.
 */
public class CustomFragmentPagerAdapter extends FragmentPagerAdapter {
    /** {@link PageItem} のリスト. */
    private ArrayList<PageItem> mList;

    /**
     * コンストラクタ.
     * @param fm {@link FragmentManager}
     */
    public CustomFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        mList = new ArrayList<PageItem>();
    }

    @Override
    public Fragment getItem(int position) {
        PageItem item = mList.get(position);

        Fragment fragment = null;
        switch (item.fragmentType) {
            case PageItem.FRAGMENT_TYPE_LIST:
                fragment = new ListViewFragment();
                break;

            case PageItem.FRAGMENT_TYPE_GRID:
                fragment = new GridViewFragment();
                break;

            case PageItem.FRAGMENT_TYPE_RECOMMEND:
                fragment = new RecommendFragment();
                break;
        }

        // Bundle を作成
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", item.list);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mList.get(position).title;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    /**
     * アイテムを追加する.
     * @param item {@link PageItem}
     */
    public void addItem(PageItem item) {
        mList.add(item);
    }
}