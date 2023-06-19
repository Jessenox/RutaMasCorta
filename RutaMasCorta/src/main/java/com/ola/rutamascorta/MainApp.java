package com.ola.rutamascorta;

import com.ola.controllers.Controlador;
import com.ola.controllers.TagController;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainApp extends Application{
    int cont = 0;
    int nodoOrigen;
    int nodoDestino;
    ArrayList<Controlador> array = new ArrayList<>();
    ArrayList<TagController> tags = new ArrayList<>();
    List<Integer> nodos = new ArrayList<>();
    
    @Override
    public void start(Stage stage) throws Exception {
        AnchorPane root = new AnchorPane();
        Scene s = new Scene(root, 860, 480, Color.WHITE);
        
        
        Label lab1 = new Label("Ruta mas corta");
        VBox mainLabel = new VBox();
        
        // Texto de nodos, etc
        HBox txtContainer = new HBox();
        Label ndIniLabel = new Label("Nodo Inicial");
        Label ndFinLabel = new Label("Nodo Final");
        Label ndDistance = new Label("Distancia");
        txtContainer.getChildren().addAll(ndIniLabel, ndDistance, ndFinLabel);
        
        VBox nodesContainer = new VBox();
        HBox actionButtonsContainer = new HBox();
        // Action buttons
        Button addNodeButton = new Button("Anadir nodos");
        Button removeNodeButton = new Button("Eliminar");
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
            getAllNodesData(nodesContainer);
            getActualNodes(array);
            showOriginNodeMessage();
            showDestinationNodeMessage();
            //shortestPath(array);
            //resultWindow();
        });
        
        actionButtonsContainer.getChildren().addAll(addNodeButton, removeNodeButton);
        mainLabel.getChildren().addAll(lab1, txtContainer, nodesContainer, actionButtonsContainer, calculateButton);
        root.getChildren().add(mainLabel);
        // Show main app
        stage.setScene(s);
        stage.setTitle("Ruta mas corta");
        stage.show();
    }
    
    public static void main(String[] args) {
        launch();
    }
    
    private void getActualNodes(ArrayList<Controlador> al){
        // Get All nodes
        for (Controlador controlador : al) {
            if(!nodos.contains(controlador.getNodeBase())){
                nodos.add(controlador.getNodeBase());
            }
        }
    }
    
    private void getAllNodesData(VBox nodeContainer) {
        // Get HBox containers
        for (Node nodo: nodeContainer.getChildren()) { 
            HBox fields = (HBox)nodo;
            Controlador obj = new Controlador();
            // Get TextFields containers
            for(Node txtFields: fields.getChildren()){
                TextField aux = (TextField)txtFields;
                switch (aux.getId()) {
                    case "nodoInicial":
                        obj.setNodeBase(Integer.parseInt(aux.getText()));
                        break;
                    case "nodoFinal":
                        obj.setNodeEnded(Integer.parseInt(aux.getText()));
                        break;
                    case "distancia":
                        obj.setNodeDistance(Integer.parseInt(aux.getText()));
                        break;
                    default:
                        throw new AssertionError();
                }
            }
            // Add nodes information to ArrayList
            array.add(obj);
        }
    }
    // Window that shows the result and a graph
    private void resultWindow() {
        AnchorPane root = new AnchorPane();
        Scene resultWindowScene = new Scene(root, 860, 480, Color.WHITE);
        Stage resultWindowStage = new Stage();
        
        VBox mainContainer = new VBox();
        Label resultLabel = new Label("Resultado");
        Label txtResultLabel = new Label("La ruta mas corta es: ");
        Canvas canvas = new Canvas(840, 300);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        gc.setFill(Color.YELLOW);
        gc.strokeOval(30, 30, 70, 70);
        mainContainer.getChildren().addAll(resultLabel, txtResultLabel, canvas);
        
        root.getChildren().add(mainContainer);
        // Set scene and show the window
        resultWindowStage.setScene(resultWindowScene);
        resultWindowStage.setTitle("Resultado");
        resultWindowStage.show();
    }
    
    private void shortestPath (ArrayList<Controlador> al) {
        Integer dist = 0;
        Integer nodeOrigin = 0;
        Boolean exists = false;
        // Check node's distance
        for (Integer nodo : nodos) {
            TagController obj = new TagController();
            for (Controlador ctrl : al) {
                // Verify the node with current loop
                if(nodo == ctrl.getNodeEnded()) {
                    if(dist == 0) {
                        dist = ctrl.getNodeDistance();
                        nodeOrigin = ctrl.getNodeBase();
                    } else {
                        if(ctrl.getNodeDistance() < dist) {
                            dist = ctrl.getNodeDistance();
                            nodeOrigin = ctrl.getNodeBase();
                        }
                    }
                }
            }
            obj.setNode(nodo);
            if(nodeOrigin == 0) {
                obj.setTagOrigin(0);
                obj.setTagDistance(0);
            } else {
                obj.setTagOrigin(nodeOrigin);
                obj.setTagDistance(dist);
            }
            tags.add(obj);
            dist = 0;
        }
        for (TagController tag : tags) {
            System.out.println("Nodo: " + tag.getNode());
            System.out.println("Nodo origen " + tag.getTagOrigin());
            System.out.println("Distancia " + tag.getTagDistance());
        }
    }
    
    private void showOriginNodeMessage(){
        ChoiceDialog cd = new ChoiceDialog(nodos.toArray()[0],nodos.toArray());
        cd.setHeaderText("Selecciona nodo origen");
        cd.showAndWait();
        nodoOrigen = (int)cd.getSelectedItem();
        System.out.println("Nodo origen: " + nodoOrigen);
    }
    private void showDestinationNodeMessage(){
        ChoiceDialog cd = new ChoiceDialog(nodos.toArray()[0],nodos.toArray());
        cd.setHeaderText("Selecciona nodo destino");
        cd.showAndWait();
        nodoDestino = (int)cd.getSelectedItem();
        System.out.println("Nodo destino: " + nodoDestino);
    }
}
