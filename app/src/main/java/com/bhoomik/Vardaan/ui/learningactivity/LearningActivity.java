package com.bhoomik.Vardaan.ui.learningactivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bhoomik.Vardaan.Constants;
import com.bhoomik.Vardaan.Module;
import com.bhoomik.Vardaan.R;
import com.bhoomik.Vardaan.ui.CertificateActivity;
import com.bhoomik.Vardaan.ui.Dashboard;
import com.bhoomik.Vardaan.ui.Form;
import com.bhoomik.Vardaan.ui.LoginActivity;
import com.bhoomik.Vardaan.ui.aboutactivity.AboutNgo;
import com.bhoomik.Vardaan.ui.aboutactivity.Aboutus;
import com.bhoomik.Vardaan.ui.aboutactivity.CreatorUs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.bhoomik.Vardaan.Constants.User;

public class LearningActivity extends AppCompatActivity {

    public static List<Module> modules = new ArrayList<>();
    private LearningAdapter learningAdapter;
    private ProgressBar progressBar;

    private MaterialCardView extraMaterialCardView;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    SharedPreferences preferences;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void certificate(MenuItem item) {
        startActivity(new Intent(this, CertificateActivity.class));
    }

    public void FormStudents(MenuItem item) {
        Intent intent = new Intent(this, Form.class);
        intent.putExtra("mode", Constants.Forms.FORM_STUDENTS);
        startActivity(intent);
    }

    public void FormKit(MenuItem item) {
        Intent intent = new Intent(this, Form.class);
        intent.putExtra("mode", Constants.Forms.FORM_KIT);
        startActivity(intent);
    }

    public void about(MenuItem item) {
        Intent intent = new Intent(this, Aboutus.class);
        startActivity(intent);
    }
    public void creator(MenuItem item) {
        Intent intent = new Intent(this, CreatorUs.class);
        startActivity(intent);
    }
    public void aboutngo(MenuItem item) {
        Intent intent = new Intent(this, AboutNgo.class);
        intent.putExtra(Constants.Organisation.ORGANISATION_NAME, Constants.Organisation.JUMIO);
        startActivity(intent);
    }

    public void logout(MenuItem item) {
        final AlertDialog.Builder logout = new AlertDialog.Builder(this);

        View layout = getLayoutInflater().inflate(R.layout.logout_layout, null, false);
        final EditText entryName = layout.findViewById(R.id.entry_name);
        logout.setView(layout);
        logout.setCancelable(true);

        final AlertDialog logoutDialog = logout.create();


        layout.findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entryName.getText().toString().equals(Constants.name_all)) {
                    preferences.getAll().clear();
                    SharedPreferences preferences =getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
                    startActivity(new Intent(LearningActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(LearningActivity.this, "गलत नाम डाला जा रहा है", Toast.LENGTH_SHORT).show();
                    entryName.getText().clear();
                }
            }
        });

        layout.findViewById(R.id.btn_logout_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog.cancel();

            }
        });
        logoutDialog.show();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progress_circle);
        progressBar.setVisibility(View.VISIBLE);
        extraMaterialCardView = findViewById(R.id.extra_material);
        extraMaterialCardView.setVisibility(View.GONE);

        RecyclerView learningRecyclerView = findViewById(R.id.learning_recyclerview);
        View quizBottomSheet = getLayoutInflater().inflate(R.layout.test_layout, null, false);
        BottomSheetDialog quizBottomSheetDialog = new BottomSheetDialog(this);
        quizBottomSheetDialog.setContentView(quizBottomSheet);


        learningRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        learningAdapter = new LearningAdapter(this, modules, quizBottomSheet, quizBottomSheetDialog);
        learningRecyclerView.setAdapter(learningAdapter);
        if (modules.size() > 0) {
            progressBar.setVisibility(View.GONE);
            extraMaterialCardView.setVisibility(View.VISIBLE);
        }

        preferences = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);

        String userId = preferences.getString(User.USER_CONTACT_NUMBER, null);
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    User.accessModule = Objects.requireNonNull(document.getLong(User.ACCESS_MODULE)).intValue();
                                    User.accessQuestion = Objects.requireNonNull(document.getLong(User.ACCESS_QUESTION)).intValue();
                                    if (document.contains(User.PROGRESS_LECTURE)) {
                                        User.progressLecture = document.getBoolean(User.PROGRESS_LECTURE);
                                    }
                                    if (document.contains(User.PROGRESS_LINK)) {
                                        User.progressLink = document.getBoolean(User.PROGRESS_LINK);
                                    }
                                    if (document.contains(User.PROGRESS_PDF)) {
                                        User.progressPdf = document.getBoolean(User.PROGRESS_PDF);
                                    }

                                }
                            }
                        }
                    });

            firestore.collection("modules").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        modules.clear();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Map<String, Object> attachments = document.getData();

                            modules.add(new Module(Integer.parseInt(document.getId()), document.getString("Topic"), attachments));
                        }
                        Collections.sort(modules, new LearningActivity.SortbyModuleNumber());
                        try {
                            learningAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            extraMaterialCardView.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.e("TAGG", "Error getting modules", task.getException());
                    }
                }
            });
        }

    }

    static class SortbyModuleNumber implements Comparator<Module> {

        @Override
        public int compare(Module a, Module b) {
            return a.getModuleNumber() - b.getModuleNumber();
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        Intent intent = new Intent(LearningActivity.this, Dashboard.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // disable going back to the MainActivity
        Intent intent = new Intent(LearningActivity.this, Dashboard.class);
        startActivity(intent);
        return true;
    }

}
