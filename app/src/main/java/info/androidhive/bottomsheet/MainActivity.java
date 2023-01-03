package info.androidhive.bottomsheet;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
//import android.support.design.widget.BottomSheetBehavior;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
//import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import at.grabner.circleprogress.AnimationState;
import at.grabner.circleprogress.AnimationStateChangedListener;
import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import bdet.comun.Punto;
/*import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;*/
import info.androidhive.bottomsheet.enums.Consultas;
import info.androidhive.bottomsheet.listeners.Eventos;
//import info.androidhive.bottomsheet.views.FechaHoraActivity;
import info.androidhive.bottomsheet.views.BuscarFragment;
import info.androidhive.bottomsheet.views.FichaFragment;
import info.androidhive.bottomsheet.views.IPFragment;
import info.androidhive.bottomsheet.views.Teselado;
import info.androidhive.bottomsheet.views.VecinosFragment;
import info.androidhive.bottomsheet.ws.CallWS;

public class MainActivity extends AppCompatActivity implements IPFragment.IPDialogListener, VecinosFragment.VecinosDialogListener, BuscarFragment.BuscarDialogListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int CONSULTA_INTERVALO = 1111;
    public static final int CONSULTA_EVENTO = 2222;
    public static final int CONSULTA_TRAYECTORIA = 3333;
    private static final int MY_PERMISSIONS_REQUEST_INTERNET_ACCESS = 1;

    //@BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;

    /*** circle view ***/

    CircleProgressView mCircleView;
    Switch mSwitchSpin;
    Switch mSwitchShowUnit;
    SeekBar mSeekBar;
    SeekBar mSeekBarSpinnerLength;
    Boolean mShowUnit = true;
    Spinner mSpinner;
    private int tiempoActual;
    private int cantTiempos;
    Eventos eventosListener;
    Teselado t;

    /***fin circle view ***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        //ButterKnife.bind(this);

        t = (Teselado) findViewById(R.id.teseladoView);
        // Capture our button from layout
        ImageButton btnInicio = findViewById(R.id.btnInicio);
        // Register the onClick listener with the implementation above
        btnInicio.setOnClickListener(btnInicioListener);

        ImageButton btnAnterior = findViewById(R.id.btnAnterior);
        btnAnterior.setOnClickListener(btnAnteriorListener);

        ImageButton btnSiguiente = findViewById(R.id.btnSiguiente);
        btnSiguiente.setOnClickListener(btnSiguienteListener);

        // TODO REVISAR WS
        ImageButton btnFin = findViewById(R.id.btnFin);
        btnFin.setOnClickListener(btnFinListener);

        ImageButton btnAxP = findViewById(R.id.ibAnimalesPorParcela);
        btnAxP.setOnClickListener(btnAxPListener);

        ImageButton btnES = findViewById(R.id.ibEntradaYSalidaDeAnimales);
        btnES.setOnClickListener(btnESListener);

        ImageButton btnTA = findViewById(R.id.ibTrayectoriaDeAnimales);
        btnTA.setOnClickListener(btnTAListener);

        ImageButton btnActualizar = findViewById(R.id.ibActualizar);
        btnActualizar.setOnClickListener(btnActualizarListener);

        ImageButton btnBuscar = findViewById(R.id.ibBuscar);
        btnBuscar.setOnClickListener(btnBuscarListener);

        ImageButton btnClear = findViewById(R.id.ibFinalizarConsulta);
        btnClear.setOnClickListener(btnClearListener);

        ToggleButton btnComederos = findViewById(R.id.btnComederos);
        btnComederos.setOnCheckedChangeListener(btnComederosListener);

        cargarCirculos();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            /*if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {*/
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        MY_PERMISSIONS_REQUEST_INTERNET_ACCESS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            //}
        } else {
            llamarWS();
        }
            //scrollLock(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_INTERNET_ACCESS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    llamarWS();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void llamarWS(){
        /* LECTURA DE POSICIÓN INICIAL - USO DE WEB SERVICE Y JSON ***/
        CallWS cws = new CallWS();

        tiempoActual = -1;
        String resultadoJSON = cws.requestWSsConsultas(Consultas.INICIO, null, null, null, null, getApplicationContext());
        if (resultadoJSON.contains("error en ws")){
            Toast toast = Toast.makeText(getApplicationContext(), "La comunicación con el servidor falló: " + resultadoJSON, Toast.LENGTH_LONG);
            toast.show();
        } else {
            try {
                JSONObject reader = new JSONObject(resultadoJSON);
                //cargarPosicionInicial(reader);
                //cantTiempos = reader.getInt("Movimientos") - 2;
                JSONObject puntos = reader.getJSONObject("Puntos");
                cantTiempos = reader.getInt("Movimientos");
                Map<Integer, Vaca> vacas = new HashMap<>();
                for (int i = 0; i < puntos.length(); i++) {
                    JSONObject vaca = puntos.getJSONObject("Punto" + i);
                    vacas.put(i, new Vaca(vaca.getInt("x"), vaca.getInt("y")));
                }
                //Teselado t = (Teselado) findViewById(R.id.teseladoView);
                t.setVacas(vacas);
                t.drawVacas(true);
                t.setAction(Consultas.CLEAR);//Para poder elegir vaca, antes no funcionaba
                t.setOnStopTrackEventListener(new Eventos() {
                    @Override
                    public void onStopTrack(Float x, Float y, Float xDown, Float yDown) {

                    }

                    @Override
                    public void onVacaChosen(int id) {
                        vacaChosen(id);
                    }

                    @Override
                    public void onComederoChosen(int idComedero) {
                        comederoChosen(idComedero);
                    }

                });
            } catch (JSONException e) {
                Toast toast = Toast.makeText(getApplicationContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    private void vacaChosen(int id) {
        CallWS cws = new CallWS();
        Map<String, Integer> parametrosVacas = new HashMap<>();
        parametrosVacas.put("vaca", id);
        String resultadoJSON = cws.requestWSsConsultas(Consultas.DATOSVACA, null, null, null, parametrosVacas, getApplicationContext());
        if (resultadoJSON.contains("error en ws")){
            Toast toast = Toast.makeText(getApplicationContext(), "La comunicación con el servidor falló: " + resultadoJSON, Toast.LENGTH_LONG);
            toast.show();
        } else {
            try {
                JSONObject vaca = new JSONObject(resultadoJSON);

                FichaFragment newFragment = new FichaFragment();
                Bundle args = new Bundle();

                args.putString("vaca", vaca.toString());
                newFragment.setArguments(args);
                newFragment.show(getSupportFragmentManager(), "ficha");

            } catch (JSONException e) {
                Toast toast = Toast.makeText(getApplicationContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    private void comederoChosen(int idComedero) {
        DialogFragment newFragment = new VecinosFragment();
        Bundle args = new Bundle();
        args.putInt("idComedero", idComedero);
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "vecinoscomederos");
    }

    // Create an anonymous implementation of OnClickListener
    private View.OnClickListener btnInicioListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast toast = Toast.makeText(getApplicationContext(), "Inicio", Toast.LENGTH_LONG);
            toast.show();
            CallWS cws = new CallWS();
            String resultadoJSON = cws.requestWSsConsultas(Consultas.INICIO, null, null, null, null, getApplicationContext());
            if (resultadoJSON.contains("error en ws")){
                /*IPFragment newFragment = new IPFragment();
                newFragment.show(getSupportFragmentManager(), "ip");*/
                toast = Toast.makeText(getApplicationContext(), "La comunicación con el servidor falló: " + resultadoJSON, Toast.LENGTH_LONG);
                toast.show();
            } else {
                try {
                    JSONObject reader = new JSONObject(resultadoJSON);
                    JSONObject puntos = reader.getJSONObject("Puntos");
                    Map<Integer, Vaca> vacas = new HashMap<>();
                    for (int i = 0; i < puntos.length(); i++) {
                        JSONObject vaca = puntos.getJSONObject("Punto" + i);
                        vacas.put(i, new Vaca(vaca.getInt("x"), vaca.getInt("y")));
                    }
                    //Teselado t = findViewById(R.id.teseladoView);
                    t.setVacas(vacas);
                    t.drawVacasInicio(true);
                    tiempoActual = -1;
                } catch (JSONException e) {
                    toast = Toast.makeText(getApplicationContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    };

    private View.OnClickListener btnAnteriorListener = new View.OnClickListener() {
        public void onClick(View v) {
            /*Toast toast = Toast.makeText(getApplicationContext(), "Anterior", Toast.LENGTH_LONG);
            toast.show();*/
            CallWS cws = new CallWS();

            if (tiempoActual > -1) {
                Map<String, Integer> parametrosWS = new HashMap<>();
                parametrosWS.put("tiempo", tiempoActual);
                tiempoActual--;
                String resultadoJSON = cws.requestWSsConsultas(Consultas.ANTERIOR, null, null, parametrosWS, null, getApplicationContext());
                if (resultadoJSON.contains("error en ws")){
                    tiempoActual++;
                    Toast toast = Toast.makeText(getApplicationContext(), "La comunicación con el servidor falló: " + resultadoJSON, Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    try {
                        JSONObject reader = new JSONObject(resultadoJSON);
                        String fechaActual = reader.getString("Tiempo");
                        TextView ea = findViewById(R.id.tvEstadoActual);
                        ea.setText(fechaActual);
                        JSONObject puntos = reader.getJSONObject("Puntos");
                        modificarVista(puntos);
                    } catch (JSONException e) {
                        Toast toast = Toast.makeText(getApplicationContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "No existen más registros", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    };

    private View.OnClickListener btnSiguienteListener = new View.OnClickListener() {
        public void onClick(View v) {
            CallWS cws = new CallWS();
            try {
                if (tiempoActual < cantTiempos) {
                    Map<String, Integer> parametrosWS = new HashMap();
                    tiempoActual++;
                    String resultadoJSON;
                    if (tiempoActual == cantTiempos){
                        resultadoJSON = cws.requestWSsConsultas(Consultas.FIN, null, null, null, null, getApplicationContext());
                    } else {
                        parametrosWS.put("tiempo", tiempoActual);
                        resultadoJSON = cws.requestWSsConsultas(Consultas.SIGUIENTE, null, null, parametrosWS, null, getApplicationContext());
                    }
                    if (resultadoJSON.contains("error en ws")){
                        tiempoActual--;
                        Toast toast = Toast.makeText(getApplicationContext(), "La comunicación con el servidor falló: " + resultadoJSON, Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        JSONObject reader = new JSONObject(resultadoJSON);
                        String fechaActual = reader.getString("Tiempo");
                        TextView ea = findViewById(R.id.tvEstadoActual);
                        ea.setText(fechaActual);
                        JSONObject puntos = reader.getJSONObject("Puntos");
                        modificarVista(puntos);
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "No existen más registros", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } catch (JSONException e) {
                Toast toast = Toast.makeText(getApplicationContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    };

    private void modificarVista(JSONObject puntos) throws JSONException{
        Map<Integer, Vaca> vacas = new HashMap<>();
        for (int i = 0; i < puntos.length(); i++) {
            Integer oId = Integer.parseInt(((String)puntos.names().get(i)).substring(5));
            JSONObject vaca = puntos.getJSONObject("Punto" + oId);
            vacas.put(oId, new Vaca(vaca.getInt("x"), vaca.getInt("y")));
        }
        //Teselado t = (Teselado) findViewById(R.id.teseladoView);
        t.setVacasModificadas(vacas);
        t.drawVacas(true);
    }

    private View.OnClickListener btnFinListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast toast = Toast.makeText(getApplicationContext(), "Fin", Toast.LENGTH_LONG);
            toast.show();
            CallWS cws = new CallWS();
            try {
                tiempoActual = cantTiempos;//1272;
                JSONObject reader = new JSONObject(cws.requestWSsConsultas(Consultas.FIN, null, null, null, null, getApplicationContext()));
                JSONObject puntos = reader.getJSONObject("Puntos");
                Map<Integer, Vaca> vacas = new HashMap<>();
                for (int i = 0; i < puntos.length(); i++) {
                    JSONObject vaca = puntos.getJSONObject("Punto" + i);
                    vacas.put(i, new Vaca(vaca.getInt("x"), vaca.getInt("y")));
                }
                //Teselado t = (Teselado)findViewById(R.id.teseladoView);
                t.setVacas(vacas);
                t.drawVacasInicio(true);
            } catch (JSONException e) {
                toast = Toast.makeText(getApplicationContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    };

    private View.OnClickListener btnAxPListener = new View.OnClickListener() {
        public void onClick(View v) {
            DialogFragment newFragment = new IPFragment();
            Bundle args = new Bundle();
            args.putInt("tipo", CONSULTA_INTERVALO);
            newFragment.setArguments(args);
            newFragment.show(getSupportFragmentManager(), "fechayhora");
        }
    };

    private View.OnClickListener btnESListener = new View.OnClickListener() {
        public void onClick(View v) {
            DialogFragment newFragment = new IPFragment();
            Bundle args = new Bundle();
            args.putInt("tipo", CONSULTA_EVENTO);
            newFragment.setArguments(args);
            newFragment.show(getSupportFragmentManager(), "fechayhora");
        }
    };

    private View.OnClickListener btnTAListener = new View.OnClickListener() {
        public void onClick(View v) {
            DialogFragment newFragment = new IPFragment();
            Bundle args = new Bundle();
            args.putInt("tipo", CONSULTA_TRAYECTORIA);
            newFragment.setArguments(args);
            newFragment.show(getSupportFragmentManager(), "fechayhora");
        }
    };

    private View.OnClickListener btnClearListener = new View.OnClickListener() {
        public void onClick(View v) {
            //Teselado t = findViewById(R.id.teseladoView);
            t.setAction(Consultas.CLEAR);
            ImageButton btnFinalizarConsulta = findViewById(R.id.ibFinalizarConsulta);
            btnFinalizarConsulta.setClickable(false);
            btnFinalizarConsulta.setAlpha(0.3f);
        }
    };

    private View.OnClickListener btnActualizarListener = new View.OnClickListener() {
        public void onClick(View v) {
            llamarWS();
            //Teselado t = findViewById(R.id.teseladoView);
            t.setAction(Consultas.CLEAR);
        }
    };

    private View.OnClickListener btnBuscarListener = new View.OnClickListener() {
        public void onClick(View v) {
            DialogFragment newFragment = new BuscarFragment();
            newFragment.show(getSupportFragmentManager(), "buscar");
        }
    };

    private CompoundButton.OnCheckedChangeListener btnComederosListener = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                Drawable img = getResources().getDrawable(R.drawable.cubeta);
                img.setBounds(buttonView.getCompoundDrawables()[1].getBounds());//[1] para quedarse con el boundsTop
                ((ToggleButton)buttonView).setCompoundDrawables(null,img,null,null);
                Toast toast = Toast.makeText(getApplicationContext(), "Mostrar comederos", Toast.LENGTH_LONG);
                toast.show();
                CallWS cws = new CallWS();
                try {
                    JSONObject reader = new JSONObject(cws.requestWSsConsultas(Consultas.COMEDEROS, null, null, null, null, getApplicationContext()));
                    JSONObject comederos = reader.getJSONObject("Comederos");
                    Map<Integer, Vaca> comederosId = new HashMap<>();
                    for (int i = 0; i < comederos.length(); i++) {
                        JSONObject comedero = comederos.getJSONObject("Comedero" + i);
                        comederosId.put(i, new Vaca(comedero.getInt("X"), comedero.getInt("Y")));
                    }
                    //Teselado t = (Teselado) findViewById(R.id.teseladoView);
                    t.setComederos(comederosId);
                    t.drawComederos(true);
                } catch (JSONException e) {
                    toast = Toast.makeText(getApplicationContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
                    toast.show();
                }
            } else {
                Drawable img = getResources().getDrawable(R.drawable.cubetaoff);
                img.setBounds(buttonView.getCompoundDrawables()[1].getBounds());//[1] para quedarse con el boundsTop
                ((ToggleButton)buttonView).setCompoundDrawables(null,img,null,null);
                Toast toast = Toast.makeText(getApplicationContext(), "Ocultar comederos", Toast.LENGTH_LONG);
                toast.show();
                //Teselado t = (Teselado) findViewById(R.id.teseladoView);
                t.drawComederos(false);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void cargarCirculos() {

        mCircleView = findViewById(R.id.circleView);
        mCircleView.setOnProgressChangedListener(new CircleProgressView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float value) {
                //Log.d(TAG, "Progress Changed: " + value);
            }
        });

        //this example shows how to show a loading text if it is in spinning mode, and the current percent value otherwise.
        mCircleView.setShowTextWhileSpinning(true); // Show/hide text in spinning mode
        mCircleView.setText("Cargando...");
        mCircleView.setOnAnimationStateChangedListener(
                new AnimationStateChangedListener() {
                    @Override
                    public void onAnimationStateChanged(AnimationState _animationState) {
                        switch (_animationState) {
                            case IDLE:
                            case ANIMATING:
                            case START_ANIMATING_AFTER_SPINNING:
                                mCircleView.setTextMode(TextMode.PERCENT); // show percent if not spinning
                                mCircleView.setUnitVisible(mShowUnit);
                                break;
                            case SPINNING:
                                mCircleView.setTextMode(TextMode.TEXT); // show text while spinning
                                mCircleView.setUnitVisible(false);
                            case END_SPINNING:
                                break;
                            case END_SPINNING_START_ANIMATING:
                                break;

                        }
                    }
                }
        );
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if(((IPFragment)dialog).getConsulta() == CONSULTA_INTERVALO){
            final long fi = ((IPFragment)dialog).getFechaIni();
            final int hi = ((IPFragment)dialog).getHoraIni();
            final long ff = ((IPFragment)dialog).getFechaFin();
            final int hf = ((IPFragment)dialog).getHoraFin();
            Toast toast = Toast.makeText(getApplicationContext(), "Indicar parcela", Toast.LENGTH_LONG);
            toast.show();

            scrollBottom();

            //Teselado t = findViewById(R.id.teseladoView);
            t.setAction(Consultas.INTERVALO);
            t.setOnStopTrackEventListener(new Eventos() {
                @Override
                public void onStopTrack(Float xDown, Float yDown, Float x, Float y) {
                    System.out.println("Parcela: " + xDown + yDown + x + y);
                    CallWS cws = new CallWS();
                    Map<String, Float> parametrosArea = new HashMap<>();
                    parametrosArea.put("xmin", xDown.intValue() / 1000f);
                    parametrosArea.put("ymin", yDown.intValue() / 1000f);
                    parametrosArea.put("xmax", x.intValue() / 1000f);
                    parametrosArea.put("ymax", y.intValue() / 1000f);
                    Map<String, Long> parametroFechas = new HashMap<>();
                    parametroFechas.put("ti", fi);
                    parametroFechas.put("tf", ff);
                    Map<String, Integer> parametrosHoras = new HashMap<>();
                    parametrosHoras.put("hi", hi);
                    parametrosHoras.put("hf", hf);
                    Map<String, Integer> parametrosVacas = new HashMap<>();

                    try {
                        JSONObject reader = new JSONObject(cws.requestWSsConsultas(Consultas.INTERVALO, parametrosArea, parametroFechas, parametrosHoras, parametrosVacas, getApplicationContext()));
                        JSONObject puntos = reader.getJSONObject("Entradas");
                        ArrayList<Integer> vacasID = new ArrayList<>();
                        for (int i = 0; i < puntos.length(); i++) {
                            vacasID.add( puntos.getInt("ID" + i));
                        }
                        //Teselado t = findViewById(R.id.teseladoView);
                        t.setVacasSeleccionadas(vacasID);
                        t.drawVacas(true);
                    } catch (JSONException e) {
                        Toast toast = Toast.makeText(getApplicationContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }

                @Override
                public void onVacaChosen(int id) {
                    vacaChosen(id);
                }

                @Override
                public void onComederoChosen(int id) {
                    comederoChosen(id);
                }
            });
        } else if(((IPFragment)dialog).getConsulta() == CONSULTA_EVENTO){
            final long fi = ((IPFragment)dialog).getFechaIni();
            final int hi = ((IPFragment)dialog).getHoraIni();
            Toast toast = Toast.makeText(getApplicationContext(), "Indicar parcela", Toast.LENGTH_LONG);
            toast.show();

            scrollBottom();

            //Teselado t = findViewById(R.id.teseladoView);
            t.setAction(Consultas.EVENTO);
            t.setOnStopTrackEventListener(new Eventos() {
                @Override
                public void onStopTrack(Float xDown, Float yDown, Float x, Float y) {
                    System.out.println("Parcela: " + xDown + yDown + x + y);
                    CallWS cws = new CallWS();
                    Map<String, Float> parametrosArea = new HashMap<>();
                    parametrosArea.put("xmin", xDown.intValue() / 1000f);
                    parametrosArea.put("ymin", yDown.intValue() / 1000f);
                    parametrosArea.put("xmax", x.intValue() / 1000f);
                    parametrosArea.put("ymax", y.intValue() / 1000f);
                    Map<String, Long> parametroFechas = new HashMap<>();
                    parametroFechas.put("ti", fi);
                    Map<String, Integer> parametrosHoras = new HashMap<>();
                    parametrosHoras.put("hi", hi);
                    Map<String, Integer> parametrosVacas = new HashMap<>();

                    try {
                        JSONObject reader = new JSONObject(cws.requestWSsConsultas(Consultas.EVENTO, parametrosArea, parametroFechas, parametrosHoras, parametrosVacas, getApplicationContext()));

                        JSONObject puntosIn = reader.getJSONObject("Entradas");
                        ArrayList<Integer> vacasIDIn = new ArrayList<>();
                        for (int i = 0; i < puntosIn.length(); i++) {
                            vacasIDIn.add( puntosIn.getInt("ID" + i));
                        }

                        JSONObject puntosOut = reader.getJSONObject("Salidas");
                        ArrayList<Integer> vacasIDOut = new ArrayList<>();
                        for (int i = 0; i < puntosOut.length(); i++) {
                            vacasIDOut.add( puntosOut.getInt("ID" + i));
                        }
                        //Teselado t = findViewById(R.id.teseladoView);
                        t.setVacasInOut(vacasIDIn, vacasIDOut);
                        t.drawVacas(true);
                    } catch (JSONException e) {
                        Toast toast = Toast.makeText(getApplicationContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }

                @Override
                public void onVacaChosen(int id) {
                    vacaChosen(id);
                }

                @Override
                public void onComederoChosen(int id) {
                    comederoChosen(id);
                }
            });

        } else if(((IPFragment)dialog).getConsulta() == CONSULTA_TRAYECTORIA){
            int vacaID = ((IPFragment)dialog).getVaca();
            final long fi = ((IPFragment)dialog).getFechaIni();
            final int hi = ((IPFragment)dialog).getHoraIni();
            final long ff = ((IPFragment)dialog).getFechaFin();
            final int hf = ((IPFragment)dialog).getHoraFin();

            scrollBottom();

            CallWS cws = new CallWS();

            Map<String, Float> parametrosArea = new HashMap<>();
            Map<String, Long> parametroFechas = new HashMap<>();
            parametroFechas.put("ti", fi);
            parametroFechas.put("tf", ff);
            Map<String, Integer> parametrosHoras = new HashMap<>();
            parametrosHoras.put("hi", hi);
            parametrosHoras.put("hf", hf);
            Map<String, Integer> parametrosVacas = new HashMap<>();
            parametrosVacas.put("vaca", vacaID);
            try {
                JSONObject reader = new JSONObject(cws.requestWSsConsultas(Consultas.TRAYECTORIA, parametrosArea, parametroFechas, parametrosHoras, parametrosVacas, getApplicationContext()));
                JSONObject puntos = reader.getJSONObject("Trayectorias");
                vacaID = reader.getInt("Vaca"); //Debería ser igual al valor que traía

                ArrayList<Punto> trayectorias = new ArrayList<>();
                for (int i = 0; i < puntos.length(); i++) {
                    JSONObject trayectoria = puntos.getJSONObject("Trayectoria" + i);
                    if (!trayectoria.isNull("x")) {
                        trayectorias.add(new Punto(trayectoria.getInt("x"), trayectoria.getInt("y")));
                    }
                }
                //Teselado t = findViewById(R.id.teseladoView);
                t.setAction(Consultas.TRAYECTORIA);
                t.setTrayectoria(vacaID, trayectorias);
                t.drawVacas(true);
            } catch (JSONException e) {
                Toast toast = Toast.makeText(getApplicationContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    private void scrollBottom() {
        final ScrollView mScrollView = (ScrollView) findViewById(R.id.svPrincipal);
        mScrollView.post(new Runnable() {
            public void run() {
                mScrollView.fullScroll(mScrollView.FOCUS_DOWN);
            }
        });
        ImageButton btnFinalizarConsulta = findViewById(R.id.ibFinalizarConsulta);
        btnFinalizarConsulta.setClickable(true);
        btnFinalizarConsulta.setAlpha(1f);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogPositiveClickVecinos(DialogFragment dialog) {
        int cantidad = ((VecinosFragment)dialog).getCantidad();
        int distancia = ((VecinosFragment)dialog).getDistancia();
        if(cantidad >= 0) {
            final long fi = ((VecinosFragment)dialog).getFechaIni();
            final int hi = ((VecinosFragment)dialog).getHoraIni();
            scrollBottom();

            CallWS cws = new CallWS();

            Map<String, Float> parametrosArea = new HashMap<>();
            Map<String, Long> parametroFechas = new HashMap<>();
            parametroFechas.put("ti", fi);
            Map<String, Integer> parametrosHoras = new HashMap<>();
            parametrosHoras.put("hi", hi);
            Map<String, Integer> parametrosVecinos = new HashMap<>();
            parametrosVecinos.put("cantidad", cantidad);
            parametrosArea.put("distancia", distancia + 0f);
            try {
                JSONObject reader = new JSONObject(cws.requestWSsConsultas(Consultas.VECINOS, parametrosArea, parametroFechas, parametrosHoras, parametrosVecinos, getApplicationContext()));
                JSONObject comederos = reader.getJSONObject("Comederos");

                //Teselado t = findViewById(R.id.teseladoView);
                //ArrayList<Punto> trayectorias = new ArrayList<>();
                for (int i = 0; i < comederos.length(); i++) {
                    if (i != t.getComederoSeleccionado()){
                        continue;
                    }
                    JSONObject comedero = comederos.getJSONObject("Comedero" + i);
                    ArrayList<Integer> vacasID = new ArrayList<>();
                    for (int j = 0; j < comedero.length(); j++) {
                        JSONObject vecino = comedero.getJSONObject("Vecino" + j);
                        vacasID.add(vecino.getInt("ID"));
                    }
                    t.setVacasVecinas(vacasID);
                    //JSONObject vecinos = comederos.getJSONObject("Vecino");
                }

                t.setAction(Consultas.VECINOS);
                t.drawVacas(true);
            } catch (JSONException e) {
                Toast toast = Toast.makeText(getApplicationContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    @Override
    public void onDialogNegativeClickVecinos(DialogFragment dialog) {

    }

    @Override
    public void onDialogPositiveClickBuscar(DialogFragment dialog) {
        int vacaID = ((BuscarFragment)dialog).getVacaID();
        t.setVacaShow(vacaID);
        vacaChosen(vacaID);
    }

    @Override
    public void onDialogNegativeClickBuscar(DialogFragment dialog) {

    }
}
