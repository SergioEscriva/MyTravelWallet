package me.spenades.mytravelwallet;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import me.spenades.mytravelwallet.controllers.CategoriaController;
import me.spenades.mytravelwallet.controllers.ParticipanteController;
import me.spenades.mytravelwallet.controllers.TransaccionController;
import me.spenades.mytravelwallet.models.Categoria;
import me.spenades.mytravelwallet.models.Participante;
import me.spenades.mytravelwallet.models.Transaccion;
import me.spenades.mytravelwallet.utilities.DatePickerFragment;
import me.spenades.mytravelwallet.utilities.Operaciones;
import me.spenades.mytravelwallet.utilities.PopUpPagadorActivity;

public class AgregarTransaccionActivity extends AppCompatActivity {

    private static EditText etPagadorId, etNombrePagador;
    private static String nuevosParticipan;
    public PopUpPagadorActivity popUp;
    private EditText etDescripcion, etImporte, etTransaccionFecha;
    private AutoCompleteTextView etCategoria;
    private TransaccionController transaccionController;
    private ParticipanteController participanteController;
    private CategoriaController categoriaController;
    private ParticipanAdapters participanAdapters;
    private List<Participante> listaDeParticipantes;
    private List<Categoria> listaDeCategorias;
    private long walletId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_transaction); //Se utiliza el mismo layer en edit/add
        popUp = new PopUpPagadorActivity();

        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        String walletName = extras.getString("walletName");
        this.walletId = Long.parseLong(extras.getString("walletId"));
        long usuarioIdActivo = extras.getInt("usuarioIdActivo") + 1;
        String usuarioActivo = extras.getString("usuarioActivo");

        // Definir el controlador
        transaccionController = new TransaccionController(AgregarTransaccionActivity.this);
        participanteController = new ParticipanteController(AgregarTransaccionActivity.this);
        categoriaController = new CategoriaController(AgregarTransaccionActivity.this);

        // Ahora declaramos las vistas
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

        // Lista Categorias Por defecto es una lista vacía,
        listaDeCategorias = categoriaController.obtenerCategorias();

        // https://developer.android.com/develop/ui/views/touch-and-input/keyboard-input/style?hl=es-419#AutoComplete
        // Convertimos la Lista Categoria, en String[]
        Operaciones listaCategoriasString = new Operaciones();
        String[] categorias = listaCategoriasString.listaDeCategoriasString(listaDeCategorias);

        // Creamos el adaptador Autoc AutoCompleteTextView con el layer por defecto de Android.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categorias);
        etCategoria.setAdapter(adapter);

        // Ponemos la lista al adaptador y configuramos el recyclerView
        RecyclerView.LayoutManager mLayoutManagerParticipan =
                new LinearLayoutManager(getApplicationContext());
        recyclerViewParticipan.setLayoutManager(mLayoutManagerParticipan);
        recyclerViewParticipan.setItemAnimator(new DefaultItemAnimator());
        recyclerViewParticipan.setAdapter(participanAdapters);

        //Refrescamos datos del RecycleView
        refrescarListaDeParticipantes();

        // Listener del PopUp para elegir pagador.
        etNombrePagador.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideKeyboard(etNombrePagador);
                PopUpPagadorActivity popUpPagadorActivity = new PopUpPagadorActivity();
                popUpPagadorActivity.showPopupWindow(v, listaDeParticipantes, "agregar");
            }
        });

        // Creamos el picker de fecha
        // https://programacionymas.com/blog/como-pedir-fecha-android-usando-date-picker
        etTransaccionFecha.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                hideKeyboard(etCategoria);
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

                //Añade la Categoria a la BD, siempre la introduce como nueva,TODO se deben actualizar en otro sitio.
                ArrayList<Categoria> categoriaIdActual = categoriaController.obtenerCategoriaId(nuevaCategoria);
                Categoria categoriaActual = new Categoria(String.valueOf(categoriaIdActual));
                long categoriaNuevaId = categoriaController.nuevaCategoria(categoriaActual);
                if (categoriaNuevaId == - 1) categoriaNuevaId = categoriaActual.getId();

                // Si llegamos hasta aquí es porque los datos ya están validados
                Transaccion transaccionConNuevosCambios = new Transaccion(nuevaDescripcion,
                        nuevoImporte, nuevoIdPagadorId, nuevosParticipantes, categoriaNuevaId,
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

        //Apdaptador Categoria
        listaDeCategorias = categoriaController.obtenerCategorias();
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


    // Cierra el teclado
    // https://umhandroid.momrach.es/ocultar-el-teclado-virtual/
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
