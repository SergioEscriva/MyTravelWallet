package me.spenades.mytravelwallet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import me.spenades.mytravelwallet.controllers.TransaccionController;
import me.spenades.mytravelwallet.models.Transaccion;

public class AgregarTransaccionActivity extends AppCompatActivity {
    private Button btnAgregarTransaccion, btnCancelarNuevaTransaccion;
    private EditText etDescripcion, etImporte, etPagadorId, etNombrePagador, etParticipantes, etCategoria, etFecha;
    private TransaccionController transaccionController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        // Si no hay datos (cosa rara) salimos
        if (extras == null) {
            finish();
            return;
        }

        // Recuperamos WalletId Activo
        long walletIdSelected = extras.getLong("walletId");


        // Instanciar vistas
        etDescripcion = findViewById(R.id.etDescripcion);
        etImporte = findViewById(R.id.etImporte);
        etNombrePagador = findViewById(R.id.etNombrePagador);
        etPagadorId = findViewById(R.id.etPagadorId);
        etParticipantes = findViewById(R.id.etParticipantes);
        etCategoria = findViewById(R.id.etCategoria);
        etFecha = findViewById(R.id.etFecha);
        // etWalletId = findViewById(R.id.etWalletid);

        btnAgregarTransaccion = findViewById(R.id.btn_agregar_transaccion);
        btnCancelarNuevaTransaccion = findViewById(R.id.btn_cancelar_nueva_transaccion);
        // Crear el controlador
        transaccionController = new TransaccionController(AgregarTransaccionActivity.this);

        // Agregar listener del botón de guardar
        btnAgregarTransaccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Resetear errores
                etDescripcion.setError(null);
                etImporte.setError(null);
                etPagadorId.setError(null);
                etParticipantes.setError(null);
                etCategoria.setError(null);
                etFecha.setError(null);
                //etWalletId.setError(null);

                String descripcion = etDescripcion.getText().toString(),
                        importeString = etImporte.getText().toString(),
                        //pagadorId = etPagadorId.getText().toString(),
                        nombrePagador = etNombrePagador.getText().toString(),
                        participantesString = etParticipantes.getText().toString(),
                        categoria = etCategoria.getText().toString(),
                        fechaString = etFecha.getText().toString();
                //TODO PARA QUE FUNCIONA HASTA ELECCION POR MENU
                String pagadorId = "1";

                if ("".equals(descripcion)) {
                    etDescripcion.setError("Escribe la descripción");
                    etDescripcion.requestFocus();
                    return;
                }
                if ("".equals(importeString)) {
                    etImporte.setError("Escribe el importe");
                    etImporte.requestFocus();
                    return;
                }
                if ("".equals(nombrePagador)) {
                    etPagadorId.setError("Escribe Nombre del que efectua el pago");
                    etPagadorId.requestFocus();
                    return;
                }
                if ("".equals(participantesString)) {
                    etParticipantes.setError("Escribe número participantes");
                    etParticipantes.requestFocus();
                    return;
                }
                if ("".equals(categoria)) {
                    etCategoria.setError("Escribe nombre de la categoría de la compra");
                    etCategoria.requestFocus();
                    return;
                }
                if ("".equals(fechaString)) {
                    etFecha.setError("Escribe la fecha");
                    etFecha.requestFocus();
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
                Transaccion nuevaTransaccion = new Transaccion(descripcion, importeString, Long.parseLong(pagadorId), participantesString, categoria, Integer.parseInt(fechaString), walletIdSelected);
                long id = transaccionController.nuevaTransaccion(nuevaTransaccion);
                if (id == -1) {
                    // De alguna manera ocurrió un error
                    Toast.makeText(AgregarTransaccionActivity.this, "Error al guardar. Intenta de nuevo", Toast.LENGTH_SHORT).show();
                } else {
                    // Terminar
                    finish();
                }
            }
        });

        // El de cancelar simplemente cierra la actividad
        btnCancelarNuevaTransaccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}