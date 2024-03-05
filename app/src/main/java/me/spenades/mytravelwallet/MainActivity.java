package me.spenades.mytravelwallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.spenades.mytravelwallet.adapters.UsuariosAdapters;
import me.spenades.mytravelwallet.controllers.UsuarioController;
import me.spenades.mytravelwallet.models.Usuario;

public class MainActivity extends AppCompatActivity {

    private UsuarioController usuarioController;
    private List<Usuario> listaDeUsuarios;
    private UsuariosAdapters usuariosAdapters;
    private Button btnEmpezar;
    private EditText etNombrePropietario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Definir nuestro controlador
        usuarioController = new UsuarioController(MainActivity.this);

        // Recuperamos lista de usuarios
        listaDeUsuarios = new ArrayList<>();
        usuariosAdapters = new UsuariosAdapters(listaDeUsuarios);
        // Se envía el wallet 0 para poder recuperar todos los usuarios
        listaDeUsuarios = usuarioController.obtenerUsuarios();
        usuariosAdapters.setListaDeUsuarios(listaDeUsuarios);
        int cantidadUsuarios = usuariosAdapters.getItemCount();

        // Una vez que ya configuramos el RecyclerView le ponemos los datos de la BD
        refrescarListaDeUsuarios();


        // Si la lista está vacía se insta a añadir usuario Propietario
        if (cantidadUsuarios == 0) {

            // Instanciamos vistas
            etNombrePropietario = findViewById(R.id.etNombrePropietario);
            btnEmpezar = findViewById(R.id.btnEmpezar);

            btnEmpezar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Resetear errores
                    etNombrePropietario.setError(null);
                    String nombrePropietario = etNombrePropietario.getText().toString();

                    if ("".equals(nombrePropietario)) {
                        etNombrePropietario.setError("Escribe tu Nombre");
                        etNombrePropietario.requestFocus();
                        return;
                    }

                    Usuario nuevoPropietario = new Usuario(nombrePropietario, nombrePropietario);

                    long id = usuarioController.nuevoUsuario(nuevoPropietario);
                    if (id == -1) {
                        // De alguna manera ocurrió un error
                        Toast.makeText(MainActivity.this, "Error al guardar. Intenta de nuevo", Toast.LENGTH_SHORT).show();
                    } else {
                        continuar();
                    }
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

        Intent intent = new Intent(MainActivity.this, ListarWalletsActivity.class);
        intent.putExtra("usuarioActivo", usuarioActivo);
        intent.putExtra("usuarioIdActivo", usuarioIdActivo);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refrescarListaDeUsuarios();
    }

    public void refrescarListaDeUsuarios() {
        listaDeUsuarios = usuarioController.obtenerUsuarios();
        usuariosAdapters.setListaDeUsuarios(listaDeUsuarios);
        usuariosAdapters.notifyDataSetChanged();
    }

}


