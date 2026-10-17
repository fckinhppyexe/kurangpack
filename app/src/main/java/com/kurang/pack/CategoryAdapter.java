package com.kurang.pack;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public interface OnCategoryActionListener {
        void onNameChanged(Category cat, String name, int pos);
        void onEditName(Category cat, int pos);
        void onDelete(Category cat, int pos);
        void onStep(Category cat, String field, int delta, int pos);
        void onFieldChanged(Category cat, String field, int value, int pos);
    }

    private final ArrayList<Category> categories;
    private final OnCategoryActionListener listener;
    private boolean binding;

    public CategoryAdapter(ArrayList<Category> categories, OnCategoryActionListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    OnCategoryActionListener getListener() {
        return listener;
    }

    Category getItem(int pos) {
        return categories.get(pos);
    }

    boolean isBinding() {
        return binding;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        binding = true;
        Category cat = categories.get(position);
        holder.nameView.setText(cat.name);
        holder.editIsi.setText(String.valueOf(cat.isi));
        holder.editKardus.setText(String.valueOf(cat.kardus));
        holder.editAda.setText(String.valueOf(cat.ada));
        binding = false;

        updateResult(holder, cat);

        holder.stepIsiMinus.setOnClickListener(v -> listener.onStep(cat, "isi", -1, holder.getBindingAdapterPosition()));
        holder.stepIsiPlus.setOnClickListener(v -> listener.onStep(cat, "isi", 1, holder.getBindingAdapterPosition()));
        holder.stepKardusMinus.setOnClickListener(v -> listener.onStep(cat, "kardus", -1, holder.getBindingAdapterPosition()));
        holder.stepKardusPlus.setOnClickListener(v -> listener.onStep(cat, "kardus", 1, holder.getBindingAdapterPosition()));
        holder.stepAdaMinus.setOnClickListener(v -> listener.onStep(cat, "ada", -1, holder.getBindingAdapterPosition()));
        holder.stepAdaPlus.setOnClickListener(v -> listener.onStep(cat, "ada", 1, holder.getBindingAdapterPosition()));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(cat, holder.getBindingAdapterPosition()));
    }

    public void updateResult(int position) {
        if (position >= 0 && position < categories.size()) {
            notifyItemChanged(position, "result");
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (payloads != null && !payloads.isEmpty()) {
            if (payloads.contains("result")) {
                updateResult(holder, categories.get(position));
                return;
            }
        }
        onBindViewHolder(holder, position);
    }

    private void updateResult(ViewHolder holder, Category cat) {
        int butuh = cat.getButuh();
        int kurang = cat.getKurang();

        holder.txtDetail.setText(cat.kardus + " kardus × " + cat.isi + " = " + butuh + " pack butuh, sudah ada " + cat.ada + " pack.");
        // Memperbaiki penulisan Sino-Korea
        holder.txtHangul.setText("Sino: " + cat.kardus + " 박스 × " + cat.isi + " = " + butuh + " 팩 필요, 현재 " + cat.ada + " 팩 있음.");
        holder.txtHangulUnit.setText(kurang + " 팩 부족");

        int zeroColor = Color.parseColor("#6b755d");
        int normDetail = Color.parseColor("#b9c9bd");
        int normKr = Color.parseColor("#b59d00");

        if (kurang == 0) {
            holder.resultBox.setBackgroundColor(Color.parseColor("#2f6f4e"));
            holder.txtDetail.setTextColor(zeroColor);
            holder.txtHangul.setTextColor(zeroColor);
            holder.txtKurang.setTextColor(Color.WHITE);
            holder.txtUnit.setTextColor(Color.parseColor("#d7ead9"));
            holder.txtHangulUnit.setTextColor(Color.parseColor("#7d8d00"));
            holder.txtKurangKorean.setTextColor(Color.parseColor("#9db500"));
        } else {
            holder.resultBox.setBackgroundColor(Color.parseColor("#1c2b24"));
            holder.txtDetail.setTextColor(normDetail);
            holder.txtHangul.setTextColor(Color.parseColor("#8a8776"));
            holder.txtKurang.setTextColor(Color.parseColor("#c96a3c"));
            holder.txtUnit.setTextColor(normDetail);
            holder.txtHangulUnit.setTextColor(Color.parseColor("#8d7c00"));
            holder.txtKurangKorean.setTextColor(normKr);
        }

        holder.txtKurang.setText(String.valueOf(kurang));
        // Menampilkan Sino + Pure Korean (Korea Asli)
        holder.txtKurangKorean.setText(angkaKeKorea(kurang) + " | " + getPureKorean(kurang));
    }

    // Memperbaiki logika Sino-Korea
    private String angkaKeKorea(int n) {
        if (n == 0) return "영";
        String[] sino = {"", "일", "이", "삼", "사", "오", "육", "칠", "팔", "구", "십"};
        StringBuilder sb = new StringBuilder();
        if (n >= 100) {
            sb.append(sino[n / 100]).append("백");
            n %= 100;
        }
        if (n >= 10) {
            if (n / 10 > 1) sb.append(sino[n / 10]);
            sb.append("십");
            n %= 10;
        }
        if (n > 0) sb.append(sino[n]);
        return sb.toString();
    }

    // Menambahkan logika Pure Korean (Korea Asli) hingga 500
    private String getPureKorean(int n) {
        if (n <= 0) return "-";
        String[] nativeK = {"", "하나", "둘", "셋", "넷", "다섯", "여섯", "일곱", "여덟", "아홉", "열"};
        String[] tens = {"", "열", "스물", "서른", "마흔", "쉰", "예순", "일흔", "여든", "아흔"};
        
        StringBuilder sb = new StringBuilder();
        
        // Ratusan tetap menggunakan Sino (aturan bahasa)
        if (n >= 100) {
            String[] sinoRatusan = {"", "백", "이백", "삼백", "사백", "오백"};
            sb.append(sinoRatusan[n / 100]);
            n %= 100;
        }
        
        // Puluhan dan Satuan menggunakan Pure Korean
        if (n >= 10) {
            sb.append(tens[n / 10]);
            n %= 10;
        }
        if (n > 0) {
            sb.append(nativeK[n]);
        }
        return sb.toString();
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameView;
        public EditText editIsi, editKardus, editAda;
        public Button stepIsiMinus, stepIsiPlus;
        public Button stepKardusMinus, stepKardusPlus;
        public Button stepAdaMinus, stepAdaPlus;
        public ImageButton btnDelete;
        public TextView txtDetail, txtHangul, txtKurang, txtUnit, txtHangulUnit, txtKurangKorean;
        public View resultBox;

        private final CategoryAdapter adapter;

        public ViewHolder(View itemView, CategoryAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            nameView = itemView.findViewById(R.id.editName);
            editIsi = itemView.findViewById(R.id.editIsi);
            editKardus = itemView.findViewById(R.id.editKardus);
            editAda = itemView.findViewById(R.id.editAda);
            stepIsiMinus = itemView.findViewById(R.id.stepIsiMinus);
            stepIsiPlus = itemView.findViewById(R.id.stepIsiPlus);
            stepKardusMinus = itemView.findViewById(R.id.stepKardusMinus);
            stepKardusPlus = itemView.findViewById(R.id.stepKardusPlus);
            stepAdaMinus = itemView.findViewById(R.id.stepAdaMinus);
            stepAdaPlus = itemView.findViewById(R.id.stepAdaPlus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            txtDetail = itemView.findViewById(R.id.txtDetail);
            txtHangul = itemView.findViewById(R.id.txtHangul);
            txtKurang = itemView.findViewById(R.id.txtKurang);
            txtUnit = itemView.findViewById(R.id.txtUnit);
            txtHangulUnit = itemView.findViewById(R.id.txtHangulUnit);
            txtKurangKorean = itemView.findViewById(R.id.txtKurangKorean);
            resultBox = itemView.findViewById(R.id.resultBox);

            nameView.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
                @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
                @Override public void afterTextChanged(Editable s) {
                    if (adapter == null || adapter.isBinding()) return;
                    int pos = getBindingAdapterPosition();
                    if (pos == RecyclerView.NO_POSITION) return;
                    Category cat = adapter.getItem(pos);
                    adapter.getListener().onNameChanged(cat, s.toString(), pos);
                }
            });
            addNumWatcher(editIsi, "isi");
            addNumWatcher(editKardus, "kardus");
            addNumWatcher(editAda, "ada");
        }

        private void addNumWatcher(final EditText v, final String field) {
            v.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
                @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
                @Override public void afterTextChanged(Editable s) {
                    if (adapter == null || adapter.isBinding()) return;
                    int pos = getBindingAdapterPosition();
                    if (pos == RecyclerView.NO_POSITION) return;
                    Category cat = adapter.getItem(pos);
                    int value = 0;
                    try {
                        value = s.toString().isEmpty() ? 0 : Integer.parseInt(s.toString());
                    } catch (NumberFormatException ignored) {}
                    adapter.getListener().onFieldChanged(cat, field, value, pos);
                }
            });
        }
    }
}