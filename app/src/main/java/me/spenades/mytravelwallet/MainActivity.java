package me.spenades.mytravelwallet;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import me.spenades.mytravelwallet.activities.UsuarioInicialActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Simplemente cambiamos de actividad
        Intent intent = new Intent(MainActivity.this,
                UsuarioInicialActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        //iniciar(); //esto hace que vuelva a la lista de Wallets al pulsar atrás.
        finish();
        super.onResume();

    }

}