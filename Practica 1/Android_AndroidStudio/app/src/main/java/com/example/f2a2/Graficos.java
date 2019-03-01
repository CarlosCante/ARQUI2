package com.example.f2a2;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class Graficos extends AppCompatActivity {

    String valoresNoSplit = null;
    BarChart graficaBarras;
    TextView txtCo2;
    TextView txtCo;
    TextView txtUv;
    TextView txtMsg;

    String[] sensores = new String[]{"CO2","CO","UV"};
    int[] toxicidad = new int[]{0,0,0};
    int[] colors = new int[]{Color.GREEN,Color.BLUE,Color.YELLOW};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graficos);

        graficaBarras = (BarChart)findViewById(R.id.graficaBarras);
        txtCo2 = (TextView)findViewById(R.id.txtCo2);
        txtCo = (TextView)findViewById(R.id.txtCo);
        txtUv = (TextView)findViewById(R.id.txtUv);
        txtMsg = (TextView)findViewById(R.id.txtMsg);
//        valoresNoSplit = getIntent().getExtras().getString("vals");
        //String valSensNoSplit = "351,77,2;351,76,2;351,75,2;347,74,2;351,75,2;347,74,2;347,75,2;342,74,2;347,74,2;342,74,2";
        String tipoSensor = getIntent().getExtras().getString("tSensor");
        String valSensNoSplit = getIntent().getExtras().getString("vals");
        String[] valSens  = valSensNoSplit.split(";");

        ArrayList<String> co2 = new ArrayList<>();
        ArrayList<String> co = new ArrayList<>();
        ArrayList<String> uv = new ArrayList<>();


        for(String c: valSens){ //foreach
            String[] fil = c.split(",");
            co2.add(fil[0]);
            co.add(fil[1]);
            uv.add(fil[2]);
        }

        if(tipoSensor.equals("all")){
            toxicidad[0] = sacarMedia(co2);
            toxicidad[1] = sacarMedia(co);
            toxicidad[2] = sacarMedia(uv);
        } else if(tipoSensor.equals("co2")){
            toxicidad[0] = sacarMedia(co2);
        } else if(tipoSensor.equals("co")) {
            toxicidad[1] = sacarMedia(co);
        } else if(tipoSensor.equals("uv")) {
            toxicidad[2] = sacarMedia(uv);
        }

        createCharts();
        decidirNivel();
    }

    public Chart getSameChart(Chart chart, String descripcion, int textColor, int background, int animateY){
        chart.getDescription().setText("");
        chart.getDescription().setTextSize(15);
        chart.setBackgroundColor(background);
        chart.animateY(animateY);
        legend(chart);
        return chart;  //retorno d ela grafica ya persnalizada
    }

    public void legend(Chart chart){
        Legend legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        //datos que van dentro de la leyenda
        ArrayList<LegendEntry> entries = new ArrayList<>();
        for (int i = 0 ; i < sensores.length; i++){
            LegendEntry entry = new LegendEntry();
            entry.formColor=colors[i];
            entry.label=sensores[i];
            entries.add(entry);
        }

        legend.setCustom(entries);
    }

    //ahora vamos con los valores que tiene nuestras graficas

    public ArrayList<BarEntry> getBarEntries(){
        ArrayList<BarEntry>  entries = new ArrayList<>();
        for(int i = 0; i < toxicidad.length; i++)
            entries.add(new BarEntry(i,toxicidad[i]));
        return entries;
    }

    public  void axisX(XAxis axis){
        axis.setGranularityEnabled(true);
        axis.setPosition(XAxis.XAxisPosition.BOTTOM);
        axis.setValueFormatter(new IndexAxisValueFormatter(sensores));
    }

    public  void axisLeft(YAxis axis){
        axis.setSpaceTop(30);
        axis.setAxisMinimum(0);
        axis.setGranularity(10);
    }

    public  void axisRigth(YAxis axis){ //para desabilitar el lado derecho
        axis.setEnabled(false);
    }

    public void createCharts(){
        graficaBarras =(BarChart)getSameChart(graficaBarras,"Sensores",Color.RED,Color.WHITE,3000);
        graficaBarras.setDrawGridBackground(true); //para que se muestren las lineas de fondo.
        graficaBarras.setDrawBarShadow(true); //agrega sombra a la grafica
        graficaBarras.setData(getBarData());
        graficaBarras.invalidate();

        axisX(graficaBarras.getXAxis());
        axisLeft(graficaBarras.getAxisLeft());
        axisRigth(graficaBarras.getAxisRight());

        graficaBarras.getLegend().setEnabled(false);
    }

    public DataSet getData(DataSet dataSet) { //para agregarles valores
        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10);
        return dataSet;
    }

    private BarData getBarData(){
        BarDataSet barDataSet = (BarDataSet)getData(new BarDataSet(getBarEntries(),""));
        barDataSet.setBarShadowColor(Color.GRAY);
        BarData barData = new  BarData(barDataSet);

        barData.setBarWidth(0.45f);
        return barData;
    }

    public int sacarMedia(ArrayList<String> datos){
        int acumulador = 0 ;

        for(String val: datos){
            acumulador += new Integer(val);
        }

        if(datos.size() != 0)
            return acumulador / datos.size();
        else
            return 0;
    }

    public void decidirNivel(){
        int nivelCo2 = 1;
        int nivelCo = 1;
        int nivelUv = 1;

        if(toxicidad[0] > 200 ){
             if(toxicidad[0] >= 300  && toxicidad[0] <= 500){
                 txtCo2.setText("Bueno");
                 nivelCo2 = 1;
                 txtCo2.setTextColor(Color.GREEN);
             } else if(toxicidad[0] > 500 && toxicidad[0] <=1000){
                 txtCo2.setText("Regular");
                 nivelCo2 = 2;
                 txtCo2.setTextColor(Color.YELLOW);
             } else if(toxicidad[0] > 1000){
                 txtCo2.setText("Malo");
                 nivelCo2 = 3;
                 txtCo2.setTextColor(Color.RED);
             }
        }
        else
            txtCo2.setText("--");

        if(toxicidad[1] > 10 ){
            if(toxicidad[1] >= 30  && toxicidad[1] <= 70){
                txtCo.setText("Bueno");
                nivelCo = 1;
                txtCo.setTextColor(Color.GREEN);
            } else if(toxicidad[1] > 70 && toxicidad[1] <=200){
                txtCo.setText("Regular");
                nivelCo = 2;
                txtCo.setTextColor(Color.YELLOW);
            } else if(toxicidad[1] > 200){
                txtCo.setText("Malo");
                nivelCo = 3;
                txtCo.setTextColor(Color.RED);
            }
        }
        else
            txtCo.setText("--");

        if(toxicidad[2] > 0 ){
            if(toxicidad[2] >= 1  && toxicidad[2] <= 2){
                txtUv.setText("Bueno");
                txtUv.setTextColor(Color.GREEN);
                nivelUv = 1;
            } else if(toxicidad[2] > 2 && toxicidad[2] <=7){
                txtUv.setText("Regular");
                txtUv.setTextColor(Color.YELLOW);
                nivelUv = 2;
            } else if(toxicidad[2] > 7){
                txtUv.setText("Malo");
                txtUv.setTextColor(Color.RED);
                nivelUv = 3;
            }
        }
        else
            txtUv.setText("--");

        if(nivelCo == 1 && nivelCo2 == 1 && nivelUv == 1){
            txtMsg.setText("Hay un excelente ambiente");
        } else if(nivelCo2 == 1 && nivelCo == 2 && nivelUv == 1){
            txtMsg.setText("Hay buen ambiente pero no se exponga mucho al aire");
        } else if(nivelCo2 == 1 && nivelCo == 3 && nivelUv == 1){
            txtMsg.setText("El mabiente es malo no respire mucho aire");
        } else if(nivelCo2 == 2 && nivelCo == 1 && nivelUv == 1){
            txtMsg.setText("El ambiente es bueno pero cuidese del aire que sueltan los vehiculos");
        } else if(nivelCo2 == 3 && nivelCo == 1 && nivelUv == 1){
            txtMsg.setText("el ambiente es malo por el humo de los vehiculos");
        } else if(nivelCo2 == 1 && nivelCo == 1 && nivelUv == 2){
            txtMsg.setText("el sol es bueno pero mejor si usa manga larga y protector solar");
        } else if(nivelCo2 == 1 && nivelCo == 1 && nivelUv == 3){
            txtMsg.setText("el sol es malo use todos los protectores necesarios");
        } else if(nivelCo2 == 2 && nivelCo == 2 && nivelUv == 1){
            txtMsg.setText("el aire es regular mejor no se exponga mucho");
        } else if(nivelCo2 == 2 && nivelCo == 2 && nivelUv == 2){
            txtMsg.setText("el aire y el sol es regular mejor no se exponga mucho");
        } else if(nivelCo2 == 3 && nivelCo == 2 && nivelUv == 1){
            txtMsg.setText("el aire es malo no lo respire mucho tiempo");
        } else if(nivelCo2 == 2 && nivelCo == 3 && nivelUv == 1){
            txtMsg.setText("el ambiente es malo hay mucho humo y gases mejor ni lo respire");
        } else if(nivelCo2 > 1 && nivelCo > 1 && nivelUv == 2){
            txtMsg.setText("el aire y el sol son regulares mejor no se mantenga mucho tiempo en ese lugar de ese lugar");
        } else if(nivelCo2 > 1 && nivelCo > 1 && nivelUv == 3){
            txtMsg.setText("el ire y el sol son malos salga de ese lugar!");
        }
    }
}
