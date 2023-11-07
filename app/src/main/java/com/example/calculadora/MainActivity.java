package com.example.calculadora;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etNumero1, etNumero2;
    private TextView tvResultado;
    private ToggleButton tbSuma, tbResta, tbMulti, tbDivi;
    private Button bCalcular, bLimpiar, bGuardar, bRecuperar;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(Preferencias.DATOS, Context.MODE_PRIVATE);

        etNumero1 = findViewById(R.id.etNumero1);
        etNumero2 = findViewById(R.id.etNumero2);

        tvResultado = findViewById(R.id.tvResultado);

        bCalcular = findViewById(R.id.bCalcular);
        bLimpiar = findViewById(R.id.bLimpiar);
        bGuardar = findViewById(R.id.bGuardar);
        bRecuperar = findViewById(R.id.bRecuperar);

        bCalcular.setOnClickListener(this);
        bLimpiar.setOnClickListener(this);
        bGuardar.setOnClickListener(this);
        bRecuperar.setOnClickListener(this);

        tbSuma = findViewById(R.id.tbSuma);
        tbResta = findViewById(R.id.tbResta);
        tbMulti = findViewById(R.id.tbMulti);
        tbDivi = findViewById(R.id.tbDivi);

        tbSuma.setOnClickListener(this);
        tbResta.setOnClickListener(this);
        tbMulti.setOnClickListener(this);
        tbDivi.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.bCalcular) operar(v);
        else if (v.getId() == R.id.bLimpiar) limpiar(v);
        else if (v.getId() == R.id.bGuardar) guardarDatos(v);
        else if (v.getId() == R.id.bRecuperar) recuperarDatos(v);
        else {
            apagarTooggleButtons(v);
            if (v.getId() == R.id.tbSuma) tbSuma.setChecked(true);
            else if (v.getId() == R.id.tbResta) tbResta.setChecked(true);
            else if (v.getId() == R.id.tbMulti) tbMulti.setChecked(true);
            else if (v.getId() == R.id.tbDivi) tbDivi.setChecked(true);
        }
    }

    public void operar(View v) {
        Double n1, n2, res;
        try {
            ocultarTeclado(this);
            n1 = Double.parseDouble(etNumero1.getText().toString());
            n2 = Double.parseDouble(etNumero2.getText().toString());
            if (tbSuma.isChecked()) res = n1 + n2;
            else if (tbResta.isChecked()) res = n1 - n2;
            else if (tbMulti.isChecked()) res = n1 * n2;
            else if (tbDivi.isChecked()) res = n1 / n2;
            else throw new DatosException(getString(R.string.error_no_operacion));
            if (Math.abs(res) == Double.POSITIVE_INFINITY)
                throw new DatosException(getString(R.string.error_division_cero));
            DecimalFormat formato = new DecimalFormat(getString(R.string.pattern_numero));
            tvResultado.setText(formato.format(res));
        } catch (NumberFormatException | DatosException e) {
            String texto = e.getMessage();
            if (texto.equals(getString(R.string.error_vacio))) texto = getString(R.string.error_no_numeros);
            textoCentrado(texto, true);
            tvResultado.setText(getString(R.string.resultado));
        }
    }

    public void limpiar(View v) {
        tvResultado.setText(getString(R.string.resultado));
        etNumero1.setText("");
        etNumero2.setText("");
        apagarTooggleButtons(v);
    }

    public void apagarTooggleButtons(View v) {
        tbSuma.setChecked(false);
        tbResta.setChecked(false);
        tbMulti.setChecked(false);
        tbDivi.setChecked(false);
    }

    public void guardarDatos(View v) {
        prefs.edit().putString(Preferencias.VALOR1, etNumero1.getText().toString()).apply();
        prefs.edit().putString(Preferencias.VALOR2, etNumero2.getText().toString()).apply();
        prefs.edit().putString(Preferencias.RESULTADO, tvResultado.getText().toString()).apply();
        if (tbSuma.isChecked())
            prefs.edit().putString(Preferencias.OPERANDO, getString(R.string.suma)).apply();
        else if (tbResta.isChecked())
            prefs.edit().putString(Preferencias.OPERANDO, getString(R.string.resta)).apply();
        else if (tbMulti.isChecked())
            prefs.edit().putString(Preferencias.OPERANDO, getString(R.string.multiplicacion)).apply();
        else if (tbDivi.isChecked())
            prefs.edit().putString(Preferencias.OPERANDO, getString(R.string.division)).apply();
        else
            prefs.edit().putString(Preferencias.OPERANDO, getString(R.string.cadena_vacia)).apply();
        textoCentrado(getString(R.string.datos_guardados), false);
    }

    public void recuperarDatos(View v) {
        apagarTooggleButtons(v);
        etNumero1.setText(prefs.getString(Preferencias.VALOR1, getString(R.string.cadena_vacia)));
        etNumero2.setText(prefs.getString(Preferencias.VALOR2, getString(R.string.cadena_vacia)));
        tvResultado.setText(prefs.getString(Preferencias.RESULTADO, getString(R.string.cadena_vacia)));
        String operando = prefs.getString(Preferencias.OPERANDO, getString(R.string.cadena_vacia));
        if (operando.equals(getString(R.string.suma))) tbSuma.setChecked(true);
        else if (operando.equals(getString(R.string.resta))) tbResta.setChecked(true);
        else if (operando.equals(getString(R.string.multiplicacion))) tbMulti.setChecked(true);
        else if (operando.equals(getString(R.string.division))) tbDivi.setChecked(true);
        textoCentrado(getString(R.string.datos_recuperados), false);
    }

    private void textoCentrado(String texto, boolean esError) {
        Toast toast = Toast.makeText(getApplicationContext(), texto, Toast.LENGTH_SHORT);
        ViewGroup vg = (ViewGroup) toast.getView();
        if (esError) vg.setBackgroundResource(R.drawable.toast_error);
        else vg.setBackgroundResource(R.drawable.toast_informacion);
        TextView mensaje = vg.findViewById(android.R.id.message);
        mensaje.setTextSize(25);
        mensaje.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        mensaje.setTextColor(getColor(R.color.white));
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void ocultarTeclado(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(),
                0);
    }
}
