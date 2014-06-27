package jp.violetyk.android.w.app;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListViewFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ListView listView = new ListView(getActivity());

        Bundle bundle = getArguments();
        @SuppressWarnings("unchecked")
        ArrayList<Note> list = (ArrayList<Note>)bundle.get("list");
        listView.setAdapter(new NoteListAdapter(getActivity(), R.layout.item_note_list, list));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                MainActivity activity = (MainActivity)adapterView.getContext();
                Intent intent = new Intent();
                intent.setClassName(activity.getPackageName(), activity.getPackageName() + ".EditActivity");
                intent.putExtra("noteDir", activity.getNoteDir());
                intent.putExtra("noteName", ((Note)adapterView.getItemAtPosition(i)).path);
                startActivity(intent);
            }
        });
        return listView;
    }

}
