package me.spenades.mywallettravel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import me.spenades.mywallettravel.controllers.TransaccionController;
import me.spenades.mywallettravel.modelos.Transaccion;

public class EditarTransaccionesActivity extends AppCompatActivity {
    private EditText etDescripcion, etImporte, etPagador, etParticipantes, etCategoria, etFecha, etWalletId;
    private Button btnGuardarCambios, btnCancelarEdicion;
    private Transaccion transaccion;//La transacción que vamos a estar editando
    private TransaccionController transaccionController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);

        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        // Si no hay datos (cosa rara) salimos
        if (extras == null) {
            finish();
            return;
        }
        // Instanciar el controlador de las transacciones
        transaccionController = new TransaccionController(EditarTransaccionesActivity.this);

        // Rearmar la transacción
        long transaccionId = extras.getLong("transaccionId");
        String descripcionTransaccion = extras.getString("descripcionTransaccion");
        int importeTransaccion = extras.getInt("importeTransaccion");
        String pagadorTransaccion = extras.getString("pagadorTransaccion");
        int participantesTransaccion = extras.getInt("participantesTransaccion");
        String categoriaTransaccion = extras.getString("categoriaTransaccion");
        int fechaTransaccion = extras.getInt("fechaTransaccion");
        int walletId = extras.getInt("walletId");
        transaccion = new Transaccion(descripcionTransaccion, importeTransaccion, pagadorTransaccion, participantesTransaccion, categoriaTransaccion, fechaTransaccion, walletId, transaccionId);

        // Ahora declaramos las vistas
        etDescripcion = findViewById(R.id.etEditarDescripcion);
        etImporte = findViewById(R.id.etEditarImporte);
        etPagador = findViewById(R.id.etEditarPagador);
        etParticipantes = findViewById(R.id.etEditarParticipantes);
        etCategoria = findViewById(R.id.etEditarCategoria);
        etFecha = findViewById(R.id.etEditarFecha);
        //etWalletId = findViewById(R.id.etEditarWalletId);
        btnCancelarEdicion = findViewById(R.id.btnCancelarEdicionTransaccion);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambiosTransaccion);

        // Rellenar los EditText de la pantalla
        etDescripcion.setText(transaccion.getDescripcion());
        etImporte.setText(String.valueOf(transaccion.getImporte()));
        etPagador.setText(transaccion.getPagador());
        etParticipantes.setText(String.valueOf(transaccion.getParticipantes()));
        etCategoria.setText(transaccion.getCategoria());
        etFecha.setText(String.valueOf(transaccion.getFecha()));
        //etWalletId.setText(String.valueOf(transaccion.getWalletId()));

        // Listener del click del botón para salir, simplemente cierra la actividad
        btnCancelarEdicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Listener del click del botón que guarda cambios
        btnGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remover previos errores si existen
                etDescripcion.setError(null);
                etImporte.setError(null);
                etPagador.setError(null);
                etParticipantes.setError(null);
                etCategoria.setError(null);
                etFecha.setError(null);
                etWalletId.setError(null);

                // Crear la transaccion con los cambio y su id
                String nuevaDescripcion = etDescripcion.getText().toString();
                String nuevoImporte = etImporte.getText().toString();
                String nuevoPagador = etPagador.getText().toString();
                String nuevosParticipantes = etParticipantes.getText().toString();
                String nuevaCategoria = etCategoria.getText().toString();
                String nuevaFecha = etFecha.getText().toString();
                //String nuevaWalletId = etWalletId.getText().toString();

                if ("".equals(nuevaDescripcion)) {
                    etDescripcion.setError("Escribe la descripción");
                    etDescripcion.requestFocus();
                    return;
                }
                if ("".equals(nuevoImporte)) {
                    etImporte.setError("Escribe el importe");
                    etImporte.requestFocus();
                    return;
                }
                if ("".equals(nuevoPagador)) {
                    etPagador.setError("Escribe Nombre del que efectua el pago");
                    etPagador.requestFocus();
                    return;
                }
                if ("".equals(nuevosParticipantes)) {
                    etParticipantes.setError("Escribe número participantes");
                    etParticipantes.requestFocus();
                    return;
                }
                if ("".equals(nuevaCategoria)) {
                    etCategoria.setError("Escribe nombre de la categoría de la compra");
                    etCategoria.requestFocus();
                    return;
                }
                if ("".equals(nuevaFecha)) {
                    etFecha.setError("Escribe la fecha");
                    etFecha.requestFocus();
                    return;
                }

                /*
                // Si no es entero, igualmente marcar error
                int nuevaEdad;
                try {
                    nuevaEdad = Integer.parseInt(posibleNuevaEdad);
                } catch (NumberFormatException e) {
                    etEditarEdad.setError("Escribe un número");
                    etEditarEdad.requestFocus();
                    return;
                }

                 */

                System.out.println("Editaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                // Si llegamos hasta aquí es porque los datos ya están validados
                Transaccion transaccionConNuevosCambios = new Transaccion(nuevaDescripcion, Integer.parseInt(nuevoImporte), nuevoPagador, Integer.parseInt(nuevosParticipantes), nuevaCategoria, Integer.parseInt(nuevaFecha), transaccion.getWalletId(), transaccion.getTransaccionId());
                int filasModificadas = transaccionController.guardarCambios(transaccionConNuevosCambios);
                if (filasModificadas != 1) {
                    // De alguna forma ocurrió un error porque se debió modificar únicamente una fila
                    Toast.makeText(EditarTransaccionesActivity.this, "Error guardando cambios. Intente de nuevo.", Toast.LENGTH_SHORT).show();
                } else {
                    // Si las cosas van bien, volvemos a la principal
                    // cerrando esta actividad
                    finish();
                }
            }
        });
    }
}
