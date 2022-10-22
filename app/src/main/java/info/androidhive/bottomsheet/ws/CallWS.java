package info.androidhive.bottomsheet.ws;

import android.content.Context;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Map;

import info.androidhive.bottomsheet.enums.Consultas;
import info.androidhive.bottomsheet.enums.Wss;

public class CallWS {
    /** Called when the activity is first created.
     * URL: It is the url of WSDL file.
     *
     * NAMESPACE: The targetNamespace in WSDL.
     *
     * METHOD_NAME: It is the method name in web service. We can have several methods.
     *
     * SOAP_ACTION: NAMESPACE + METHOD_NAME.*/

    //private static String SOAP_ACTION1 = "http://ws.prp.com/datosIniciales";
    private static String NAMESPACE = "http://ws.prp.com/";
    private static String METHOD_NAME1 = "posicionInicial";
    private static String URL = "http://190.122.229.51:8080/MyWS/TestWS?wsdl";
    //private static String URL = "http://192.168.1.73:8084/MyWS/TestWS?wsdl";

    public String requestWS(Consultas parametro, Context context){
        SoapObject request;
        String wsNombre = "";

        switch (parametro) {
            case INICIO:
                wsNombre = Wss.posicionInicial.name();
                request = new SoapObject(NAMESPACE, wsNombre);
                break;
            case LISTADO:
                wsNombre = Wss.listarVaca.name();
                request = new SoapObject(NAMESPACE, wsNombre);
                break;
        }

        request = new SoapObject(NAMESPACE, wsNombre);
        //Use this to add parameters
        PropertyInfo propertyInfo = new PropertyInfo();
        propertyInfo.setName("arg0");
        propertyInfo.setValue(parametro);
        propertyInfo.setType(String.class);
        //request.addProperty(propertyInfo);
        //Declare the version of the SOAP request
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        //envelope.dotNet = true;
        try {
            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
            httpTransportSE.call(NAMESPACE + wsNombre, envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive)envelope.getResponse();
            String result = soapPrimitive.toString();
            if(result != null) {
                //Get the first property and change the label text
                return result;//.getProperty(0).toString();
            } else {
                Toast.makeText(context, "No Response",Toast.LENGTH_LONG).show();
                return "error en ws" + "retorna nada";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error en ws" + e.getMessage();
        }
    }

    public String requestWSs(Consultas parametro, Map<String, Integer> parametrosWS, Context context){
        SoapObject request = null;
        String wsNombre = "";
        SoapSerializationEnvelope envelope;
        HttpTransportSE httpTransportSE;
        SoapPrimitive soapPrimitive;
        PropertyInfo propertyInfo;
        String result;
        switch (parametro){
            case INICIO:
                wsNombre =  Wss.posicionInicial.name();
                request = new SoapObject(NAMESPACE, wsNombre);
                break;
            case ANTERIOR:
                wsNombre = Wss.consultarMovimientoAnterior.name();
                request = new SoapObject(NAMESPACE, wsNombre);

                propertyInfo = new PropertyInfo();
                propertyInfo.setName("arg0");
                propertyInfo.setValue(parametrosWS.get("tiempo"));
                propertyInfo.setType(Integer.class);
                request.addProperty(propertyInfo);
                break;
            case SIGUIENTE:
                wsNombre = Wss.consultarMovimientoSiguiente.name();
                request = new SoapObject(NAMESPACE, wsNombre);

                propertyInfo = new PropertyInfo();
                propertyInfo.setName("arg0");
                propertyInfo.setValue(parametrosWS.get("tiempo"));
                propertyInfo.setType(Integer.class);
                request.addProperty(propertyInfo);
                break;
            case FIN:
                wsNombre =  Wss.posicionFinal.name();
                request = new SoapObject(NAMESPACE, wsNombre);
                break;
            case COMEDEROS:
                wsNombre =  Wss.getMuchosComederos.name();
                request = new SoapObject(NAMESPACE, wsNombre);
                break;
        }

        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        try {
            httpTransportSE = new HttpTransportSE(URL);
            httpTransportSE.call(NAMESPACE + wsNombre, envelope);
            soapPrimitive = (SoapPrimitive)envelope.getResponse();
            result = soapPrimitive.toString();
            if(result != null) {
                return result;
            } else {
                Toast.makeText(context, "Not  Response",Toast.LENGTH_LONG).show();
                return "error en ws retorna nada";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error en ws " + e.getMessage();
        }
    }

    public String requestWSsConsultas(Consultas parametro, Map<String, Float> parametrosWSArea, Map<String, Long> parametrosWSFechas, Map<String, Integer> parametrosWSHoras, Map<String, Integer> parametrosWSVacas, Context context){
        SoapObject request = null;
        String wsNombre = "";
        SoapSerializationEnvelope envelope;
        HttpTransportSE httpTransportSE;
        SoapPrimitive soapPrimitive;
        PropertyInfo propertyInfo;
        String result;
        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        //envelope.dotNet = true;
        switch (parametro){
            case INTERVALO:
                wsNombre = Wss.consultarIntervalo.name();
                request = new SoapObject(NAMESPACE, wsNombre);

                propertyInfo = new PropertyInfo();
                propertyInfo.setName("arg0");
                propertyInfo.setValue(parametrosWSArea.get("xmin"));//0.253
                propertyInfo.setType(Float.class);
                request.addProperty(propertyInfo);
                PropertyInfo propertyInfo1 = new PropertyInfo();
                propertyInfo1.setName("arg1");
                propertyInfo1.setValue(parametrosWSArea.get("ymin"));//0.285
                propertyInfo1.setType(Float.class);
                request.addProperty(propertyInfo1);
                PropertyInfo propertyInfo2 = new PropertyInfo();
                propertyInfo2.setName("arg2");
                propertyInfo2.setValue(parametrosWSArea.get("xmax"));//0.511
                propertyInfo2.setType(Float.class);
                request.addProperty(propertyInfo2);
                PropertyInfo propertyInfo3 = new PropertyInfo();
                propertyInfo3.setName("arg3");
                propertyInfo3.setValue(parametrosWSArea.get("ymax"));//0.574
                propertyInfo3.setType(Float.class);
                request.addProperty(propertyInfo3);

                MarshalFloat md = new MarshalFloat();
                md.register(envelope);

                PropertyInfo propertyInfo4 = new PropertyInfo();
                propertyInfo4.setName("arg4");
                propertyInfo4.setValue(parametrosWSFechas.get("ti"));//1509591500000
                propertyInfo4.setType(Long.class);
                request.addProperty(propertyInfo4);
                PropertyInfo propertyInfo5 = new PropertyInfo();
                propertyInfo5.setName("arg5");
                propertyInfo5.setValue(parametrosWSHoras.get("hi"));
                propertyInfo5.setType(Integer.class);
                request.addProperty(propertyInfo5);
                PropertyInfo propertyInfo6 = new PropertyInfo();
                propertyInfo6.setName("arg6");
                propertyInfo6.setValue(parametrosWSFechas.get("tf"));//1512183600000
                propertyInfo6.setType(Long.class);
                request.addProperty(propertyInfo6);
                PropertyInfo propertyInfo7 = new PropertyInfo();
                propertyInfo7.setName("arg7");
                propertyInfo7.setValue(parametrosWSHoras.get("hf"));
                propertyInfo7.setType(Integer.class);
                request.addProperty(propertyInfo7);
                break;
            case EVENTO:
                wsNombre = Wss.consultarEvento.name();
                request = new SoapObject(NAMESPACE, wsNombre);

                propertyInfo = new PropertyInfo();
                propertyInfo.setName("arg0");
                propertyInfo.setValue(parametrosWSArea.get("xmin"));//0.508
                propertyInfo.setType(Float.class);
                request.addProperty(propertyInfo);
                propertyInfo1 = new PropertyInfo();
                propertyInfo1.setName("arg1");
                propertyInfo1.setValue(parametrosWSArea.get("ymin"));//0.282
                propertyInfo1.setType(Float.class);
                request.addProperty(propertyInfo1);
                propertyInfo2 = new PropertyInfo();
                propertyInfo2.setName("arg2");
                propertyInfo2.setValue(parametrosWSArea.get("xmax"));//0.653
                propertyInfo2.setType(Float.class);
                request.addProperty(propertyInfo2);
                propertyInfo3 = new PropertyInfo();
                propertyInfo3.setName("arg3");
                propertyInfo3.setValue(parametrosWSArea.get("ymax"));//0.511
                propertyInfo3.setType(Float.class);
                request.addProperty(propertyInfo3);

                md = new MarshalFloat();
                md.register(envelope);

                propertyInfo4 = new PropertyInfo();
                propertyInfo4.setName("arg4");
                propertyInfo4.setValue(parametrosWSFechas.get("ti"));//1509505200000
                propertyInfo4.setType(Long.class);
                request.addProperty(propertyInfo4);
                propertyInfo5 = new PropertyInfo();
                propertyInfo5.setName("arg5");
                propertyInfo5.setValue(parametrosWSHoras.get("hi"));//3
                propertyInfo5.setType(Integer.class);
                request.addProperty(propertyInfo5);
                break;
            case TRAYECTORIA:
                wsNombre = Wss.consultarTrayectoria.name();
                request = new SoapObject(NAMESPACE, wsNombre);

                propertyInfo = new PropertyInfo();
                propertyInfo.setName("arg0");
                propertyInfo.setValue(parametrosWSVacas.get("vaca"));//0.253
                propertyInfo.setType(Integer.class);
                request.addProperty(propertyInfo);
                propertyInfo1 = new PropertyInfo();
                propertyInfo1.setName("arg1");
                propertyInfo1.setValue(parametrosWSFechas.get("ti"));//0.285
                propertyInfo1.setType(Long.class);
                request.addProperty(propertyInfo1);
                propertyInfo2 = new PropertyInfo();
                propertyInfo2.setName("arg2");
                propertyInfo2.setValue(parametrosWSHoras.get("hi"));//0.511
                propertyInfo2.setType(Integer.class);
                request.addProperty(propertyInfo2);
                propertyInfo3 = new PropertyInfo();
                propertyInfo3.setName("arg3");
                propertyInfo3.setValue(parametrosWSFechas.get("tf"));//0.574
                propertyInfo3.setType(Long.class);
                request.addProperty(propertyInfo3);
                propertyInfo4 = new PropertyInfo();
                propertyInfo4.setName("arg4");
                propertyInfo4.setValue(parametrosWSHoras.get("hf"));//1509591500000
                propertyInfo4.setType(Integer.class);
                request.addProperty(propertyInfo4);
                break;
            case DATOSVACA:
                wsNombre = Wss.consultarVaca.name();
                request = new SoapObject(NAMESPACE, wsNombre);

                propertyInfo = new PropertyInfo();
                propertyInfo.setName("arg0");
                propertyInfo.setValue(parametrosWSVacas.get("vaca"));//0.253
                propertyInfo.setType(Integer.class);
                request.addProperty(propertyInfo);
                break;
            case VECINOS:
                wsNombre = Wss.getVecinosComedero.name();
                request = new SoapObject(NAMESPACE, wsNombre);

                propertyInfo = new PropertyInfo();
                propertyInfo.setName("arg0");
                propertyInfo.setValue(parametrosWSVacas.get("cantidad"));//0.253
                propertyInfo.setType(Integer.class);
                request.addProperty(propertyInfo);
                propertyInfo1 = new PropertyInfo();
                propertyInfo1.setName("arg1");
                propertyInfo1.setValue(parametrosWSArea.get("distancia"));//0.285
                propertyInfo1.setType(Float.class);
                request.addProperty(propertyInfo1);

                md = new MarshalFloat();
                md.register(envelope);

                propertyInfo2 = new PropertyInfo();
                propertyInfo2.setName("arg2");
                propertyInfo2.setValue(parametrosWSFechas.get("ti"));//0.285
                propertyInfo2.setType(Long.class);
                request.addProperty(propertyInfo2);
                propertyInfo3 = new PropertyInfo();
                propertyInfo3.setName("arg3");
                propertyInfo3.setValue(parametrosWSHoras.get("hi"));//0.511
                propertyInfo3.setType(Integer.class);
                request.addProperty(propertyInfo3);
        }

        envelope.setOutputSoapObject(request);
        try {
            httpTransportSE = new HttpTransportSE(URL);
            httpTransportSE.call(NAMESPACE + wsNombre, envelope);
            soapPrimitive = (SoapPrimitive)envelope.getResponse();
            result = soapPrimitive.toString();
            if(result != null) {
                return result;
            } else {
                Toast.makeText(context, "Not  Response",Toast.LENGTH_LONG).show();
                return "error en ws retorna nada";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error en ws error" + e.getMessage();
        }
    }
}
