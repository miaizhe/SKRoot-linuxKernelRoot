package com.linux.permissionmanager.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricUtils {
    private static final String TAG = "LyricUtils";

    public static class LyricEntry implements Comparable<LyricEntry> {
        public long time;
        public String text;

        public LyricEntry(long time, String text) {
            this.time = time;
            this.text = text;
        }

        @Override
        public int compareTo(LyricEntry other) {
            return Long.compare(this.time, other.time);
        }
    }

    public static List<LyricEntry> parseLrc(Context context, Uri uri) {
        List<LyricEntry> lyrics = new ArrayList<>();
        if (uri == null) return lyrics;

        try (InputStream is = context.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
            String line;
            // Pattern for [mm:ss.xx] or [mm:ss:xx]
            Pattern pattern = Pattern.compile("\\[(\\d{2}):(\\d{2})[.:](\\d{2,3})\\](.*)");
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    long min = Long.parseLong(matcher.group(1));
                    long sec = Long.parseLong(matcher.group(2));
                    long ms = Long.parseLong(matcher.group(3));
                    
                    // If ms is 2 digits, it's 1/100 of a second, so multiply by 10
                    if (matcher.group(3).length() == 2) {
                        ms *= 10;
                    }
                    
                    long time = min * 60 * 1000 + sec * 1000 + ms;
                    String text = matcher.group(4).trim();
                    
                    if (!text.isEmpty()) {
                        lyrics.add(new LyricEntry(time, text));
                    }
                }
            }
            Collections.sort(lyrics);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing LRC file", e);
        }
        return lyrics;
    }

    public static String getCurrentLyric(List<LyricEntry> lyrics, long currentTime) {
        if (lyrics == null || lyrics.isEmpty()) return "";
        
        String currentText = "";
        for (LyricEntry entry : lyrics) {
            if (currentTime >= entry.time) {
                currentText = entry.text;
            } else {
                break;
            }
        }
        return currentText;
    }
}
