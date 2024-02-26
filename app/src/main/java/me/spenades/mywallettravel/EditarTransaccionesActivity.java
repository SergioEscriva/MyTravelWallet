package me.spenades.mywallettravel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.spenades.mywallettravel.adapters.AdaptadorPagadores;
import me.spenades.mywallettravel.controllers.ParticipanteController;
import me.spenades.mywallettravel.controllers.TransaccionController;
import me.spenades.mywallettravel.models.Participante;
import me.spenades.mywallettravel.models.Transaccion;

public class EditarTransaccionesActivity extends AppCompatActivity {
    private EditText etDescripcion, etImporte, spPagador, etParticipantes, etCategoria, etFecha, etWalletId;
    private Button btnGuardarCambios, btnCancelarEdicion;
    private Transaccion transaccion;//La transacción que vamos a estar editando
    private Participante participante;
    private TransaccionController transaccionController;
    private ParticipanteController participanteController;
    private AdaptadorPagadores adaptadorPagadores;
    private List<Participante> listaDePagadores;
    private RecyclerView recyclerViewPagadores;
    private String nuevoPagador;
    private String walletName;
    private long walletId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);
        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        this.walletName = extras.getString("walletName");
        this.walletId = Long.parseLong(extras.getString("walletId"));
        // Si no hay datos salimos
        if (extras == null) {
            finish();
            return;
        }


        // Definir el controlador
        transaccionController = new TransaccionController(EditarTransaccionesActivity.this);
        participanteController = new ParticipanteController(EditarTransaccionesActivity.this);

        // Ahora declaramos las vistas
        recyclerViewPagadores = findViewById(R.id.recyclerViewPagadores);
        etDescripcion = findViewById(R.id.etEditarDescripcion);
        etImporte = findViewById(R.id.etEditarImporte);
        etParticipantes = findViewById(R.id.etEditarParticipantes);
        etCategoria = findViewById(R.id.etEditarCategoria);
        etFecha = findViewById(R.id.etEditarFecha);
        btnCancelarEdicion = findViewById(R.id.btnCancelarEdicionTransaccion);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambiosTransaccion);

        // Rearmar la transacción
        String descripcionTransaccion = extras.getString("descripcionTransaccion");
        String importeTransaccion = extras.getString("importeTransaccion");
        String pagadorTransaccion = extras.getString("pagadorTransaccion");
        String participantesTransaccion = extras.getString("participantesTransaccion");
        String categoriaTransaccion = extras.getString("categoriaTransaccion");
        int fechaTransaccion = extras.getInt("fechaTransaccion");
        long transaccionId = extras.getLong("transaccionId");
        transaccion = new Transaccion(descripcionTransaccion, importeTransaccion, pagadorTransaccion, participantesTransaccion, categoriaTransaccion, fechaTransaccion, walletId, transaccionId);

        // Por defecto es una lista vacía,
        listaDePagadores = new ArrayList<>();
        adaptadorPagadores = new AdaptadorPagadores(listaDePagadores);
        // Ponemos la lista al adaptador y configuramos el recyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewPagadores.setLayoutManager(mLayoutManager);
        recyclerViewPagadores.setItemAnimator(new DefaultItemAnimator());
        recyclerViewPagadores.setAdapter(adaptadorPagadores);

        //Refrescamos datos del RecycleView
        refrescarListaDeParticipantes();

        // Rellenar los EditText de la pantalla
        etDescripcion.setText(transaccion.getDescripcion());
        etImporte.setText(transaccion.getImporte());
        etParticipantes.setText(String.valueOf(transaccion.getParticipantes()));
        etCategoria.setText(transaccion.getCategoria());
        etFecha.setText(String.valueOf(transaccion.getFecha()));


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
                //spPagador.setError(null);
                etParticipantes.setError(null);
                etCategoria.setError(null);
                etFecha.setError(null);
                //etWalletId.setError(null);

                // Crear la transaccion con los cambio y su id
                String nuevaDescripcion = etDescripcion.getText().toString();
                String nuevoImporte = etImporte.getText().toString();
                String nuevoPagador = EditarTransaccionesActivity.this.nuevoPagador;
                String nuevosParticipantes = etParticipantes.getText().toString();
                String nuevaCategoria = etCategoria.getText().toString();
                String nuevaFecha = etFecha.getText().toString();

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

                // Si llegamos hasta aquí es porque los datos ya están validados
                Transaccion transaccionConNuevosCambios = new Transaccion(nuevaDescripcion, nuevoImporte, nuevoPagador, nuevosParticipantes, nuevaCategoria, Integer.parseInt(nuevaFecha), walletId, transaccion.getId());

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
    public void refrescarListaDeParticipantes () {
        listaDePagadores = participanteController.obtenerParticipantes(walletId);
        adaptadorPagadores.setListaDeParticipantes(listaDePagadores);
        adaptadorPagadores.notifyDataSetChanged();


    }

}
