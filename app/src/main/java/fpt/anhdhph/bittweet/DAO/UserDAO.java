package fpt.anhdhph.bittweet.DAO;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import fpt.anhdhph.bittweet.model.User;

public class UserDAO {

    FirebaseFirestore db;
    CollectionReference userRef;

    public UserDAO() {
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("Users");
    }

    // Callback khi lấy danh sách user
    public interface UserCallback {
        void onSuccess(List<User> userList);
        void onError(Exception e);
    }

    // Functional Interface để dùng với lambda
    @FunctionalInterface
    public interface SuccessCallback {
        void onSuccess();
    }

    // Khi muốn xử lý lỗi, dùng thêm callback này
    public interface ErrorCallback {
        void onError(@NonNull Exception e);
    }

    // Lấy danh sách người dùng
    public void getAllUsers(UserCallback callback) {
        userRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> list = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        User u = doc.toObject(User.class);
                        if (u != null) {
                            u.setId(doc.getId());
                            list.add(u);
                        }
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onError);
    }



    // Cập nhật người dùng
    public void updateUser(String id, User user, SuccessCallback successCallback, ErrorCallback errorCallback) {
        userRef.document(id).set(user)
                .addOnSuccessListener(unused -> successCallback.onSuccess())
                .addOnFailureListener(errorCallback::onError);
    }

    // Xoá người dùng
    public void deleteUser(String id, SuccessCallback successCallback, ErrorCallback errorCallback) {
        userRef.document(id).delete()
                .addOnSuccessListener(unused -> successCallback.onSuccess())
                .addOnFailureListener(errorCallback::onError);
    }
}
