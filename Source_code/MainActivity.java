package com.example.hienthigps_nhom1;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    Button kiemtra;
    Switch coi,khoa;
    TextView trangthai;
    private DatabaseReference xedap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        kiemtra = (Button) findViewById(R.id.btktra);
        coi = (Switch) findViewById(R.id.swcoi);
        khoa = (Switch) findViewById(R.id.swkhoa);
        trangthai = (TextView) findViewById(R.id.txttthai);
        xedap= FirebaseDatabase.getInstance().getReference("xe");
        kiemtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,Hienthi_vitri.class);
                startActivity(i);
            }
        });
        xedap.child("khoa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean key = snapshot.getValue(Boolean.class);
                if(key==true) khoa.setChecked(true);
                else khoa.setChecked(false);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        xedap.child("coi").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean c = snapshot.getValue(Boolean.class);
                if(c==true) coi.setChecked(true);
                else coi.setChecked(false);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        xedap.child("speed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double vtoc = snapshot.getValue(Double.class);
                if (vtoc != null) {
                    if (vtoc > 3) trangthai.setText("Trạng thái: Di chuyển");
                    else trangthai.setText("Trạng thái: Đứng yên");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        khoa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                xedap.child("khoa").setValue(isChecked);
            }
        });

        coi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                xedap.child("coi").setValue(isChecked);
            }
        });

    }
}