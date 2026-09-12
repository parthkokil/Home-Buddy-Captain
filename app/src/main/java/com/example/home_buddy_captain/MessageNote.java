package com.example.home_buddy_captain;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

public class MessageNote {

    public static void sendInformationMessage(View view, String text) {
        boolean requestSent = true; // Assume request is successfully added to Firebase

        if (requestSent) {
            Snackbar snackbar = Snackbar.make(view,
                    "ℹ️ " + text,
                    Snackbar.LENGTH_SHORT);

            // 🎨 Set custom background color
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.yellow)); // Change color
            snackbarView.setTranslationY(-100); // Move UP
            // 🎨 Customize text appearance
            TextView snackbarText = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
            snackbarText.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white)); // White text
            snackbarText.setTextSize(16); // Increase text size
            snackbarText.setTypeface(snackbarText.getTypeface(), Typeface.BOLD); // Bold text
            snackbarText.setGravity(Gravity.CENTER_VERTICAL); // Center text
            snackbarView.setPadding(20,0,20, 0);

            // 🎨 Set rounded corners (API 23+)
            GradientDrawable background = new GradientDrawable();
            background.setColor(ContextCompat.getColor(view.getContext(), R.color.white));
            background.setCornerRadius(40f);
            snackbarView.setBackground(background);

            snackbar.show();
        }
    }


    public static void sendSuccessMessage(View view, String text) {
        boolean requestSent = true; // Assume request is successfully added to Firebase

        if (requestSent) {
            Snackbar snackbar = Snackbar.make(view,
                    "✅ " + text,
                    Snackbar.LENGTH_SHORT);

            // 🎨 Set custom background color
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.yellow)); // Change color
            snackbarView.setTranslationY(-100); // Move UP
            // 🎨 Customize text appearance
            TextView snackbarText = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
            snackbarText.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white)); // White text
            snackbarText.setTextSize(16); // Increase text size
            snackbarText.setTypeface(snackbarText.getTypeface(), Typeface.BOLD); // Bold text
            snackbarText.setGravity(Gravity.CENTER_VERTICAL); // Center text
            snackbarView.setPadding(20,0,20, 0);

            // 🎨 Set rounded corners (API 23+)
            GradientDrawable background = new GradientDrawable();
            background.setColor(ContextCompat.getColor(view.getContext(), R.color.white));
            background.setCornerRadius(40f);
            snackbarView.setBackground(background);

            snackbar.show();
        }
    }

    public static void sendFailureMessage(View view, String text) {
        boolean requestSent = true; // Assume request is successfully added to Firebase

        if (requestSent) {
            Snackbar snackbar = Snackbar.make(view,
                    "❌ " + text,
                    Snackbar.LENGTH_SHORT);

            // 🎨 Set custom background color
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.yellow)); // Change color
            snackbarView.setTranslationY(-100); // Move UP
            // 🎨 Customize text appearance
            TextView snackbarText = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
            snackbarText.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white)); // White text
            snackbarText.setTextSize(16); // Increase text size
            snackbarText.setTypeface(snackbarText.getTypeface(), Typeface.BOLD); // Bold text
            snackbarText.setGravity(Gravity.CENTER_VERTICAL); // Center text
            snackbarView.setPadding(20,0,20, 0);

            // 🎨 Set rounded corners (API 23+)
            GradientDrawable background = new GradientDrawable();
            background.setColor(ContextCompat.getColor(view.getContext(), R.color.white));
            background.setCornerRadius(40f);
            snackbarView.setBackground(background);

            snackbar.show();
        }
    }

}
