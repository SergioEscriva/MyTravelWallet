package me.spenades.mywallettravel;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.spenades.mywallettravel.adapters.AdaptadorParticipantes;
import me.spenades.mywallettravel.adapters.AdaptadorWallets;
import me.spenades.mywallettravel.controllers.ParticipanteController;
import me.spenades.mywallettravel.controllers.WalletController;
import me.spenades.mywallettravel.models.Participante;
import me.spenades.mywallettravel.models.Wallet;

public class EditarWalletActivity extends AppCompatActivity {
    private List<Wallet> listaDeWallets;
    private AdaptadorWallets adaptadorWallets;
    private List<Participante> listaDeParticipantes;
    private AdaptadorParticipantes adaptadorParticipantes;
    private WalletController walletController;
    private Wallet wallet;
    private ParticipanteController participanteController;
    private RecyclerView recyclerViewParticipantes;
    private Button btnGuardarCambios, btnAgregarParticipante, btnEliminarWallet;
    private EditText etNombre, etDescripcion, etPropietarioId, etWalletId, etAddParticipante;
    private CheckBox checkBoxCompartir;
    private FloatingActionButton btnCancelarEdicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet);

        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }

        // Instanciar el controlador
        walletController = new WalletController(EditarWalletActivity.this);
        participanteController = new ParticipanteController(EditarWalletActivity.this);

        // Recuperamos datos
        String nombreUsuario = extras.getString("nombreUsuario");
        int usuarioId= extras.getInt("usuarioId");
        long walletId = extras.getLong("walletId");
        String nombreWallet = extras.getString("nombreWallet");
        String descripcion = extras.getString("descripcion");
        long propietario = extras.getLong("propietarioId");
        int compartir = extras.getInt("checkCompartir");
        wallet = new Wallet(nombreWallet, descripcion, propietario, compartir, walletId);

        // Ahora declaramos las vistas
        recyclerViewParticipantes = findViewById(R.id.recyclerViewParticipantes) ;
        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPropietarioId = findViewById(R.id.etPropietarioId);
        etAddParticipante = findViewById(R.id.etAddParticipante);
        etWalletId = findViewById(R.id.etWalletId);
        CheckBox checkBoxCompartir = findViewById(R.id.checkBox_Compartir);
        btnGuardarCambios = findViewById(R.id.btn_agregar_wallet);
        btnCancelarEdicion = findViewById(R.id.btn_cancelar_nuevo_wallet);
        btnAgregarParticipante = findViewById(R.id. btnAgregarParticipante);
        btnEliminarWallet = findViewById(R.id. btnEliminarWallet);
        etPropietarioId.setVisibility(View.INVISIBLE);


        // Hacer visible las vistas de botones y lista participantes al compartir activiy con agregar
        btnGuardarCambios.setVisibility(View.VISIBLE);
        recyclerViewParticipantes.setVisibility(View.VISIBLE);
        etAddParticipante.setVisibility(View.VISIBLE);
        btnAgregarParticipante.setVisibility(View.VISIBLE);
        btnEliminarWallet.setVisibility(View.VISIBLE);

        // Rellenar los EditText de la pantalla
        etNombre.setText(wallet.getNombre());
        etDescripcion.setText(wallet.getDescripcion());
        etPropietarioId.setText(String.valueOf(propietario));
        etWalletId.setText(String.valueOf(walletId));
        Boolean compartirCheck = (compartir == 0) ? false : true;
        checkBoxCompartir.setChecked(compartirCheck);

        // Por defecto es una lista vacía,
        listaDeParticipantes = new ArrayList<>();
        adaptadorParticipantes = new AdaptadorParticipantes(listaDeParticipantes);

        listaDeWallets = new ArrayList<>();
        adaptadorWallets = new AdaptadorWallets(listaDeWallets);
        adaptadorWallets.setListaDeWallets(listaDeWallets);
        adaptadorWallets.notifyDataSetChanged();
        //refrescarListaDeWallets();



        // se la ponemos al adaptador y configuramos el recyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewParticipantes.setLayoutManager(mLayoutManager);
        recyclerViewParticipantes.setItemAnimator(new DefaultItemAnimator());
        recyclerViewParticipantes.setAdapter(adaptadorParticipantes);
        System.out.println("EditarWalletActi " + recyclerViewParticipantes);
        // Una vez que ya configuramos el RecyclerView le ponemos los datos de la BD
        refrescarListaDeParticipantes();

        // Cancelar o Salir de Editar
        btnCancelarEdicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    finish();
            }
        });

        // Listener del click del botón que guarda cambios Wallet
        btnGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Resetear errores
                etNombre.setError(null);
                etDescripcion.setError(null);
                etPropietarioId.setError(null);
                checkBoxCompartir.setError(null);
                etWalletId.setError(null);

                String nombre = etNombre.getText().toString(),
                        descripcion = etDescripcion.getText().toString(),
                        propietario = etPropietarioId.getText().toString();


                Boolean checkBoxStateCompartir = checkBoxCompartir.isChecked();

                int compartir = (checkBoxStateCompartir) ? 1 : 0;

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
                    etPropietarioId.setError("Escribe Nombre del propietario");
                    etPropietarioId.requestFocus();
                    return;
                }


                // Ya pasó la validación

                Wallet editarWallet = new Wallet(nombre, descripcion, Long.parseLong(propietario), compartir);
                long walletIdGuardado = walletController.guardarCambios(editarWallet);
                etWalletId.setText(String.valueOf(walletIdGuardado));
                if (walletIdGuardado == -1) {
                    // De alguna manera ocurrió un error
                    Toast.makeText(EditarWalletActivity.this, "Error al guardar. Intenta de nuevo", Toast.LENGTH_SHORT).show();

                } else {
                    refrescarListaDeParticipantes();
                    //finish();
                }
            }
        });
        // Eliminar Wallet siempre que estén saldadas las cuentas.
        btnEliminarWallet.setOnClickListener(new View.OnClickListener() {
            @Override // Un toque Eliminamos Wallet
            public void onClick(View view) {

                // Refrescamos la lista, Recuperamos WalletId y lo añadimos a Eliminar
                refrescarListaDeWallets();
                long walletId = extras.getLong("walletId");
                String nombreWallet = extras.getString("nombreWallet");
                //Wallet walletParaEliminar = listaDeWallets.get((int) walletId);

                AlertDialog dialog = new AlertDialog
                        .Builder(EditarWalletActivity.this)
                        .setPositiveButton("Sí, eliminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                walletController.eliminarWallet(walletId);
                                finish();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setTitle("Confirmar")
                        .setMessage("¿Eliminar el Wallet " + nombreWallet + "?")
                        .create();
                        refrescarListaDeWallets();
                dialog.show();
            }
        });


            // Listener botón agregar participante y a su vez como usuario
        btnAgregarParticipante.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Resetear errores
                    etAddParticipante.setError(null);
                    // Recuperar datos
                    String nuevoParticipante = etAddParticipante.getText().toString();
                    //long walletId = Long.parseLong(etWalletId.getText().toString()+1);

                    // Listamos participantes del Wallet
                    participanteController.obtenerParticipantes(walletId);
                    listaDeParticipantes = participanteController.obtenerParticipantes(walletId);

                    refrescarListaDeParticipantes();

                    if ("".equals(nuevoParticipante)) {
                        etAddParticipante.setError("Escribe tu Nombre");
                        etAddParticipante.requestFocus();
                        return;
                    }

                    // Agregamos Participante y Refrescamos, la nueva transacción nos devuelve el id del nuevo participante.
                    Participante nuevoParticipanteGuardar = new Participante(walletId,String.valueOf(nuevoParticipante));
                    long id = participanteController.nuevoParticipante(nuevoParticipanteGuardar);
                    if (id == -1) {
                        // Participante error
                        refrescarListaDeParticipantes();
                        Toast.makeText(EditarWalletActivity.this, "Error al guardar. Intenta de nuevo", Toast.LENGTH_SHORT).show();

                    } else {
                        refrescarListaDeParticipantes();
                        etAddParticipante.setText("");

                        Toast.makeText(EditarWalletActivity.this, "Participante añadido", Toast.LENGTH_SHORT).show();

                    }

                   // agregarParticipante();
                }
            });

    }

    public void refrescarListaDeParticipantes() {
        if (adaptadorParticipantes == null) return;
        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        // Si no hay datos (cosa rara) salimos
        if (extras == null) {
            finish();
            return;
        }

        // Recuperamos WalletId Activo
        int walletIdSelected = extras.getInt("walletId");
        String walletNameSelected = extras.getString("walletName");

        listaDeParticipantes = participanteController.obtenerParticipantes(walletIdSelected);
        adaptadorParticipantes.setListaDeParticipantes(listaDeParticipantes);
        adaptadorParticipantes.notifyDataSetChanged();
    }

    public void refrescarListaDeWallets(){
        listaDeWallets = walletController.obtenerWallets();
        adaptadorWallets.setListaDeWallets(listaDeWallets);
        adaptadorWallets.notifyDataSetChanged();
    }
}
