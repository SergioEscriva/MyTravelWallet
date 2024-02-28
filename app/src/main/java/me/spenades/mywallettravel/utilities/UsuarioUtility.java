package me.spenades.mywallettravel.utilities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import me.spenades.mywallettravel.AgregarWalletActivity;
import me.spenades.mywallettravel.EditarWalletActivity;
import me.spenades.mywallettravel.ListarTransaccionesActivity;
import me.spenades.mywallettravel.R;
import me.spenades.mywallettravel.RecyclerTouchListener;
import me.spenades.mywallettravel.adapters.AdaptadorWallets;
import me.spenades.mywallettravel.controllers.ParticipanteController;
import me.spenades.mywallettravel.controllers.UsuarioController;
import me.spenades.mywallettravel.controllers.WalletController;
import me.spenades.mywallettravel.models.Participante;
import me.spenades.mywallettravel.models.Usuario;
import me.spenades.mywallettravel.models.Wallet;


public class UsuarioUtility extends AppCompatActivity {
    private List<Wallet> listaDeWallets;

    private RecyclerView recyclerViewWallets, recyclerViewParticipantes;

    private AdaptadorWallets adaptadorWallets;

    private WalletController walletController;
    private UsuarioController usuarioController;
    private ParticipanteController participanteController;

    private FloatingActionButton fabAgregarWallet;

    private Button btnAgregarWallet, btnAgregarUsuario;
    private EditText etAddParticipante;
    //private long walletId;
    private String nombreWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // código es generado automáticamente
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wallets);

        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        long usuarioIdActivo = extras.getInt("usuarioIdActivo");
        String usuarioActivo = extras.getString("usuarioActivo");
        // Si no hay datos (cosa rara) salimos
        if (extras == null) {
            finish();
            return;
        }

        // Definir nuestro controlador
        walletController = new WalletController(UsuarioUtility.this);
        participanteController = new ParticipanteController(UsuarioUtility.this);
        usuarioController = new UsuarioController(UsuarioUtility.this);

        // Instanciar vistas
        recyclerViewWallets = findViewById(R.id.recyclerViewWallets);
        fabAgregarWallet = findViewById(R.id.fabAgregarWallet);

        // Por defecto es una lista vacía,
        // se la ponemos al adaptador y configuramos el recyclerView
        listaDeWallets = new ArrayList<>();
        adaptadorWallets = new AdaptadorWallets(listaDeWallets);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewWallets.setLayoutManager(mLayoutManager);
        recyclerViewWallets.setItemAnimator(new DefaultItemAnimator());
        recyclerViewWallets.setAdapter(adaptadorWallets);

        // Una vez que ya configuramos el RecyclerView le ponemos los datos de la BD
        refrescarListaDeWallets();
        // Listener de los clicks en la lista WALLET.
        recyclerViewWallets.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewWallets, new RecyclerTouchListener.ClickListener() {
            @Override // Un toque Entrar en el Wallet y sus transacciones
            public void onClick(View view, int position) {
                // Pasar a la actividad editarwallet
                final Wallet walletNameActivo = listaDeWallets.get(position);
                String posicion = String.valueOf(position + 1);
                //walletId = Long.parseLong(posicion);
                nombreWallet = walletNameActivo.getNombre();
                Intent intent = new Intent(UsuarioUtility.this, ListarTransaccionesActivity.class);
                intent.putExtra("usuarioActivo", String.valueOf(usuarioActivo));
                intent.putExtra("usuarioIdActivo", String.valueOf(usuarioIdActivo));
                intent.putExtra("walletId", String.valueOf(posicion));
                intent.putExtra("nombreWallet", String.valueOf(nombreWallet));

                startActivity(intent);

            }

            @Override // Un toque Largo Editar
            public void onLongClick(View view, int position) {
                // Pasar a la actividad editarwallet
                final Wallet walletNameActivo = listaDeWallets.get(position);
                String posicion = String.valueOf(position + 1);
                nombreWallet = walletNameActivo.getNombre();
                Intent intent = new Intent(UsuarioUtility.this, EditarWalletActivity.class);
                intent.putExtra("nombreUsuario", String.valueOf(usuarioActivo));
                intent.putExtra("usuarioId", String.valueOf(usuarioIdActivo));
                intent.putExtra("walletId", String.valueOf(posicion));
                intent.putExtra("nombreWallet", String.valueOf(nombreWallet));
                intent.putExtra("descripcion", String.valueOf(walletNameActivo.getDescripcion()));
                intent.putExtra("propietarioId", String.valueOf(walletNameActivo.getPropietarioId()));
                intent.putExtra("checkCompartir", String.valueOf(walletNameActivo.getCompartir()));

                startActivity(intent);

            }

        }));

        // Listener Agregar Wallet Nuevo
        fabAgregarWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simplemente cambiamos de actividad
                Intent intent = new Intent(UsuarioUtility.this, AgregarWalletActivity.class);
                intent.putExtra("usuarioActivo", String.valueOf(usuarioActivo));
                intent.putExtra("usuarioIdActivo", String.valueOf(usuarioIdActivo));
                startActivity(intent);
            }
        });

        // Créditos
        fabAgregarWallet.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(UsuarioUtility.this)
                        .setTitle("Acerca de")
                        .setMessage("Wallet Travel Universae\n\nIcons www.flaticon.com, y plantilla código de www.parzibyte.me")
                        .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogo, int which) {
                                dialogo.dismiss();
                            }
                        })
                        .setPositiveButton("Sitio web", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intentNavegador = new Intent(Intent.ACTION_VIEW, Uri.parse("https://universae.com"));
                                startActivity(intentNavegador);
                            }
                        })
                        .create()
                        .show();
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        refrescarListaDeWallets();
    }

    public void refrescarListaDeWallets() {
        if (adaptadorWallets == null) return;
        listaDeWallets = walletController.obtenerWallets();
        adaptadorWallets.setListaDeWallets(listaDeWallets);
        adaptadorWallets.notifyDataSetChanged();

    }

    public long nuevoParticipante(long walletId, String nuevoParticipante) {
        ArrayList<Usuario> usuarioIdDb = new ArrayList<>();

        //Instanciamos controlador TODO para poner en una clase sóla, pero no me funciona.
        // usuarioController = new UsuarioController(UsuarioUtility.this);
        // participanteController = new ParticipanteController(UsuarioUtility.this);

        // Formateamos nombre
        Usuario nuevoUsuario = new Usuario(nuevoParticipante, nuevoParticipante);


        // Si No existe lo agregamos como usuario, y recuperamos su nuevo Id.
        if (usuarioIdDb == null) {
            // Añadimos Usuario
            long usuarioIdExiste = usuarioController.nuevoUsuario(nuevoUsuario);
            return usuarioIdExiste;
        }

        //Formateamos variables Para Participante
        Participante nuevoParticipanteGuardar = new Participante(walletId, nuevoParticipante);
        // Ahora lo añadimos como Participante, aquí existe como usuario.
        long agregarParticipante = participanteController.nuevoParticipante(nuevoParticipanteGuardar);
        return agregarParticipante;

    }
}

