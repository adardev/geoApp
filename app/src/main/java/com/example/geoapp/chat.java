package com.example.geoapp;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.geoapp.adapters.ChatRoomAdapterFirestore;
import com.example.geoapp.model.ChatRoomModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class chat extends Fragment {
    public chat() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerChatRooms);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUid = global.getInstance().getUid();
        Query query = db.collection("chatrooms")
                .whereArrayContains("users", currentUid)
                .orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatRoomModel> options = new FirestoreRecyclerOptions.Builder<ChatRoomModel>()
                .setQuery(query, ChatRoomModel.class)
                .setLifecycleOwner(this)
                .build();
        ChatRoomAdapterFirestore adapter = new ChatRoomAdapterFirestore(options, getContext());
        recyclerView.setAdapter(adapter);
        return view;
    }
}
