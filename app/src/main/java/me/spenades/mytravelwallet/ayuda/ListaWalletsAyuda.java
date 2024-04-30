package me.spenades.mytravelwallet.ayuda;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import me.spenades.mytravelwallet.R;

public class ListaWalletsAyuda extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ayuda_lista_wallet);

        ImageView ivListaWallets = findViewById(R.id.ivListaWallets);

        ivListaWallets.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }


        });


    }

}
