package me.spenades.mytravelwallet.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import me.spenades.mytravelwallet.R;
import me.spenades.mytravelwallet.controllers.DemoController;
import me.spenades.mytravelwallet.controllers.UsuarioAppController;
import me.spenades.mytravelwallet.models.Usuario;

public class UsuarioInicialActivity extends AppCompatActivity {

    private UsuarioAppController usuarioAppController;
    private DemoController demoController;

    private List<Usuario> listaDeUsuarios;

    private Button btnEmpezar;
    private EditText etNombrePropietario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        // Definir nuestro controlador
        usuarioAppController = new UsuarioAppController(UsuarioInicialActivity.this);
        demoController = new DemoController(UsuarioInicialActivity.this);

        // Instanciamos vistas
        etNombrePropietario = findViewById(R.id.etNombrePropietario);
        btnEmpezar = findViewById(R.id.btnEmpezar);

        listaDeUsuarios = new ArrayList<>();
        refrescarListaDeUsuarios();
        iniciar();

    }

    public void iniciar() {

        // Se envía el wallet 0 para poder recuperar todos los usuarios
        listaDeUsuarios = usuarioAppController.obtenerUsuarios();
        int cantidadUsuarios = listaDeUsuarios.size();

        // Si la lista está vacía se insta a añadir usuario Propietario
        if (cantidadUsuarios == 0) {
            btnEmpezar.setOnClickListener(new View.OnClickListener() {

                // Añadimos Usuario Nuevo Inicial
                @Override
                public void onClick(View v) {
                    usuarioNuevoInicial();
                }

            });
        } else {
            continuar();
        }
    }


    private void continuar() {

        //cambiamos de actividad
        refrescarListaDeUsuarios();
        final Usuario usuario = listaDeUsuarios.get(0);
        String usuarioActivo = usuario.getNombre();
        long usuarioIdActivo = usuario.getId();

        Intent intent = new Intent(UsuarioInicialActivity.this, ListarWalletsActivity.class);
        intent.putExtra("usuarioActivo", usuarioActivo);
        intent.putExtra("usuarioIdActivo", usuarioIdActivo);
        startActivity(intent);

    }


    public void refrescarListaDeUsuarios() {
        listaDeUsuarios = usuarioAppController.obtenerUsuarios();
    }


    public void usuarioNuevoInicial() {

        // Resetear errores
        etNombrePropietario.setError(null);
        String nombrePropietario = etNombrePropietario.getText().toString();

        if ("".equals(nombrePropietario)) {
            etNombrePropietario.setError("Escribe tu Nombre");
            etNombrePropietario.requestFocus();
            return;
        }
        // se añade el nombre introducido como usuario y será el propietario de los wallets
        Usuario nuevoPropietario = new Usuario(nombrePropietario, nombrePropietario);

        long id = usuarioAppController.nuevoUsuario(nuevoPropietario);
        if (id == - 1) {
            // De alguna manera ocurrió un error
            Toast.makeText(UsuarioInicialActivity.this, "Error al guardar. Intenta de nuevo",
                    Toast.LENGTH_SHORT).show();
        } else {
            demoController.demoIncial();
            continuar();
        }
    }


    @Override
    protected void onResume() {
        iniciar(); //esto hace que vuelva a la lista de Wallets al pulsar atrás.
        super.onResume();
        //finishAndRemoveTask();
    }


}





