/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appagenda;

import entidades.Persona;
import entidades.Provincia;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.RollbackException;

/**
 * FXML Controller class
 *
 * @author Lorenzo
 */
public class PersonaDetalleViewController implements Initializable {
    
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldApellidos;
    @FXML
    private TextField textFieldTelefono;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldNumHijos;
    @FXML
    private TextField textFieldSalario;
    @FXML
    private CheckBox checkBoxJubilado;
    @FXML
    private RadioButton radioButtonSoltero;
    @FXML
    private RadioButton radioButtonCasado;
    @FXML
    private RadioButton radioButtonViudo;
    @FXML
    private ComboBox<Provincia> comboBoxProvincia;
    @FXML
    private DatePicker datePickerFechaNacimiento;
    @FXML
    private ImageView imageViewFoto;
    @FXML
    private AnchorPane rootPersonaDetalleView;
    
    private AnchorPane rootAgendaView;
    private TableView tableViewPrevio;
    private Persona persona;
    private EntityManager entityManager;
    private boolean nuevaPersona;

    /**
     * Initializes the controller class.
     * @param rootAgendaView
     */
    
    public void setRootAgendaView(AnchorPane rootAgendaView) {
        this.rootAgendaView = rootAgendaView;
    }
    
    public void setTableViewPrevio(TableView tableViewPrevio) {
        this.tableViewPrevio = tableViewPrevio;
    }
    
    public void setPersona(EntityManager entityManager, Persona persona, boolean nuevaPersona) {
        this.entityManager = entityManager;
        entityManager.getTransaction().begin();
        if(!nuevaPersona) {
            this.persona = entityManager.find(Persona.class, persona.getId());
        } else {
            this.persona = persona;
        }
        this.nuevaPersona = nuevaPersona;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    } 
    
    public void mostrarDatos() {
        textFieldNombre.setText(persona.getNombre());
        textFieldApellidos.setText(persona.getApellidos());
        textFieldTelefono.setText(persona.getTelefono());
        textFieldEmail.setText(persona.getEmail());
        
        Query queryProvinciaFindAll = entityManager.createNamedQuery("Provincia.findAll");
        List listProvincia = queryProvinciaFindAll.getResultList();
        comboBoxProvincia.setItems(FXCollections.observableList(listProvincia));
        
        if(persona.getProvincia() != null) {
            comboBoxProvincia.setValue(persona.getProvincia());
        }
        
        comboBoxProvincia.setCellFactory((ListView<Provincia> l) -> new ListCell<Provincia>() {
        @Override
        protected void updateItem(Provincia provincia, boolean empty) {
            super.updateItem(provincia, empty);
            if (provincia == null || empty) {
                setText("");
            } else {
                setText(provincia.getCodigo() + "-" + provincia.getNombre());
            }
        }
        });
        
        comboBoxProvincia.setConverter(new StringConverter<Provincia>() {
            @Override
            public String toString(Provincia provincia) {
                if (provincia == null) {
                    return null;
                } else {
                    return provincia.getCodigo() + "-" + provincia.getNombre();
                }
            }
            @Override
            public Provincia fromString(String userId) {
                return null;
            }
        });
    }

    @FXML
    private void onActionButtonGuardar(ActionEvent event) {
        int numFilaSeleccionada;
        boolean error = false;
        
        persona.setNombre(textFieldNombre.getText());
        persona.setApellidos(textFieldApellidos.getText());
        persona.setTelefono(textFieldTelefono.getText());
        persona.setEmail(textFieldEmail.getText());
        
        if(comboBoxProvincia.getValue() != null) {
           persona.setProvincia(comboBoxProvincia.getValue());
        } else {
            Alert alert = new Alert(AlertType.INFORMATION, "Indique una provincia");
            alert.showAndWait();
            error = true;
        }
        
        if(!error) {
            try {
                if(nuevaPersona) {
                    entityManager.persist(persona);
                } else {
                    entityManager.merge(persona);
                }
                entityManager.getTransaction().commit();

                StackPane rootMain = (StackPane)rootPersonaDetalleView.getScene().getRoot();
                rootMain.getChildren().remove(rootPersonaDetalleView); 
                rootAgendaView.setVisible(true);

                if(nuevaPersona){
                    tableViewPrevio.getItems().add(persona);
                    numFilaSeleccionada = tableViewPrevio.getItems().size() - 1;
                    tableViewPrevio.getSelectionModel().select(numFilaSeleccionada);
                    tableViewPrevio.scrollTo(numFilaSeleccionada);
                } else {
                    numFilaSeleccionada = tableViewPrevio.getSelectionModel().getSelectedIndex();
                    tableViewPrevio.getItems().set(numFilaSeleccionada, persona);
                }

                TablePosition pos = new TablePosition(tableViewPrevio, numFilaSeleccionada, null);
                tableViewPrevio.getFocusModel().focus(pos);
                tableViewPrevio.requestFocus();
            } catch(RollbackException ex) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText("No se han guardado los cambios. " + "Compruebe los datos");
                alert.setContentText(ex.getLocalizedMessage());
                alert.showAndWait();
            }
        }
     }

     @FXML
     private void onActionButtonCancelar(ActionEvent event) {
        entityManager.getTransaction().rollback();

        StackPane rootMain = (StackPane)rootPersonaDetalleView.getScene().getRoot();
        rootMain.getChildren().remove(rootPersonaDetalleView); 
        rootAgendaView.setVisible(true);
        
        int numFilaSeleccionada = tableViewPrevio.getSelectionModel().getSelectedIndex();
        TablePosition pos = new TablePosition(tableViewPrevio, numFilaSeleccionada, null);
        tableViewPrevio.getFocusModel().focus(pos);
        tableViewPrevio.requestFocus();
    }
  
}
