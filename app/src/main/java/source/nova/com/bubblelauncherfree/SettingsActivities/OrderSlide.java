package source.nova.com.bubblelauncherfree.SettingsActivities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import source.nova.com.bubblelauncherfree.MainActivity;
import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.Util.Util;

import static source.nova.com.bubblelauncherfree.Util.Util.showNetworkNotAvailableWarning;

public class OrderSlide extends Fragment {
    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private int layoutResId;

    public static final String PATTERN = "pattern";
    public static final String PATTERN_CLASSIC = "classic";
    public static final String PATTERN_SORT = "sort";
    public static final String PATTERN_THEME1 = "theme_1";
    public static final String PATTERN_THEME2 = "theme_2";
    public static final String PATTERN_THEME4 = "theme_4";
    public static final String PATTERN_THEME5 = "theme_5";

    public Context ctx;

    public static OrderSlide newInstance(int layoutResId, Context ctx) {
        OrderSlide sampleSlide = new OrderSlide();
        sampleSlide.ctx = ctx;
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID)) {
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(layoutResId, container, false);

        final CheckBox theme1 = rootView.findViewById(R.id.theme1);
        final CheckBox theme2 = rootView.findViewById(R.id.theme2);
        final CheckBox theme4 = rootView.findViewById(R.id.theme4);
        final CheckBox theme5 = rootView.findViewById(R.id.theme5);

        final CheckBox classic = rootView.findViewById(R.id.classic);
        final CheckBox sort = rootView.findViewById(R.id.sort);
        sort.setChecked(true);

        TextView premium_label_1 = rootView.findViewById(R.id.premium_label_1);
        TextView premium_label_2 = rootView.findViewById(R.id.premium_label_2);
        TextView premium_label_4 = rootView.findViewById(R.id.premium_label_4);
        TextView premium_label_5 = rootView.findViewById(R.id.premium_label_5);


        if(MainActivity.IS_PREMIUM){
            premium_label_1.setVisibility(View.GONE);
            premium_label_2.setVisibility(View.GONE);
            premium_label_4.setVisibility(View.GONE);
            premium_label_5.setVisibility(View.GONE);
        }else{
            theme1.setVisibility(View.GONE);
            theme2.setVisibility(View.GONE);
            theme4.setVisibility(View.GONE);
            theme5.setVisibility(View.GONE);
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PATTERN,PATTERN_SORT);
        editor.commit();

        classic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    sort.setChecked(false);
                    theme1.setChecked(false);
                    theme2.setChecked(false);
                    theme4.setChecked(false);
                    theme5.setChecked(false);
                }
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(PATTERN,PATTERN_CLASSIC);
                editor.commit();
            }
        });

        sort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(!Util.isNetworkAvailable(ctx)){
                    showNetworkNotAvailableWarning(ctx);
                    sort.setChecked(false);
                }else{
                    if(isChecked){
                        classic.setChecked(false);
                        theme1.setChecked(false);
                        theme2.setChecked(false);
                        theme4.setChecked(false);
                        theme5.setChecked(false);
                    }
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(PATTERN,PATTERN_SORT);
                    editor.commit();
                }
            }
        });


        theme1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(!Util.isNetworkAvailable(ctx)){
                    showNetworkNotAvailableWarning(ctx);
                    theme1.setChecked(false);
                }else{
                    if(isChecked){
                        sort.setChecked(false);
                        classic.setChecked(false);
                        theme2.setChecked(false);
                        theme4.setChecked(false);
                        theme5.setChecked(false);
                    }
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(PATTERN,PATTERN_THEME1);
                    editor.commit();
                }

            }
        });

        theme2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!Util.isNetworkAvailable(ctx)){
                    showNetworkNotAvailableWarning(ctx);
                    theme2.setChecked(false);
                }else{
                    if(isChecked){
                        sort.setChecked(false);
                        theme1.setChecked(false);
                        classic.setChecked(false);
                        theme4.setChecked(false);
                        theme5.setChecked(false);
                    }
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(PATTERN,PATTERN_THEME2);
                    editor.commit();
                }

            }
        });

        theme4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!Util.isNetworkAvailable(ctx)){
                    showNetworkNotAvailableWarning(ctx);
                    theme4.setChecked(false);
                }else{
                    if(isChecked){
                        sort.setChecked(false);
                        theme1.setChecked(false);
                        classic.setChecked(false);
                        theme2.setChecked(false);
                        theme5.setChecked(false);
                    }
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(PATTERN,PATTERN_THEME4);
                    editor.commit();
                }

            }
        });

        theme5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!Util.isNetworkAvailable(ctx)){
                    showNetworkNotAvailableWarning(ctx);
                    theme5.setChecked(false);
                }else{
                    if(isChecked){
                        sort.setChecked(false);
                        theme1.setChecked(false);
                        classic.setChecked(false);
                        theme4.setChecked(false);
                        theme2.setChecked(false);
                    }
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(PATTERN,PATTERN_THEME5);
                    editor.commit();
                }

            }
        });

        return rootView;
    }
}