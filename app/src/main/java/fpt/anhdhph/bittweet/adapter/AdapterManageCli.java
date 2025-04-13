package fpt.anhdhph.bittweet.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fpt.anhdhph.bittweet.DAO.UserDAO;
import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.model.User;

public class AdapterManageCli extends RecyclerView.Adapter<AdapterManageCli.UserViewHolder> {

    private Context context;
    private List<User> userList;
    private UserDAO userDAO;
    private final ReloadCallback reloadCallback;

    public interface ReloadCallback {
        void reload(); // Callback cho việc load lại dữ liệu
    }
    public AdapterManageCli(Context context, List<User> userList, ReloadCallback reloadCallback) {
        this.context = context;
        this.userList = userList;
        this.reloadCallback = reloadCallback;
        this.userDAO = new UserDAO();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_manage_client, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        if (user == null) return;

        holder.tvTen.setText("Tên: " + user.getName());
        holder.tvDiaChi.setText("Địa chỉ: " + user.getAddress());
        holder.tvNgaySinh.setText("Ngày sinh: " + user.getBirthdate());
        holder.tvGioiTinh.setText("Giới tính: " + user.getGender());
        holder.tvEmail.setText("Email: " + user.getEmail());
        holder.tvSdt.setText("SDT: " + user.getPhone());

        holder.btnEdit.setOnClickListener(v -> showEditDialog(user, position));
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận xoá")
                    .setMessage("Bạn có chắc muốn xoá người dùng này?")
                    .setPositiveButton("Xoá", (dialog, which) -> {
                        userDAO.deleteUser(user.getId(),
                                () -> {
                                    Toast.makeText(context, "Đã xoá người dùng", Toast.LENGTH_SHORT).show();
                                    userList.remove(position);
                                    notifyItemRemoved(position);
                                },
                                e -> Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
            return true;
        });
    }

    private void showEditDialog(User user, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_dialog_add_user, null);
        EditText edtName = dialogView.findViewById(R.id.edtName);
        EditText edtAddress = dialogView.findViewById(R.id.edtAddress);
        EditText edtBirthdate = dialogView.findViewById(R.id.edtBirthdate);
        EditText edtGender = dialogView.findViewById(R.id.edtGender);
        EditText edtEmail = dialogView.findViewById(R.id.edtEmail);
        EditText edtPhone = dialogView.findViewById(R.id.edtPhone);

        // Gán dữ liệu hiện tại
        edtName.setText(user.getName());
        edtAddress.setText(user.getAddress());
        edtBirthdate.setText(user.getBirthdate());
        edtGender.setText(user.getGender());
        edtEmail.setText(user.getEmail());
        edtPhone.setText(user.getPhone());

        new AlertDialog.Builder(context)
                .setTitle("Cập nhật người dùng")
                .setView(dialogView)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    // Cập nhật dữ liệu
                    user.setName(edtName.getText().toString());
                    user.setAddress(edtAddress.getText().toString());
                    user.setBirthdate(edtBirthdate.getText().toString());
                    user.setGender(edtGender.getText().toString());
                    user.setEmail(edtEmail.getText().toString());
                    user.setPhone(edtPhone.getText().toString());

                    userDAO.updateUser(user.getId(), user,
                            () -> {
                                Toast.makeText(context, "Đã cập nhật người dùng", Toast.LENGTH_SHORT).show();
                                notifyItemChanged(position);
                            },
                            e -> Toast.makeText(context, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView tvTen, tvDiaChi, tvNgaySinh, tvGioiTinh, tvEmail, tvSdt;
        ImageView btnEdit;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTen = itemView.findViewById(R.id.tvTen);
            tvDiaChi = itemView.findViewById(R.id.tvDiaChi);
            tvNgaySinh = itemView.findViewById(R.id.tvNgaySinh);
            tvGioiTinh = itemView.findViewById(R.id.tvGioiTinh);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvSdt = itemView.findViewById(R.id.tvSdt);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
    public void updateList(List<User> newList) {
        this.userList = newList;
        notifyDataSetChanged(); // Cập nhật lại RecyclerView
    }

}
