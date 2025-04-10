package fpt.anhdhph.bittweet.DAO;
import androidx.annotation.NonNull;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import fpt.anhdhph.bittweet.model.CartItem;

public class CartDAO {
    private FirebaseFirestore db;
    private CollectionReference cartRef;

    public CartDAO() {
        db = FirebaseFirestore.getInstance();
        cartRef = db.collection("Carts"); // Tham chiếu đến collection "cart"
    }

    public void getCartItems(final CartCallback callback) {
        cartRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null || snapshot == null) {
                    callback.onCallback(new ArrayList<>());
                    return;
                }

                List<CartItem> cartItems = new ArrayList<>();
                for (QueryDocumentSnapshot doc : snapshot) {
                    CartItem item = doc.toObject(CartItem.class);
                    cartItems.add(item);
                }
                callback.onCallback(cartItems);
            }
        });
    }

    public interface CartCallback {
        void onCallback(List<CartItem> cartItems);
    }
}
