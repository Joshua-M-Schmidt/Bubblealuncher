package source.nova.com.bubblelauncherfree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class SwipeDeckAdapter extends BaseAdapter {

    private Context context;
    int[] data = {};

    public SwipeDeckAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater inflater =  (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            // normally use a viewholder
            v = inflater.inflate(R.layout.card_item, parent, false);
        }

        ImageView imageView = v.findViewById(R.id.card_image);
        imageView.setImageResource(data[position]);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return v;
    }
}
