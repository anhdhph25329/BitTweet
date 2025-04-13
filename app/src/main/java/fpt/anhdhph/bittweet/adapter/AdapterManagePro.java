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

public class AdapterManagePro extends RecyclerView.Adapter<AdapterManagePro.ManageProViewHolder> {

    private List<Product> productList;
    private final Context context;
    private final ProductDAO productDAO;
    private final Runnable refreshCallback;
    FirebaseFirestore db;

    public AdapterManagePro(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.refreshCallback = () -> {};
        this.productDAO = new ProductDAO();
        this.db = FirebaseFirestore.getInstance();
    }

    public AdapterManagePro(Context context, List<Product> productList, Runnable refreshCallback) {
        this.context = context;
        this.productList = productList;
        this.refreshCallback = refreshCallback;
        this.productDAO = new ProductDAO();
        this.db = FirebaseFirestore.getInstance();
    }

    public void setRefreshCallback(Runnable refreshCallback) {
        // Nếu cần thiết, bạn có thể thêm phương thức này để cập nhật callback sau khi adapter được tạo
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
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            Picasso.get()
                    .load(product.getImage())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(R.drawable.ic_launcher_background);
        }

        // Nhấn giữ để xóa sản phẩm
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xóa sản phẩm")
                    .setMessage("Bạn có chắc muốn xóa sản phẩm này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        productDAO.deleteProduct(product.getCategory(), product.getId(), new ProductDAO.OnProductLoadListener() {
                            @Override
                            public void onSuccess(List<Product> productList) {
                                Toast.makeText(context, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                                refreshCallback.run();
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
            return true;
        });

        holder.btnEdit.setOnClickListener(v -> showUpdateDialog(product));
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
        TextInputEditText edtImageUrl = view.findViewById(R.id.edtImageUrl);
        Spinner spCategory = view.findViewById(R.id.spCategory);
        Button btnAdd = view.findViewById(R.id.btnAddPro);
        btnAdd.setText("Cập nhật");

        edtProName.setText(product.getProName());
        edtDes.setText(product.getDes());
        edtSPrice.setText(product.getSPrice());
        edtMPrice.setText(product.getMPrice());
        edtLPrice.setText(product.getLPrice());
        edtImageUrl.setText(product.getImage());

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
                    int position = cateList.indexOf(product.getCategory());
                    if (position >= 0) {
                        spCategory.setSelection(position);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Lỗi khi tải danh mục!", Toast.LENGTH_SHORT).show());

        AlertDialog dialog = builder.create();

        btnAdd.setOnClickListener(v1 -> {
            String proName = edtProName.getText() != null ? edtProName.getText().toString().trim() : "";
            String des = edtDes.getText() != null ? edtDes.getText().toString().trim() : "";
            String sPrice = edtSPrice.getText() != null ? edtSPrice.getText().toString().trim() : "";
            String mPrice = edtMPrice.getText() != null ? edtMPrice.getText().toString().trim() : "";
            String lPrice = edtLPrice.getText() != null ? edtLPrice.getText().toString().trim() : "";
            String imageUrl = edtImageUrl.getText() != null ? edtImageUrl.getText().toString().trim() : "";
            String category = spCategory.getSelectedItem() != null ? spCategory.getSelectedItem().toString() : "";

            if (proName.isEmpty()) {
                edtProName.setError("Vui lòng nhập tên sản phẩm");
                return;
            }
            if (des.isEmpty()) {
                edtDes.setError("Vui lòng nhập mô tả");
                return;
            }
            if (category.isEmpty()) {
                Toast.makeText(context, "Vui lòng chọn danh mục!", Toast.LENGTH_SHORT).show();
                return;
            }

            Product updatedProduct = new Product(proName, des, sPrice, mPrice, lPrice, category, imageUrl);
            updatedProduct.setId(product.getId());

            productDAO.updateProduct(category, updatedProduct, new ProductDAO.OnProductLoadListener() {
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
        ImageView ivProductImage, btnEdit;

        public ManageProViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProName = itemView.findViewById(R.id.tvProName);
            tvDes = itemView.findViewById(R.id.tvDes);
            tvSPrice = itemView.findViewById(R.id.tvSPrice);
            tvMPrice = itemView.findViewById(R.id.tvMPrice);
            tvLPrice = itemView.findViewById(R.id.tvLPrice);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            ivProductImage = itemView.findViewById(R.id.imageViewPro);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}