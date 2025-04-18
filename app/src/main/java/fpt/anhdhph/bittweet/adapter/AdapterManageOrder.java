package fpt.anhdhph.bittweet.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.model.Order;

public class AdapterManageOrder extends RecyclerView.Adapter<AdapterManageOrder.OrderViewHolder> {

    private Context context;
    private List<Order> orders;
    private OnOrderLongClickListener longClickListener;
    private OnOrderClickListener clickListener;

    public AdapterManageOrder(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders != null ? orders : new ArrayList<>();
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders != null ? orders : new ArrayList<>();
        notifyDataSetChanged();
    }

    public interface OnOrderLongClickListener {
        void onOrderLongClicked(Order order, int position);
    }

    public interface OnOrderClickListener {
        void onOrderClicked(Order order, int position);
    }

    public void setOnOrderLongClickListener(OnOrderLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_manage_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvCustomerName.setText("Khách hàng: " + (order.getCustomerName() != null ? order.getCustomerName() : "N/A"));
        holder.tvPhoneNumber.setText("Số điện thoại: " + (order.getPhoneNumber() != null ? order.getPhoneNumber() : "N/A"));
        holder.tvAddress.setText("Địa chỉ: " + (order.getAddress() != null ? order.getAddress() : "N/A"));
        holder.tvOrderDate.setText("Ngày đặt: " + (order.getOrderDate() != null ? order.getOrderDate() : "N/A"));
        holder.tvTotalPrice.setText("Tổng tiền: " + (order.getTotalPrice() != null ? order.getTotalPrice() + " VNĐ" : "0 VNĐ"));
        holder.tvStatus.setText("Trạng thái: " + (order.getStatus() != null ? order.getStatus() : "Chờ xác nhận"));

        // Đặt màu sắc cho trạng thái
        String status = order.getStatus() != null ? order.getStatus() : "Chờ xác nhận";
        switch (status) {
            case "Chờ xác nhận":
                holder.tvStatus.setTextColor(Color.parseColor("#FFA500"));
                break;
            case "Đang pha chế":
                holder.tvStatus.setTextColor(Color.parseColor("#1E90FF"));
                break;
            case "Hoàn tất":
                holder.tvStatus.setTextColor(Color.parseColor("#008000"));
                break;
            case "Đã nhận":
                holder.tvStatus.setTextColor(Color.parseColor("#008000"));
                break;
            case "Đã hủy":
                holder.tvStatus.setTextColor(Color.parseColor("#FF0000"));
                break;
            default:
                holder.tvStatus.setTextColor(Color.parseColor("#000000"));
                break;
        }

        // Hiển thị danh sách sản phẩm trong đơn hàng
        AdapterOrderItem adapterOrderItem = new AdapterOrderItem(context, order.getItems());
        holder.rvOrderItems.setLayoutManager(new LinearLayoutManager(context));
        holder.rvOrderItems.setAdapter(adapterOrderItem);

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onOrderLongClicked(order, position);
            }
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onOrderClicked(order, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvPhoneNumber, tvAddress, tvOrderDate, tvTotalPrice, tvStatus;
        RecyclerView rvOrderItems;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvPhoneNumber = itemView.findViewById(R.id.tv_phone_number);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvStatus = itemView.findViewById(R.id.tv_status);
            rvOrderItems = itemView.findViewById(R.id.rv_order_items);
        }
    }
}