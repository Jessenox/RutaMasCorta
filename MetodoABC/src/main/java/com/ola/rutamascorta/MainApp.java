package com.ola.rutamascorta;

import com.ola.controllers.Articulos;
import com.ola.controllers.Controlador;
import com.ola.controllers.Final;
import com.ola.controllers.TablaCompleta;
import com.ola.controllers.TagController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainApp extends Application{
    int cont = 0;
    int distanciaTotal = 0;
    ArrayList<Controlador> array = new ArrayList<>();
    ArrayList<TagController> tags = new ArrayList<>();
    ArrayList<TagController> shortestPath = new ArrayList<>();
    List<Integer> nodos = new ArrayList<>();
    
    private ArrayList<Articulos> artis = new ArrayList<>();
    private ArrayList<TablaCompleta> tablosa = new ArrayList<>();
    private ArrayList<Float> porcentajes = new ArrayList<>();
    private ArrayList<Final> fina = new ArrayList<>();
    
    @Override
    public void start(Stage stage) throws Exception {
        
        AnchorPane root = new AnchorPane();
        Scene s = new Scene(root, 860, 480, Color.WHITE);
        
        
        Label lab1 = new Label("Metodo ABC");
        VBox mainLabel = new VBox();
        
        // Texto de nodos, etc
        HBox txtContainer = new HBox();
        Label ndIniLabel = new Label(" Articulo | ");
        Label ndFinLabel = new Label(" Consumo anual en unidades |");
        Label ndDistance = new Label(" Costo unitario $ | ");
        txtContainer.getChildren().addAll(ndIniLabel, ndDistance, ndFinLabel);
        
        VBox nodesContainer = new VBox();
        HBox actionButtonsContainer = new HBox();
        // Action buttons
        Button addNodeButton = new Button("Añadir articulos");
        Button removeNodeButton = new Button("Eliminar articulos");
        Button calculateButton = new Button("Calcular");
        removeNodeButton.setDisable(true);
        
        // Event Handler for addNodeButton
        addNodeButton.setOnAction(eh -> {
            cont++;
            if(cont == 1) {
                removeNodeButton.setDisable(false);
            }
            // Textfields for nodes
            HBox txtFldContainer = new HBox();
            TextField ndIniTxtFld =  new TextField();
            ndIniTxtFld.setId("nodoInicial");
            TextField ndFinTxtFld =  new TextField();
            ndFinTxtFld.setId("nodoFinal");
            TextField distTxtFld =  new TextField();
            distTxtFld.setId("distancia");
            txtFldContainer.getChildren().addAll(ndIniTxtFld, distTxtFld, ndFinTxtFld);
            nodesContainer.getChildren().add(txtFldContainer);
        });
        
        // Event handler for removeNodeButton
        removeNodeButton.setOnAction(eh -> {
           if(cont == 1){
               removeNodeButton.setDisable(true);
           }
            cont--;
            nodesContainer.getChildren().remove(cont);
        });
        
        // Event handler for calculateButton
        calculateButton.setOnAction(eh -> {
            artis.clear();
            porcentajes.clear();
            fina.clear();
            tablosa.clear();
            getAllNodesData(nodesContainer);
            float num = getActualNodes(artis);
            float parti = porcentaje(num, tablosa);
            ordenamiento(tablosa, porcentajes);
            elPro(parti, porcentajes);
            

            resultWindow();
        });
        
        actionButtonsContainer.getChildren().addAll(addNodeButton, removeNodeButton);
        mainLabel.getChildren().addAll(lab1, txtContainer, nodesContainer, actionButtonsContainer, calculateButton);
        root.getChildren().add(mainLabel);
        // Show main app
        stage.setScene(s);
        stage.setTitle("Metodo ABC");
        stage.show();
    }
    
    public static void main(String[] args) {
        launch();
    }
    private float porcentaje(float numerr, ArrayList<TablaCompleta> tabi) {
        for (TablaCompleta completa : tabi) {
            float porcentaje = (100.0f * completa.getValorAnual()) / numerr;
            porcentajes.add(porcentaje);
        }
    
        return 100f / tabi.size();
    }
    public void elPro(float parti, ArrayList<Float> floti) {
        float porcentajeAcumulado = 0;
        float ggga = parti;
        for (int i = 0; i < tablosa.size(); i++) {
            TablaCompleta tabgd = tablosa.get(i);
            Final fnn = new Final();
            fnn.setNumero(Integer.parseInt(tabgd.getArticulo()));
            fnn.setPorcentajePart(parti);
            fnn.setPorcAcumulado(ggga);
            fnn.setPorcentValora(floti.get(i));
            porcentajeAcumulado += floti.get(i);
            fnn.setPorcentValoraAcum(porcentajeAcumulado);
            ggga+=parti;
            if(porcentajeAcumulado < 80){
                fnn.setClase('A');
            }else if(porcentajeAcumulado >80 && porcentajeAcumulado < 90){
                fnn.setClase('B');
            }else{
                fnn.setClase('C');
            }
            System.out.println(fnn.getNumero()+" "+ fnn.getPorcentajePart() +" "+fnn.getPorcAcumulado()+" "+ fnn.getPorcentValora()+" "+ fnn.getPorcentValoraAcum() + " "+fnn.getClase());
            fina.add(fnn);
        }
    }
    private void ordenamiento(ArrayList<TablaCompleta> tabg, ArrayList<Float> porcentajes){
        Comparator<TablaCompleta> comparadorValorAnual = Comparator.comparing(TablaCompleta::getValorAnual);
        Collections.sort(tablosa, comparadorValorAnual.reversed());
        
        Comparator<Float> flotes = Comparator.reverseOrder();
        Collections.sort(porcentajes, flotes);
        
    }
    private float getActualNodes(ArrayList<Articulos> ar) {
        float numero = 0;
        for (Articulos articulos : ar) {
            TablaCompleta tab = new TablaCompleta();
            tab.setArticulo(articulos.getArticulo());
            tab.setConsumoAnual(articulos.getConsumoAnual());
            tab.setCostoUnitario(articulos.getCostoUnitario());
            tab.setValorAnual(articulos.getConsumoAnual() * articulos.getCostoUnitario());
            tablosa.add(tab);
            numero += tab.getValorAnual();
        }
        return numero;
    }
    
    private void getAllNodesData(VBox nodeContainer) {
        // Get HBox containers
        for (Node nodo: nodeContainer.getChildren()) { 
            HBox fields = (HBox)nodo;
            Articulos art = new Articulos();
            // Get TextFields containers
            for(Node txtFields: fields.getChildren()){
                TextField aux = (TextField)txtFields;
                switch (aux.getId()) {
                    case "nodoInicial":
                        art.setArticulo(aux.getText());
                        break;
                    case "nodoFinal":
                        art.setConsumoAnual(Integer.parseInt(aux.getText()));
                        break;
                    case "distancia":
                        art.setCostoUnitario(Float.parseFloat(aux.getText()));
                        break;
                    default:
                        throw new AssertionError();
                }
            }
            // Add nodes information to ArrayList
            artis.add(art);
        }
    }
    
    private void shortestPath (ArrayList<Controlador> al) {
        
    }
    
    private void showOriginNodeMessage(){
        
    }
    private void showDestinationNodeMessage(){
       
    }
    private void tagDistanceCalculation() {
       
    }
        // Window that shows the result and a graph
    private void resultWindow() {
        AnchorPane root = new AnchorPane();
        Scene resultWindowScene = new Scene(root, 860, 480, Color.WHITE);
        Stage resultWindowStage = new Stage();
        
        VBox mainContainer = new VBox();
        
        Label resultLabel = new Label("Resultado");
        Label txtResultLabel = new Label("El analisis ABC de la tabla nos da: ");
        TableView<Final> tablaview = new TableView<>();
        TableColumn<Final, Integer> articuloColumna = new TableColumn<>("Articulo #");
        TableColumn<Final, Float> porcentajeColum = new TableColumn<>("% de participacion");
        TableColumn<Final, Float> porcentajeAcum = new TableColumn<>("% de participacion acumulado");
        TableColumn<Final, Float> porcentajeValor = new TableColumn<>("% de valorizacion");
        TableColumn<Final, Float> porcentajeValorAcum = new TableColumn<>("% de valorizacion acumulado");
        TableColumn<Final, String> clase = new TableColumn<>("Clase");
        
        articuloColumna.setCellValueFactory(new PropertyValueFactory<>("numero"));
        porcentajeColum.setCellValueFactory(new PropertyValueFactory<>("porcentajePart"));
        porcentajeAcum.setCellValueFactory(new PropertyValueFactory<>("porcAcumulado"));
        porcentajeValor.setCellValueFactory(new PropertyValueFactory<>("porcentValora"));
        porcentajeValorAcum.setCellValueFactory(new PropertyValueFactory<>("porcentValoraAcum"));
        clase.setCellValueFactory(new PropertyValueFactory<>("clase"));
        
        tablaview.getColumns().addAll(articuloColumna, porcentajeColum, porcentajeAcum, porcentajeValor,porcentajeValorAcum, clase);
        
        ObservableList<Final> datosos = FXCollections.observableArrayList(fina);
        
        tablaview.setItems(datosos);
        Button grafica = new Button("Ver gráfica");
        grafica.setOnAction(eh->{
            grafico();
        });
        mainContainer.getChildren().addAll(resultLabel, txtResultLabel, tablaview, grafica);
        
        root.getChildren().add(mainContainer);
        // Set scene and show the window
        
        resultWindowStage.setScene(resultWindowScene);
        resultWindowStage.setTitle("Resultado");
        resultWindowStage.show();
    }
    private void grafico(){
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> lineSeries = new XYChart.Series<>();
        ObservableList<Final> datos = FXCollections.observableArrayList(fina);
        for (Final dato : datos) {
            String categoria = String.valueOf(dato.getNumero());
            float valor = dato.getPorcentValora();
            float acumulado = dato.getPorcentValoraAcum();

            barSeries.getData().add(new XYChart.Data<>(categoria, valor));
            lineSeries.getData().add(new XYChart.Data<>(categoria, acumulado));
        }

        barChart.getData().add(barSeries);
        lineChart.getData().add(lineSeries);

        VBox chartContainer = new VBox();
        chartContainer.getChildren().addAll(barChart, lineChart);

        barChart.setPrefHeight(400);
        lineChart.setPrefHeight(400);
        
        barChart.setAnimated(false);
        lineChart.setAnimated(false);
        
        
        Stage graficaStage = new Stage();
        Scene graficaScene = new Scene(chartContainer, 800, 600);
        graficaStage.setScene(graficaScene);
        graficaStage.setTitle("Gráficos");
        graficaStage.show();
        
    }

}
