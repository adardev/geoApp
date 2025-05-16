package com.example.geoapp;
import static android.widget.Toast.LENGTH_LONG;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.geoapp.adapters.MensajeAdapter;
import com.example.geoapp.model.Mensaje;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRoom extends AppCompatActivity {
    EditText barra;
    ImageButton salir;
    ImageButton enviar;
    TextView nombreuser;
    String chatroomId;
    String nombre;
    RecyclerView recyclerView;
    MensajeAdapter mensajeAdapter;
    List<Mensaje> mensajeList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_room);
        barra = findViewById(R.id.input_text);
        salir = findViewById(R.id.back_btn);
        enviar = findViewById(R.id.boton_enviar_texto);
        nombreuser = findViewById(R.id.userNameName);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mensajeAdapter = new MensajeAdapter(mensajeList);
        recyclerView.setAdapter(mensajeAdapter);
        chatroomId = getIntent().getStringExtra("documentId");
        nombre = getIntent().getStringExtra("nombre");
        if (chatroomId == null) {
            Toast.makeText(this, "Chatroom ID no recibido", LENGTH_LONG).show();
            finish();
            return;
        }
        nombreuser.setText(nombre);
        salir.setOnClickListener(v -> {
            Intent intent = new Intent(ChatRoom.this, StartActivity.class);
            intent.putExtra("estado", true);
            startActivity(intent);
        });
        enviar.setOnClickListener(v -> enviarMensaje());
        FirebaseFirestore.getInstance()
                .collection("chatrooms")
                .document(chatroomId)
                .collection("messages")
                .orderBy("timestamp"
                        , Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    List<Mensaje> nuevos = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        Mensaje m = doc.toObject(Mensaje.class);
                        nuevos.add(m);
                    }
                    mensajeAdapter.actualizarMensajes(nuevos);
                    recyclerView.scrollToPosition(nuevos.size() - 1);
                });
    }
    private void enviarMensaje() {
        String mensajeTexto = barra.getText().toString().trim();
        if (mensajeTexto.isEmpty()) {
            Toast.makeText(this, "Escribe un mensaje", LENGTH_LONG).show();
            return;
        }
        String uid = global.getInstance().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> mensaje = new HashMap<>();
        mensaje.put("message", mensajeTexto);
        mensaje.put("uid", uid);
        mensaje.put("timestamp", Timestamp.now());

        db.collection("chatrooms")
                .document(chatroomId)
                .collection("messages")
                .add(mensaje)
                .addOnSuccessListener(documentReference -> {
                    barra.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al enviar mensaje", LENGTH_LONG).show();
                });
    }
}