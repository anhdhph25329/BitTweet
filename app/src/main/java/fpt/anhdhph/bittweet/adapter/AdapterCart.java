package fpt.anhdhph.bittweet.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import fpt.anhdhph.bittweet.DAO.CartDAO;
import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.model.CartItem;

public class AdapterCart extends RecyclerView.Adapter<AdapterCart.CartViewHolder> {

    private final List<CartItem> cartItems;
    private final CartDAO cartDAO;
    private final Context context;
    private final FirebaseFirestore db;

    public interface OnQuantityChangedListener {
        void onQuantityChanged();
    }

    private OnQuantityChangedListener listener;

    public void setOnQuantityChangedListener(OnQuantityChangedListener listener) {
        this.listener = listener;
    }

    public AdapterCart(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        this.cartDAO = new CartDAO(context);
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.tvTitle.setText(item.getName() != null ? item.getName() : "Không có tên");
        holder.tvPrice.setText(item.getPrice() != null ? item.getPrice() + " VNĐ" : "0 VNĐ");
        holder.tvQuantity.setText(item.getQuantity() != null ? item.getQuantity() : "1");
        holder.btnSize.setText(item.getSize() != null ? item.getSize() : "N/A");

        if (item.getImage() != null && !item.getImage().isEmpty()) {
            Picasso.get().load(item.getImage()).into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.sample_coffee);
        }

        // Xử lý tăng số lượng
        holder.btnIncrease.setOnClickListener(v -> {
            try {
                int currentQuantity = item.getQuantity() != null ? Integer.parseInt(item.getQuantity()) : 1;
                final int newQuantity = currentQuantity + 1;
                item.setQuantity(String.valueOf(newQuantity));
                cartDAO.updateQuantity(item.getId(), newQuantity,
                        () -> {
                            notifyItemChanged(position);
                            if (listener != null) listener.onQuantityChanged();
                        },
                        () -> {
                            item.setQuantity(String.valueOf(currentQuantity));
                            notifyItemChanged(position);
                            Toast.makeText(context, "Lỗi khi cập nhật số lượng", Toast.LENGTH_SHORT).show();
                        });
            } catch (NumberFormatException e) {
                Log.e("AdapterCart", "Lỗi parse quantity: " + e.getMessage());
                Toast.makeText(context, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý giảm số lượng
        holder.btnDecrease.setOnClickListener(v -> {
            try {
                int currentQuantity = item.getQuantity() != null ? Integer.parseInt(item.getQuantity()) : 1;
                if (currentQuantity > 1) {
                    final int newQuantity = currentQuantity - 1;
                    item.setQuantity(String.valueOf(newQuantity));
                    cartDAO.updateQuantity(item.getId(), newQuantity,
                            () -> {
                                notifyItemChanged(position);
                                if (listener != null) listener.onQuantityChanged();
                            },
                            () -> {
                                item.setQuantity(String.valueOf(currentQuantity));
                                notifyItemChanged(position);
                                Toast.makeText(context, "Lỗi khi cập nhật số lượng", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                    String userId = prefs.getString("user_id", null);
                    if (userId != null) {
                        db.collection("Users")
                                .document(userId)
                                .collection("cart")
                                .document(item.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    cartItems.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, cartItems.size());
                                    if (listener != null) listener.onQuantityChanged();
                                    Toast.makeText(context, "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Lỗi khi xóa sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                }
            } catch (NumberFormatException e) {
                Log.e("AdapterCart", "Lỗi parse quantity: " + e.getMessage());
                Toast.makeText(context, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý chọn kích thước
        holder.btnSize.setOnClickListener(v -> showSizeSelectionDialog(item, position));
    }

    private void showSizeSelectionDialog(CartItem item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Chọn kích thước");

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_select_size, null);
        CheckBox cbSizeS = dialogView.findViewById(R.id.cb_size_s);
        CheckBox cbSizeM = dialogView.findViewById(R.id.cb_size_m);
        CheckBox cbSizeL = dialogView.findViewById(R.id.cb_size_l);

        String currentSize = item.getSize();
        if (currentSize != null) {
            cbSizeS.setChecked(currentSize.contains("S"));
            cbSizeM.setChecked(currentSize.contains("M"));
            cbSizeL.setChecked(currentSize.contains("L"));
        }

        builder.setView(dialogView);

        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            List<String> selectedSizes = new ArrayList<>();
            if (cbSizeS.isChecked()) selectedSizes.add("S");
            if (cbSizeM.isChecked()) selectedSizes.add("M");
            if (cbSizeL.isChecked()) selectedSizes.add("L");

            if (selectedSizes.isEmpty()) {
                Toast.makeText(context, "Vui lòng chọn ít nhất một kích thước", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            String userId = prefs.getString("user_id", null);
            if (userId == null) {
                Toast.makeText(context, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                return;
            }

            // Xóa mục cũ nếu không còn kích thước nào được chọn
            boolean shouldDeleteOldItem = true;
            for (String size : selectedSizes) {
                if (size.equals(currentSize)) {
                    shouldDeleteOldItem = false;
                    break;
                }
            }

            if (shouldDeleteOldItem) {
                db.collection("Users")
                        .document(userId)
                        .collection("cart")
                        .document(item.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            cartItems.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, cartItems.size());
                        });
            }

            // Tạo mục mới cho từng kích thước được chọn
            for (String size : selectedSizes) {
                if (size.equals(currentSize) && !shouldDeleteOldItem) {
                    continue;
                }

                String cartItemId = item.getIdProducts() + "_" + size;
                db.collection("Users")
                        .document(userId)
                        .collection("cart")
                        .document(cartItemId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Cập nhật số lượng nếu mục đã tồn tại
                                int currentQuantity = Integer.parseInt(documentSnapshot.getString("quantity"));
                                db.collection("Users")
                                        .document(userId)
                                        .collection("cart")
                                        .document(cartItemId)
                                        .update("quantity", String.valueOf(currentQuantity + 1))
                                        .addOnSuccessListener(aVoid -> {
                                            if (listener != null) listener.onQuantityChanged();
                                        });
                            } else {
                                // Tạo mục mới nếu chưa tồn tại
                                CartItem newItem = new CartItem(
                                        cartItemId,
                                        item.getIdProducts(),
                                        item.getName(),
                                        size,
                                        item.getPrice(),
                                        "1",
                                        item.getImage(),
                                        item.getCategory()
                                );
                                db.collection("Users")
                                        .document(userId)
                                        .collection("cart")
                                        .document(cartItemId)
                                        .set(newItem)
                                        .addOnSuccessListener(aVoid -> {
                                            // Thêm mục mới vào danh sách cartItems
                                            cartItems.add(newItem);
                                            notifyItemInserted(cartItems.size() - 1);
                                            if (listener != null) listener.onQuantityChanged();
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Lỗi khi cập nhật kích thước: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvTitle, tvNote, tvPrice, tvQuantity, btnSize, btnIncrease, btnDecrease;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.image_product);
            tvTitle = itemView.findViewById(R.id.text_title);
            tvNote = itemView.findViewById(R.id.text_note);
            tvPrice = itemView.findViewById(R.id.text_price);
            tvQuantity = itemView.findViewById(R.id.text_quantity);
            btnSize = itemView.findViewById(R.id.btn_size);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
        }
    }
}