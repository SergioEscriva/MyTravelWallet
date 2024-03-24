package me.spenades.mytravelwallet.ayuda;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import me.spenades.mytravelwallet.R;
import me.spenades.mytravelwallet.activities.ListarWalletsActivity;

public class SaldarDeudasAyuda extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ayuda_saldar_deudas);

        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        long usuarioIdActivo = extras.getInt("usuarioIdActivo");
        String usuarioActivo = extras.getString("usuarioActivo");
        // Se muestra sólo la primera vez la Ayuda
        int info = extras.getInt("info");

        // Si no hay datos (cosa rara) salimos
        if (extras == null) {
            finish();
            return;
        }

        ImageView ivSaldarDeudas = findViewById(R.id.ivSaldarDeudas);

        ivSaldarDeudas.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SaldarDeudasAyuda.this, ListarWalletsActivity.class);
                intent.putExtra("usuarioActivo", usuarioActivo);
                intent.putExtra("usuarioIdActivo", usuarioIdActivo);
                intent.putExtra("info", info);
                startActivity(intent);
            }


        });


    }

}
