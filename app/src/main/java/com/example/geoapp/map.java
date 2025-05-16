package com.example.geoapp;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.database.*;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.List;

public class map extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private double currentLat = 0.0;
    private double currentLng = 0.0;
    private String key;
    String uid;
    private boolean firstLocationUpdate = true;
    public map() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        createLocationRequest();
        createLocationCallback();

        uid = global.getInstance().getNombre();

        if (getArguments() != null) {
            key = getArguments().getString("key");
            Log.d("MapFragment", "Key recibida: " + key);
        } else {
            Log.e("MapFragment", "No se recibió argumento 'key'");
        }
        return view;
    }
    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {

                for (Location location : locationResult.getLocations()) {
                    currentLat = location.getLatitude();
                    currentLng = location.getLongitude();
                    Log.d("LocationUpdate", "Lat: " + currentLat + " Lng: " + currentLng);

                    uploadLocationToFirebase(currentLat, currentLng);

                    if (mMap != null && firstLocationUpdate) {
                        LatLng currentLatLng = new LatLng(currentLat, currentLng);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                        firstLocationUpdate = false;
                    }
                }
            }
        };
    }
    private void uploadLocationToFirebase(double lat, double lng) {
        if (uid == null) {
            Log.e("UploadLocation", "uid es null. No se sube ubicación.");
            return;
        }
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid);
        HashMap<String, Object> data = new HashMap<>();
        data.put("lat", lat);
        data.put("lng", lng);
        dbRef.setValue(data)
                .addOnSuccessListener(aVoid -> Log.d("RealtimeDB", "Ubicación subida correctamente"))
                .addOnFailureListener(e -> Log.e("RealtimeDB", "Error al subir ubicación", e));
    }
    private void loadOtherUsersLocations() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mMap == null) return;
                mMap.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String username = userSnapshot.getKey();
                    if (username == null || username.equals(key)) continue;
                    Double lat = userSnapshot.child("lat").getValue(Double.class);
                    Double lng = userSnapshot.child("lng").getValue(Double.class);
                    if (lat != null && lng != null) {
                        LatLng userLatLng = new LatLng(lat, lng);
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(userLatLng)
                                .title(username));
                        if (marker != null) {
                            marker.setTag(username);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseData", "Error al leer datos: " + error.getMessage());
            }
        });
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mMap.setMyLocationEnabled(true);
        startLocationUpdates();
        loadOtherUsersLocations();
        mMap.setOnMarkerClickListener(marker -> {
            String clickedUsername = (String) marker.getTag();
            if (clickedUsername != null) {
                buscarOCrearChat(clickedUsername);
            }
            return false;
        });
    }
    private void buscarOCrearChat(String clickedUsername) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("username", clickedUsername)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String uidOtro = document.getId();
                        String miUid = global.getInstance().getUid();
                        db.collection("chatrooms")
                                .whereArrayContains("users", miUid)
                                .get()
                                .addOnSuccessListener(chatroomsSnapshot -> {
                                    for (DocumentSnapshot chatroom : chatroomsSnapshot) {
                                        List<String> users = (List<String>) chatroom.get("users");
                                        if (users != null && users.contains(uidOtro)) {
                                            abrirChat(chatroom.getId(), clickedUsername);
                                            return;
                                        }
                                    }
                                    HashMap<String, Object> nuevoChatroom = new HashMap<>();
                                    nuevoChatroom.put("users", java.util.Arrays.asList(miUid, uidOtro));
                                    nuevoChatroom.put("timestamp", com.google.firebase.Timestamp.now());
                                    db.collection("chatrooms")
                                            .add(nuevoChatroom)
                                            .addOnSuccessListener(newChatroomRef -> {
                                                abrirChat(newChatroomRef.getId(), clickedUsername);
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("Firestore", "Error al crear nuevo chatroom", e);
                                            });
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar chatrooms", e));
                    } else {
                        Log.w("Firestore", "No se encontró usuario con nombre: " + clickedUsername);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error consultando Firestore", e));
    }
    private void abrirChat(String chatroomId, String nombreUsuario) {
        Intent intent = new Intent(requireContext(), ChatRoom.class);
        intent.putExtra("documentId", chatroomId);
        intent.putExtra("nombre", nombreUsuario);
        startActivity(intent);
    }
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}