package com.example.geoapp.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
public class utilsFirebase {
    public static String userid() {
        return FirebaseAuth.getInstance().getUid();
    }
    public static boolean islogged() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }
    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(userid());
    }
    public static DocumentReference getChatroomReference(String chatroomid) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomid);
    }
    public static String getChatroomId(String user1, String user2) {
        if (user1.hashCode() < user2.hashCode()) {
            return user1 + "_" + user2;
        } else {
            return user2 + "_" + user1;
        }
    }
}