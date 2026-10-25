package com.example.pm01e1221160004;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.uth.pm01e1.R;

/** Pantalla 3: marca el número con ACTION_CALL (código de país + número). */
public class LlamarActivity extends AppCompatActivity {

    private static final int REQ_CALL = 100;
    private static final String TAG = "LlamarActivityError";
    private String numero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llamar);

        // Recibimos los datos del intent anterior
        numero = getIntent().getStringExtra("numero");
        String nombre = getIntent().getStringExtra("nombre");

        TextView tvNombre = findViewById(R.id.tvNombre);
        TextView tvNumero = findViewById(R.id.tvNumero);
        Button btnAtras = findViewById(R.id.btnAtras);
        ImageButton btnLlamar = findViewById(R.id.btnLlamar);

        if (nombre != null) {
            tvNombre.setText(nombre);
        }
        if (numero != null) {
            tvNumero.setText(numero);
        }

        btnAtras.setOnClickListener(v -> finish());
        btnLlamar.setOnClickListener(v -> llamar());
    }

    private void llamar() {
        if (numero == null || numero.trim().isEmpty()) {
            Toast.makeText(this, "El número de teléfono está vacío", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error: El número de teléfono es nulo o está vacío.");
            return;
        }

        // Verificamos si tenemos el permiso CALL_PHONE concedido
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            // Si no lo tenemos, lo solicitamos en tiempo de ejecución
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQ_CALL);
        } else {
            // Si ya lo tenemos, realizamos la llamada directamente
            realizarLlamada();
        }
    }

    private void realizarLlamada() {
        try {
            String numeroLimpio = numero.trim();
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + numeroLimpio));
            startActivity(callIntent);
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException al intentar llamar: " + e.getMessage(), e);
            Toast.makeText(this, "Error de seguridad: Permiso de llamada denegado", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Excepción general al intentar llamar: " + e.getMessage(), e);
            Toast.makeText(this, "No se pudo realizar la llamada: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido por el usuario, procedemos a llamar
                realizarLlamada();
            } else {
                Toast.makeText(this, "Se necesita el permiso de llamadas para continuar", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "El usuario denegó el permiso CALL_PHONE.");
            }
        }
    }
}