package me.spenades.mytravelwallet.ayuda;


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

        ImageView ivListaTransacciones = findViewById(R.id.ivListaTransacciones);

        ivListaTransacciones.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

}
