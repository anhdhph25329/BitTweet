package fpt.anhdhph.bittweet.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import fpt.anhdhph.bittweet.DAO.ProductDAO;
import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.model.Product;
import fpt.anhdhph.bittweet.screen.ScreenManagePro;

public class AdapterManagePro extends RecyclerView.Adapter<AdapterManagePro.ManageProViewHolder> {

    private List<Product> productList;
    private final Context context;
    private final ProductDAO productDAO;
    private final String docName;
    private final Runnable refreshCallback;
    FirebaseFirestore db;

    // Constructor nhận vào 4 tham số: context, danh sách sản phẩm, docName, refreshCallback
    public AdapterManagePro(Context context, List<Product> productList, String docName, Runnable refreshCallback) {
        this.context = context;
        this.productList = productList;
        this.docName = docName;
        this.refreshCallback = refreshCallback;
        this.productDAO = new ProductDAO();
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ManageProViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_manage_pro, parent, false);
        return new ManageProViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageProViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvProName.setText(product.getProName());
        holder.tvDes.setText("Mô tả: " + product.getDes());
        holder.tvSPrice.setText("Size S: " + product.getSPrice() + "đ");
        holder.tvMPrice.setText("Size M: " + product.getMPrice() + "đ");
        holder.tvLPrice.setText("Size L: " + product.getLPrice() + "đ");
        holder.tvCategory.setText(product.getCategory());

        // Sử dụng Picasso để tải ảnh từ URL và hiển thị
        Picasso.get()
                .load(product.getImage()) // URL ảnh từ Firebase
                .placeholder(R.drawable.ic_launcher_background) // Placeholder trong khi ảnh chưa tải xong
                .into(holder.ivProductImage); // Gán vào ImageView

        // Nhấn giữ để xoá sản phẩm
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xoá sản phẩm")
                    .setMessage("Bạn có chắc muốn xoá sản phẩm này không?")
                    .setPositiveButton("Xoá", (dialog, which) -> {
                        // Xóa sản phẩm khỏi Firebase và gọi lại callback để refresh dữ liệu
                        productDAO.deleteProduct(docName, product.getId(), new ProductDAO.OnProductLoadListener() {
                            @Override
                            public void onSuccess(List<Product> productList) {
                                Toast.makeText(context, "Đã xoá sản phẩm", Toast.LENGTH_SHORT).show();
                                refreshCallback.run(); // Gọi lại để load lại danh sách
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
            return true;
        });
        holder.btnEdit.setOnClickListener(v -> {
            // Hiển thị dialog cập nhật sản phẩm
            showUpdateDialog(product);
        });
    }

    private void showUpdateDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_add_pro, null);
        builder.setView(view);

        TextInputEditText edtProName = view.findViewById(R.id.edtProName);
        TextInputEditText edtDes = view.findViewById(R.id.edtDes);
        TextInputEditText edtSPrice = view.findViewById(R.id.edtSPrice);
        TextInputEditText edtMPrice = view.findViewById(R.id.edtMPrice);
        TextInputEditText edtLPrice = view.findViewById(R.id.edtLPrice);
        Spinner spCategory = view.findViewById(R.id.spCategory);
        Button btnAdd = view.findViewById(R.id.btnAddPro);
        btnAdd.setText("Cập nhật");

        edtProName.setText(product.getProName());
        edtDes.setText(product.getDes());
        edtSPrice.setText(product.getSPrice());
        edtMPrice.setText(product.getMPrice());
        edtLPrice.setText(product.getLPrice());

        List<String> cateList = new ArrayList<>();
        ArrayAdapter<String> spAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, cateList);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(spAdapter);

        db.collection("Categories").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (var doc : queryDocumentSnapshots) {
                        String categoryName = doc.getString("name");
                        if (categoryName != null) cateList.add(categoryName);
                    }
                    spAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Lỗi khi tải danh mục!", Toast.LENGTH_SHORT).show());

        // Load category (nếu cần)
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, List.of(product.getCategory()));
//        spCategory.setAdapter(adapter);

        AlertDialog dialog = builder.create();

        btnAdd.setOnClickListener(v1 -> {
            String proName = edtProName.getText().toString();
            String des = edtDes.getText().toString();
            String sPrice = edtSPrice.getText().toString();
            String mPrice = edtMPrice.getText().toString();
            String lPrice = edtLPrice.getText().toString();
            String category = spCategory.getSelectedItem().toString();

            Product updatedProduct = new Product(proName, des, sPrice, mPrice, lPrice, category);
            updatedProduct.setId(product.getId());

            productDAO.updateProduct(docName, updatedProduct, new ProductDAO.OnProductLoadListener() {
                @Override
                public void onSuccess(List<Product> list) {
                    Toast.makeText(context, "Đã cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
                    refreshCallback.run();
                    dialog.dismiss();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ManageProViewHolder extends RecyclerView.ViewHolder {
        TextView tvProName, tvDes, tvSPrice, tvMPrice, tvLPrice, tvCategory;
        ImageView ivProductImage,btnEdit;  // ImageView để hiển thị ảnh sản phẩm

        public ManageProViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProName = itemView.findViewById(R.id.tvProName);
            tvDes = itemView.findViewById(R.id.tvDes);
            tvSPrice = itemView.findViewById(R.id.tvSPrice);
            tvMPrice = itemView.findViewById(R.id.tvMPrice);
            tvLPrice = itemView.findViewById(R.id.tvLPrice);
            tvCategory = itemView.findViewById(R.id.tvCategory); // THÊM view để hiển thị category
            ivProductImage = itemView.findViewById(R.id.imageViewPro); // Liên kết với ImageView
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
