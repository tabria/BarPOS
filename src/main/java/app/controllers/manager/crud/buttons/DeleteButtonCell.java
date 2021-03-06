package app.controllers.manager.crud.buttons;

import app.controllers.manager.crud.dialogs.ManagerDialogController;
import app.cores.StageManager;
import app.enums.Pathable;
import app.enums.ViewElementPath;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;




public class DeleteButtonCell<S> extends TableCell<S, Boolean> {

    private static final String MANAGE_DELETE_DIALOG = "MANAGE_%s_DELETE_DIALOG";
    private Button deleteButton;
    private StageManager stageManager;

    public DeleteButtonCell() {
    }

    //    @Autowired
//    @Lazy
//    public DeleteButtonCell(StageManager stageManager) {
//        this.stageManager = stageManager;
//    }
   public void createButton(TableView genericTable, StageManager stageManager) {
       this.stageManager = stageManager;
       this.deleteButton = new Button();
        this.buttonProperties();
        this.deleteButton.setOnAction(event -> {
            int selectedIndex = getTableRow().getIndex();
            Object itemObject = genericTable.getItems().get(selectedIndex);
            String dialogPath = String.format(MANAGE_DELETE_DIALOG, itemObject.getClass().getSimpleName().toUpperCase());
            Pathable crudDialogPath = ViewElementPath.valueOf(dialogPath);
            showProductEditDialog(itemObject, crudDialogPath, genericTable, selectedIndex);
            genericTable.refresh();
        });
    }

    private <S> void showProductEditDialog(S deleteObject, Pathable viewPath, TableView genericTable, int selectedIndex){
        Parent deleteDialogParent = stageManager.getPane(viewPath);
        Stage deleteDialog = new Stage();
        deleteDialog.initStyle(StageStyle.UNDECORATED);

        //pop up window must be closed to continue interaction with the program
        deleteDialog.initModality(Modality.APPLICATION_MODAL);
        deleteDialog.setTitle("Delete");

        //set scene
        Scene dialogScene = new Scene(deleteDialogParent);
        deleteDialog.setScene(dialogScene);

        ManagerDialogController controller = this.stageManager.getController();
        controller.setDialogStage(deleteDialog);
        controller.setEditObject(deleteObject);
        controller.setTableView(genericTable);
        controller.setSelectedIndex(selectedIndex);

        deleteDialog.showAndWait();
    }

    private void buttonProperties() {
        this.deleteButton.getStyleClass().add("deleteButton");
        this.deleteButton.setText("DELETE");
    }

    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty){
            setGraphic(this.deleteButton);
        }
    }
}
