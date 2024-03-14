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

import java.util.List;

import me.spenades.mytravelwallet.adapters.ParticipanAdapters;
import me.spenades.mytravelwallet.controllers.ParticipanController;
import me.spenades.mytravelwallet.controllers.ParticipanteController;
import me.spenades.mytravelwallet.controllers.TransaccionController;
import me.spenades.mytravelwallet.controllers.UsuarioController;
import me.spenades.mytravelwallet.models.Participante;
import me.spenades.mytravelwallet.models.Transaccion;
import me.spenades.mytravelwallet.utilities.DatePickerFragment;
import me.spenades.mytravelwallet.utilities.Operaciones;
import me.spenades.mytravelwallet.utilities.PopUpPagadorActivity;
import me.spenades.mytravelwallet.utilities.UsuarioUtility;

public class AgregarTransaccionActivity extends AppCompatActivity {

    private static EditText etPagadorId, etNombrePagador;
    private static String nuevosParticipan;
    public PopUpPagadorActivity f;

    private EditText etDescripcion, etImporte, etCategoria, etTransaccionFecha;
    private TransaccionController transaccionController;
    private ParticipanteController participanteController;
    private ParticipanAdapters participanAdapters;
    private List<Participante> listaDeParticipantes;
    private long walletId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_add_transaction);
        setContentView(R.layout.activity_transaction); //Se utiliza el mismo layer en edit/add
        f = new PopUpPagadorActivity();

        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        String walletName = extras.getString("walletName");
        this.walletId = Long.parseLong(extras.getString("walletId"));
        long usuarioIdActivo = extras.getInt("usuarioIdActivo") + 1;
        String usuarioActivo = extras.getString("usuarioActivo");

        // Definir el controlador
        transaccionController = new TransaccionController(AgregarTransaccionActivity.this);
        participanteController = new ParticipanteController(AgregarTransaccionActivity.this);
        ParticipanController participanController = new ParticipanController(AgregarTransaccionActivity.this);
        UsuarioController usuarioController = new UsuarioController(AgregarTransaccionActivity.this);
        UsuarioUtility usuarioUtility = new UsuarioUtility();

        // Ahora declaramos las vistas
        RecyclerView recyclerViewPagadores = findViewById(R.id.recyclerViewParticipan);
        RecyclerView recyclerViewParticipan = findViewById(R.id.recyclerViewParticipan);
        etDescripcion = findViewById(R.id.etTransaccionDescripcion);
        TextView evTransaccionTitulo = findViewById(R.id.evTransaccionDescripcion);
        etImporte = findViewById(R.id.etTransaccionImporte);
        etCategoria = findViewById(R.id.etTransaccionCategoria);
        etTransaccionFecha = findViewById(R.id.etTransaccionFecha);
        etNombrePagador = findViewById(R.id.etNombrePagador);
        etPagadorId = findViewById(R.id.etPagadorId);
        Button btnCancelarTransaccion = findViewById(R.id.btnCancelarTransaccion);
        Button btnGuardarTransaccion = findViewById(R.id.btnGuardarTransaccion);
        etNombrePagador.setText(usuarioActivo);
        etPagadorId.setText(String.valueOf(usuarioIdActivo));
        String transaccionTitulo = "Wallet" + walletName;
        evTransaccionTitulo.setText(transaccionTitulo);

        // Lista Participan Por defecto es una lista vacía,
        participanAdapters = new ParticipanAdapters(listaDeParticipantes, listaDeParticipantes);

        // Ponemos la lista al adaptador y configuramos el recyclerView
        RecyclerView.LayoutManager mLayoutManagerParticipan =
                new LinearLayoutManager(getApplicationContext());
        recyclerViewParticipan.setLayoutManager(mLayoutManagerParticipan);
        recyclerViewParticipan.setItemAnimator(new DefaultItemAnimator());
        recyclerViewParticipan.setAdapter(participanAdapters);
        //Adaptador del autoCompletado de Categoria
        AutoCompleteTextView textView = (AutoCompleteTextView) etCategoria;
        String[] countries = getResources().getStringArray(R.array.countries_array);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries);
        textView.setAdapter(adapter);

        //Refrescamos datos del RecycleView
        refrescarListaDeParticipantes();

        Operaciones operarFecha = new Operaciones();


        // Listener del PopUp para elegir pagador.
        etNombrePagador.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PopUpPagadorActivity popUpPagadorActivity = new PopUpPagadorActivity();
                popUpPagadorActivity.showPopupWindow(v, listaDeParticipantes, "agregar");
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


        // El de cancelar simplemente cierra la actividad
        btnCancelarTransaccion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                finish();
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

                if (nuevaDescripcion.isEmpty()) {
                    etDescripcion.setError("Escribe la descripción");
                    etDescripcion.requestFocus();
                    return;
                }
                if (nuevoImporte.isEmpty()) {
                    etImporte.setError("Escribe el importe");
                    etImporte.requestFocus();
                    return;
                }

                if ("".equals(nuevosParticipantes)) {
                    Toast.makeText(AgregarTransaccionActivity.this, "Error, selecciona un " +
                            "participante mínimo.", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (nuevaCategoria.isEmpty()) {
                    etCategoria.setError("Escribe nombre de la categoría de la compra");
                    etCategoria.requestFocus();
                    return;
                }
                if (nuevaFecha.isEmpty()) {
                    etTransaccionFecha.setError("Escribe la fecha");
                    etTransaccionFecha.requestFocus();
                    return;
                }
                // Conversión de nombre de pagador a id para DB
                long nuevoIdPagadorId = Long.parseLong(nuevoPagadorId);

                // Si llegamos hasta aquí es porque los datos ya están validados
                Transaccion transaccionConNuevosCambios = new Transaccion(nuevaDescripcion,
                        nuevoImporte, nuevoIdPagadorId, nuevosParticipantes, nuevaCategoria,
                        nuevaFecha, walletId);

                long transaccionId =
                        transaccionController.nuevaTransaccion(transaccionConNuevosCambios);
                //int filasModificadas = 1;
                if (transaccionId == - 1) {
                    // De alguna forma ocurrió un error porque se debió modificar únicamente una
                    // fila
                    Toast.makeText(AgregarTransaccionActivity.this, "Error guardando cambios. " +
                            "Intente de nuevo.", Toast.LENGTH_SHORT).show();
                } else {
                    // Si las cosas van bien, volvemos a la principal
                    // cerrando esta actividad
                    finish();
                }
                finish();
            }

        });

    }


    public void refrescarListaDeParticipantes() {
        // Rellenamos la lista
        listaDeParticipantes = participanteController.obtenerParticipantes(walletId);

        //Adaptador Participa Lista total
        participanAdapters.setListaDeParticipan(listaDeParticipantes, listaDeParticipantes);
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
                System.out.println(selectedDate);
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}
