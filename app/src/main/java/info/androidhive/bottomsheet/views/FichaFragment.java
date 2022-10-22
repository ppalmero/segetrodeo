package info.androidhive.bottomsheet.views;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import info.androidhive.bottomsheet.R;

public class FichaFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "vaca";

    // TODO: Rename and change types of parameters
    private String mParam1;
    FloatingActionButton fab;

    public FichaFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    /*public static FichaFragment newInstance(int param1, String param2) {
        FichaFragment fragment = new FichaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ficha, container, false);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        try {
            JSONObject vaca = new JSONObject(mParam1);
            TextView tv = (TextView) view.findViewById(R.id.tvNombre);
            tv.setText(vaca.getString("Nombre"));

            tv = (TextView) view.findViewById(R.id.tvSenasa);
            tv.setText(vaca.getString("Senasa"));

            tv = (TextView) view.findViewById(R.id.tvADN);
            tv.setText(vaca.getString("ADN"));

            tv = (TextView) view.findViewById(R.id.tvRP);
            tv.setText(vaca.getString("RP"));

            tv = (TextView) view.findViewById(R.id.tvHBA);
            tv.setText(vaca.getString("HBA"));

            tv = (TextView) view.findViewById(R.id.tvfn);
            tv.setText(vaca.getString("Fecha de nacimiento"));

            tv = (TextView) view.findViewById(R.id.tvpn);
            tv.setText(vaca.getString("Peso nacimiento") + "kg");

            tv = (TextView) view.findViewById(R.id.tvpa);
            tv.setText(vaca.getString("Peso actual") + "kg");

            tv = (TextView) view.findViewById(R.id.tvFrame);
            tv.setText(vaca.getString("Frame"));

        } catch (JSONException e) {
            Toast toast = Toast.makeText(getContext(), "JSON Malformado " + e.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

        return view;
    }

    /*private View.OnClickListener btnCerrar = new View.OnClickListener() {
        public void onClick(View v) {
            dismiss();
        }
    };*/
}