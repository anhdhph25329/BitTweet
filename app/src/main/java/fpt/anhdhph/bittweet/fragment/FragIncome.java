package fpt.anhdhph.bittweet.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fpt.anhdhph.bittweet.R;

public class FragIncome extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho Fragment này
        return inflater.inflate(R.layout.layout_frag_income, container, false);
    }

    // Bạn có thể thêm các phương thức khác để xử lý các sự kiện hoặc tương tác với giao diện người dùng ở đây.
}
