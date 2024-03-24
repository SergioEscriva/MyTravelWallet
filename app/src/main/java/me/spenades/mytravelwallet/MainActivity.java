package me.spenades.mytravelwallet;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import me.spenades.mytravelwallet.activities.ListarWalletsActivity;
import me.spenades.mytravelwallet.adapters.UsuariosAdapters;
import me.spenades.mytravelwallet.ayuda.ListaWalletsAyuda;
import me.spenades.mytravelwallet.controllers.DemoController;
import me.spenades.mytravelwallet.controllers.UsuarioAppController;
import me.spenades.mytravelwallet.models.Usuario;

public class MainActivity extends AppCompatActivity {

    private UsuarioAppController usuarioAppController;
    private DemoController demoController;
    private List<Usuario> listaDeUsuarios;
    private UsuariosAdapters usuariosAdapters;
    private Button btnEmpezar;
    private EditText etNombrePropietario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Definir nuestro controlador
        usuarioAppController = new UsuarioAppController(MainActivity.this);
        demoController = new DemoController(MainActivity.this);

        // Instanciamos vistas
        etNombrePropietario = findViewById(R.id.etNombrePropietario);
        btnEmpezar = findViewById(R.id.btnEmpezar);
        iniciar();

    }


    private void continuar(int info) {

        //cambiamos de actividad
        refrescarListaDeUsuarios();
        final Usuario usuario = listaDeUsuarios.get(0);
        String usuarioActivo = usuario.getNombre();
        long usuarioIdActivo = usuario.getId();

        if (info == 0) {
            Intent intent = new Intent(MainActivity.this, ListaWalletsAyuda.class);
            intent.putExtra("usuarioActivo", usuarioActivo);
            intent.putExtra("usuarioIdActivo", usuarioIdActivo);
            intent.putExtra("info", info);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, ListarWalletsActivity.class);
            intent.putExtra("usuarioActivo", usuarioActivo);
            intent.putExtra("usuarioIdActivo", usuarioIdActivo);
            intent.putExtra("info", info);
            startActivity(intent);
        }
    }


    @Override
    protected void onResume() {
        iniciar(); //esto hace que vuelva a la lista de Wallets al ir pulsar atrás.
        super.onResume();

    }


    public void refrescarListaDeUsuarios() {
        listaDeUsuarios = usuarioAppController.obtenerUsuarios();
        usuariosAdapters.setListaDeUsuarios(listaDeUsuarios);
        usuariosAdapters.notifyDataSetChanged();
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

        Usuario nuevoPropietario = new Usuario(nombrePropietario, nombrePropietario);

        long id = usuarioAppController.nuevoUsuario(nuevoPropietario);
        if (id == - 1) {
            // De alguna manera ocurrió un error
            Toast.makeText(MainActivity.this, "Error al guardar. Intenta de nuevo",
                    Toast.LENGTH_SHORT).show();
        } else {
            demoController.demoIncial();
            continuar(1);
        }
    }


    public int usuarioExiste() {

        // Recuperamos lista de usuarios
        listaDeUsuarios = new ArrayList<>();
        usuariosAdapters = new UsuariosAdapters(listaDeUsuarios);

        // Se envía el wallet 0 para poder recuperar todos los usuarios
        listaDeUsuarios = usuarioAppController.obtenerUsuarios();
        usuariosAdapters.setListaDeUsuarios(listaDeUsuarios);
        int cantidadUsuarios = usuariosAdapters.getItemCount();

        // Una vez que ya configuramos el RecyclerView le ponemos los datos de la BD
        refrescarListaDeUsuarios();
        return cantidadUsuarios;
    }


    public void iniciar() {
        int cantidadUsuarios = usuarioExiste();

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
            continuar(0);
        }
    }
}





