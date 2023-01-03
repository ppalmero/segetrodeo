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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import info.androidhive.bottomsheet.R;
import info.androidhive.bottomsheet.Vaca;
import info.androidhive.bottomsheet.enums.Consultas;
import info.androidhive.bottomsheet.ws.CallWS;

/**
 * A simple {@link Fragment} subclass.
 */
public class BuscarFragment extends DialogFragment {
    private View v;

    public interface BuscarDialogListener {
        public void onDialogPositiveClickBuscar(DialogFragment dialog);
        public void onDialogNegativeClickBuscar(DialogFragment dialog);
    }

    BuscarDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        v = inflater.inflate(R.layout.fragment_buscar, null);
        builder.setView(v)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogPositiveClickBuscar(BuscarFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogNegativeClickBuscar(BuscarFragment.this);
                    }
                });
        HashMap<Integer, Vaca> vacas = obtenerListadoVacas();
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < vacas.size(); i++) {
            list.add(i + " - " + vacas.get(i).getNombre());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.select_dialog_item, list);
        AutoCompleteTextView actvN = v.findViewById(R.id.actvNombres);
        actvN.setThreshold(1);
        actvN.setAdapter(dataAdapter);
        return builder.create();
    }

    private HashMap<Integer, Vaca> obtenerListadoVacas() {
        CallWS cws = new CallWS();

        HashMap<Integer, Vaca> vacas = null;

        String resultadoJSON = cws.requestWSsConsultas(Consultas.LISTADO, null, null, null, null, getContext());
        if (resultadoJSON.contains("error en ws")){
            Toast toast = Toast.makeText(getContext(), "La comunicación con el servidor falló: " + resultadoJSON, Toast.LENGTH_LONG);
            toast.show();
        } else {
            try {
                JSONObject reader = new JSONObject(resultadoJSON);
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

    public int getVacaID() {
        AutoCompleteTextView actvN = v.findViewById(R.id.actvNombres);
        String vaca = actvN.getText().toString();
        try {
            int vacaID = Integer.parseInt(vaca.substring(0, vaca.indexOf("-") - 1));
            return vacaID;
        } catch (NumberFormatException e){
            return -1;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (BuscarFragment.BuscarDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception

        }
    }
}