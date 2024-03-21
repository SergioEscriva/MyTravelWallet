package me.spenades.mytravelwallet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import me.spenades.mytravelwallet.adapters.MiembrosAdapters;
import me.spenades.mytravelwallet.controllers.MiembroWalletController;
import me.spenades.mytravelwallet.controllers.UsuarioAppController;
import me.spenades.mytravelwallet.controllers.WalletController;
import me.spenades.mytravelwallet.models.Miembro;
import me.spenades.mytravelwallet.models.Wallet;

public class AgregarWalletActivity extends AppCompatActivity {

    private List<Miembro> listaDeMiembros;
    private MiembrosAdapters miembrosAdapters;
    private WalletController walletController;
    private UsuarioAppController usuarioAppController;
    private MiembroWalletController miembroWalletController;
    private RecyclerView recyclerViewMiembros;
    private Button btnAgregarWallet, btnAgregarUsuario;
    private EditText etNombre, etDescripcion, etPropietarioId, etAddMiembro, etWaletId;
    private FloatingActionButton btnCancelarNuevoWallet;
    private long walletId;
    private long userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        String usuarioActivo = extras.getString("usuarioActivo");
        userId = extras.getLong("usuarioIdActivo");
        userId = userId + 1;
        // Si no hay datos (cosa rara) salimos
        if (extras == null) {
            finish();
            return;
        }

        // Crear el controlador
        walletController = new WalletController(AgregarWalletActivity.this);
        miembroWalletController = new MiembroWalletController(AgregarWalletActivity.this);
        usuarioAppController = new UsuarioAppController(AgregarWalletActivity.this);

        // Instanciar vistas
        recyclerViewMiembros = findViewById(R.id.recyclerViewMiembros);

        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPropietarioId = findViewById(R.id.etPropietarioId);
        etAddMiembro = findViewById(R.id.etAddMiembro);
        etWaletId = findViewById(R.id.etWalletId);
        CheckBox checkBoxCompartir = findViewById(R.id.checkBox_Compartir);
        btnAgregarWallet = findViewById(R.id.btn_agregar_wallet);
        btnCancelarNuevoWallet = findViewById(R.id.btn_cancelar_nuevo_wallet);
        btnAgregarUsuario = findViewById(R.id.btnAgregarMiembro);

        // Añade nuestro nombre de usuario rescatado del inicio
        etPropietarioId.setText(String.valueOf(userId));
        etPropietarioId.setVisibility(View.INVISIBLE);
        recyclerViewMiembros.setVisibility(View.VISIBLE);

        // Por defecto es una lista vacía,
        listaDeMiembros = new ArrayList<>();
        miembrosAdapters = new MiembrosAdapters(listaDeMiembros);

        // se la ponemos al adaptador y configuramos el recyclerView
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());
        recyclerViewMiembros.setLayoutManager(mLayoutManager);
        recyclerViewMiembros.setItemAnimator(new DefaultItemAnimator());
        recyclerViewMiembros.setAdapter(miembrosAdapters);
        //refrescarListaDeMiembros();


        // Listener del Crear Wallet Nuevo
        btnAgregarWallet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Resetear errores
                etNombre.setError(null);
                etDescripcion.setError(null);
                etPropietarioId.setError(null);
                checkBoxCompartir.setError(null);
                etWaletId.setError(null);
                //etApodo.setError(null);

                String nombre = etNombre.getText().toString(),
                        descripcion = etDescripcion.getText().toString(),
                        propietarioName = usuarioActivo;
                long propietarioId = userId;

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
                if ("".equals(propietarioId)) {
                    etPropietarioId.setError("Escribe Nombre del propietario");
                    etPropietarioId.requestFocus();
                    return;
                }

                // Ya pasó la validación
                Wallet nuevoWallet = new Wallet(nombre, descripcion, propietarioId, compartir);
                long walletId = walletController.nuevoWallet(nuevoWallet);
                etWaletId.setText(String.valueOf(walletId));

                if (walletId == - 1) {

                    // De alguna manera ocurrió un error
                    Toast.makeText(AgregarWalletActivity.this, "Error al guardar. Intenta de " +
                            "nuevo", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(AgregarWalletActivity.this, "Wallet Guardada.",
                            Toast.LENGTH_SHORT).show();

                    // Agrega al propietario del Wallet como Miembro
                    Miembro nuevoMiembroGuardar = new Miembro(walletId, userId,
                            usuarioActivo);

                    // Ahora lo añadimos como Miembro, aquí existe como usuario seguro.
                    miembroWalletController.nuevoMiembro(nuevoMiembroGuardar);

                    // Seguimos en Editar Wallet
                    Intent intent = new Intent(AgregarWalletActivity.this,
                            EditarWalletActivity.class);
                    intent.putExtra("nombreUsuario", String.valueOf(usuarioActivo));
                    intent.putExtra("usuarioId", String.valueOf(userId));
                    intent.putExtra("walletId", String.valueOf(walletId));
                    intent.putExtra("nombreWallet", String.valueOf(nombre));
                    intent.putExtra("descripcion", String.valueOf(descripcion));
                    intent.putExtra("propietarioId", String.valueOf(userId));
                    intent.putExtra("checkCompartir", String.valueOf(compartir));
                    intent.putExtra("agregar", true);
                    startActivity(intent);
                }

            }
        });

        // El de cancelar simplemente cierra la actividad
        btnCancelarNuevoWallet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }
}



