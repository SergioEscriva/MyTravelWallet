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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.spenades.mytravelwallet.adapters.ParticipanAdapters;
import me.spenades.mytravelwallet.controllers.ParticipanController;
import me.spenades.mytravelwallet.controllers.ParticipanteController;
import me.spenades.mytravelwallet.controllers.TransaccionController;
import me.spenades.mytravelwallet.controllers.UsuarioController;
import me.spenades.mytravelwallet.models.Participan;
import me.spenades.mytravelwallet.models.Participante;
import me.spenades.mytravelwallet.models.Transaccion;
import me.spenades.mytravelwallet.utilities.UsuarioUtility;

public class EditarTransaccionesActivity extends AppCompatActivity {

    private static EditText etPagadorId, etNombrePagador;
    private static String nuevosParticipan;
    public PopUpClassPagador f;
    private ArrayList<String> participantesNuevos = new ArrayList<>();
    private String pagadorElegido;
    private EditText etDescripcion, etImporte, etParticipantes, etCategoria, etFecha, etWalletId, tvTransaccionId;
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
    private UsuarioUtility usuarioUtility;
    private UsuarioController usuarioController;
    private List<Participante> listaDeParticipantes;
    private List<Participante> listaDeParticipan;
    private RecyclerView recyclerViewPagadores, recyclerViewParticipan;
    private String nuevoPagador;
    private String walletName;
    private String moniato;
    private long walletId;
    private long transaccionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);
        f = new PopUpClassPagador();

        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        this.walletName = extras.getString("walletName");
        this.walletId = Long.parseLong(extras.getString("walletId"));
        this.transaccionId = Long.parseLong(extras.getString("transaccionId"));
        this.moniato = extras.getString("walletName");
        // Si no hay datos salimos
        if (extras == null) {
            finish();
            return;
        }


        // Definir el controlador
        transaccionController = new TransaccionController(EditarTransaccionesActivity.this);
        participanteController = new ParticipanteController(EditarTransaccionesActivity.this);
        participanController = new ParticipanController(EditarTransaccionesActivity.this);
        usuarioController = new UsuarioController(EditarTransaccionesActivity.this);
        usuarioUtility = new UsuarioUtility();

        // Ahora declaramos las vistas
        recyclerViewPagadores = findViewById(R.id.recyclerViewParticipan);
        recyclerViewParticipan = findViewById(R.id.recyclerViewParticipan);
        etDescripcion = findViewById(R.id.etEditarDescripcion);
        etImporte = findViewById(R.id.etEditarImporte);
        etCategoria = findViewById(R.id.etEditarCategoria);
        etFecha = findViewById(R.id.etEditarFecha);
        etNombrePagador = findViewById(R.id.etNombrePagador);
        etPagadorId = findViewById(R.id.etPagadorId);
        btnCancelarEdicion = findViewById(R.id.btnCancelarEdicionTransaccion);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambiosTransaccion);


        // Rearmar la transacción
        String descripcionTransaccion = extras.getString("descripcionTransaccion");
        String importeTransaccion = extras.getString("importeTransaccion");
        long pagadorIdTransaccion = extras.getLong("pagadorIdTransaccion");
        String nombrePagadorTransaccion = extras.getString("nombrePagadorTransaccion");
        String participantesTransaccion = extras.getString("participantesTransaccion");
        int fechaTransaccion = extras.getInt("fechaTransaccion");
        String categoriaTransaccion = extras.getString("categoriaTransaccion");
        transaccion = new Transaccion(descripcionTransaccion, importeTransaccion, pagadorIdTransaccion, nombrePagadorTransaccion, participantesTransaccion, categoriaTransaccion, fechaTransaccion, walletId, transaccionId);
        nuevosParticipan = participantesTransaccion;
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

        // Rellenar los EditText de la pantalla
        etDescripcion.setText(transaccion.getDescripcion());
        etImporte.setText(transaccion.getImporte());
        etCategoria.setText(transaccion.getCategoria());
        etFecha.setText(String.valueOf(transaccion.getFecha()));
        etPagadorId.setText(String.valueOf(transaccion.getPagadorId()));
        etNombrePagador.setText(String.valueOf(transaccion.getNombrePagador()));


        // Listener del PopUp para elegir pagador.
        etNombrePagador.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                PopUpClassPagador popUpClassPagador = new PopUpClassPagador();
                popUpClassPagador.showPopupWindow(v, listaDeParticipantes);
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
                etNombrePagador.setError(null);
                etPagadorId.setError(null);
                etCategoria.setError(null);
                etFecha.setError(null);

                // Crear la transaccion con los cambio y su id
                String nuevaDescripcion = etDescripcion.getText().toString();
                String nuevoImporte = etImporte.getText().toString();
                String nuevosParticipantes = nuevosParticipan;
                String nuevaCategoria = etCategoria.getText().toString();
                String nuevaFecha = etFecha.getText().toString();
                String nuevoPagador = etNombrePagador.getText().toString();
                String nuevoPagadorId = etPagadorId.getText().toString();

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
                    Toast.makeText(EditarTransaccionesActivity.this, "Error, selecciona un participante mínimo.", Toast.LENGTH_SHORT).show();
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
                // Conversión de nombre de pagador a id para DB
                String nuevoNombrePagador = nuevoPagador;
                long nuevoIdPagadorId = Long.parseLong(nuevoPagadorId);

                // Si llegamos hasta aquí es porque los datos ya están validados
                Transaccion transaccionConNuevosCambios = new Transaccion(nuevaDescripcion, nuevoImporte, nuevoIdPagadorId, nuevosParticipantes, nuevaCategoria, Integer.parseInt(nuevaFecha), walletId, transaccion.getId());

                //int filasModificadas = transaccionController.guardarCambios(transaccionConNuevosCambios);
                int filasModificadas = 1;
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

    // Esto es para pasar datos del PopUp aquí
    public TextView retornaNombrePagador() {
        return this.etNombrePagador;
    }

    // Esto es para pasar datos del PopUp aquí
    public TextView retornaPagadorId() {
        return this.etPagadorId;
    }

    public String paticipanCheck(List<String> participaCheck) {
        // Convertimos de lista a String para poder guardar en DB
        this.nuevosParticipan = String.join(",", participaCheck);
        return this.nuevosParticipan;
    }

}