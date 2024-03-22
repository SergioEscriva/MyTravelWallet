package me.spenades.mytravelwallet.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.spenades.mytravelwallet.R;
import me.spenades.mytravelwallet.adapters.ParticipanAdapters;
import me.spenades.mytravelwallet.controllers.CategoriaController;
import me.spenades.mytravelwallet.controllers.MiembroWalletController;
import me.spenades.mytravelwallet.controllers.TransaccionController;
import me.spenades.mytravelwallet.models.Categoria;
import me.spenades.mytravelwallet.models.Miembro;
import me.spenades.mytravelwallet.models.Transaccion;
import me.spenades.mytravelwallet.utilities.DatePickerFragment;
import me.spenades.mytravelwallet.utilities.DeudaUtility;
import me.spenades.mytravelwallet.utilities.Operaciones;
import me.spenades.mytravelwallet.utilities.PopUpPagadorActivity;

public class AgregarTransaccionActivity extends AppCompatActivity {

    private static EditText etPagadorId, etNombrePagador;
    private static String nuevosParticipan;
    private static String importeADividir;
    private static EditText etImporte;
    private static TextView tvDivision;
    public PopUpPagadorActivity popUp;
    private EditText etDescripcion, etTransaccionFecha;
    private AutoCompleteTextView etCategoria;
    private TransaccionController transaccionController;
    private MiembroWalletController miembroWalletController;
    private CategoriaController categoriaController;
    private ParticipanAdapters participanAdapters;
    private List<Miembro> listaDeMiembros;
    private List<Categoria> listaDeCategorias;
    private RecyclerView recyclerViewParticipan;
    private long walletId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_transaction); //Se utiliza el mismo layer en edit/add
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        popUp = new PopUpPagadorActivity();

        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        String walletName = extras.getString("walletName");
        this.walletId = Long.parseLong(extras.getString("walletId"));
        long usuarioIdActivo = extras.getInt("usuarioIdActivo") + 1;
        String usuarioActivo = extras.getString("usuarioActivo");

        // Definir el controlador
        transaccionController = new TransaccionController(AgregarTransaccionActivity.this);
        miembroWalletController = new MiembroWalletController(AgregarTransaccionActivity.this);
        categoriaController = new CategoriaController(AgregarTransaccionActivity.this);

        // Ahora declaramos las vistas
        recyclerViewParticipan = findViewById(R.id.recyclerViewParticipan);
        etDescripcion = findViewById(R.id.etTransaccionDescripcion);
        TextView evTransaccionTitulo = findViewById(R.id.evTransaccionDescripcion);
        etImporte = findViewById(R.id.etTransaccionImporte);
        etCategoria = findViewById(R.id.etTransaccionCategoria);
        etTransaccionFecha = findViewById(R.id.etTransaccionFecha);
        etNombrePagador = findViewById(R.id.etNombrePagador);
        etPagadorId = findViewById(R.id.etPagadorId);
        tvDivision = findViewById(R.id.tvDivision);
        Button btnCancelarTransaccion = findViewById(R.id.btnCancelarTransaccion);
        Button btnGuardarTransaccion = findViewById(R.id.btnGuardarTransaccion);
        etNombrePagador.setText(usuarioActivo);
        etPagadorId.setText(String.valueOf(usuarioIdActivo));
        String transaccionTitulo = "Wallet " + walletName;
        evTransaccionTitulo.setText(transaccionTitulo);


        // Lista Participan Por defecto es una lista vacía,
        participanAdapters = new ParticipanAdapters(listaDeMiembros, listaDeMiembros, true);

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

        Operaciones operaciones = new Operaciones();
        String fecha = operaciones.fechaDeHoy();
        etCategoria.setText("Varios");
        etImporte.setText("0");

        etTransaccionFecha.setText(fecha);

        //Refrescamos datos del RecycleView
        refrescarListas();

        //Listener importe
        //https://es.stackoverflow.com/questions/291613/c%C3%B3mo-actualizar-un-dato-autom%C3%A1ticamente-en-android-studio
        etImporte.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String nuevoImporte = etImporte.getText().toString();
                if (etImporte == null) nuevoImporte = "0";
                importeADividir = nuevoImporte;
                participanImporte();
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        // Listener del PopUp para elegir pagador.
        etNombrePagador.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideKeyboard(etNombrePagador);
                // Enviamos al popup la lista de miembros, y la lista de importe pagado por cada miembro.
                Map<Long, Double> listaImportePagado = importePagado();
                PopUpPagadorActivity popUpPagadorActivity = new PopUpPagadorActivity();
                popUpPagadorActivity.showPopupWindow(v, listaDeMiembros, listaImportePagado, "agregar");
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
                String nuevosMiembros = nuevosParticipan;
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

                if ("".equals(nuevosMiembros)) {
                    Toast.makeText(AgregarTransaccionActivity.this, "Error, selecciona un " +
                            "miembro mínimo.", Toast.LENGTH_SHORT).show();
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

                //limpiamos importe introducido 2 decimales, etc
                Operaciones operaciones = new Operaciones();
                String nuevoImporteLimpio = operaciones.dosDecimalesStringString(nuevoImporte);

                // Conversión de nombre de pagador a id para DB
                long nuevoIdPagadorId = Long.parseLong(nuevoPagadorId);

                // Comprobamos si la Categoría existe y según la añadimos o recuperamos el Id
                long categoriaId = operarCategoria(nuevaCategoria);

                // Si llegamos hasta aquí es porque los datos ya están validados
                Transaccion transaccionConNuevosCambios = new Transaccion(nuevaDescripcion,
                        nuevoImporteLimpio, nuevoIdPagadorId, nuevosMiembros, categoriaId,
                        nuevaFecha, walletId);
                long transaccionId = transaccionController.nuevaTransaccion(transaccionConNuevosCambios);
                //int transaccionId = 1;
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


    public void refrescarListas() {
        // Rellenamos la lista
        listaDeMiembros = miembroWalletController.obtenerMiembros(walletId);

        //Adaptador Participa Lista total
        participanAdapters.setListaDeParticipan(listaDeMiembros, listaDeMiembros, true);
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
        participanImporte();
        return this.nuevosParticipan;
    }


    // Rellenamos el importe a pagar por cada participante al añadir transacción
    // variables static para poder traerlo aquí.
    public void participanImporte() {
        Operaciones operaciones = new Operaciones();
        if (importeADividir == null) importeADividir = "0";
        double importeADividirD = Double.parseDouble(importeADividir);

        // limpiamos el String que viene de comas y sacamos cuantos numero hay
        int cantidadParticipa = nuevosParticipan.replaceAll(",", "").length();

        double resultado = importeADividirD / cantidadParticipa;
        String resultadoLimpio = operaciones.dosDecimalesDoubleString(resultado);
        tvDivision.setText("Cada participante deberá pagar: " + resultadoLimpio + "€");

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


    // Cierra el teclado
    // https://umhandroid.momrach.es/ocultar-el-teclado-virtual/
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    //Añade la Categoria a la BD Si no Existe Si existe recupera Id
    public Long operarCategoria(String nuevaCategoria) {

        // Si existe recupera Id y lo añade a la transacción
        Categoria categoriaActual = new Categoria(String.valueOf(nuevaCategoria));
        long categoriaNuevaId = categoriaController.nuevaCategoria(categoriaActual);
        if (categoriaNuevaId <= 0) {
            List<Categoria> categoriaIdActual = categoriaController.obtenerCategoriaId(nuevaCategoria);
            categoriaNuevaId = categoriaIdActual.get(0).getId();
        }
        return categoriaNuevaId;
    }

    // Para rellenar en le popup de pagadores, info de cuanto han pagado ya.
    public Map<Long, Double> importePagado() {
        ArrayList<Transaccion> listaDeTransacciones = transaccionController.obtenerTransacciones(walletId);
        ArrayList<Miembro> listaDeMiembros = miembroWalletController.obtenerMiembros(walletId);

        // Iniciamos DeudaUtility
        DeudaUtility deudaUtility = new DeudaUtility();
        deudaUtility.sumaTransacciones(listaDeTransacciones, listaDeMiembros);
        Map<Long, Double> resolucionDeuda = deudaUtility.transacionesGastosTotales();

        return resolucionDeuda;
    }

}
