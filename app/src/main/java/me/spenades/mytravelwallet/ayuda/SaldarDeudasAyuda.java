package me.spenades.mytravelwallet.ayuda;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import me.spenades.mytravelwallet.R;

public class SaldarDeudasAyuda extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ayuda_saldar_deudas);

        ImageView ivSaldarDeudas = findViewById(R.id.ivSaldarDeudas);

        ivSaldarDeudas.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

}
