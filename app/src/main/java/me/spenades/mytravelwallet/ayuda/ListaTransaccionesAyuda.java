package me.spenades.mytravelwallet.ayuda;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import me.spenades.mytravelwallet.R;

public class ListaTransaccionesAyuda extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ayuda_lista_transacciones);

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

        ImageView ivListaTransacciones = findViewById(R.id.ivListaTransacciones);

        ivListaTransacciones.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ListaTransaccionesAyuda.this, SaldarDeudasAyuda.class);
                intent.putExtra("usuarioActivo", usuarioActivo);
                intent.putExtra("usuarioIdActivo", usuarioIdActivo);
                intent.putExtra("info", info);
                startActivity(intent);
            }


        });


    }

}