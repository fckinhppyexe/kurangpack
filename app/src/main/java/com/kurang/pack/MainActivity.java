package com.kurang.pack;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryActionListener {
    private static final String PREFS_NAME = "kurangpack_prefs";
    private static final String KEY_CATEGORIES = "categories_json";

    private final Gson gson = new Gson();
    private ArrayList<Category> categories;
    private CategoryAdapter adapter;
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        categories = loadCategories();
        if (categories.isEmpty()) {
            categories.add(new Category("Kategori 1", 24, 2, 13));
            saveCategories();
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategoryAdapter(categories, this);
        recyclerView.setAdapter(adapter);

        Button addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener(v -> addNewCategory());
    }

    private ArrayList<Category> loadCategories() {
        String json = sharedPrefs.getString(KEY_CATEGORIES, null);
        if (json == null) return new ArrayList<>();
        try {
            Type type = new TypeToken<ArrayList<Category>>() {}.getType();
            ArrayList<Category> list = gson.fromJson(json, type);
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void saveCategories() {
        sharedPrefs.edit().putString(KEY_CATEGORIES, gson.toJson(categories)).apply();
    }

    private void addNewCategory() {
        Category cat = new Category("Kategori " + (categories.size() + 1), 24, 1, 0);
        categories.add(cat);
        saveCategories();
        adapter.notifyItemInserted(categories.size() - 1);
    }

    @Override
    public void onNameChanged(Category cat, String name, int pos) {
        cat.name = name;
        saveCategories();
    }

    @Override
    public void onEditName(Category cat, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Nama Kategori");

        final EditText input = new EditText(this);
        input.setText(cat.name);
        input.setSelectAllOnFocus(true);
        builder.setView(input);

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String name = input.getText().toString().trim();
            cat.name = name.isEmpty() ? "Kategori" : name;
            saveCategories();
            adapter.notifyItemChanged(pos);
        });
        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    @Override
    public void onDelete(Category cat, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hapus \"" + cat.name + "\"?");
        builder.setMessage("Kategori ini akan dihapus.");
        builder.setPositiveButton("Hapus", (dialog, which) -> {
            categories.remove(pos);
            saveCategories();
            adapter.notifyItemRemoved(pos);
        });
        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    @Override
    public void onStep(Category cat, String field, int delta, int pos) {
        updateField(cat, field, catField(cat, field) + delta);
        saveCategories();
        adapter.updateResult(pos);
    }

    @Override
    public void onFieldChanged(Category cat, String field, int value, int pos) {
        updateField(cat, field, value);
        saveCategories();
        adapter.updateResult(pos);
    }

    private void updateField(Category cat, String field, int value) {
        if (value < 0) value = 0;
        switch (field) {
            case "isi": cat.isi = value; break;
            case "kardus": cat.kardus = value; break;
            case "ada": cat.ada = value; break;
        }
    }

    private int catField(Category cat, String field) {
        switch (field) {
            case "isi": return cat.isi;
            case "kardus": return cat.kardus;
            case "ada": return cat.ada;
        }
        return 0;
    }
}