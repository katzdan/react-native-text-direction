package com.katzdan.langdetect;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Detection of language using {@link android.view.textclassifier.TextClassificationManager}
 * This code came from the very useful Github.com repo at https://github.com/JeppeLeth/android-languagedetection-study
 */
@TargetApi(26)
class DetectionTextClassifier {

    private static Class<?> txtClassifClzz;
    private static Method findLanguages;
    private static Field mScore;
    private static Field mLanguage;
    private static Method getLanguageDetector;
    private static boolean isChecked;

    private Context mContext;

    public DetectionTextClassifier(Context context) {
        mContext = context;
    }

    public DetectionTextClassifier.DetectionResult detectLanguages(String... params) {
        long startTime = SystemClock.elapsedRealtime();
        DetectionResult result = new DetectionResult();
        try {
            if (!isAvailable(mContext)) {
                throw new IllegalStateException("TextClassificationManager not available");
            }
            Object textClassifi = mContext.getSystemService(txtClassifClzz);

            Object langId = getLanguageDetector.invoke(textClassifi);
            Object[] res = (Object[]) findLanguages.invoke(langId, params[0]);

            result.list = new ArrayList<>();
            double score;
            for (Object claRes : res) {
                score = mScore.getDouble(claRes);
                if (score > 0) {
                    result.list.add(new Language(
                            (String) mLanguage.get(claRes),
                            score
                    ));
                }
            }

            Collections.sort(result.list);
            result.executionTime = SystemClock.elapsedRealtime() - startTime;
        } catch (Exception e) {
            Log.d("MAIN", "Error", e);
            result.e = e;
        }
        return result;
    }

    public static boolean isAvailable(@NonNull Context context) {
        if (!isChecked) {
            initReflection(context);
        }
        return getLanguageDetector != null;
    }

    private static boolean initReflection(Context context) {
        isChecked = true;
        if (Build.VERSION.SDK_INT < 26) {
            return false;
        }
        try {
            txtClassifClzz = Class.forName("android.view.textclassifier.TextClassificationManager");
            Object textClassifi = context.getSystemService(txtClassifClzz);

            getLanguageDetector = textClassifi.getClass().getDeclaredMethod("getLanguageDetector");
            getLanguageDetector.setAccessible(true);
            Object langId = getLanguageDetector.invoke(textClassifi);
            findLanguages = langId.getClass().getDeclaredMethod("findLanguages", String.class);

            Class<?> classificationResultClzz = Class.forName("android.view.textclassifier.LangId$ClassificationResult");
            mLanguage = classificationResultClzz.getDeclaredField("mLanguage");
            mLanguage.setAccessible(true);
            mScore = classificationResultClzz.getDeclaredField("mScore");
            mScore.setAccessible(true);
            return true;
        } catch (Exception e) {
            Log.d("MAIN", "Error", e);
            return false;
        }

    }

    public class DetectionResult {
        public List<Language> list;
        public long executionTime;
        public Exception e;

        public boolean hasError() {
            return e != null;
        }

        public boolean isSuccess() {
            return list != null;
        }


    }

    public class Language implements Comparable<Language> {
        public String lang;
        public double prob;

        public Language(String lang, double prob) {
            this.lang = lang;
            this.prob = prob;
        }

        public String toString() {
            if (lang == null) return "";
            return lang + ":" + prob;
        }

        @Override
        public int compareTo(@NonNull Language o) {
            return (int) ((o.prob - this.prob) * 1000f);
        }
    }
}