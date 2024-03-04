package me.spenades.mytravelwallet;

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
import me.spenades.mytravelwallet.models.Usuario;
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


        // Por defecto es una lista vacía,
        listaDeParticipantes = new ArrayList<>();
        participantesAdapters = new ParticipantesAdapters(listaDeParticipantes);

        // se la ponemos al adaptador y configuramos el recyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewParticipantes.setLayoutManager(mLayoutManager);
        recyclerViewParticipantes.setItemAnimator(new DefaultItemAnimator());
        recyclerViewParticipantes.setAdapter(participantesAdapters);
        refrescarListaDeParticipantes();


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
                long propietarioId = Long.parseLong(etPropietarioId.getText().toString());

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
                    agregarParticipante(walletId, propietarioName);

                    // Se quita el botón de Añadir Wallet nuevo y se hace visible los participantes.

                    btnAgregarWallet.setVisibility(View.INVISIBLE);
                    recyclerViewParticipantes.setVisibility(View.VISIBLE);
                    etAddParticipante.setVisibility(View.VISIBLE);
                    btnAgregarUsuario.setVisibility(View.VISIBLE);

                    //finish();
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
        // Listener de los clicks en la lista Participantes
        recyclerViewParticipantes.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewParticipantes, new RecyclerTouchListener.ClickListener() {
            @Override // Un toque Será Editar Usuario
            public void onClick(View view, int position) {
                /* Pasar a la actividad EditarTransaccionesActivity.java
                Transaccion transaccionSeleccionada = listaUsuarios.get(position);
                Intent intent = new Intent(ListarTransaccionesActivity.this, EditarTransaccionesActivity.class);
                intent.putExtra("idTransaccion", transaccionSeleccionada.getId());
                intent.putExtra("descripcionTransaccion", transaccionSeleccionada.getDescripcion());
                intent.putExtra("importeTransaccion", transaccionSeleccionada.getImporte());
                intent.putExtra("pagadorTransaccion", transaccionSeleccionada.getPagador());
                intent.putExtra("participantesTransaccion", transaccionSeleccionada.getParticipantes());
                intent.putExtra("categoriaTransaccion", transaccionSeleccionada.getCategoria());
                intent.putExtra("fechaTransaccion", transaccionSeleccionada.getFecha());
                intent.putExtra("walletId", walletIdSelected);
                startActivity(intent);

                 */
            }

            @Override // Un toque largo Será borrar Usuario
            public void onLongClick(View view, int position) {

                Toast.makeText(AgregarWalletActivity.this, "Esto borrará el Participante", Toast.LENGTH_SHORT).show();

              /* final Transaccion transaccionParaEliminar = listaUsuarios.get(position);
                AlertDialog dialog = new AlertDialog
                        .Builder(ListarTransaccionesActivity.this)
                        .setPositiveButton("Sí, eliminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                transaccionController.eliminarTransaccion(transaccionParaEliminar);
                                refrescarListaDeUsuarios();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setTitle("Confirmar")
                        .setMessage("¿Eliminar a la transacción " + transaccionParaEliminar.getDescripcion() + "?")
                        .create();
                dialog.show();
                */

            }

        }));


        // Listener botón agregar participante y a su vez como usuario
        btnAgregarUsuario.setOnClickListener(new View.OnClickListener() {
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

                // Agregamos Participante y Refrescamos, la nueva transacción nos devuelve el id del nuevo participante.

                //TODO HAY QUE INTENTAR SACARLO A OTRA CLASE
                long id = agregarParticipante(walletId, nuevoParticipante);
                if (id == -1) {
                    // Participante error
                    Toast.makeText(AgregarWalletActivity.this, "Error al guardar. Intenta de nuevo", Toast.LENGTH_SHORT).show();

                } else {
                    etAddParticipante.setText("");
                    Toast.makeText(AgregarWalletActivity.this, "Participante añadido", Toast.LENGTH_SHORT).show();

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
        Participante nuevoParticipanteGuardar = new Participante(walletId, usuarioIdDb, nuevoParticipante);
        // Ahora lo añadimos como Participante, aquí existe como usuario seguro.
        long agregarParticipante = participanteController.nuevoParticipante(nuevoParticipanteGuardar);
        refrescarListaDeParticipantes();
        return agregarParticipante;
    }


}


