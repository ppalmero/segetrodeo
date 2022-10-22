package info.androidhive.bottomsheet.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import info.androidhive.bottomsheet.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VecinosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VecinosFragment extends DialogFragment implements DatePickerFragment.OnDateSelectedInterface, TimePickerFragment.OnTimeSelectedInterface {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int idComedero = -1;
    private DatePickerFragment newFragmentDate;
    private TimePickerFragment newFragmentTime;
    private View v;
    private long fechaIni;
    private int horaIni;

    public interface VecinosDialogListener {
        public void onDialogPositiveClickVecinos(DialogFragment dialog);
        public void onDialogNegativeClickVecinos(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    VecinosDialogListener listener;

    public VecinosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VecinosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VecinosFragment newInstance(String param1, String param2) {
        VecinosFragment fragment = new VecinosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vecinos, container, false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        v = inflater.inflate(R.layout.fragment_vecinos, null);

        builder.setView(v)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TextView tvVF = v.findViewById(R.id.etFechaVecinos);
                        TextView tvVH = v.findViewById(R.id.etHoraVecinos);
                        if (tvVF.getText().toString().equals("") || tvVH.getText().toString().equals("")) {
                            Toast toast = Toast.makeText(getContext(), "Debe completar los campos fecha y hora.", Toast.LENGTH_LONG);
                            toast.show();
                        } else {
                            listener.onDialogPositiveClickVecinos(VecinosFragment.this);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogNegativeClickVecinos(VecinosFragment.this);
                    }
                });
        //Bundle extras = getIntent().getExtras();
        Bundle extras = this.getArguments();
        if (extras != null) {
            idComedero = extras.getInt("idComedero");
        } else {
            /*NO SE RECIBIERON ARGUMENTOS*/
        }

        ImageButton ibFV = v.findViewById(R.id.ibFechaVecinos);
        ibFV.setOnClickListener(ibFVListener);

        ImageButton ibHV = v.findViewById(R.id.ibHoraVecinos);
        ibHV.setOnClickListener(ibHVListener);

        newFragmentDate = new DatePickerFragment();
        newFragmentDate.setOnDateSelectedInterface(this);
        newFragmentTime = new TimePickerFragment();
        newFragmentTime.setOnTimeSelectedInterface(this);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (VecinosDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception

        }
    }

    private View.OnClickListener ibFVListener = new View.OnClickListener() {
        public void onClick(View v) {
            Bundle args = new Bundle();
            int vista = R.id.etFechaVecinos;
            args.putInt("vista",vista);

            newFragmentDate.setArguments(args);
            newFragmentDate.show(getFragmentManager(), "datePicker");
        }
    };

    private View.OnClickListener ibHVListener = new View.OnClickListener() {
        public void onClick(View v) {
            //android.app.DialogFragment newFragment = new TimePickerFragment();
            Bundle args = new Bundle();
            int vista = R.id.etHoraVecinos;
            args.putInt("vista",vista);
            newFragmentTime.setArguments(args);
            newFragmentTime.show(getFragmentManager(), "timePicker");
        }
    };

    @Override
    public void OnDateSelected(String fecha, Integer vista) {
        TextView tv = (TextView) v.findViewById(vista);
        tv.setText(fecha);
        if (vista == R.id.etFechaVecinos){
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dateFI = null;
            try {
                dateFI = sdf.parse(fecha);
                fechaIni = dateFI.getTime();
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
        if (vista == R.id.etHoraVecinos){
            horaIni = Integer.parseInt(time.substring(0, time.indexOf(":")));
        }
    }

    public Integer getCantidad(){
        TextView tvV = v.findViewById(R.id.tvCantidadVecinos);
        if (tvV.getText().toString().equals("")){
            return -1;
        } else {
            return Integer.parseInt((String) tvV.getText().toString());
        }
    }

    public Integer getDistancia(){
        TextView tvV = v.findViewById(R.id.tvDistanciaVecinos);
        if (tvV.getText().toString().equals("")){
            return -1;
        } else {
            return Integer.parseInt((String) tvV.getText().toString());
        }
    }

    public long getFechaIni() {
        return fechaIni;
    }

    public int getHoraIni() {
        return horaIni;
    }
}