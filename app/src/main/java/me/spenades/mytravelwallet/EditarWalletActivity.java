package me.spenades.mytravelwallet;

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
import java.util.Map;

import me.spenades.mytravelwallet.adapters.ParticipantesAdapters;
import me.spenades.mytravelwallet.adapters.WalletsAdapters;
import me.spenades.mytravelwallet.controllers.ParticipanteController;
import me.spenades.mytravelwallet.controllers.UsuarioController;
import me.spenades.mytravelwallet.controllers.WalletController;
import me.spenades.mytravelwallet.models.Participante;
import me.spenades.mytravelwallet.models.Usuario;
import me.spenades.mytravelwallet.models.Wallet;


public class EditarWalletActivity extends AppCompatActivity {

    public ParticipanteController participanteController;
    public UsuarioController usuarioController;
    private List<Wallet> listaDeWallets;
    private ArrayList<Map> listaDeImportes;
    private WalletsAdapters walletsAdapters;
    private List<Participante> listaDeParticipantes;
    private ParticipantesAdapters participantesAdapters;
    private WalletController walletController;
    private Wallet wallet;
    private RecyclerView recyclerViewParticipantes;
    private Button btnGuardarCambios, btnAgregarParticipante, btnEliminarWallet;
    private EditText etNombre, etDescripcion, etPropietarioId, etWalletId, etAddParticipante;
    private FloatingActionButton btnCancelarEdicion;
    private long walletId;
    private String nombreWallet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet);

        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        this.walletId = Long.parseLong(extras.getString("walletId"));
        this.nombreWallet = extras.getString("nombreWallet");

        if (extras == null) {
            finish();
            return;
        }

        // Instanciar el controlador
        walletController = new WalletController(EditarWalletActivity.this);
        participanteController = new ParticipanteController(EditarWalletActivity.this);
        usuarioController = new UsuarioController(EditarWalletActivity.this);

        // Recuperamos datos
        String descripcion = extras.getString("descripcion");
        String propietario = extras.getString("propietarioId");
        String compartir = extras.getString("checkCompartir");
        wallet = new Wallet(nombreWallet, descripcion, Integer.parseInt(propietario),
                Integer.parseInt(compartir), walletId);

        // Ahora declaramos las vistas
        recyclerViewParticipantes = findViewById(R.id.recyclerViewParticipantes);
        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPropietarioId = findViewById(R.id.etPropietarioId);
        etAddParticipante = findViewById(R.id.etAddParticipante);
        etWalletId = findViewById(R.id.etWalletId);
        CheckBox checkBoxCompartir = findViewById(R.id.checkBox_Compartir);
        btnGuardarCambios = findViewById(R.id.btn_agregar_wallet);
        btnCancelarEdicion = findViewById(R.id.btn_cancelar_nuevo_wallet);
        btnAgregarParticipante = findViewById(R.id.btnAgregarParticipante);
        btnEliminarWallet = findViewById(R.id.btnEliminarWallet);
        etPropietarioId.setVisibility(View.INVISIBLE);


        // Hacer visible las vistas de botones y lista participantes al compartir activiy con
        // agregar
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

        Boolean compartirCheck = (compartir == "0") ? false : true;
        checkBoxCompartir.setChecked(compartirCheck);

        // Por defecto es una lista vacía,
        listaDeParticipantes = new ArrayList<>();
        participantesAdapters = new ParticipantesAdapters(listaDeParticipantes);

        listaDeWallets = new ArrayList<>();
        listaDeImportes = new ArrayList<>();
        walletsAdapters = new WalletsAdapters(listaDeWallets);
        walletsAdapters.setListaDeWallets(listaDeWallets, listaDeImportes);
        walletsAdapters.notifyDataSetChanged();

        // se la ponemos al adaptador y configuramos el recyclerView
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());
        recyclerViewParticipantes.setLayoutManager(mLayoutManager);
        recyclerViewParticipantes.setItemAnimator(new DefaultItemAnimator());
        recyclerViewParticipantes.setAdapter(participantesAdapters);

        // Cancelar o Salir de Editar
        btnCancelarEdicion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
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
                Wallet editarWallet = new Wallet(nombre, descripcion, Long.parseLong(propietario)
                        , compartir, walletId);
                long walletIdGuardado = walletController.guardarCambios(editarWallet);
                etWalletId.setText(String.valueOf(walletIdGuardado));
                walletId = walletIdGuardado;
                if (walletIdGuardado == - 1) {

                    // De alguna manera ocurrió un error
                    Toast.makeText(EditarWalletActivity.this, "Error al guardar. Intenta de " +
                            "nuevo", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(EditarWalletActivity.this, "Guardado Wallet.",
                            Toast.LENGTH_SHORT).show();
                    refrescarListaDeWallets();
                }
            }
        });

        // Eliminar Wallet siempre que estén saldadas las cuentas. TODO
        btnEliminarWallet.setOnClickListener(new View.OnClickListener() {

            @Override // Un toque Eliminamos Wallet
            public void onClick(View view) {

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


                if ("".equals(nuevoParticipante)) {
                    etAddParticipante.setError("Escribe tu Nombre");
                    etAddParticipante.requestFocus();
                    return;
                }

                // Agregamos Participante y Refrescamos, la nueva transacción nos devuelve el id
                // del nuevo participante.
                long id = agregarParticipante(walletId, nuevoParticipante);
                //long id = usuarioUtility.nuevoParticipante(walletId, nuevoParticipante);
                if (id == - 1) {
                    // Participante error
                    Toast.makeText(EditarWalletActivity.this, "Error al guardar. Intenta de " +
                            "nuevo", Toast.LENGTH_SHORT).show();

                } else {

                    etAddParticipante.setText("");
                    Toast.makeText(EditarWalletActivity.this, "Participante añadido",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        refrescarListaDeParticipantes();
    }


    public void refrescarListaDeParticipantes() {
        if (participantesAdapters == null) return;
        listaDeParticipantes = participanteController.obtenerParticipantes(walletId);
        participantesAdapters.setListaDeParticipantes(listaDeParticipantes);
        participantesAdapters.notifyDataSetChanged();

    }


    public void refrescarListaDeWallets() {
        listaDeWallets = walletController.obtenerWallets();
        listaDeImportes = walletController.obtenerWalletsImporte();
        walletsAdapters.setListaDeWallets(listaDeWallets, listaDeImportes);
        walletsAdapters.notifyDataSetChanged();
    }


    //TODO esto debe pasar a una clase, pero no me funciona.
    public long agregarParticipante(long walletId, String nuevoParticipante) {
        ArrayList<Usuario> usuarios;

        // Busca el usuario y devuelve su ID, si es 0 es que no está
        Usuario usuarioNuevo = new Usuario(nuevoParticipante, nuevoParticipante);
        usuarios = usuarioController.obtenerUsuarioId(nuevoParticipante);

        // Si la lista devuelta es 0, usuario no existe
        long usuarioExiste = usuarios.size();

        // Si No existe lo agregamos como usuario, y recuperamos su nuevo Id.
        long usuarioIdDb = 0;
        if (usuarioExiste == 0) {
            // Añadimos Usuario
            long usuarioRevision = usuarioController.nuevoUsuario(usuarioNuevo);
            // Ya tenemos el ID del nuevo usuario
            usuarioIdDb = usuarioRevision;
        } else {
            // Si existe, recuperamos Variable con el Id del usuario Existente
            usuarioIdDb = usuarios.get(0).getId();
        }
        //Formateamos variables Para Participant
        Participante nuevoParticipanteGuardar = new Participante(walletId, usuarioIdDb,
                nuevoParticipante);
        // Ahora lo añadimos como Participante, aquí existe como usuario seguro.
        long agregarParticipante =
                participanteController.nuevoParticipante(nuevoParticipanteGuardar);
        refrescarListaDeParticipantes();
        return agregarParticipante;
    }
}
