package info.androidhive.bottomsheet.views;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    OnDateSelectedInterface callback;
    public void setOnDateSelectedInterface(OnDateSelectedInterface callback) {
        this.callback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = 2021;//c.get(Calendar.YEAR);
        int month = 1;//c.get(Calendar.MONTH);
        int day = 1;//c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        //return new DatePickerDialog(getActivity(), this, year, month, day);
        return new DatePickerDialog(getContext(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        int vista = getArguments().getInt("vista");

        // Do something with the date chosen by the user
        month++;
        String fecha = ((day < 10)? "0" + day : day) + "/" + ((month < 10)? "0" + month : month) + "/" + year;
        //TextView tv = (TextView) getActivity().findViewById(vista);
        //TextView tv = (TextView) ((IPFragment)getActivity().getSupportFragmentManager().findFragmentByTag("fechayhora")).getView().findViewById(vista);
        //tv.setText(fecha);
        callback.OnDateSelected(fecha, vista);
    }

    public interface OnDateSelectedInterface {
        public void OnDateSelected(String fecha, Integer vista);
    }

}