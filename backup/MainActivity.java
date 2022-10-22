package info.androidhive.bottomsheet;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
//import android.support.design.widget.BottomSheetBehavior;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
//import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import info.androidhive.bottomsheet.views.FichaFragment;
import info.androidhive.bottomsheet.views.IPFragment;
import info.androidhive.bottomsheet.views.Teselado;
import info.androidhive.bottomsheet.ws.callWS;

public class MainActivity extends AppCompatActivity implements IPFragment.IPDialogListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int CONSULTA_INTERVALO = 1111;
    public static final int CONSULTA_EVENTO = 2222;
    public static final int CONSULTA_TRAYECTORIA = 3333;
    private static final int MY_PERMISSIONS_REQUEST_INTERNET_ACCESS = 1;

    //@BindView(R.id.expandir)
    //Button btnExpandir;

    //@BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;

    //BottomSheetBehavior sheetBehavior;

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

    /***fin circle view ***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
        //ButterKnife.bind(this);

        //obtenerDatos();
        //inicializarEstructura();

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

        ImageButton btnClear = findViewById(R.id.ibFinalizarConsulta);
        btnClear.setOnClickListener(btnClearListener);

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
        /*** LECTURA DE POSICIÓN INICIAL - USO DE WEB SERVICE Y JSON ***/
        callWS cws = new callWS();

            tiempoActual = -1;
            String resultadoJSON = cws.requestWS(Consultas.INICIO, null, getApplicationContext());
            if (resultadoJSON.contains("error en ws")){
                /*IPFragment newFragment = new IPFragment();
                newFragment.show(getSupportFragmentManager(), "ip");*/
                Toast toast = Toast.makeText(getApplicationContext(), "La comunicación con el servidor falló: " + resultadoJSON, Toast.LENGTH_LONG);
                toast.show();
            } else {
                try {
                    JSONObject reader = new JSONObject(resultadoJSON);
                    //cargarPosicionInicial(reader);
                    cantTiempos = reader.getInt("Movimientos") - 2;
                    JSONObject puntos = reader.getJSONObject("Puntos");
                    Map<Integer, Vaca> vacas = new HashMap<>();
                    for (int i = 0; i < puntos.length(); i++) {
                        JSONObject vaca = puntos.getJSONObject("Punto" + i);
                        vacas.put(i, new Vaca(vaca.getInt("x"), vaca.getInt("y")));
                    }
                    /*sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
                    sheetBehavior.setHideable(false);
                    sheetBehavior.setSkipCollapsed(false);*/
                    Teselado t = (Teselado) findViewById(R.id.teseladoView);//((LinearLayout)((CardView)layoutBottomSheet.getChildAt(1)).getChildAt(0)).getChildAt(0);
                    t.setVacas(vacas);
                    t.drawVacas(true);
                    t.setAction(Consultas.CLEAR);//Para poder elegir vaca, antes no funcionaba
                    t.setOnStopTrackEventListener(new Eventos() {
                        @Override
                        public void onStopTrack(Float x, Float y, Float xDown, Float yDown) {

                        }

                        @Override
                        public void onVacaChosen(int id) {
                            FichaFragment newFragment = new FichaFragment();
                            Bundle args = new Bundle();
                            args.putInt("id", id);
                            newFragment.setArguments(args);
                            newFragment.show(getSupportFragmentManager(), "ficha");
                        }
                    });
                } catch (JSONException e) {
                    Toast toast = Toast.makeText(getApplicationContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
                    toast.show();
                }
            }


        /*
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         */
        /*sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        //btnBottomSheet.setText("Close Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        //btnBottomSheet.setText("Expand Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                /*Context context = getApplicationContext();
                CharSequence text = "Hello toast!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();//
            }
        });*/
    }

    // Create an anonymous implementation of OnClickListener
    private View.OnClickListener btnInicioListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast toast = Toast.makeText(getApplicationContext(), "Inicio", Toast.LENGTH_LONG);
            toast.show();
            callWS cws = new callWS();
            String resultadoJSON = cws.requestWSs(Consultas.INICIO, null, getApplicationContext());
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
                    Teselado t = findViewById(R.id.teseladoView);
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
            callWS cws = new callWS();

            if (tiempoActual > -1) {
                Map<String, Integer> parametrosWS = new HashMap<>();
                parametrosWS.put("tiempo", tiempoActual);
                String resultadoJSON = cws.requestWSs(Consultas.ANTERIOR, parametrosWS, getApplicationContext());
                if (resultadoJSON.contains("error en ws")){
                    /*IPFragment newFragment = new IPFragment();
                    newFragment.show(getSupportFragmentManager(), "ip");*/
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
                        tiempoActual--;
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
            /*Toast toast = Toast.makeText(getApplicationContext(), "Siguiente", Toast.LENGTH_SHORT);
            toast.show();*/
            callWS cws = new callWS();
            try {
                if (tiempoActual < cantTiempos) {
                    Map<String, Integer> parametrosWS = new HashMap();
                    parametrosWS.put("tiempo", tiempoActual + 1);

                    JSONObject reader = new JSONObject(cws.requestWSs(Consultas.SIGUIENTE, parametrosWS, getApplicationContext()));
                    String fechaActual = reader.getString("Tiempo");
                    TextView ea = findViewById(R.id.tvEstadoActual);
                    ea.setText(fechaActual);
                    JSONObject puntos = reader.getJSONObject("Puntos");
                    modificarVista(puntos);
                    tiempoActual++;
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
        /*sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setHideable(false);
        sheetBehavior.setSkipCollapsed(false);*/
        Teselado t = (Teselado) findViewById(R.id.teseladoView);
        t.setVacasModificadas(vacas);
        t.drawVacas(true);
    }

    private View.OnClickListener btnFinListener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast toast = Toast.makeText(getApplicationContext(), "Fin", Toast.LENGTH_LONG);
            toast.show();
            callWS cws = new callWS();
            try {
                //TODO quitar hardcode, obtenerlo por WS
                tiempoActual = cantTiempos;//1272;
                JSONObject reader = new JSONObject(cws.requestWSs(Consultas.FIN, null, getApplicationContext()));
                JSONObject puntos = reader.getJSONObject("Puntos");
                Map<Integer, Vaca> vacas = new HashMap<>();
                for (int i = 0; i < puntos.length(); i++) {
                    JSONObject vaca = puntos.getJSONObject("Punto" + i);
                    vacas.put(i, new Vaca(vaca.getInt("x"), vaca.getInt("y")));
                }
                Teselado t = (Teselado)findViewById(R.id.teseladoView);
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
            /*Intent intent = new Intent(MainActivity.this, FechaHoraActivity.class);
            intent.putExtra("tipo", "intervalo");
            startActivityForResult(intent, CONSULTA_INTERVALO);*/
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
            Teselado t = findViewById(R.id.teseladoView);
            t.setAction(Consultas.CLEAR);
            ImageButton btnFinalizarConsulta = findViewById(R.id.ibFinalizarConsulta);
            btnFinalizarConsulta.setClickable(false);
            btnFinalizarConsulta.setAlpha(0.3f);
        }
    };

    private View.OnClickListener btnActualizarListener = new View.OnClickListener() {
        public void onClick(View v) {
            llamarWS();
            Teselado t = findViewById(R.id.teseladoView);
            t.setAction(Consultas.CLEAR);
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

        //value setting
//        mCircleView.setMaxValue(100);
//        mCircleView.setValue(0);
//        mCircleView.setValueAnimated(24);

        //growing/rotating counter-clockwise
//        mCircleView.setDirection(Direction.CCW)

//        //show unit
//        mCircleView.setUnit("%");
//        mCircleView.setUnitVisible(mShowUnit);
//
//        //text sizes
//        mCircleView.setTextSize(50); // text size set, auto text size off
//        mCircleView.setUnitSize(40); // if i set the text size i also have to set the unit size
//        mCircleView.setAutoTextSize(true); // enable auto text size, previous values are overwritten
//        //if you want the calculated text sizes to be bigger/smaller you can do so via
//        mCircleView.setUnitScale(0.9f);
//        mCircleView.setTextScale(0.9f);
//
////        //custom typeface
////        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/ANDROID_ROBOT.ttf");
////        mCircleView.setTextTypeface(font);
////        mCircleView.setUnitTextTypeface(font);
//
//
//        //color
//        //you can use a gradient
//        mCircleView.setBarColor(getResources().getColor(R.color.primary), getResources().getColor(R.color.accent));
//
//        //colors of text and unit can be set via
//        mCircleView.setTextColor(Color.RED);
//        mCircleView.setTextColor(Color.BLUE);
//        //or to use the same color as in the gradient
//        mCircleView.setTextColorAuto(true); //previous set values are ignored
//
//        //text mode
//        mCircleView.setText("Text"); //shows the given text in the circle view
//        mCircleView.setTextMode(TextMode.TEXT); // Set text mode to text to show text
//
//        //in the following text modes, the text is ignored
//        mCircleView.setTextMode(TextMode.VALUE); // Shows the current value
//        mCircleView.setTextMode(TextMode.PERCENT); // Shows current percent of the current value from the max value

        //spinning
//        mCircleView.spin(); // start spinning
//        mCircleView.stopSpinning(); // stops spinning. Spinner gets shorter until it disappears.
//        mCircleView.setValueAnimated(24); // stops spinning. Spinner spins until on top. Then fills to set value.


        //animation callbacks

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


        // region setup other ui elements
        //Setup Switch
        /*mSwitchSpin = (Switch) findViewById(R.id.switch1);
        mSwitchSpin.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            mCircleView.spin();
                        } else {
                            mCircleView.stopSpinning();
                        }
                    }
                }

        );

        mSwitchShowUnit = (Switch) findViewById(R.id.switch2);
        mSwitchShowUnit.setChecked(mShowUnit);
        mSwitchShowUnit.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mCircleView.setUnitVisible(isChecked);
                        mShowUnit = isChecked;
                    }
                }

        );*/

        //Setup SeekBar
        /*mSeekBar = (SeekBar) findViewById(R.id.seekBar);

        mSeekBar.setMax(100);
        mSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mCircleView.setValueAnimated(seekBar.getProgress(), 1500);
                        mSwitchSpin.setChecked(false);
                    }
                }
        );
/*
        mSeekBarSpinnerLength = (SeekBar) findViewById(R.id.seekBar2);
        mSeekBarSpinnerLength.setMax(360);
        mSeekBarSpinnerLength.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mCircleView.setSpinningBarLength(seekBar.getProgress());
                    }
                });

        mSpinner = (Spinner) findViewById(R.id.spinner);
        List<String> list = new ArrayList<>();
        list.add("Left Top");
        list.add("Left Bottom");
        list.add("Right Top");
        list.add("Right Bottom");
        list.add("Top");
        list.add("Bottom");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        mSpinner.setAdapter(dataAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mCircleView.setUnitPosition(UnitPosition.LEFT_TOP);
                        break;
                    case 1:
                        mCircleView.setUnitPosition(UnitPosition.LEFT_BOTTOM);
                        break;
                    case 2:
                        mCircleView.setUnitPosition(UnitPosition.RIGHT_TOP);
                        break;
                    case 3:
                        mCircleView.setUnitPosition(UnitPosition.RIGHT_BOTTOM);
                        break;
                    case 4:
                        mCircleView.setUnitPosition(UnitPosition.TOP);
                        break;
                    case 5:
                        mCircleView.setUnitPosition(UnitPosition.BOTTOM);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpinner.setSelection(2);*/
    }

    /**
     * manually opening / closing bottom sheet on button click
     *
    @OnClick(R.id.btn_bottom_sheet)
    public void toggleBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            btnBottomSheet.setText("Close sheet");
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            btnBottomSheet.setText("Expand sheet");
        }
    }*/

    /*@OnClick(R.id.expandir)
    public void expandirBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            //btnBottomSheet.setText("Close sheet");
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            //btnBottomSheet.setText("Expand sheet");
        }
    }*/

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

            //sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            Teselado t = findViewById(R.id.teseladoView);
            t.setAction(Consultas.INTERVALO);
            t.setOnStopTrackEventListener(new Eventos() {
                @Override
                public void onStopTrack(Float xDown, Float yDown, Float x, Float y) {
                    //scrollLock(false);
                    Toast toast = Toast.makeText(getApplicationContext(), "Parcela: " + xDown + yDown + x + y, Toast.LENGTH_LONG);
                    toast.show();
                    callWS cws = new callWS();
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
                        Teselado t = findViewById(R.id.teseladoView);
                        t.setVacasSeleccionadas(vacasID);
                        t.drawVacas(true);
                    } catch (JSONException e) {
                        toast = Toast.makeText(getApplicationContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }

                @Override
                public void onVacaChosen(int id) {

                }
            });
        } else if(((IPFragment)dialog).getConsulta() == CONSULTA_EVENTO){
            final long fi = ((IPFragment)dialog).getFechaIni();
            final int hi = ((IPFragment)dialog).getHoraIni();
            Toast toast = Toast.makeText(getApplicationContext(), "Indicar parcela", Toast.LENGTH_LONG);
            toast.show();

            scrollBottom();

            Teselado t = findViewById(R.id.teseladoView);
            t.setAction(Consultas.EVENTO);
            t.setOnStopTrackEventListener(new Eventos() {
                @Override
                public void onStopTrack(Float xDown, Float yDown, Float x, Float y) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Parcela: " + xDown + yDown + x + y, Toast.LENGTH_LONG);
                    toast.show();
                    callWS cws = new callWS();
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
                        Teselado t = findViewById(R.id.teseladoView);
                        t.setVacasInOut(vacasIDIn, vacasIDOut);
                        t.drawVacas(true);
                    } catch (JSONException e) {
                        toast = Toast.makeText(getApplicationContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }

                @Override
                public void onVacaChosen(int id) {

                }
            });

        } else if(((IPFragment)dialog).getConsulta() == CONSULTA_TRAYECTORIA){
            int vacaID = ((IPFragment)dialog).getVaca();
            final long fi = ((IPFragment)dialog).getFechaIni();
            final int hi = ((IPFragment)dialog).getHoraIni();
            final long ff = ((IPFragment)dialog).getFechaFin();
            final int hf = ((IPFragment)dialog).getHoraFin();

            scrollBottom();

            callWS cws = new callWS();

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
                Teselado t = findViewById(R.id.teseladoView);
                t.setAction(Consultas.TRAYECTORIA);
                t.setTrayectoria(vacaID, trayectorias);
                t.drawVacas(true);
                //sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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

    /**
     * showing bottom sheet dialog
     *
    @OnClick(R.id.btn_bottom_sheet_dialog)
    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();
    }


    /**
     * showing bottom sheet dialog fragment
     * same layout is used in both dialog and dialog fragment
     *
    @OnClick(R.id.btn_bottom_sheet_dialog_fragment)
    public void showBottomSheetDialogFragment() {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }*/
}
