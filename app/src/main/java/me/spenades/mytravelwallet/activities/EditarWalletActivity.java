package me.spenades.mytravelwallet.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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

import me.spenades.mytravelwallet.MainActivity;
import me.spenades.mytravelwallet.R;
import me.spenades.mytravelwallet.adapters.MiembrosAdapters;
import me.spenades.mytravelwallet.adapters.WalletsAdapters;
import me.spenades.mytravelwallet.ayuda.EditarWalletAyuda;
import me.spenades.mytravelwallet.controllers.AyudaAppController;
import me.spenades.mytravelwallet.controllers.MiembroWalletController;
import me.spenades.mytravelwallet.controllers.ParticipaTransaccionController;
import me.spenades.mytravelwallet.controllers.TransaccionController;
import me.spenades.mytravelwallet.controllers.UsuarioAppController;
import me.spenades.mytravelwallet.controllers.WalletController;
import me.spenades.mytravelwallet.models.Ayuda;
import me.spenades.mytravelwallet.models.Miembro;
import me.spenades.mytravelwallet.models.Transaccion;
import me.spenades.mytravelwallet.models.Usuario;
import me.spenades.mytravelwallet.models.Wallet;
import me.spenades.mytravelwallet.utilities.DeudaUtility;
import me.spenades.mytravelwallet.utilities.RecyclerTouchListener;


public class EditarWalletActivity extends AppCompatActivity {

    public MiembroWalletController miembroWalletController;
    public UsuarioAppController usuarioAppController;
    public TransaccionController transaccionController;
    public ParticipaTransaccionController participaTransaccionController;
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
    private CheckBox cbCompartir;
    private FloatingActionButton btnCancelarEdicion;
    private long walletId;
    private String nombreWallet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        this.walletId = Long.parseLong(extras.getString("walletId"));
        this.nombreWallet = extras.getString("nombreWallet");
        boolean agregar = extras.getBoolean("agregar");

        if (extras == null) {

            return;
        }

        // Instanciar el controlador
        walletController = new WalletController(EditarWalletActivity.this);
        miembroWalletController = new MiembroWalletController(EditarWalletActivity.this);
        usuarioAppController = new UsuarioAppController(EditarWalletActivity.this);
        transaccionController = new TransaccionController(this);
        participaTransaccionController = new ParticipaTransaccionController(this);

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
        cbCompartir = findViewById(R.id.cbCompartir);
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
        if (agregar) {
            btnEliminarWallet.setVisibility(View.INVISIBLE);
            btnGuardarCambios.setVisibility(View.INVISIBLE);
        }

        // Rellenar los EditText de la pantalla
        etNombre.setText(wallet.getNombre());
        etDescripcion.setText(wallet.getDescripcion());
        etPropietarioId.setText(propietario);
        etWalletId.setText(String.valueOf(walletId));
        int compartirWallet = wallet.getCompartir();
        boolean cbCompartirResultado = compartirWallet == 1;
        cbCompartir.setChecked(cbCompartirResultado);
        /*
        System.out.println(cbCompartirResultado);
        Boolean compartirCheck = (compartir == "0") ? false : true;
        checkBoxCompartir.setChecked(compartirCheck);


         */


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
        ayuda();

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
                cbCompartir.setError(null);
                etWalletId.setError(null);

                String nombre = etNombre.getText().toString(),
                        descripcion = etDescripcion.getText().toString(),
                        propietario = etPropietarioId.getText().toString();


                Boolean checkBoxStateCompartir = cbCompartir.isChecked();

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
        // Listener de los Miembros.
        recyclerViewMiembros.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewMiembros,
                new RecyclerTouchListener.ClickListener() {

                    @Override
                    public void onClick(View view, int position) {
                        Miembro miembro = listaDeMiembros.get(position);
                        cambioDeNombre(miembro);
                    }

                    // Eliminamos Miembro si no tiene deudas
                    @Override
                    public void onLongClick(View view, int position) {
                        Miembro miembro = listaDeMiembros.get(position);

                        // Si el miembro tiene deudas se insta a saldarlas
                        AlertDialog dialog = new AlertDialog
                                .Builder(EditarWalletActivity.this)
                                .setPositiveButton("Sí, eliminar", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        borrarParticipante(miembro);
                                        //finish();
                                    }
                                })
                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setTitle("Confirmar")
                                .setMessage("¿Seguro que deseas ELIMINAR a " + miembro.getNombre() + "\n del Wallet " + wallet.getNombre() + "?")
                                .create();
                        refrescarListaDeWallets();
                        dialog.show();
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
        listaDeMiembros = miembroWalletController.obtenerMiembros(walletId);
        miembrosAdapters.setListaDeMiembros(listaDeMiembros);
        miembrosAdapters.notifyDataSetChanged();

    }


    public void refrescarListaDeWallets() {
        listaDeWallets = walletController.obtenerWallets();
        listaDeImportes = walletController.obtenerWalletsImporte();
        walletsAdapters.setListaDeWallets(listaDeWallets, listaDeImportes);
        walletsAdapters.notifyDataSetChanged();
    }


    public long agregarMiembro(long walletId, String nuevoMiembro) {
        ArrayList<Usuario> usuarios;

        // Busca el usuario y devuelve su ID, si es 0 es que no está
        Usuario usuarioNuevo = new Usuario(nuevoMiembro, nuevoMiembro);
        usuarios = usuarioAppController.obtenerUsuarioId(nuevoMiembro);

        // Si la lista devuelta es 0, usuario no existe
        long usuarioExiste = usuarios.size();

        // Si No existe lo agregamos como usuario, y recuperamos su nuevo Id.
        long usuarioIdDb = 0;
        if (usuarioExiste == 0) {
            // Añadimos Usuario
            long usuarioRevision = usuarioAppController.nuevoUsuario(usuarioNuevo);
            // Ya tenemos el ID del nuevo usuario
            usuarioIdDb = usuarioRevision;
        } else {
            // Si existe, recuperamos Variable con el Id del usuario Existente
            usuarioIdDb = usuarios.get(0).getId();
        }
        //Formateamos variables Para Participante
        Miembro nuevoMiembroGuardar = new Miembro(walletId, usuarioIdDb,
                nuevoMiembro);
        // Ahora lo añadimos como Miembro, aquí existe como usuario seguro.
        long agregarMiembro =
                miembroWalletController.nuevoMiembro(nuevoMiembroGuardar);
        refrescarListaDeMiembros();
        return agregarMiembro;
    }

    // Comprueba que el miembro no tiene deudas que saldar ni con él mismo de otros y lo borra.
    public void borrarParticipante(Miembro miembro) {
        long miembroId = miembro.getUserId();
        ArrayList<Transaccion> listaDeTransacciones = transaccionController.obtenerTransacciones(walletId);
        ArrayList<Miembro> listaDeMiembros = miembroWalletController.obtenerMiembros(walletId);

        // Iniciamos DeudaUtility
        DeudaUtility deudaUtility = new DeudaUtility();
        deudaUtility.sumaTransacciones(listaDeTransacciones, listaDeMiembros);
        Map<Long, Double> resolucionDeuda = deudaUtility.transacionesGastosTotales();

        // Borramos si coincide el id y tenga importe 0
        Double importe = resolucionDeuda.get(miembroId);
        if (importe == 0.0 && listaDeMiembros.size() > 2) {
            listaDeMiembros.remove(miembro);
            miembrosAdapters.setListaDeMiembros(listaDeMiembros);
            miembrosAdapters.notifyDataSetChanged();
            miembroWalletController.eliminarMiembro(miembro);

        } else if (listaDeMiembros.size() <= 1) {
            String tienePagos = "¿El miembro " + miembro.getNombre() + "\n no puede Eliminarse porque es el último Moicano del Wallet " + wallet.getNombre() + "?";
            alertDialogoTieneDeudas(tienePagos);
        } else {
            String tienePagos = "¿El miembro " + miembro.getNombre() + "\n no puede Eliminarse porque ha realizado pagos en el Wallet " + wallet.getNombre() + "?";
            alertDialogoTieneDeudas(tienePagos);
        }
    }

    public void alertDialogoTieneDeudas(String tienePagos) {
        // Si el miembro tiene movimientos de pagos no se podrá eliminar.
        AlertDialog dialog = new AlertDialog
                .Builder(EditarWalletActivity.this)

                .setNegativeButton("Entendido", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setTitle("No se puede ELIMINAR")
                .setMessage(tienePagos)
                .create();
        refrescarListaDeWallets();
        dialog.show();
    }

    // Creamos una ventana para escibir el nuevo nombre del miembro.
    //https://stackoverflow.com/questions/10903754/input-text-dialog-android
    public void cambioDeNombre(Miembro miembro) {
        final EditText etNombre = new EditText(this);

        // Nombre a mostrar de forma predeterminada.
        etNombre.setHint(miembro.getNombre());
        etNombre.requestFocus();
        etNombre.selectAll();

        new AlertDialog.Builder(this)
                .setTitle("Editar Nombre Miembro")
                .setMessage("Escribe el nuevo Nombre")
                .setView(etNombre)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String nombreNuevo = etNombre.getText().toString();
                        // Si devuelve nombre vacio se pone el anterior.
                        if (nombreNuevo.equals("")) {
                            nombreNuevo = miembro.getNombre();
                        }

                        // Formateamos el nuevo nombre
                        listaDeMiembros.remove(miembro);
                        Usuario usuarioEditado = new Usuario(miembro.getUserId());
                        usuarioEditado.setNombre(nombreNuevo);
                        usuarioEditado.setApodo(nombreNuevo);
                        Miembro miembroEditado = new Miembro(nombreNuevo, miembro.getUserId());
                        listaDeMiembros.add(miembroEditado);
                        usuarioAppController.guardarCambios(usuarioEditado);
                        miembrosAdapters.setListaDeMiembros(listaDeMiembros);
                        miembrosAdapters.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    public void ayuda() {
        // Abrir ayuda o no según su visualización previa.
        AyudaAppController ayudaAppController;
        ayudaAppController = new AyudaAppController(EditarWalletActivity.this);
        ArrayList<Ayuda> ayuda = ayudaAppController.obtenerAyuda();
        Ayuda ayudas = ayuda.get(2);

        // Recuperamos de la DB si se ha accedido ya a la ayuda o no.
        if (ayudas.getAyuda() == 1) {
            // Introducimos valor 0 para que no se muestre la ayuda la próxima vez.
            Ayuda ayudasModificado = new Ayuda(0, ayudas.getAyudaNombre(), ayudas.getId());
            ayudaAppController.modificarAyuda(ayudasModificado);
            // Pasar a la actividad
            Intent intent = new Intent(EditarWalletActivity.this,
                    EditarWalletAyuda.class);
            startActivity(intent);
        }
    }

}