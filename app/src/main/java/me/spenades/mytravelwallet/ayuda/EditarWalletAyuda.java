package me.spenades.mytravelwallet.ayuda;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import me.spenades.mytravelwallet.R;

public class EditarWalletAyuda extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ayuda_editar_wallet);

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

        ImageView ivEditarWallets = findViewById(R.id.ivEditarWallets);

        ivEditarWallets.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EditarWalletAyuda.this, ListaTransaccionesAyuda.class);
                intent.putExtra("usuarioActivo", usuarioActivo);
                intent.putExtra("usuarioIdActivo", usuarioIdActivo);
                intent.putExtra("info", info);
                startActivity(intent);
            }


        });


    }

}
