package fpt.anhdhph.bittweet.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fpt.anhdhph.bittweet.R;
import fpt.anhdhph.bittweet.model.Order;

public class AdapterOrderHistory extends RecyclerView.Adapter<AdapterOrderHistory.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;
    private OnCancelOrderClickListener cancelOrderClickListener;
    private OnConfirmReceivedClickListener confirmReceivedClickListener;

    public interface OnCancelOrderClickListener {
        void onCancelOrderClick(Order order, int position);
    }

    public interface OnConfirmReceivedClickListener {
        void onConfirmReceivedClick(Order order, int position);
    }

    public AdapterOrderHistory(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    public void setOnCancelOrderClickListener(OnCancelOrderClickListener listener) {
        this.cancelOrderClickListener = listener;
    }

    public void setOnConfirmReceivedClickListener(OnConfirmReceivedClickListener listener) {
        this.confirmReceivedClickListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        if (order == null) return;

        holder.tvOrderId.setText("Mã đơn hàng: " + order.getId());
        holder.tvOrderDate.setText("Ngày đặt: " + order.getOrderDate());
        holder.tvTotalPrice.setText("Tổng tiền: " + order.getTotalPrice() + " VND");
        holder.tvStatus.setText("Trạng thái: " + (order.getStatus() != null ? order.getStatus() : "Chờ xác nhận"));
        holder.tvCustomerName.setText("Khách hàng: " + order.getCustomerName());
        holder.tvPhoneNumber.setText("SĐT: " + order.getPhoneNumber());
        holder.tvAddress.setText("Địa chỉ: " + order.getAddress());

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

        // Hiển thị/ẩn nút hủy đơn hàng
        if ("Chờ xác nhận".equals(status)) {
            holder.btnCancelOrder.setVisibility(View.VISIBLE);
            holder.btnConfirmReceived.setVisibility(View.GONE);
            holder.btnCancelOrder.setOnClickListener(v -> {
                if (cancelOrderClickListener != null) {
                    cancelOrderClickListener.onCancelOrderClick(order, position);
                }
            });
        } else if ("Hoàn tất".equals(status)) {
            holder.btnCancelOrder.setVisibility(View.GONE);
            holder.btnConfirmReceived.setVisibility(View.VISIBLE);
            holder.btnConfirmReceived.setOnClickListener(v -> {
                if (confirmReceivedClickListener != null) {
                    confirmReceivedClickListener.onConfirmReceivedClick(order, position);
                }
            });
        } else {
            holder.btnCancelOrder.setVisibility(View.GONE);
            holder.btnConfirmReceived.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvTotalPrice, tvStatus, tvCustomerName, tvPhoneNumber, tvAddress;
        RecyclerView rvOrderItems;
        Button btnCancelOrder, btnConfirmReceived;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvPhoneNumber = itemView.findViewById(R.id.tv_phone_number);
            tvAddress = itemView.findViewById(R.id.tv_address);
            rvOrderItems = itemView.findViewById(R.id.rv_order_items);
            btnCancelOrder = itemView.findViewById(R.id.btn_cancel_order);
            btnConfirmReceived = itemView.findViewById(R.id.btn_confirm_received);
        }
    }
}