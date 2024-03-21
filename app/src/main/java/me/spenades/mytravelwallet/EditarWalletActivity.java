package me.spenades.mytravelwallet;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.spenades.mytravelwallet.adapters.MiembrosAdapters;
import me.spenades.mytravelwallet.adapters.WalletsAdapters;
import me.spenades.mytravelwallet.controllers.MiembroController;
import me.spenades.mytravelwallet.controllers.ParticipanController;
import me.spenades.mytravelwallet.controllers.TransaccionController;
import me.spenades.mytravelwallet.controllers.UsuarioController;
import me.spenades.mytravelwallet.controllers.WalletController;
import me.spenades.mytravelwallet.models.Miembro;
import me.spenades.mytravelwallet.models.Transaccion;
import me.spenades.mytravelwallet.models.Usuario;
import me.spenades.mytravelwallet.models.Wallet;
import me.spenades.mytravelwallet.utilities.DeudaUtility;
import me.spenades.mytravelwallet.utilities.RecyclerTouchListener;


public class EditarWalletActivity extends AppCompatActivity {

    public MiembroController miembroController;
    public UsuarioController usuarioController;
    public TransaccionController transaccionController;
    public ParticipanController participanController;
    private List<Wallet> listaDeWallets;
    private ArrayList<Map> listaDeImportes;
    private WalletsAdapters walletsAdapters;
    private List<Miembro> listaDeMiembros;
    private MiembrosAdapters miembrosAdapters;
    private WalletController walletController;
    private Wallet wallet;
    private RecyclerView recyclerViewMiembros;
    private Button btnGuardarCambios, btnAgregarMiembro, btnEliminarWallet;
    private EditText etNombre, etDescripcion, etPropietarioId, etWalletId, etAddMiembro;
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
        boolean agregar = extras.getBoolean("agregar");
        System.out.println(agregar);

        if (extras == null) {
            finish();
            return;
        }

        // Instanciar el controlador
        walletController = new WalletController(EditarWalletActivity.this);
        miembroController = new MiembroController(EditarWalletActivity.this);
        usuarioController = new UsuarioController(EditarWalletActivity.this);
        transaccionController = new TransaccionController(this);
        participanController = new ParticipanController(this);

        // Recuperamos datos
        String descripcion = extras.getString("descripcion");
        String propietario = extras.getString("propietarioId");
        String compartir = extras.getString("checkCompartir");
        wallet = new Wallet(nombreWallet, descripcion, Integer.parseInt(propietario),
                Integer.parseInt(compartir), walletId);

        // Ahora declaramos las vistas
        recyclerViewMiembros = findViewById(R.id.recyclerViewMiembros);
        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPropietarioId = findViewById(R.id.etPropietarioId);
        etAddMiembro = findViewById(R.id.etAddMiembro);
        etWalletId = findViewById(R.id.etWalletId);
        CheckBox checkBoxCompartir = findViewById(R.id.checkBox_Compartir);
        btnGuardarCambios = findViewById(R.id.btn_agregar_wallet);
        btnCancelarEdicion = findViewById(R.id.btn_cancelar_nuevo_wallet);
        btnAgregarMiembro = findViewById(R.id.btnAgregarMiembro);
        btnEliminarWallet = findViewById(R.id.btnEliminarWallet);
        etPropietarioId.setVisibility(View.INVISIBLE);


        // Hacer visible las vistas de botones y lista miembros al compartir activiy con
        // agregar
        btnGuardarCambios.setVisibility(View.VISIBLE);
        recyclerViewMiembros.setVisibility(View.VISIBLE);
        etAddMiembro.setVisibility(View.VISIBLE);
        btnAgregarMiembro.setVisibility(View.VISIBLE);
        btnEliminarWallet.setVisibility(View.VISIBLE);
        // Si venimos de Agregar Wallet, eliminamos los botones de guardar y eliminar Wallet.
        if (agregar == true) {
            btnEliminarWallet.setVisibility(View.INVISIBLE);
            btnGuardarCambios.setVisibility(View.INVISIBLE);
        }


        // Rellenar los EditText de la pantalla
        etNombre.setText(wallet.getNombre());
        etDescripcion.setText(wallet.getDescripcion());
        etPropietarioId.setText(String.valueOf(propietario));
        etWalletId.setText(String.valueOf(walletId));

        Boolean compartirCheck = (compartir == "0") ? false : true;
        checkBoxCompartir.setChecked(compartirCheck);

        // Por defecto es una lista vacía,
        listaDeMiembros = new ArrayList<>();
        miembrosAdapters = new MiembrosAdapters(listaDeMiembros);

        listaDeWallets = new ArrayList<>();
        listaDeImportes = new ArrayList<>();
        walletsAdapters = new WalletsAdapters(listaDeWallets);
        walletsAdapters.setListaDeWallets(listaDeWallets, listaDeImportes);
        walletsAdapters.notifyDataSetChanged();

        // se la ponemos al adaptador y configuramos el recyclerView
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());
        recyclerViewMiembros.setLayoutManager(mLayoutManager);
        recyclerViewMiembros.setItemAnimator(new DefaultItemAnimator());
        recyclerViewMiembros.setAdapter(miembrosAdapters);

        borrarParticipante(1L); //TODO ///////////////////////

        // Cancelar o Salir de Editar
        btnCancelarEdicion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditarWalletActivity.this,
                        MainActivity.class);
                startActivity(intent);
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


        // Eliminar Wallet siempre que estén saldadas las cuentas.
        btnEliminarWallet.setOnClickListener(new View.OnClickListener() {

            @Override // Un toque Eliminamos Wallet
            public void onClick(View view) {
                ArrayList<Map> importe = walletController.obtenerWalletsImporte();

                // Iteramos sobre la lista de importes la pasamos a String y luego a Double.
                double importeWallet = Double.valueOf(importe.iterator().next().get(walletId).toString());
                if (importeWallet > 0) {
                    AlertDialog dialog = new AlertDialog
                            .Builder(EditarWalletActivity.this)
                            .setPositiveButton("Resolver Deuda", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Simplemente cambiamos de actividad
                                    Intent intent = new Intent(EditarWalletActivity.this,
                                            ResolverDeudaActivity.class);
                                    intent.putExtra("walletId", walletId);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setTitle("Borrar")
                            .setMessage("El Wallet " + nombreWallet + " solo se puede eliminar \n si las dedudas están RESUELTAS.")
                            .create();
                    refrescarListaDeWallets();
                    dialog.show();


                } else {
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
            }
        });


        // Listener botón agregar miembro y a su vez como usuario
        btnAgregarMiembro.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Resetear errores
                etAddMiembro.setError(null);

                // Recuperar datos
                String nuevoMiembro = etAddMiembro.getText().toString();


                if ("".equals(nuevoMiembro)) {
                    etAddMiembro.setError("Escribe tu Nombre");
                    etAddMiembro.requestFocus();
                    return;
                }

                // Agregamos Miembro y Refrescamos, la nueva transacción nos devuelve el id
                // del nuevo miembro.
                long id = agregarMiembro(walletId, nuevoMiembro);
                //long id = usuarioUtility.nuevoMiembro(walletId, nuevoMiembro);
                if (id == - 1) {
                    // Miembro error
                    Toast.makeText(EditarWalletActivity.this, "Error al guardar. Intenta de " +
                            "nuevo", Toast.LENGTH_SHORT).show();

                } else {

                    etAddMiembro.setText("");
                    Toast.makeText(EditarWalletActivity.this, "Miembro añadido",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerViewMiembros.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewMiembros,
                new RecyclerTouchListener.ClickListener() {

                    @Override
                    public void onClick(View view, int position) {

                    }


                    // Eliminamos Miembro si no tiene deudas
                    // TODO faltaría comprobar si el miembro tiene deudas.
                    @Override
                    public void onLongClick(View view, int position) {
                        Miembro miembro = listaDeMiembros.get(position);

                        // Si el miembro tiene deudas se insta a saldarlas
                        // TODO fatlta implementar comprobar si hay deudas pendientes.
                        // por el momento, si se se está creando por primera vez el Wallet, si deja borrar, no en Editar.
                        if (agregar == false) {
                            AlertDialog dialog = new AlertDialog
                                    .Builder(EditarWalletActivity.this)
                                    .setPositiveButton("Resolver Deudas", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Intent intent = new Intent(EditarWalletActivity.this,
                                                    ResolverDeudaActivity.class);
                                            intent.putExtra("walletId", String.valueOf(walletId));
                                            intent.putExtra("info", 1);
                                            startActivity(intent);

                                        }
                                    })
                                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setTitle("Borrar Miembro")
                                    .setMessage("El Miembro " + miembro.getNombre() + ", " + "\ndel Wallet " + nombreWallet + "\nsolo se puede " +
                                            "eliminar " +
                                            "\nsi tiene sus deudas RESUELTAS.")
                                    .create();
                            refrescarListaDeWallets();
                            dialog.show();

                            // Si no tiene deudas se puede borrar el Miembro
                        } else {
                            AlertDialog dialog = new AlertDialog
                                    .Builder(EditarWalletActivity.this)
                                    .setPositiveButton("Sí, eliminar", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            participanController.eliminarMiembro(miembro);
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
                                    .setMessage("¿Eliminar el Miembro " + miembro.getNombre() + "?")
                                    .create();
                            refrescarListaDeWallets();
                            dialog.show();
                        }

                    }
                }) {

            @Override
            public void onClick(View view, int position) {

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        refrescarListaDeMiembros();
    }


    public void refrescarListaDeMiembros() {
        if (miembrosAdapters == null) return;
        listaDeMiembros = miembroController.obtenerMiembros(walletId);
        miembrosAdapters.setListaDeMiembros(listaDeMiembros);
        miembrosAdapters.notifyDataSetChanged();

    }


    public void refrescarListaDeWallets() {
        listaDeWallets = walletController.obtenerWallets();
        listaDeImportes = walletController.obtenerWalletsImporte();
        walletsAdapters.setListaDeWallets(listaDeWallets, listaDeImportes);
        walletsAdapters.notifyDataSetChanged();
    }


    //TODO esto debe pasar a una clase, pero no me funciona.
    public long agregarMiembro(long walletId, String nuevoMiembro) {
        ArrayList<Usuario> usuarios;

        // Busca el usuario y devuelve su ID, si es 0 es que no está
        Usuario usuarioNuevo = new Usuario(nuevoMiembro, nuevoMiembro);
        usuarios = usuarioController.obtenerUsuarioId(nuevoMiembro);

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
        Miembro nuevoMiembroGuardar = new Miembro(walletId, usuarioIdDb,
                nuevoMiembro);
        // Ahora lo añadimos como Miembro, aquí existe como usuario seguro.
        long agregarMiembro =
                miembroController.nuevoMiembro(nuevoMiembroGuardar);
        refrescarListaDeMiembros();
        return agregarMiembro;
    }


    public void borrarParticipante(Long walletId) {

        ArrayList<Transaccion> listaDeTransacciones = transaccionController.obtenerTransacciones(walletId);
        ArrayList<Miembro> listaDeMiembros = miembroController.obtenerMiembros(walletId);
        DeudaUtility deudaUtility = new DeudaUtility();
        // Iniciamos DeudaUtility
        deudaUtility.sumaTransacciones(listaDeTransacciones, listaDeMiembros);
        Map<Long, Double> gastoTotalpagador = deudaUtility.transacionesGastosTotales();
        deudaUtility.transacionesGastosTotales();
        System.out.println("Mas coas " + gastoTotalpagador);
    }

}
