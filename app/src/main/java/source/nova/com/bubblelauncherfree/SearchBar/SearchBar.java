package source.nova.com.bubblelauncherfree.SearchBar;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatDelegate;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.Util.Util;
import top.defaults.drawabletoolbox.DrawableBuilder;

import static source.nova.com.bubblelauncherfree.SearchBar.SearchBarSettings.SEARCHBAR_BACKGROUND_COLOR;
import static source.nova.com.bubblelauncherfree.SearchBar.SearchBarSettings.SEARCHBAR_BORDER_RADIUS_KEY;
import static source.nova.com.bubblelauncherfree.SearchBar.SearchBarSettings.SEARCHBAR_MARGIN_KEY;
import static source.nova.com.bubblelauncherfree.SearchBar.SearchBarSettings.SEARCHBAR_TEXT_COLOR_KEY;

public class SearchBar extends LinearLayout {

    public SearchBarListener listener;

    public interface SearchBarListener{
        void onTextChanged(String text);
        void onButtonClicked();
        void onTextFocus();
    }

    private LinearLayout searchBackground;
    public EditText searchText;
    private ImageButton searchButton;

    public void setOnSearchBarListener(SearchBarListener listener){
        this.listener = listener;
    }

    public SearchBar(Context context) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.search_bar, this);

        searchText = findViewById(R.id.search);
        searchText.setSingleLine(true);
        searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButtonClicked();
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(listener != null) {
                    listener.onTextChanged(s.toString());
                }
            }
        });

        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(listener != null){
                    listener.onTextFocus();
                }
            }
        });

        searchBackground = findViewById(R.id.search_background);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(int) Util.dipToPixels(context,80));
        //layoutParams.setMargins(0,0,0,0);
        setLayoutParams(layoutParams);
        searchBackground.setBackground(getDrawable());
        float margin = PreferenceManager.getDefaultSharedPreferences(getContext()).getFloat(SEARCHBAR_MARGIN_KEY,10f);
        //layoutParams.setMargins((int) margin, 0,(int) margin,0);
        setLayoutParams(layoutParams);
        setAlpha(0f);
        //searchBackground.setLayoutParams(layoutParams);

        color = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(SEARCHBAR_TEXT_COLOR_KEY,0xff555555);
        searchText.setHintTextColor(color);
        searchText.setTextColor(color);
        searchButton.setImageDrawable(changeDrawableColor(getContext(),R.drawable.baseline_close_24px,color));

    }

    int color;

    public boolean isExpanded = false;

    public void setExpanded(){
        isExpanded = true;
    }

    public void setCollapsed(){
        searchText.clearFocus();
        searchText.setText("");
        isExpanded = false;
    }

    public static Drawable changeDrawableColor(Context context,int icon, int newColor) {
        Drawable mDrawable = ContextCompat.getDrawable(context, icon).mutate();
        mDrawable.setColorFilter(new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_IN));
        return mDrawable;
    }

    public void focus(){
        searchText.requestFocus();
    }

    public void unfocus(){
        searchText.clearFocus();
        searchText.setText("");
        searchText.setFocusable(false);
    }

    private Drawable getDrawable(){
        int color = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(SEARCHBAR_BACKGROUND_COLOR,0xffffffff);
        float radius = PreferenceManager.getDefaultSharedPreferences(getContext()).getFloat(SEARCHBAR_BORDER_RADIUS_KEY,100f);

        Drawable drawable = new DrawableBuilder()
                .rectangle()
                .solidColor(color)
                .cornerRadii((int) radius,(int) radius, (int) radius,(int) radius) // the same as the two lines above
                .build();
        return drawable;
    }
}
