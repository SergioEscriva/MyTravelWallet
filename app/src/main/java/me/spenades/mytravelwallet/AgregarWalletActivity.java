package me.spenades.mytravelwallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import me.spenades.mytravelwallet.adapters.ParticipantesAdapters;
import me.spenades.mytravelwallet.controllers.ParticipanteController;
import me.spenades.mytravelwallet.controllers.UsuarioController;
import me.spenades.mytravelwallet.controllers.WalletController;
import me.spenades.mytravelwallet.models.Participante;
import me.spenades.mytravelwallet.models.Wallet;

public class AgregarWalletActivity extends AppCompatActivity {

    private List<Participante> listaDeParticipantes;
    private ParticipantesAdapters participantesAdapters;
    private WalletController walletController;
    private UsuarioController usuarioController;
    private ParticipanteController participanteController;
    private RecyclerView recyclerViewParticipantes;
    private Button btnAgregarWallet, btnAgregarUsuario;
    private EditText etNombre, etDescripcion, etPropietarioId, etAddParticipante, etWaletId;
    private CheckBox checkBoxCompartir;
    private FloatingActionButton btnCancelarNuevoWallet;
    private long walletId;
    private long userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet);

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
        participanteController = new ParticipanteController(AgregarWalletActivity.this);
        usuarioController = new UsuarioController(AgregarWalletActivity.this);

        // Instanciar vistas
        recyclerViewParticipantes = findViewById(R.id.recyclerViewParticipantes);

        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPropietarioId = findViewById(R.id.etPropietarioId);
        etAddParticipante = findViewById(R.id.etAddParticipante);
        etWaletId = findViewById(R.id.etWalletId);
        CheckBox checkBoxCompartir = findViewById(R.id.checkBox_Compartir);
        btnAgregarWallet = findViewById(R.id.btn_agregar_wallet);
        btnCancelarNuevoWallet = findViewById(R.id.btn_cancelar_nuevo_wallet);
        btnAgregarUsuario = findViewById(R.id.btnAgregarParticipante);

        // Añade nuestro nombre de usuario rescatado del inicio
        etPropietarioId.setText(String.valueOf(userId));
        etPropietarioId.setVisibility(View.INVISIBLE);
        recyclerViewParticipantes.setVisibility(View.VISIBLE);

        // Por defecto es una lista vacía,
        listaDeParticipantes = new ArrayList<>();
        participantesAdapters = new ParticipantesAdapters(listaDeParticipantes);

        // se la ponemos al adaptador y configuramos el recyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewParticipantes.setLayoutManager(mLayoutManager);
        recyclerViewParticipantes.setItemAnimator(new DefaultItemAnimator());
        recyclerViewParticipantes.setAdapter(participantesAdapters);
        //refrescarListaDeParticipantes();


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

                if (walletId == -1) {

                    // De alguna manera ocurrió un error
                    Toast.makeText(AgregarWalletActivity.this, "Error al guardar. Intenta de nuevo", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(AgregarWalletActivity.this, "Wallet Guardada.", Toast.LENGTH_SHORT).show();

                    // Agrega al propietario del Wallet como Participante
                    Participante nuevoParticipanteGuardar = new Participante(walletId, userId, usuarioActivo);

                    // Ahora lo añadimos como Participante, aquí existe como usuario seguro.
                    participanteController.nuevoParticipante(nuevoParticipanteGuardar);

                    // Seguimos en Editar Wallet
                    Intent intent = new Intent(AgregarWalletActivity.this, EditarWalletActivity.class);
                    intent.putExtra("nombreUsuario", String.valueOf(usuarioActivo));
                    intent.putExtra("usuarioId", String.valueOf(userId));
                    intent.putExtra("walletId", String.valueOf(walletId));
                    intent.putExtra("nombreWallet", String.valueOf(nombre));
                    intent.putExtra("descripcion", String.valueOf(descripcion));
                    intent.putExtra("propietarioId", String.valueOf(userId));
                    intent.putExtra("checkCompartir", String.valueOf(compartir));
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



