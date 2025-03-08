package com.example.techassignment_2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class InsertProfileDialogFragment extends DialogFragment {

    private EditText surnameEditText, nameEditText, idEditText, gpaEditText;
    private DatabaseHelper dbHelper;
    private InsertProfileListener listener;

    // Interface for sending data back to MainActivity
    public interface InsertProfileListener {
        void onProfileInserted();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof InsertProfileListener) {
            listener = (InsertProfileListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement InsertProfileListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_insert_profile, null);

        // Initialize UI
        surnameEditText = view.findViewById(R.id.surnameEditText);
        nameEditText = view.findViewById(R.id.nameEditText);
        idEditText = view.findViewById(R.id.idEditText);
        gpaEditText = view.findViewById(R.id.gpaEditText);
        Button saveButton = view.findViewById(R.id.saveButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        dbHelper = new DatabaseHelper(getActivity());

        // Handle Save Button Click
        saveButton.setOnClickListener(v -> {
            String surname = surnameEditText.getText().toString().trim();
            String name = nameEditText.getText().toString().trim();
            String idStr = idEditText.getText().toString().trim();
            String gpaStr = gpaEditText.getText().toString().trim();

            // Validate input
            if (TextUtils.isEmpty(surname) || TextUtils.isEmpty(name) ||
                    TextUtils.isEmpty(idStr) || TextUtils.isEmpty(gpaStr)) {
                Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate ID (Must be 8 digits)
            if (idStr.length() != 8 || !idStr.matches("\\d{8}")) {
                Toast.makeText(getActivity(), "ID must be an 8-digit number", Toast.LENGTH_SHORT).show();
                return;
            }

            int id = Integer.parseInt(idStr);

            // Check if ID already exists
            if (dbHelper.isIdExists(id)) {
                Toast.makeText(getActivity(), "ID already exists!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate GPA (Must be between 0 and 4.3)
            float gpa;
            try {
                gpa = Float.parseFloat(gpaStr);
                if (gpa < 0 || gpa > 4.3) {
                    Toast.makeText(getActivity(), "GPA must be between 0 and 4.3", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Invalid GPA", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save Profile to Database
            dbHelper.addProfile(new Profile(id, surname, name, gpa));
            Toast.makeText(getActivity(), "Profile Added!", Toast.LENGTH_SHORT).show();

            // Notify MainActivity & Dismiss Dialog
            if (listener != null) {
                listener.onProfileInserted();
            }
            dismiss();
        });

        // Handle Cancel Button Click
        cancelButton.setOnClickListener(v -> dismiss());

        builder.setView(view);
        return builder.create();
    }
}