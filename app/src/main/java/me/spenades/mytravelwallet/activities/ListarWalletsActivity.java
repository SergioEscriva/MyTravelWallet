package me.spenades.mytravelwallet.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.spenades.mytravelwallet.R;
import me.spenades.mytravelwallet.adapters.WalletsAdapters;
import me.spenades.mytravelwallet.controllers.TransaccionController;
import me.spenades.mytravelwallet.controllers.WalletController;
import me.spenades.mytravelwallet.models.Transaccion;
import me.spenades.mytravelwallet.models.Wallet;
import me.spenades.mytravelwallet.utilities.RecyclerTouchListener;


public class ListarWalletsActivity extends AppCompatActivity {

    private List<Wallet> listaDeWallets;
    private ArrayList<Map> listaDeImportes;
    private List<Transaccion> listaDeTransaccionesWalletId;
    private RecyclerView recyclerViewWallets, recyclerViewMiembros;
    private WalletsAdapters walletsAdapters;
    private WalletController walletController;
    private TransaccionController transaccionController;
    private FloatingActionButton fabAgregarWallet;
    private FrameLayout flInfoWallets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // código es generado automáticamente
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wallets);
        //https://stackoverflow.com/questions/582185/how-can-i-disable-landscape-mode-in-android
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        long usuarioIdActivo = extras.getInt("usuarioIdActivo");
        String usuarioActivo = extras.getString("usuarioActivo");
        // Se muestra sólo la primera vez la Ayuda
        int info = extras.getInt("info");
        flInfoWallets = findViewById(R.id.flInfoWallets);
        if (info == 1) flInfoWallets.setVisibility(View.VISIBLE);

        // Si no hay datos (cosa rara) salimos
        if (extras == null) {
            finish();
            return;
        }

        // Definir nuestro controlador
        walletController = new WalletController(ListarWalletsActivity.this);
        transaccionController = new TransaccionController(ListarWalletsActivity.this);

        // Instanciar vistas
        recyclerViewWallets = findViewById(R.id.recyclerViewWallets);
        fabAgregarWallet = findViewById(R.id.fabAgregarWallet);

        // Por defecto es una lista vacía,
        // se la ponemos al adaptador y configuramos el recyclerView

        listaDeTransaccionesWalletId = new ArrayList<>();
        listaDeWallets = new ArrayList<>();
        listaDeImportes = new ArrayList<>();
        walletsAdapters = new WalletsAdapters(listaDeWallets);
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());
        recyclerViewWallets.setLayoutManager(mLayoutManager);
        recyclerViewWallets.setItemAnimator(new DefaultItemAnimator());
        recyclerViewWallets.setAdapter(walletsAdapters);

        // Una vez que ya configuramos el RecyclerView le ponemos los datos de la BD
        refrescarListaDeWallets();

        // Listener de los clicks en la lista WALLET.
        recyclerViewWallets.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewWallets,
                new RecyclerTouchListener.ClickListener() {

                    @Override
                    // Un toque Entrar en el Wallet y sus transacciones
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
                        intent.putExtra("info", info);

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
                        intent.putExtra("info", info);
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
                intent.putExtra("info", info);
                startActivity(intent);
            }
        });

        // Créditos
        fabAgregarWallet.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(ListarWalletsActivity.this)
                        .setTitle("Acerca de")
                        .setMessage("My Travel Wallet, una aplicación fin proyecto DAM para Universae")
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
        listaDeImportes = walletController.obtenerWalletsImporte();
        walletsAdapters.setListaDeWallets(listaDeWallets, listaDeImportes);
        walletsAdapters.notifyDataSetChanged();

    }

/*
    ///Cierra la app al cerrar
    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);

    }
*/
}

