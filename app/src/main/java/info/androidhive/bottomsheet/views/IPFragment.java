package info.androidhive.bottomsheet.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.fragment.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import info.androidhive.bottomsheet.MainActivity;
import info.androidhive.bottomsheet.R;
import info.androidhive.bottomsheet.Vaca;
import info.androidhive.bottomsheet.enums.Consultas;
import info.androidhive.bottomsheet.ws.CallWS;

public class IPFragment extends DialogFragment implements DatePickerFragment.OnDateSelectedInterface, TimePickerFragment.OnTimeSelectedInterface {

    private DatePickerFragment newFragmentDate;
    private TimePickerFragment newFragmentTime;
    private View v;

    private long fechaIni;
    private long fechaFin;

    private int horaIni;
    private int horaFin;
    private int consulta;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface IPDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    IPDialogListener listener;
@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        v = inflater.inflate(R.layout.fragment_i_p, null);
        builder.setView(v)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    listener.onDialogPositiveClick(IPFragment.this);
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    listener.onDialogNegativeClick(IPFragment.this);
                }
            });
        //Bundle extras = getIntent().getExtras();
        Bundle extras = this.getArguments();
        consulta = 0;
        if (extras != null) {
            consulta = extras.getInt("tipo");
        } else {
            /*NO SE RECIBIERON ARGUMENTOS*/
        }

        ImageButton ibFI = v.findViewById(R.id.ibFI);
        ibFI.setOnClickListener(ibFIListener);

        ImageButton ibHI = v.findViewById(R.id.ibHI);
        ibHI.setOnClickListener(ibHIListener);


        if (consulta == MainActivity.CONSULTA_EVENTO) {
            TextView tvV = v.findViewById(R.id.tvVacas);
            tvV.setVisibility(View.INVISIBLE);
            Spinner spV = v.findViewById(R.id.spVacas);
            spV.setVisibility(View.INVISIBLE);

            LinearLayout llFF = v.findViewById(R.id.llFF);
            llFF.setVisibility(View.INVISIBLE);

            LinearLayout llHF = v.findViewById(R.id.llHF);
            llHF.setVisibility(View.INVISIBLE);
        } else {
            ImageButton ibFF = v.findViewById(R.id.ibFF);
            ImageButton ibHF = v.findViewById(R.id.ibHF);
            ibFF.setOnClickListener(ibFFListener);
            ibHF.setOnClickListener(ibHFListener);
            if (consulta == MainActivity.CONSULTA_INTERVALO) {
                TextView tvV = v.findViewById(R.id.tvVacas);
                tvV.setVisibility(View.INVISIBLE);
                Spinner spV = v.findViewById(R.id.spVacas);
                spV.setVisibility(View.INVISIBLE);
            } else {
                /*** CONSULTAR WEBSERVICE ***/
                HashMap<Integer, Vaca> vacas = obtenerListadoVacas();
                ArrayList<String> list = new ArrayList<>();
                Spinner spV = v.findViewById(R.id.spVacas);
                for (int i = 0; i < vacas.size(); i++) {
                    list.add(i + " - " + vacas.get(i).getNombre());
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spV.setAdapter(dataAdapter);
            }
        }
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout

        newFragmentDate = new DatePickerFragment();
        newFragmentDate.setOnDateSelectedInterface(this);
        newFragmentTime = new TimePickerFragment();
        newFragmentTime.setOnTimeSelectedInterface(this);

        return builder.create();
    }

    private HashMap<Integer, Vaca> obtenerListadoVacas() {
        CallWS cws = new CallWS();

        HashMap<Integer, Vaca> vacas = null;

        String resultadoJSON = cws.requestWS(Consultas.LISTADO, getContext());
        if (resultadoJSON.contains("error en ws")){
            /*IPFragment newFragment = new IPFragment();
            newFragment.show(getSupportFragmentManager(), "ip");*/
            Toast toast = Toast.makeText(getContext(), "La comunicación con el servidor falló: " + resultadoJSON, Toast.LENGTH_LONG);
            toast.show();
        } else {
            try {
                JSONObject reader = new JSONObject(resultadoJSON);
                //cargarPosicionInicial(reader);
                //cantTiempos = reader.getInt("Movimientos") - 2;
                JSONObject puntos = reader.getJSONObject("Vacas");
                vacas = new HashMap<>();
                for (int i = 0; i < puntos.length(); i++) {
                    JSONObject vaca = puntos.getJSONObject("Vaca" + i);
                    vacas.put(i, new Vaca(vaca.getInt("ID"), vaca.getString("Nombre")));
                }
            } catch (JSONException e) {
                Toast toast = Toast.makeText(getContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        }
        return vacas;
    }

    private View.OnClickListener ibFIListener = new View.OnClickListener() {
        public void onClick(View v) {
            Bundle args = new Bundle();
            int vista = R.id.etFIIP;
            args.putInt("vista",vista);

            newFragmentDate.setArguments(args);
            newFragmentDate.show(getFragmentManager(), "datePicker");
        }
    };

    private View.OnClickListener ibHIListener = new View.OnClickListener() {
        public void onClick(View v) {
            //android.app.DialogFragment newFragment = new TimePickerFragment();
            Bundle args = new Bundle();
            int vista = R.id.etHI;
            args.putInt("vista",vista);
            newFragmentTime.setArguments(args);
            newFragmentTime.show(getFragmentManager(), "timePicker");
        }
    };

    private View.OnClickListener ibFFListener = new View.OnClickListener() {
        public void onClick(View v) {
            //DialogFragment newFragment = new DatePickerFragment();
            Bundle args = new Bundle();
            int vista = R.id.etFF;
            args.putInt("vista",vista);
            newFragmentDate.setArguments(args);
            newFragmentDate.show(getFragmentManager(), "datePicker");
        }
    };

    private View.OnClickListener ibHFListener = new View.OnClickListener() {
        public void onClick(View v) {
            //android.app.DialogFragment newFragment = new TimePickerFragment();
            Bundle args = new Bundle();
            int vista = R.id.etHF;
            args.putInt("vista",vista);
            newFragmentTime.setArguments(args);
            newFragmentTime.show(getFragmentManager(), "timePicker");
        }
    };

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (IPDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception

        }
    }

    @Override
    public void OnDateSelected(String fecha, Integer vista) {
        //View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_i_p, null);
        TextView tv = (TextView) v.findViewById(vista);
        tv.setText(fecha);
        if (vista == R.id.etFIIP){
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dateFI = null;
            try {
                dateFI = sdf.parse(fecha);
                fechaIni = dateFI.getTime();
            } catch (ParseException e) {
                Toast toast = Toast.makeText(getContext(), "La fecha debe ser dada en formato dd/mm/yyyy", Toast.LENGTH_LONG);
                toast.show();
            }
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dateFF = null;
            try {
                dateFF = sdf.parse(fecha);
                fechaFin = dateFF.getTime();
            } catch (ParseException e) {
                Toast toast = Toast.makeText(getContext(), "La fecha debe ser dada en formato dd/mm/yyyy", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    @Override
    public void OnTimeSelected(String time, Integer vista) {
        TextView tv = (TextView) v.findViewById(vista);
        tv.setText(time);
        if (vista == R.id.etHI){
            horaIni = Integer.parseInt(time.substring(0, time.indexOf(":")));
        } else {
            horaFin = Integer.parseInt(time.substring(0, time.indexOf(":")));
        }
    }

    public Long getFechaIni() {
        return fechaIni;
    }

    public Long getFechaFin() {
        return fechaFin;
    }

    public Integer getHoraIni() {
        return horaIni;
    }

    public Integer getHoraFin() {
        return horaFin;
    }

    public Integer getConsulta() {
        return consulta;
    }

    public Integer getVaca(){
        TextView tvV = v.findViewById(R.id.tvVacas);
        if (tvV.isShown()) {
            Spinner spV = v.findViewById(R.id.spVacas);
            return spV.getSelectedItemPosition();
        } else {
            return -1;
        }
    }
}
