package com.linux.permissionmanager.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;
import com.linux.permissionmanager.AppSettings;
import com.linux.permissionmanager.R;

public class ThemeUtils {
    public static final int DEFAULT_PRIMARY_COLOR = Color.parseColor("#6750A4");

    public static int getThemeColor() {
        return AppSettings.getInt("theme_color", DEFAULT_PRIMARY_COLOR);
    }

    public static void setThemeColor(int color) {
        AppSettings.setInt("theme_color", color);
    }

    public static void applyTheme(Activity activity) {
        int color = getThemeColor();
        applyToWindow(activity, color);
        
        View rootView = activity.findViewById(android.R.id.content);
        if (rootView != null) {
            applyToViewTree(rootView, color);
        }
    }

    public static void applyToWindow(Activity activity, int color) {
        Window window = activity.getWindow();
        
        // Update status bar and navigation bar if they are not transparent (Edge-to-Edge handles this differently)
        // In this app, MainActivity uses transparent bars with Edge-to-Edge.
        
        // Update Toolbar if found
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        if (toolbar != null) {
            // Only update background if no custom background image is set
            String bgPath = AppSettings.getString("background_path", "");
            if (bgPath.isEmpty()) {
                // In Material 3, we usually want a surface color for toolbar, but we can tint it
                // toolbar.setBackgroundColor(getSurfaceColorWithTint(color));
            }
        }

        // Update BottomNavigationView
        BottomNavigationView nav = activity.findViewById(R.id.bottom_navigation);
        if (nav != null) {
            ColorStateList csl = new ColorStateList(
                new int[][]{
                    new int[]{android.R.attr.state_checked},
                    new int[]{-android.R.attr.state_checked}
                },
                new int[]{
                    color,
                    Color.GRAY
                }
            );
            nav.setItemIconTintList(csl);
            nav.setItemTextColor(csl);
            // Material 3 indicator color
            nav.setItemRippleColor(ColorStateList.valueOf(color & 0x20FFFFFF));
        }
    }

    public static void applyToViewTree(View view, int color) {
        if (view instanceof MaterialButton) {
            MaterialButton btn = (MaterialButton) view;
            ColorStateList csl = ColorStateList.valueOf(color);
            ColorStateList tonalCsl = ColorStateList.valueOf(color & 0x20FFFFFF);
            
            btn.setStrokeColor(csl);
            btn.setRippleColor(tonalCsl);
            
            if (btn.getIcon() != null) {
                btn.setIconTint(csl);
            }
        } else if (view instanceof TextView) {
            TextView tv = (TextView) view;
            // Handle links or specific primary colored text
            if (tv.getTextColors().getDefaultColor() == DEFAULT_PRIMARY_COLOR || 
                tv.getLinkTextColors() != null && tv.getLinkTextColors().getDefaultColor() == DEFAULT_PRIMARY_COLOR) {
                tv.setTextColor(color);
            }
            // Some specific IDs might need color update
            if (tv.getId() == R.id.link_tv || tv.getId() == R.id.core_update_found_tv) {
                tv.setTextColor(color);
            }
        } else if (view instanceof MaterialSwitch) {
            MaterialSwitch sw = (MaterialSwitch) view;
            sw.setThumbTintList(new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{color, Color.WHITE}
            ));
            sw.setTrackTintList(new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{color & 0x40FFFFFF, Color.LTGRAY}
            ));
        } else if (view instanceof Slider) {
            Slider slider = (Slider) view;
            slider.setThumbTintList(ColorStateList.valueOf(color));
            slider.setTrackTintList(ColorStateList.valueOf(color));
            slider.setHaloTintList(ColorStateList.valueOf(color & 0x20FFFFFF));
        } else if (view instanceof CheckBox) {
            ((CheckBox) view).setButtonTintList(ColorStateList.valueOf(color));
        } else if (view instanceof RadioButton) {
            ((RadioButton) view).setButtonTintList(ColorStateList.valueOf(color));
        }

        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                applyToViewTree(vg.getChildAt(i), color);
            }
        }
    }
    
    private static int getSurfaceColorWithTint(int color) {
        // Simple surface tint logic: mix surface color with a bit of primary color
        return Color.argb(255, 
            (int)(Color.red(color) * 0.05 + 255 * 0.95),
            (int)(Color.green(color) * 0.05 + 255 * 0.95),
            (int)(Color.blue(color) * 0.05 + 255 * 0.95));
    }
}
