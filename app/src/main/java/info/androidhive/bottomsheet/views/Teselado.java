package info.androidhive.bottomsheet.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ScrollView;

import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bdet.comun.Punto;
import info.androidhive.bottomsheet.R;
import info.androidhive.bottomsheet.Vaca;
import info.androidhive.bottomsheet.enums.Consultas;
import info.androidhive.bottomsheet.listeners.Eventos;

/**
 * TODO: document your custom view class.
 */
public class Teselado extends View {

    Path path = new Path();
    String accion = "";
    float x = 50, y = 50;
    float xDown, yDown;
    private boolean dibujarVacas;
    private boolean dibujarComederos = false;
    private Map<Integer, Vaca> vacas;
    private Map<Integer, Vaca> vacasModificadas = new HashMap<>();
    private Map<Integer, Vaca> oldsVacas = new HashMap<>();
    private Map<Integer, Vaca> comederos;
    private ArrayList<Integer> vacasSeleccionadas = new ArrayList<>();
    private ArrayList<Integer> vacasVecinas = new ArrayList<>();
    private ArrayList<Integer> vacasIn = new ArrayList<>();
    private ArrayList<Integer> vacasOut = new ArrayList<>();
    private float ancho, alto;
    private int vacaSelected;
    private ArrayList<Punto> trayectoria;
    private boolean drawTrayectoria = false;
    private boolean drawEvento = false;
    private boolean drawIntervalo = false;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private float mFocusX = 500f;
    private float mFocusY = 500f;
    private int comederoSelected = -1;
    private boolean drawVecinos = false;
    private boolean isVacaModificada;


    public Teselado(Context context) {
        super(context);
    }

    public Teselado(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public Teselado(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor, mFocusX, mFocusY);

        Drawable fondo = getResources().getDrawable(R.drawable.campo4, null);
        fondo.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        fondo.draw(canvas);
        @SuppressLint("DrawAllocation")
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
        paint.setColor(Color.BLUE);

        ancho = canvas.getWidth() / 1000f;
        alto = canvas.getHeight() / 1000f;

        if (accion.equals("down")) {
            //path.moveTo(x, y);
            xDown = x / mScaleFactor + canvas.getClipBounds().left;
            yDown = y / mScaleFactor + canvas.getClipBounds().top;
        }
        if ((accion.equals("move")) && (drawEvento || drawIntervalo)) {
            //path.lineTo(x, y);
            path.reset();
            x = x / mScaleFactor + canvas.getClipBounds().left;
            y = y / mScaleFactor + canvas.getClipBounds().top;
            if ((x < xDown) && (y < yDown)) {
                path.addRect(x, y, xDown, yDown, Path.Direction.CW);
            } else if (x < xDown) {
                path.addRect(x, yDown, xDown, y, Path.Direction.CW);
            } else if (y < yDown) {
                path.addRect(xDown, y, x, yDown, Path.Direction.CW);
            } else {
                path.addRect(xDown, yDown, x, y, Path.Direction.CW);
            }
            canvas.drawPath(path, paint);
        }

        if (accion.equals("select")) {
            Point raton = new Point(Math.round(x / ancho), Math.round(y / alto));

            if (dibujarComederos) {
                for (int i = 0; i < comederos.size(); i++) {
                    Region r = new Region(getRegion(new Point(comederos.get(i).getX(), comederos.get(i).getY()), 100));
                    if (r.contains(raton.x, raton.y)) {
                        comederoSelected = i;
                        comederoChosen(i);
                    }
                }
            } else {
                for (int i = 0; i < vacas.size(); i++) {
                    Region r = new Region(getRegion(new Point(vacas.get(i).getX(), vacas.get(i).getY()), 50));
                    if (r.contains(raton.x, raton.y)) {
                        vacaSelected = i;
                        vacaChosen(i);
                        // TODO Lanzar evento para obtener la info de la vaca elegida.
                    }
                }
            }

        }

        if (dibujarVacas) {
            Drawable d = getResources().getDrawable(R.drawable.vaca_actionbar, null);
            int vacaWidth = d.getIntrinsicWidth();
            int vacaHeight = d.getIntrinsicHeight();
            System.out.println("Cantidad de vacas modificadas: " + vacasModificadas.size());
            for (Integer i : vacas.keySet()) {
                Vaca vacaCanvas;
                if (vacasModificadas.containsKey(i)) {
                    vacaCanvas = vacasModificadas.get(i);
                    isVacaModificada = true;
                } else {
                    vacaCanvas = vacas.get(i);
                    isVacaModificada = false;
                }
                d.setBounds((int) ((vacaCanvas.getX() - d.getIntrinsicWidth() / 2) * ancho), (int) ((vacaCanvas.getY() - vacaHeight / 2) * alto), (int) ((vacaCanvas.getX() + d.getIntrinsicWidth() / 2) * ancho), (int) ((vacaCanvas.getY() + vacaHeight / 2) * alto));
                d.draw(canvas);
                if (dibujarComederos && drawVecinos && vacasVecinas.contains(i)) {
                    paint.setColor(Color.CYAN);
                    canvas.drawCircle((vacaCanvas.getX()) * ancho, (vacaCanvas.getY()) * alto, vacaWidth / 2, paint);
                    paint.setColor(Color.BLUE);
                } else if (drawIntervalo && vacasSeleccionadas.contains(i)) {
                    paint.setColor(Color.YELLOW);
                    canvas.drawCircle((vacaCanvas.getX()) * ancho, (vacaCanvas.getY()) * alto, vacaWidth / 2, paint);
                    paint.setColor(Color.BLUE);
                } else if (drawEvento) {
                    if (vacasIn.contains(i)) {
                        paint.setColor(Color.GREEN);
                        canvas.drawCircle((vacaCanvas.getX()) * ancho, (vacaCanvas.getY()) * alto, vacaWidth / 2, paint);
                        paint.setColor(Color.BLUE);
                    } else if (vacasOut.contains(i)) {
                        paint.setColor(Color.RED);
                        canvas.drawCircle((vacaCanvas.getX()) * ancho, (vacaCanvas.getY()) * alto, vacaWidth / 2, paint);
                        paint.setColor(Color.BLUE);
                    }
                } else if (drawTrayectoria && i == (vacaSelected)) {
                    paint.setColor(Color.rgb(255, 127, 0));
                    canvas.drawCircle((vacaCanvas.getX()) * ancho, (vacaCanvas.getY()) * alto, vacaWidth / 2, paint);
                    int tSize = trayectoria.size() - 1;
                    for (int j = 0; j < tSize; j++) {
                        canvas.drawLine((trayectoria.get(tSize - j).x) * ancho, (trayectoria.get(tSize - j).y) * alto, (trayectoria.get(tSize - j - 1).x) * ancho, (trayectoria.get(tSize - j - 1).y) * alto, paint);
                    }
                    if (tSize > 0) {
                        dibujarFlecha(canvas, (trayectoria.get(1).x) * ancho, (trayectoria.get(1).y) * alto, (trayectoria.get(0).x) * ancho, (trayectoria.get(0).y) * alto, 6, 6, paint);
                    }

                    paint.setColor(Color.BLUE);
                }
                if (isVacaModificada && (vacas.get(i).getX() != vacasModificadas.get(i).getX() || vacas.get(i).getY() != vacasModificadas.get(i).getY())) {
                    oldsVacas.put(i, vacas.put(i, vacasModificadas.get(i)));
                } else if (oldsVacas.get(i) != null){
                    oldsVacas.remove(i);
                }

                if (oldsVacas.get(i) != null) {//CONDICION AGREGADA PARA ARREGLAR ERROR DE IR AL FINAL Y MOVER ANTERIOR
                    System.out.println("Vaca modificada: " + i + " - (X: " + oldsVacas.get(i).getX() + ", " + oldsVacas.get(i).getY() + ")"
                            + " -> (X: " + vacas.get(i).getX() + ", " + vacas.get(i).getY() + ")");
                    //canvas.drawCircle((oldsVacas.get(i).getX() + d.getIntrinsicWidth() / 2) * ancho, (oldsVacas.get(i).getY() + d.getIntrinsicHeight() / 2) * alto, 4, paint);
                    //canvas.drawLine((oldsVacas.get(i).getX() + d.getIntrinsicWidth() / 2) * ancho, (oldsVacas.get(i).getY() + d.getIntrinsicHeight() / 2) * alto, (vacasModificadas.get(i).getX() + d.getIntrinsicWidth() / 2) * ancho, (vacasModificadas.get(i).getY() + d.getIntrinsicHeight() / 2) * alto, paint);
                    dibujarFlecha(canvas, (oldsVacas.get(i).getX()) * ancho, (oldsVacas.get(i).getY()) * alto, (vacas.get(i).getX()) * ancho, (vacas.get(i).getY()) * alto, 6, 6, paint);
                }
            }
        }

        if (dibujarComederos) {
            Drawable d = getResources().getDrawable(R.drawable.cubeta, null);
            int comederoWidth = d.getIntrinsicWidth();
            int comederoHeight = d.getIntrinsicHeight();
            for (Integer i : comederos.keySet()) {
                d.setBounds((int) ((comederos.get(i).getX() - comederoWidth / 2) * ancho), (int) ((comederos.get(i).getY() - comederoHeight / 2) * alto),
                        (int) ((comederos.get(i).getX() + comederoWidth / 2) * ancho), (int) ((comederos.get(i).getY() + comederoHeight / 2) * alto));
                d.draw(canvas);
            }
        }
        canvas.restore();
        //return;
    }

    private void dibujarFlecha(Canvas g, float x1, float y1, float x2, float y2, float d, float h, Paint p) {
        /**
         * Draw an arrow line between two points.
         *
         * @param g the graphics component.
         * @param x1 x-position of first point.
         * @param y1 y-position of first point.
         * @param x2 x-position of second point.
         * @param y2 y-position of second point.
         * @param d the width of the arrow.
         * @param h the height of the arrow.
         */
        float dx = x2 - x1, dy = y2 - y1;
        float D = (float) Math.sqrt(dx * dx + dy * dy);
        float xm = D - d, xn = xm, ym = h, yn = -h, x;
        float sin = dy / D, cos = dx / D;

        x = xm * cos - ym * sin + x1;
        ym = xm * sin + ym * cos + y1;
        xm = x;

        x = xn * cos - yn * sin + x1;
        yn = xn * sin + yn * cos + y1;
        xn = x;

        /*float[] xpoints = {x2, (int) xm, (int) xn};
        float[] ypoints = {y2, (int) ym, (int) yn};*/

        g.drawLine(x1, y1, x2, y2, p);

        Path wallpath = new Path();
        wallpath.reset(); // only needed when reusing this path for a new build
        wallpath.moveTo(xm, ym); // used for first point
        wallpath.lineTo(x2, y2);
        wallpath.lineTo(xn, yn); // there is a setLastPoint action but i found it not to work as expected

        g.drawPath(wallpath, p);
    }

    protected Rect getRegion(Point p, int ancho) {
        ancho = ancho / 2;
        return new Rect(p.x - ancho / 2, p.y - ancho / 2, p.x + ancho, p.y + ancho);
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent me) {

        mScaleDetector.onTouchEvent(me);
        final int secondAction = me.getActionMasked();
        if ((me.getAction() == MotionEvent.ACTION_DOWN && (drawEvento || drawIntervalo)) ||
                (secondAction == MotionEvent.ACTION_POINTER_DOWN)) {
            //  Disallow the touch request for parent scroll on touch of child view
            requestDisallowParentInterceptTouchEvent(this, true);
        } else if (me.getAction() == MotionEvent.ACTION_UP /*|| event.getAction() == MotionEvent.ACTION_CANCEL*/) {
            // Re-allows parent events
            requestDisallowParentInterceptTouchEvent(this, false);
        }

        x = me.getX();
        y = me.getY();

        if (me.getAction() == MotionEvent.ACTION_DOWN) {
            accion = "down";
            MotionEventCompat.getActionIndex(me);
            me.getActionIndex();
        }
        if (secondAction == MotionEvent.ACTION_POINTER_DOWN) {
            accion = "zoom";
            MotionEventCompat.getActionIndex(me);
            me.getActionIndex();
        } else {
            if (me.getAction() == MotionEvent.ACTION_MOVE) {
                accion = "move";
            }
            if (me.getAction() == MotionEvent.ACTION_UP) {
                if (!(drawEvento || drawIntervalo) && (accion == "down")) {
                    accion = "select";
                } else if (drawEvento || drawIntervalo) {
                    if ((x < xDown) && (y < yDown)) {
                        stop(x / ancho, y / alto, xDown / ancho, yDown / alto);
                    } else if (x < xDown) {
                        stop(x / ancho, yDown / alto, xDown / ancho, y / alto);
                    } else if (y < yDown) {
                        stop(xDown / ancho, y / alto, x / ancho, yDown / alto);
                    } else {
                        stop(xDown / ancho, yDown / alto, x / ancho, y / alto);
                    }
                }
            }
        }

        invalidate();
        return true;
    }

    private void requestDisallowParentInterceptTouchEvent(View __v, Boolean __disallowIntercept) {
        while (__v.getParent() != null && __v.getParent() instanceof View) {
            if (__v.getParent() instanceof ScrollView) {
                __v.getParent().requestDisallowInterceptTouchEvent(__disallowIntercept);
            }
            __v = (View) __v.getParent();
        }
    }

    public void drawVacas(boolean dibujar) {
        dibujarVacas = dibujar;
        this.invalidate(); //PARA REDIBUJAR
    }

    public void drawVacasInicio(boolean dibujar) {
        dibujarVacas = dibujar;
        this.vacasModificadas.clear();
        this.invalidate(); //PARA REDIBUJAR
    }

    public void setVacas(Map<Integer, Vaca> vacas) {
        this.vacas = vacas;
    }

    /*public void addVaca(Integer id, Vaca v){
        this.vacas.put(id, v);
    }*/

    public void setVacasModificadas(Map<Integer, Vaca> vacas) {
        this.vacasModificadas = vacas;
    }

    private Eventos mOnStopTrackEventListener;

    public void setOnStopTrackEventListener(Eventos eventListener) {
        mOnStopTrackEventListener = eventListener;
    }

    public void stop(float x, float y, float xDown, float yDown) {
        if (mOnStopTrackEventListener != null) {
            mOnStopTrackEventListener.onStopTrack(x, y, xDown, yDown);
        }
    }

    public void vacaChosen(int idVaca) {
        if (mOnStopTrackEventListener != null) {
            mOnStopTrackEventListener.onVacaChosen(idVaca);
            accion = "";
        }
    }

    public void comederoChosen(int idComedero) {
        if (mOnStopTrackEventListener != null) {
            mOnStopTrackEventListener.onComederoChosen(idComedero);
            accion = "";
        }
    }

    public int getComederoSeleccionado() {
        return this.comederoSelected;
    }

    public void setVacasSeleccionadas(ArrayList<Integer> vacasID) {
        this.vacasSeleccionadas = vacasID;
    }

    public void setVacasVecinas(ArrayList<Integer> vacasID) {
        this.vacasVecinas = vacasID;
    }

    public void setVacasInOut(ArrayList<Integer> vacasIn, ArrayList<Integer> vacasOut) {
        this.vacasIn = vacasIn;
        this.vacasOut = vacasOut;
    }

    public void setTrayectoria(int vacaID, ArrayList<Punto> trayectoria) {
        this.vacaSelected = vacaID;
        this.trayectoria = trayectoria;
    }

    public void setAction(Consultas action) {
        switch (action) {
            case VECINOS:
                this.drawIntervalo = false;
                this.drawEvento = false;
                this.drawTrayectoria = false;
                this.drawVecinos = true;
                break;
            case INTERVALO:
                this.drawIntervalo = true;
                this.drawEvento = false;
                this.drawTrayectoria = false;
                this.drawVecinos = false;
                break;
            case EVENTO:
                this.drawIntervalo = false;
                this.drawEvento = true;
                this.drawTrayectoria = false;
                this.drawVecinos = false;
                break;
            case TRAYECTORIA:
                this.drawIntervalo = false;
                this.drawEvento = false;
                this.drawTrayectoria = true;
                this.drawVecinos = false;
                break;
            case CLEAR:
                this.drawIntervalo = false;
                this.drawEvento = false;
                this.drawTrayectoria = false;
                this.vacasModificadas.clear();
                this.vacasSeleccionadas.clear();
                this.vacasIn.clear();
                this.vacasOut.clear();
                this.vacaSelected = -1;
                this.vacasVecinas.clear();
                this.drawVecinos = false;
                this.invalidate();
        }
    }

    public void setComederos(Map<Integer, Vaca> comederosId) {
        this.comederos = comederosId;
    }

    public void drawComederos(boolean dibujar) {
        this.dibujarComederos = dibujar;
        this.drawVecinos = dibujar;
        this.invalidate();
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(1f, Math.min(mScaleFactor, 5.0f));

            mFocusX = detector.getFocusX();
            mFocusY = detector.getFocusY();
            invalidate();
            return true;
        }
    }

}
