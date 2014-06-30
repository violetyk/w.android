package jp.violetyk.android.w.app;
import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by kagaya on 2014/06/03.
 */
public class TagListAdapter extends ArrayAdapter<Tag> {

    public TagListAdapter(Context context, int resource, List<Tag> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tag item = getItem(position);

        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_tag_grid, parent, false);
            holder = new ViewHolder();
            holder.nameTextView = (TextView) convertView.findViewById(R.id.name_text_view);
            holder.noteCountTextView = (TextView) convertView.findViewById(R.id.note_count_text_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.nameTextView.setText(item.name);
        holder.noteCountTextView.setText(item.note_count);

        return convertView;
    }

    /**
     * ViewHolder.
     */
    private class ViewHolder {

        public TextView nameTextView;
        public TextView noteCountTextView;

    }
}