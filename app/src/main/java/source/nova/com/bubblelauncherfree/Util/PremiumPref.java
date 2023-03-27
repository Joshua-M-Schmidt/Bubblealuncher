package source.nova.com.bubblelauncherfree.Util;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import source.nova.com.bubblelauncherfree.R;

public class PremiumPref extends Preference {
    public PremiumPref(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateView( ViewGroup parent ) {
        super.onCreateView(parent);
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return li.inflate(R.layout.preference_premium, parent, false);
    }
}
