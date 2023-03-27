package source.nova.com.bubblelauncherfree.Settings;

import android.content.Context;
import android.graphics.Typeface;
import androidx.core.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.Util.FontItem;

public class FontPickerAdapter extends BaseAdapter {

        private ArrayList<FontItem> listData;

        private LayoutInflater layoutInflater;

        private Context ctx;

        public FontPickerAdapter(Context context, ArrayList<FontItem> listData) {
            this.listData = listData;
            this.ctx = context;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.font_picker_item, null);
                holder = new ViewHolder();
                holder.font = convertView.findViewById(R.id.font);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Typeface tface = ResourcesCompat.getFont(ctx, listData.get(position).path);
            holder.font.setTypeface( tface );

            holder.font.setText(listData.get(position).name);

            return convertView;
        }

        static class ViewHolder {
            TextView font;
        }

    }