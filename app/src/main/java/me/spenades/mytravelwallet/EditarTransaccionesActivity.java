package me.spenades.mytravelwallet;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.spenades.mytravelwallet.adapters.ParticipanAdapters;
import me.spenades.mytravelwallet.controllers.ParticipanController;
import me.spenades.mytravelwallet.controllers.ParticipanteController;
import me.spenades.mytravelwallet.controllers.TransaccionController;
import me.spenades.mytravelwallet.models.Participan;
import me.spenades.mytravelwallet.models.Participante;
import me.spenades.mytravelwallet.models.Transaccion;

public class EditarTransaccionesActivity extends AppCompatActivity {

    private EditText etDescripcion, etImporte, etPagador, etParticipantes, etCategoria, etFecha, etWalletId, tvTransaccionId;
    private Button btnGuardarCambios, btnCancelarEdicion, btnPopupParticipa;
    private ConstraintLayout clParticipan;
    private Transaccion transaccion;//La transacción que vamos a estar editando
    private Participante participante;
    private Participan participan;
    private PopUpClassPagador popUpClassPagador;
    private TransaccionController transaccionController;
    private ParticipanteController participanteController;
    private ParticipanController participanController;
    private ParticipanAdapters participanAdapters;
    private List<Participante> listaDeParticipantes;
    private List<Participante> listaDeParticipan;
    private RecyclerView recyclerViewPagadores, recyclerViewParticipan;
    private String nuevoPagador;
    private String walletName;
    private long walletId;
    private long transaccionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);

        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        this.walletName = extras.getString("walletName");
        this.walletId = Long.parseLong(extras.getString("walletId"));
        this.transaccionId = Long.parseLong(extras.getString("transaccionId"));
        this.nuevoPagador = "0";
        // Si no hay datos salimos
        if (extras == null) {
            finish();
            return;
        }


        // Definir el controlador
        transaccionController = new TransaccionController(EditarTransaccionesActivity.this);
        participanteController = new ParticipanteController(EditarTransaccionesActivity.this);
        participanController = new ParticipanController(EditarTransaccionesActivity.this);


        // Ahora declaramos las vistas
        recyclerViewPagadores = findViewById(R.id.recyclerViewParticipan);
        recyclerViewParticipan = findViewById(R.id.recyclerViewParticipan);
        etDescripcion = findViewById(R.id.etEditarDescripcion);
        etImporte = findViewById(R.id.etEditarImporte);
        etCategoria = findViewById(R.id.etEditarCategoria);
        etFecha = findViewById(R.id.etEditarFecha);
        etPagador = findViewById(R.id.etPagador);

        btnCancelarEdicion = findViewById(R.id.btnCancelarEdicionTransaccion);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambiosTransaccion);


        // Rearmar la transacción
        String descripcionTransaccion = extras.getString("descripcionTransaccion");
        String importeTransaccion = extras.getString("importeTransaccion");
        String pagadorTransaccion = extras.getString("pagadorTransaccion");
        String participantesTransaccion = extras.getString("participantesTransaccion"); //TODO recuperar participan de una lista
        int fechaTransaccion = extras.getInt("fechaTransaccion");
        String categoriaTransaccion = extras.getString("categoriaTransaccion");
        transaccion = new Transaccion(descripcionTransaccion, importeTransaccion, pagadorTransaccion, participantesTransaccion, categoriaTransaccion, fechaTransaccion, walletId, transaccionId);

        // Lista Pagadores Por defecto es una lista vacía,


        // Lista Participan Por defecto es una lista vacía,
        listaDeParticipan = new ArrayList<>();
        participanAdapters = new ParticipanAdapters(listaDeParticipantes, listaDeParticipan);
        // Ponemos la lista al adaptador y configuramos el recyclerView
        RecyclerView.LayoutManager mLayoutManagerParticipan = new LinearLayoutManager(getApplicationContext());
        recyclerViewParticipan.setLayoutManager(mLayoutManagerParticipan);
        recyclerViewParticipan.setItemAnimator(new DefaultItemAnimator());
        recyclerViewParticipan.setAdapter(participanAdapters);

        //Refrescamos datos del RecycleView
        refrescarListaDeParticipantes();
        //participanAdapters = new ParticipanAdapters(listaDeParticipantes, listaDeParticipan);


        // Rellenar los EditText de la pantalla
        etDescripcion.setText(transaccion.getDescripcion());
        etImporte.setText(transaccion.getImporte());
        //etParticipantes.setText(String.valueOf(transaccion.getParticipantes()));
        etCategoria.setText(transaccion.getCategoria());
        etFecha.setText(String.valueOf(transaccion.getFecha()));
        etPagador.setText(String.valueOf(transaccion.getPagador()));


        // Listening del PopUp para elegir pagador.
        etPagador.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                PopUpClassPagador popUpClassPagador = new PopUpClassPagador();
                popUpClassPagador.showPopupWindow(v, listaDeParticipantes);
                // recyclerview tamaño fijo y linear layout
                return true;
            }

        });


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
                //etParticipantes.setError(null);
                etCategoria.setError(null);
                etFecha.setError(null);
                //etWalletId.setError(null);


                // Crear la transaccion con los cambio y su id
                String nuevaDescripcion = etDescripcion.getText().toString();
                String nuevoImporte = etImporte.getText().toString();
                // String nuevoPagador = EditarTransaccionesActivity.this.nuevoPagador;
                String nuevosParticipantes = etParticipantes.getText().toString();
                String nuevaCategoria = etCategoria.getText().toString();
                String nuevaFecha = etFecha.getText().toString();
                System.out.println(nuevoPagador);
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

    public void refrescarListaDeParticipantes() {
        // Rellenamos la lista
        listaDeParticipantes = participanteController.obtenerParticipantes(walletId);
        listaDeParticipan = participanController.obtenerParticipan(transaccionId);

        //Adaptador Participa Lista total

        participanAdapters.setListaDeParticipan(listaDeParticipan, listaDeParticipantes);
        participanAdapters.notifyDataSetChanged();
    }
}