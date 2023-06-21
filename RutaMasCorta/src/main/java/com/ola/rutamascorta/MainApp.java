package com.ola.rutamascorta;

import com.ola.controllers.Controlador;
import com.ola.controllers.DrawNode;
import com.ola.controllers.TagController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
    int distanciaTotal = 0;
    ArrayList<Controlador> array = new ArrayList<>();
    ArrayList<TagController> tags = new ArrayList<>();
    ArrayList<TagController> shortestPath = new ArrayList<>();
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
            shortestPath(array);
            resultWindow();
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
            if(!nodos.contains(controlador.getNodeEnded())){
                nodos.add(controlador.getNodeEnded());
            }
        }
        Collections.sort(nodos);
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
    
    private void shortestPath (ArrayList<Controlador> al) {
        tagDistanceCalculation();
        for (TagController tag : tags) {
            System.out.println("Nodo: " + tag.getNode());
            System.out.println("Distancia " + tag.getTagDistance());
            System.out.println("Nodo destino: " + tag.getTagOrigin());
        }
        int i = 0;
        int k = tags.size();
        int j = nodoOrigen;
        while(j != nodoDestino){
            if(tags.get(i).getNode() == j){
                shortestPath.add(tags.get(i));
                j = tags.get(i).getTagOrigin();
                distanciaTotal += tags.get(i).getTagDistance();
            }
            i++;
            if(i == k-1)
                i = 0;
        }
        System.out.println("Ruta mas corta");
        for (TagController sp : shortestPath) {
            System.out.println(sp.getNode()+" "+sp.getTagDistance()+" "+sp.getTagOrigin());
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
    private void tagDistanceCalculation() {
        int distance = 0;
        int nodoMenor = 0;
        int aux;
        for (Integer nodo : nodos) {
            TagController tagObj = new TagController();
            if(nodo == nodoDestino){
                tagObj.setNode(nodo);
                tagObj.setTagDistance(0);
                tagObj.setTagOrigin(0);
            } else {
                // Get node with their branches
                List<Controlador> cjn = array.stream().filter(item -> 
                    item.getNodeBase()== nodo)
                    .collect(Collectors.toList());
                // Get the minum distance
                for (Controlador ls : cjn) {
                    aux = ls.getNodeDistance();
                    if(distance == 0){
                        distance = aux;
                        nodoMenor = ls.getNodeEnded();
                    } else{
                        if(aux < distance){
                            distance = aux;
                            nodoMenor = ls.getNodeEnded();
                        }
                    }
                }      
                //set data
                tagObj.setNode(nodo);
                tagObj.setTagDistance(distance);
                tagObj.setTagOrigin(nodoMenor);
            }
            tags.add(tagObj);
            distance = 0;
            nodoMenor = 0;
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
        Canvas canvas = new Canvas(840, 400);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // int numeros[] = {1,2,3,4,5,6,7,8,9,10};
        int x = 20, y= 200;
        ArrayList<DrawNode> positions = new ArrayList<>();
        ArrayList<DrawNode> textPositions = new ArrayList<>();
        
        // Set nodes positions
        for (int i = 0, j = 1; i < nodos.size(); i++, j++) {
            DrawNode dn = new DrawNode();
            DrawNode dn2 = new DrawNode();
            switch (j) {
                case 1:
                    dn.setX(x);
                    dn.setY(y);
                    dn.setNodo(nodos.get(i));
                    positions.add(dn);
                    dn2.setX(x+15);
                    dn2.setY(y+25);
                    dn2.setNodo(nodos.get(i));
                    textPositions.add(dn2);
                    break;
                case 2:
                    dn.setX(x+50);
                    dn.setY(y-100);
                    dn.setNodo(nodos.get(i));
                    positions.add(dn);
                    dn2.setX(x+65);
                    dn2.setY(y-75);
                    dn2.setNodo(nodos.get(i));
                    textPositions.add(dn2);
                    break;
                case 3:
                    dn.setX(x+50);
                    dn.setY(y+100);
                    dn.setNodo(nodos.get(i));
                    positions.add(dn);
                    dn2.setX(x+65);
                    dn2.setY(y+125);
                    dn2.setNodo(nodos.get(i));
                    textPositions.add(dn2);
                    
                    x += 250;
                    j = 0;
                    break;
            }
            System.out.println("Nodo: " + nodos.get(i));
        }
        System.out.println("Tamanno: " + nodos.size());
        for (Controlador data : array) {
            int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
            int xm, ym;
            for (DrawNode pos : textPositions) {
                if(data.getNodeBase() == pos.getNodo()) {
                    x1 = pos.getX();
                    y1 = pos.getY();
                }
            }
            for (DrawNode pos : textPositions) {
                if(data.getNodeEnded()== pos.getNodo()) {
                    x2 = pos.getX();
                    y2 = pos.getY();
                }
            }
            xm = (x2 + x1) / 2;
            ym = (y2 + y1) / 2;
            if(x1 != 0 ||y1 != 0 ||x2 != 0 || y2 != 0){
                gc.fillText(String.valueOf(data.getNodeDistance()), xm, ym);
                gc.strokeLine(x1, y1, x2, y2);
            }
        }
        
        // Draw nodes
        for (DrawNode position : positions) {
            gc.setFill(Color.BLACK);
            System.out.println("x: " + position.getX() + " y: " + position.getY());
            gc.strokeOval(position.getX(), position.getY(), 40, 40);
            gc.setFill(Color.WHITE);
            gc.fillOval(position.getX(), position.getY(), 40, 40);
        }
        gc.setFill(Color.BLACK);
        for (DrawNode txtPos : textPositions) {
            gc.fillText(String.valueOf(txtPos.getNodo()), txtPos.getX(), txtPos.getY());
        }
        
        
        mainContainer.getChildren().addAll(resultLabel, txtResultLabel, canvas);
        
        root.getChildren().add(mainContainer);
        // Set scene and show the window
        resultWindowStage.setScene(resultWindowScene);
        resultWindowStage.setTitle("Resultado");
        resultWindowStage.show();
    }
    

}
