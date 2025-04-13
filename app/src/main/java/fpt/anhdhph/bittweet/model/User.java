package fpt.anhdhph.bittweet.model;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String id;
    private String name;
    private String address;
    private String birthdate;
    private String email;
    private String gender;
    private String phone;

    public User() {}

    public User(String name, String address, String birthdate, String email, String gender, String phone) {
        this.name = name;
        this.address = address;
        this.birthdate = birthdate;
        this.email = email;
        this.gender = gender;
        this.phone = phone;
    }

    public static User fromDocument(DocumentSnapshot doc) {
        User user = doc.toObject(User.class);
        if (user != null) user.setId(doc.getId());
        return user;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("address", address);
        map.put("birthdate", birthdate);
        map.put("email", email);
        map.put("gender", gender);
        map.put("phone", phone);
        return map;
    }

    // Getter & Setter...
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getBirthdate() { return birthdate; }
    public String getEmail() { return email; }
    public String getGender() { return gender; }
    public String getPhone() { return phone; }

    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }
    public void setEmail(String email) { this.email = email; }
    public void setGender(String gender) { this.gender = gender; }
    public void setPhone(String phone) { this.phone = phone; }
}
