package me.spenades.mytravelwallet;

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

import me.spenades.mytravelwallet.adapters.WalletsAdapters;
import me.spenades.mytravelwallet.controllers.WalletController;
import me.spenades.mytravelwallet.models.Wallet;


public class ListarWalletsActivity extends AppCompatActivity {

    private List<Wallet> listaDeWallets;

    private RecyclerView recyclerViewWallets, recyclerViewParticipantes;

    private WalletsAdapters walletsAdapters;

    private WalletController walletController;

    private FloatingActionButton fabAgregarWallet;

    private Button btnAgregarWallet, btnAgregarUsuario;
    private EditText etAddParticipante;


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
        walletController = new WalletController(ListarWalletsActivity.this);

        // Instanciar vistas
        recyclerViewWallets = findViewById(R.id.recyclerViewWallets);
        fabAgregarWallet = findViewById(R.id.fabAgregarWallet);


        // Por defecto es una lista vacía,
        // se la ponemos al adaptador y configuramos el recyclerView
        listaDeWallets = new ArrayList<>();
        walletsAdapters = new WalletsAdapters(listaDeWallets);
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());
        recyclerViewWallets.setLayoutManager(mLayoutManager);
        recyclerViewWallets.setItemAnimator(new DefaultItemAnimator());
        recyclerViewWallets.setAdapter(walletsAdapters);

        // Una vez que ya configuramos el RecyclerView le ponemos los datos de la BD
        refrescarListaDeWallets();
        // Listener de los clicks en la lista WALLET.

        recyclerViewWallets.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewWallets, new RecyclerTouchListener.ClickListener() {

            @Override // Un toque Entrar en el Wallet y sus transacciones
            public void onClick(View view, int position) {
                // Pasar a la actividad editarwallet
                final Wallet walletNameActivo = listaDeWallets.get(position);
                String nombreWallet = walletNameActivo.getNombre();
                long walletId = walletNameActivo.getWalletId();
                Intent intent = new Intent(ListarWalletsActivity.this,
                        ListarTransaccionesActivity.class);
                intent.putExtra("usuarioActivo", String.valueOf(usuarioActivo));
                intent.putExtra("usuarioIdActivo", String.valueOf(usuarioIdActivo));
                intent.putExtra("walletId", Long.valueOf(walletId));
                intent.putExtra("nombreWallet", String.valueOf(nombreWallet));

                startActivity(intent);

            }


            @Override // Un toque Largo Editar
            public void onLongClick(View view, int position) {
                // Pasar a la actividad editarwallet
                final Wallet walletNameActivo = listaDeWallets.get(position);
                String nombreWallet = walletNameActivo.getNombre();
                long walletId = walletNameActivo.getWalletId();
                Intent intent = new Intent(ListarWalletsActivity.this, EditarWalletActivity.class);
                intent.putExtra("nombreUsuario", String.valueOf(usuarioActivo));
                intent.putExtra("usuarioId", String.valueOf(usuarioIdActivo));
                intent.putExtra("walletId", String.valueOf(walletId));
                intent.putExtra("nombreWallet", String.valueOf(nombreWallet));
                intent.putExtra("descripcion", String.valueOf(walletNameActivo.getDescripcion()));
                intent.putExtra("propietarioId",
                        String.valueOf(walletNameActivo.getPropietarioId()));
                intent.putExtra("checkCompartir", String.valueOf(walletNameActivo.getCompartir()));
                startActivity(intent);
            }


        }) {

            @Override
            public void onClick(View view, int position) {

            }
        });


        // Listener Agregar Wallet Nuevo
        fabAgregarWallet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Simplemente cambiamos de actividad
                Intent intent = new Intent(ListarWalletsActivity.this, AgregarWalletActivity.class);
                intent.putExtra("usuarioActivo", String.valueOf(usuarioActivo));
                intent.putExtra("usuarioIdActivo", String.valueOf(usuarioIdActivo));
                startActivity(intent);
            }
        });

        // Créditos
        fabAgregarWallet.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(ListarWalletsActivity.this)
                        .setTitle("Acerca de")
                        .setMessage("My Wallet Travel, una aplicación fin proyecto DAM para " +
                                "Universae\n\nIcons www.flaticon.com, y plantilla código de www" +
                                ".parzibyte.me")
                        .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogo, int which) {
                                dialogo.dismiss();
                            }
                        })
                        .setPositiveButton("Sitio web", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intentNavegador = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://universae.com"));
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
        if (walletsAdapters == null) return;
        listaDeWallets = walletController.obtenerWallets();
        walletsAdapters.setListaDeWallets(listaDeWallets);
        walletsAdapters.notifyDataSetChanged();

    }
}

