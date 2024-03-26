package me.spenades.mytravelwallet.ayuda;


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

        ImageView ivEditarWallets = findViewById(R.id.ivEditarWallets);

        ivEditarWallets.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }


        });


    }

}
