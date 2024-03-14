package me.spenades.mytravelwallet;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
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
import me.spenades.mytravelwallet.models.Participante;
import me.spenades.mytravelwallet.models.Transaccion;
import me.spenades.mytravelwallet.utilities.DatePickerFragment;
import me.spenades.mytravelwallet.utilities.PopUpPagadorActivity;
import me.spenades.mytravelwallet.utilities.UsuarioUtility;

public class EditarTransaccionesActivity extends AppCompatActivity {

    private static EditText etPagadorId, etNombrePagador;
    private static String nuevosParticipan;
    public PopUpPagadorActivity f;

    private EditText etDescripcion, etImporte, etCategoria, etTransaccionFecha;
    private TextView evTransaccionTitulo;
    private Button btnGuardarTransaccion, btnCancelarTransaccion;
    private TransaccionController transaccionController;
    private ParticipanteController participanteController;
    private ParticipanController participanController;
    private ParticipanAdapters participanAdapters;
    private Transaccion transaccion;
    private UsuarioUtility usuarioUtility;
    private UsuarioController usuarioController;
    private List<Participante> listaDeParticipantes;
    private List<Participante> listaDeParticipan;
    private RecyclerView recyclerViewPagadores, recyclerViewParticipan;
    private String walletName;
    private long walletId;
    private long transaccionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        f = new PopUpPagadorActivity();

        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        this.walletName = extras.getString("walletName");
        this.walletId = Long.parseLong(extras.getString("walletId"));
        this.transaccionId = Long.parseLong(extras.getString("transaccionId"));

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
        etDescripcion = findViewById(R.id.etTransaccionDescripcion);
        etImporte = findViewById(R.id.etTransaccionImporte);
        etCategoria = findViewById(R.id.etTransaccionCategoria);
        etTransaccionFecha = findViewById(R.id.etTransaccionFecha);
        etNombrePagador = findViewById(R.id.etNombrePagador);
        etPagadorId = findViewById(R.id.etPagadorId);
        btnCancelarTransaccion = findViewById(R.id.btnCancelarTransaccion);
        btnGuardarTransaccion = findViewById(R.id.btnGuardarTransaccion);

        // Rearmar la transacción
        String descripcionTransaccion = extras.getString("descripcionTransaccion");
        String importeTransaccion = extras.getString("importeTransaccion");
        long pagadorIdTransaccion = extras.getLong("pagadorIdTransaccion");
        String nombrePagadorTransaccion = extras.getString("nombrePagadorTransaccion");
        String participantesTransaccion = extras.getString("participantesTransaccion");
        String fechaTransaccion = extras.getString("fechaTransaccion");
        String categoriaTransaccion = extras.getString("categoriaTransaccion");
        transaccion = new Transaccion(descripcionTransaccion, importeTransaccion,
                pagadorIdTransaccion, nombrePagadorTransaccion, participantesTransaccion,
                categoriaTransaccion, fechaTransaccion, walletId, transaccionId);
        nuevosParticipan = participantesTransaccion;


        // Lista Participan Por defecto es una lista vacía,
        listaDeParticipan = new ArrayList<>();
        participanAdapters = new ParticipanAdapters(listaDeParticipantes, listaDeParticipan);

        // Ponemos la lista al adaptador y configuramos el recyclerView
        RecyclerView.LayoutManager mLayoutManagerParticipan =
                new LinearLayoutManager(getApplicationContext());
        recyclerViewParticipan.setLayoutManager(mLayoutManagerParticipan);
        recyclerViewParticipan.setItemAnimator(new DefaultItemAnimator());
        recyclerViewParticipan.setAdapter(participanAdapters);

        //TODO Categoria
        //Adaptador del autoCompletado de Categoria
        AutoCompleteTextView textView = (AutoCompleteTextView) etCategoria;
        String[] categorias = {"Bebida", "Restaurante", "Comida", "Cena", "Gasolina", "Supermercado"};
        String[] categoria = categorias;
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categoria);
        textView.setAdapter(adapter);


        //Refrescamos datos del RecycleView
        refrescarListaDeParticipantes();

        // Rellenar los EditText de la pantalla
        etDescripcion.setText(transaccion.getDescripcion());
        evTransaccionTitulo = findViewById(R.id.evTransaccionDescripcion);
        etImporte.setText(transaccion.getImporte());
        etCategoria.setText(transaccion.getCategoria());
        etTransaccionFecha.setText(String.valueOf(transaccion.getFecha()));
        etPagadorId.setText(String.valueOf(transaccion.getPagadorId()));
        etNombrePagador.setText(String.valueOf(transaccion.getNombrePagador()));
        evTransaccionTitulo.setText("Wallet " + walletName);

        // Listener del PopUp para elegir pagador.
        etNombrePagador.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PopUpPagadorActivity popUpPagadorActivity = new PopUpPagadorActivity();
                popUpPagadorActivity.showPopupWindow(v, listaDeParticipantes, "editar");
                //return true;
            }
        });


        // Creamos el picker de fecha
        // https://programacionymas.com/blog/como-pedir-fecha-android-usando-date-picker
        etTransaccionFecha.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showDatePickerDialog();

            }

        });

        // Listener del click del botón para salir, simplemente cierra la actividad
        btnCancelarTransaccion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Listener del click del botón que guarda cambios
        btnGuardarTransaccion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Remover previos errores si existen
                etDescripcion.setError(null);
                etImporte.setError(null);
                etNombrePagador.setError(null);
                etPagadorId.setError(null);
                etCategoria.setError(null);
                etTransaccionFecha.setError(null);

                // Crear la transaccion con los cambios y su id
                String nuevaDescripcion = etDescripcion.getText().toString();
                String nuevoImporte = etImporte.getText().toString();
                String nuevosParticipantes = nuevosParticipan;
                String nuevaCategoria = etCategoria.getText().toString();
                String nuevaFecha = etTransaccionFecha.getText().toString();
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
                    Toast.makeText(EditarTransaccionesActivity.this, "Error, selecciona un " +
                            "participante mínimo.", Toast.LENGTH_SHORT).show();
                    return;
                }


                if ("".equals(nuevaCategoria)) {
                    etCategoria.setError("Escribe nombre de la categoría de la compra");
                    etCategoria.requestFocus();
                    return;
                }
                if ("".equals(nuevaFecha)) {
                    etTransaccionFecha.setError("Escribe la fecha");
                    etTransaccionFecha.requestFocus();
                    return;
                }
                // Conversión de nombre de pagador a id para DB
                String nuevoNombrePagador = nuevoPagador;
                long nuevoIdPagadorId = Long.parseLong(nuevoPagadorId);

                // Si llegamos hasta aquí es porque los datos ya están validados
                Transaccion transaccionConNuevosCambios = new Transaccion(nuevaDescripcion,
                        nuevoImporte, nuevoIdPagadorId, nuevosParticipantes, nuevaCategoria,
                        nuevaFecha, walletId, transaccion.getId());

                int filasModificadas =
                        transaccionController.guardarCambios(transaccionConNuevosCambios);
                //int filasModificadas = 1;
                if (filasModificadas != 1) {
                    // De alguna forma ocurrió un error porque se debió modificar únicamente una
                    // fila
                    Toast.makeText(EditarTransaccionesActivity.this, "Error guardando cambios. " +
                            "Intente de nuevo.", Toast.LENGTH_SHORT).show();
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


    // Muestra el picker para la fecha
    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 Enero es 0
                final String selectedDate = day + " / " + (month + 1) + " / " + year;
                etTransaccionFecha.setText(selectedDate);
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}