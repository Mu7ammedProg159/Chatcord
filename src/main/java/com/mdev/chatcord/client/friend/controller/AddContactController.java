package com.mdev.chatcord.client.friend.controller;

import com.mdev.chatcord.client.common.service.DragWindow;
import com.mdev.chatcord.client.common.service.SpringFXMLLoader;
import com.mdev.chatcord.client.chat.direct.dto.PrivateChatDTO;
import com.mdev.chatcord.client.common.implementation.UIErrorHandler;
import com.mdev.chatcord.client.friend.service.FriendService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
@RequiredArgsConstructor
@Getter
@Setter
public class AddContactController extends DragWindow implements UIErrorHandler {

    @FXML private TextField usernameField;
    @FXML private TextField tagField;
    @FXML private HBox dragRegion;
    @FXML private StackPane overlayPane;
    @FXML private Button addFriendBtn;
    @FXML private Label errorLabel;

    private Stage stage;
    private final FriendService friendService;
    private final SpringFXMLLoader springFXMLLoader;

    private VBox contactList;

    private BiConsumer<String, String> onRetrieveContact;

    private Runnable onClose;

    @FXML
    public void initialize(){
        dragRegion.setOnMousePressed(this::handleMousePressed);
        dragRegion.setOnMouseDragged(this::handleMouseDragged);
    }

    @FXML
    public  void onClose(MouseEvent event){
        if (event.getTarget() == overlayPane && onClose != null)
            onClose.run();
    }

    @FXML
    public void onAddFriend(ActionEvent actionEvent){
        String friendUsername = usernameField.getText();
        String friendTag = tagField.getText();
        PrivateChatDTO privateChatDTO = null;

        try {
            if (isAnyFieldEmpty(friendUsername, friendTag)) {
                setVisibility(true, errorLabel);
                errorLabel.setText("Please fill all fields.");
            } else {
                setVisibility(false, errorLabel);
                privateChatDTO = friendService.addFriend(friendUsername, friendTag);
                onRetrieveContact.accept(friendUsername, friendTag);
            }
        } catch (RuntimeException e){
            setVisibility(true, errorLabel);
            errorLabel.setText(e.getMessage());
        }

//        if (friendDTO != null){
//            FXMLLoader contactLoader = springFXMLLoader.getLoader("/view/main-layout/contact-view.fxml");
//            Parent newContact;
//            try {
//                newContact = contactLoader.load();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            ContactController contactController = contactLoader.getController();
//
//            contactController.setData(friendDTO.getFriendName(), null, convertToHourTime(System.currentTimeMillis()),
//                    0, friendDTO.getFriendPfp());

    }

    public void onCancel(ActionEvent e) {
        if (onClose != null)
            onClose.run();
    }
}
