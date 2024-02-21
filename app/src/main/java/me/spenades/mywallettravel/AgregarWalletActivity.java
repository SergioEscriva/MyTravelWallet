package me.spenades.mywallettravel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import me.spenades.mywallettravel.controllers.WalletController;
import me.spenades.mywallettravel.modelos.Wallet;

public class AgregarWalletActivity extends AppCompatActivity {
    private Button btnAgregarWallet, btnCancelarNuevoWallet;
    private EditText etNombre, etDescripcion, etPropietario, etCompartir;
    private WalletController walletController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet);

        // Instanciar vistas
        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPropietario = findViewById(R.id.etPropietario);
        etCompartir = findViewById(R.id.etCompartir);
        btnAgregarWallet = findViewById(R.id.btn_agregar_wallet);
        btnCancelarNuevoWallet = findViewById(R.id.btn_cancelar_nuevo_wallet);
        // Crear el controlador
        walletController = new WalletController(AgregarWalletActivity.this);

        // Agregar listener del botón de guardar
        btnAgregarWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Resetear errores
                etNombre.setError(null);
                etDescripcion.setError(null);
                etPropietario.setError(null);
                etCompartir.setError(null);

                String nombre = etNombre.getText().toString(),
                        descripcion = etDescripcion.getText().toString(),
                        propietario = etPropietario.getText().toString(),
                        compartir = etCompartir.getText().toString();



                if ("".equals(nombre)) {
                    etNombre.setError("Escribe nombre del Wallet");
                    etNombre.requestFocus();
                    return;
                }
                if ("".equals(descripcion)) {
                    etDescripcion.setError("Escribe pequeña descripción");
                    etDescripcion.requestFocus();
                    return;
                }
                if ("".equals(propietario)) {
                    etPropietario.setError("Escribe Nombre del propietario");
                    etPropietario.requestFocus();
                    return;
                }
                if ("".equals(compartir)) {
                    etCompartir.setError("Escribe si desdesas compartirlo");
                    etCompartir.requestFocus();
                    return;
                }

                /* Ver si es un entero
                int edad;
                try {
                    edad = Integer.parseInt(etEdad.getText().toString());
                } catch (NumberFormatException e) {
                    etEdad.setError("Escribe un número");
                    etEdad.requestFocus();
                    return;
                }
                */
                // Ya pasó la validación
                Wallet nuevoWallet = new Wallet(nombre, descripcion, Integer.parseInt(propietario), Integer.parseInt(compartir));
                long id = walletController.nuevoWallet(nuevoWallet);
                if (id == -1) {
                    // De alguna manera ocurrió un error
                    Toast.makeText(AgregarWalletActivity.this, "Error al guardar. Intenta de nuevo", Toast.LENGTH_SHORT).show();
                } else {
                    // Terminar
                    finish();
                }
            }
        });

        // El de cancelar simplemente cierra la actividad
        btnCancelarNuevoWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
