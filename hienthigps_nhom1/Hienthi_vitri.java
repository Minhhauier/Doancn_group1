package com.example.hienthigps_nhom1;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.MapMaker;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Hienthi_vitri extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap myMap;
    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    Button back;
    private DatabaseReference xedap;
    TextView kinhtuyen,vituyen,vantoc,sovetinh;
    Marker xemarker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hienthi_vitri);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        back = (Button) findViewById(R.id.btback);
        kinhtuyen = (TextView) findViewById(R.id.txtktuyen);
        vituyen = (TextView) findViewById(R.id.txtvtuyen);
        vantoc = (TextView) findViewById(R.id.txtvtoc);
        sovetinh = (TextView) findViewById(R.id.txtsvtinh);
        getlastLocation();
        // trở tới đối tượng xe trong firebase
        xedap= FirebaseDatabase.getInstance().getReference("xe");
        // theo dõi vị trí xe thay đổi
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.bicycle);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
        xedap.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double lat = snapshot.child("lat").getValue(Double.class);
                Double lng = snapshot.child("lng").getValue(Double.class);
                Double speed = snapshot.child("speed").getValue(Double.class);
                Integer vetinh = snapshot.child("vetinh").getValue(Integer.class);
                if (lng != null && lat != null && myMap != null) {
                    LatLng position = new LatLng(lat, lng);
                    // myMap.clear();
                    if (xemarker == null) {
                        xemarker = myMap.addMarker(new MarkerOptions().position(position).title("xe đạp").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17f));
                    } else {
                        xemarker.setPosition(position);
                    }
                }
                if (speed != null) vantoc.setText("Vận tốc: " + speed);
                if (vetinh != null) sovetinh.setText("Vệ tinh: " + vetinh);
                if (lng != null) kinhtuyen.setText("Kinh tuyến: " + lng);
                if (lat != null) vituyen.setText("Vĩ tuyến: " + lat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Listener was cancelled: " + error.getMessage());
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Hienthi_vitri.this,MainActivity.class);
                startActivity(i);
            }
        });
//        xedap.child("lat").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Double lat = snapshot.getValue(Double.class);
//                if(lat != null)
//                {
//                        xedap.child("lng").get().addOnSuccessListener(dataSnapshot -> {
//                        Double lng = dataSnapshot.getValue(Double.class);
//                        if(lng !=null && myMap!=null)
//                        {
//                            LatLng position = new LatLng(lat,lng);
//                           // myMap.clear();
//                            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.bicycle);
//                            Bitmap b = bitmapdraw.getBitmap();
//                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
//                            if(xemarker==null) {
//                                xemarker=myMap.addMarker(new MarkerOptions().position(position).title("xe đạp").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
//                                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17f));
//                            }
//                            else
//                            {
//                                xemarker.setPosition(position);
//                            }
//                        }
//                        kinhtuyen.setText("Kinh tuyến: "+lng);
//                        vituyen.setText("Vĩ tuyến: "+lat);
//                    });
//                    xedap.child("speed").get().addOnSuccessListener(dataSnapshot -> {
//                       Double speed = dataSnapshot.getValue(Double.class);
//                       vantoc.setText("Vận tốc: "+speed);
//                    });
//                    xedap.child("vetinh").get().addOnSuccessListener(dataSnapshot -> {
//                        Integer vt = dataSnapshot.getValue(Integer.class);
//                        sovetinh.setText("Vệ tinh: "+vt);
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
    }
    private void getlastLocation()
    {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},FINE_PERMISSION_CODE);

        }
        Task<Location>  task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null)
                {
                    currentLocation = location;
                    SupportMapFragment mapFragment =(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    if(mapFragment!=null) {
                        mapFragment.getMapAsync(Hienthi_vitri.this);
                    }
                }
            }
        });
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.user);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
        LatLng hanoi= new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        myMap.addMarker(new MarkerOptions().position(hanoi).title("Tôi").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        myMap.moveCamera(CameraUpdateFactory.newLatLng(hanoi));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);
        if(requestCode == FINE_PERMISSION_CODE)
        {
            if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getlastLocation();
            }
            else
            {
                Toast.makeText(this, "Vui lòng cho phép truy cập vị trí !!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}