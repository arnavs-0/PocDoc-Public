package com.arnav.pocdoc;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;


class Field extends TextInputLayout {
    TextInputEditText input;
    String firebase_id;

    Field(final Context context, String hint, Drawable icon, String id) {
        super(new ContextThemeWrapper(context, R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox), null, 0);

        input = new TextInputEditText(getContext());
        firebase_id = id;

        setHint(hint);
        setStartIconDrawable(icon);
        setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        setPadding(25, 25, 25, 25);

        input.setTextColor(Color.BLACK);
        FirebaseDatabase.getInstance().getReference().child("medical_id").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(firebase_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    input.setText(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        addView(input);
    }

    protected String getText() {
        return Objects.requireNonNull(input.getText()).toString();
    }

    protected String getFirebaseId() {
        return firebase_id;
    }
}


public class MedicalIdEdit extends DialogFragment {
    LinearLayout scrollview;
    TextView section_textview;
    Button cancel_button, save_button;

    String section;
    Bundle data;
    ArrayList<Field> fields = new ArrayList<>();

    Dialog loading_dialog;

    MedicalIdEdit(String section, Bundle data, Dialog loading_dialog) {
        this.section = section;
        this.data = data;
        this.loading_dialog = loading_dialog;
    }

    private String title(String string) {
        String empty = getString(R.string.empty_text);
        if (!string.isEmpty() && !string.equals(empty)) {
            string = string.trim();
            char first = string.charAt(0);
            String rest = string.substring(1);
            return Character.toUpperCase(first)+rest.toLowerCase();
        }
        return empty;
    }

    private boolean save() {
        boolean no_errors = true;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("medical_id").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        for (Field field : fields) {
            String id = field.getFirebaseId();
            String text = title(field.getText());
            String empty = getString(R.string.empty_text);

            boolean isGender = id.equals("gender");
            boolean isAge = id.equals("age");
            boolean gender = isGender && (text.equals("Male") || text.equals("Female") || text.equals(empty));
            boolean age = isAge && (Pattern.compile("^[0-9]+$", Pattern.CASE_INSENSITIVE).matcher(text).find() || text.equals(empty));

            if (gender || age || (!isGender && !isAge)) {
                reference.child(id).setValue((id.equals("email") && !text.equals(empty)) ? text.toLowerCase() : text);
            }
            else if (isGender) {
                no_errors = false;
                Toast.makeText(getContext(), "Please enter \"Male\" or \"Female\" for gender", Toast.LENGTH_SHORT).show();
            }
            else if (isAge) {
                no_errors = false;
                Toast.makeText(getContext(), "Please enter an integer for age", Toast.LENGTH_SHORT).show();
            }
        }
        return no_errors;
    }

    @Override
    public void dismiss() {
        loading_dialog.dismiss();
        super.dismiss();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_DialogWhenLarge);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_id_edit, container, false);

        section_textview = view.findViewById(R.id.medical_id_edit_section);
        cancel_button = view.findViewById(R.id.medical_id_edit_cancel);
        save_button = view.findViewById(R.id.medical_id_edit_save);
        scrollview = view.findViewById(R.id.medical_id_info_scrollview);

        section_textview.setText(this.section);

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (save()) {
                    Toast.makeText(getContext(), "Changes Saved", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });

        fields = new ArrayList<>();

        for (String key : data.keySet()) {
            Object[] values = (Object[]) data.get(key);

            Drawable icon = ContextCompat.getDrawable(Objects.requireNonNull(getContext()), (int) values[0]);

            if (! (icon instanceof VectorDrawable) && icon != null) {
                Bitmap icon_bitmap = Bitmap.createScaledBitmap(((BitmapDrawable) icon).getBitmap(), 100, 100, false);
                icon = new BitmapDrawable(getResources(), icon_bitmap);
            }

            Field field = new Field(scrollview.getContext(), key, icon, (String) values[1]);

            if (field.getFirebaseId().equals("patientID")) {
                field.input.setTextColor(Color.LTGRAY);
                field.setEnabled(false);
            }

            fields.add(field);
            scrollview.addView(field);
        }
        return view;
    }
}
