package jp.violetyk.android.w.app;
import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by kagaya on 2014/06/03.
 */
public class AppListAdapter extends ArrayAdapter<App> {

    public AppListAdapter(Context context, int resource, List<App> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        App item = getItem(position);

        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_app, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.title_text_view);
            holder.companyTextView = (TextView) convertView.findViewById(R.id.company_text_view);
            holder.ratingBar = (RatingBar) convertView.findViewById(R.id.rating_bar);
            holder.valueTextView = (TextView) convertView.findViewById(R.id.value_text_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleTextView.setText(item.title);
        holder.companyTextView.setText(item.company);
        holder.ratingBar.setRating(item.rate);
        holder.valueTextView.setText("￥" + item.value);

        return convertView;
    }

    /**
     * ViewHolder.
     */
    private class ViewHolder {

        /** アプリアイコン. */
        public ImageView imageView;
        /** アプリ名. */
        public TextView titleTextView;
        /** 制作会社. */
        public TextView companyTextView;
        /** 評価. */
        public RatingBar ratingBar;
        /** 価格. */
        public TextView valueTextView;

    }
}