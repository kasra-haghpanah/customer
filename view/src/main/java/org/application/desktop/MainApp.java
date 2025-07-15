package org.application.desktop;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.application.CustomerService;
import org.application.FileUtil;
import org.application.StringUtil;
import org.application.desktop.dto.CustomerDTO;
import org.application.dto.FileDTO;
import org.application.model.Customer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Oracle GraalVM for JDK + Native Image Bundle

// mvn javafx:run

public class MainApp extends Application {

    private final TableView<CustomerDTO> tableView;
    private final List<CustomerDTO> customerDTOs;
    private final CustomerService customerService;
    private final TextField firstNameField;
    private final TextField lastNameField;
    private final TextField phoneField;
    private final TextField purchaseField;

    public MainApp() {
        this.customerService = new CustomerService();
        this.tableView = new TableView<>();
        this.customerDTOs = new ArrayList<>();
        this.firstNameField = new TextField();
        this.lastNameField = new TextField();
        this.phoneField = new TextField();
        this.purchaseField = new TextField();
    }

    @Override
    public void start(Stage primaryStage) {


        List<FileDTO> selectedPhotos = new ArrayList<>();

        makeTableView(selectedPhotos);
        HBox textFieldsForm = makeTextFieldsForm(primaryStage, selectedPhotos);
        HBox buttonsForm = makeButtonsForm(primaryStage, selectedPhotos);
        StackPane searchFrame = makeSearchFrame();

        VBox root = new VBox(10, tableView, textFieldsForm, buttonsForm, searchFrame);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("نرم افزار امور مشتریان");
        primaryStage.show();
    }

    public HBox makeTextFieldsForm(Stage primaryStage, List<FileDTO> selectedPhotos) {


        Label firstNameLabel = new Label("نام:");
        firstNameLabel.setAlignment(Pos.CENTER_RIGHT);
        // تنظیم حاشیه فقط برای برچسب نام
        firstNameLabel.setPadding(new Insets(0, 20, 0, 0)); // (top, right, bottom, left)

        Label lastNameLabel = new Label("نام خانوادگی:");
        lastNameLabel.setAlignment(Pos.CENTER_RIGHT);

        Label phoneFieldLabel = new Label("شماره همراه:");
        phoneFieldLabel.setAlignment(Pos.CENTER_RIGHT);

        Label purchaseFieldLabel = new Label("مقدار خرید:");
        purchaseFieldLabel.setAlignment(Pos.CENTER_RIGHT);

        HBox form = new HBox(10, firstNameField, firstNameLabel, lastNameField, lastNameLabel, phoneField, phoneFieldLabel, purchaseField, purchaseFieldLabel);
        form.setAlignment(Pos.CENTER_RIGHT);
        form.setPadding(new Insets(0, 20, 0, 0)); // (top, right, bottom, left)

        return form;
    }

    public HBox makeButtonsForm(Stage primaryStage, List<FileDTO> selectedPhotos) {

        Button addPhotos = new Button("افزودن عکس");


        addPhotos.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("انتخاب عکس");
            selectedPhotos.clear();

            List<File> fileList = fileChooser.showOpenMultipleDialog(primaryStage);
            List<FileDTO> bytesList = new ArrayList<>();
            fileList.forEach(file -> {
                FileDTO dto = new FileDTO(file.getName(), FileUtil.readFileToByteArray(file));
                bytesList.add(dto);
            });

            selectedPhotos.addAll(bytesList);
        });

        Button saveBtn = new Button("ذخیره");
        saveBtn.setOnAction(e -> {

            Customer customer = new Customer(0,
                    this.firstNameField.getText(),
                    this.lastNameField.getText(),
                    this.phoneField.getText(),
                    StringUtil.getInt(purchaseField.getText()),
                    0,
                    selectedPhotos);

            customer = this.customerService.add(customer);

            if (customer != null) {
                CustomerDTO customerDTO = CustomerDTO.convertToCustomerDTO(customer);
                customerDTOs.add(customerDTO);
                tableView.getItems().add(customerDTO);
            }

        });

        Button deleteBtn = new Button("حذف");
        deleteBtn.setOnAction(e -> {
            CustomerDTO selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                boolean isDelete = customerService.deleteByIDs(selected.getId());
                if (isDelete) {
                    customerDTOs.remove(selected);
                    tableView.getItems().remove(selected);
                }
            }
        });


        Button editBtn = new Button("ویرایش");
        //HBox.setMargin(editBtn, new Insets(0, 0, 0, 20));

        editBtn.setOnAction(e -> {
            CustomerDTO selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {

                List<CustomerDTO> customerDTOS = this.customerDTOs.stream()
                        .filter(node -> {
                            return node.getId() == selected.getId();
                        })
                        .collect(Collectors.toList());

                if (selected.getId() > -1 && customerDTOS.size() == 1) {
                    Optional optionalFirstName = Optional.ofNullable(firstNameField.getText());
                    Optional optionalLastName = Optional.ofNullable(lastNameField.getText());
                    Optional optionalPhoneField = Optional.ofNullable(phoneField.getText());

                    if (optionalFirstName.isPresent() && !optionalFirstName.isEmpty()
                            && optionalLastName.isPresent() && !optionalLastName.isEmpty()
                            && optionalPhoneField.isPresent() && !optionalPhoneField.isEmpty()
                    ) {
                        CustomerDTO customerDTO = customerDTOS.get(0);
                        customerDTO.setFirstName(firstNameField.getText().trim());
                        customerDTO.setLastName(lastNameField.getText().trim());
                        customerDTO.setPhoneNumber(phoneField.getText().trim());
                        customerDTO.setPurchase(StringUtil.getInt(purchaseField.getText().trim()));
                        //customerDTO.setFileId(customerDTO.getFileId());
                        customerDTO.setFilename(selectedPhotos.get(0).getFileName());
                        customerDTO.setPhotos(List.of(selectedPhotos.get(0).getContent()));

                        this.customerService.update(CustomerDTO.convertToCustomer(customerDTO));

                        tableView.refresh();
                    }
                }

            }
        });

        HBox form = new HBox(10, addPhotos, saveBtn, deleteBtn, editBtn);
        form.setAlignment(Pos.CENTER_RIGHT);
        form.setPadding(new Insets(0, 20, 0, 0)); // (top, right, bottom, left)

        return form;

    }

    public void makeTableView(List<FileDTO> selectedPhotos) {

        TableColumn<CustomerDTO, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<CustomerDTO, String> fnCol = new TableColumn<>("First Name");
        fnCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<CustomerDTO, String> lnCol = new TableColumn<>("Last Name");
        lnCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<CustomerDTO, String> phoneCol = new TableColumn<>("Phone Number");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        TableColumn<CustomerDTO, Integer> purchaseCol = new TableColumn<>("purchase");
        purchaseCol.setCellValueFactory(new PropertyValueFactory<>("purchase"));

        TableColumn<CustomerDTO, Integer> fileIdCol = new TableColumn<>("file id");
        fileIdCol.setCellValueFactory(new PropertyValueFactory<>("fileId"));

        TableColumn<CustomerDTO, Integer> filenameCol = new TableColumn<>("file Name");
        filenameCol.setCellValueFactory(new PropertyValueFactory<>("filename"));

        TableColumn<CustomerDTO, HBox> photoCol = new TableColumn<>("Photos");
        photoCol.setCellValueFactory(new PropertyValueFactory<>("photoBox"));

        tableView.getColumns().addAll(idCol, fnCol, lnCol, phoneCol, purchaseCol, fileIdCol, filenameCol, photoCol);

        List<Customer> customers = this.customerService.get(null);
        tableView.getItems().clear();
        customerDTOs.clear();

        customers.stream().sorted((c1, c2) -> {
            return c1.getId() - c2.getId();
        }).forEach(customer -> {
            CustomerDTO customerDTO = CustomerDTO.convertToCustomerDTO(customer);
            customerDTOs.add(customerDTO);
            tableView.getItems().add(customerDTO);
        });

        tableView.refresh();


        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                System.out.println("رکورد جدید انتخاب شد: " + newSelection.toString());
                // می‌تونی اینجا UI رو آپدیت کنی یا دکمه‌ای فعال کنی

                CustomerDTO selected = tableView.getSelectionModel().getSelectedItem();

                if (selected != null) {
                    this.firstNameField.setText(selected.getFirstName());
                    this.lastNameField.setText(selected.getLastName());
                    this.phoneField.setText(selected.getPhoneNumber());
                    this.purchaseField.setText(selected.getPurchase() + "");
                    selectedPhotos.clear();


                    if (selected.getPhotos() != null) {
                        selected.getPhotos().forEach(file -> {
                            FileDTO dto = new FileDTO(selected.getFilename(), file);
                            selectedPhotos.add(dto);
                        });
                    }
                }

            }
        });


    }

    public StackPane makeSearchFrame() {

        // ایجاد اجزای اصلی
        CheckBox checkBox = new CheckBox("فعال کردن فرم جستجو");

        Label firstNameLabel = new Label("نام:");
        TextField firstNameField = new TextField();

        Label lastNameLabel = new Label("نام خانوادگی:");
        TextField lastNameField = new TextField();

        Label phoneLabel = new Label("شماره همراه:");
        TextField phoneField = new TextField();

        Label purchaseLabel = new Label("مقدار خرید:");
        TextField purchaseField = new TextField();


        Button searchButton = new Button("جستجو");

        searchButton.setOnAction(e -> {


            Customer model = new Customer(
                    0,
                    firstNameField.getText(),
                    lastNameField.getText(),
                    phoneField.getText(),
                    StringUtil.getInt(purchaseField.getText()),
                    0,
                    null,
                    null
            );

            List<Customer> customers = this.customerService.get(model);
            while (tableView.getItems().size() > 0) {
                tableView.getItems().remove(0);
            }
            //tableView.getItems().clear();
            customerDTOs.clear();

            customers.stream().sorted((c1, c2) -> {
                return c1.getId() - c2.getId();
            }).forEach(customer -> {
                CustomerDTO customerDTO = CustomerDTO.convertToCustomerDTO(customer);
                customerDTOs.add(customerDTO);
                tableView.getItems().add(customerDTO);
            });
            tableView.refresh();
        });


        // غیرفعال کردن فیلد و دکمه در ابتدا
        firstNameField.setDisable(true);
        lastNameField.setDisable(true);
        phoneField.setDisable(true);
        purchaseField.setDisable(true);
        searchButton.setDisable(true);

        // اضافه کردن رویداد به چک باکس
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            firstNameField.setDisable(!newValue);
            lastNameField.setDisable(!newValue);
            phoneField.setDisable(!newValue);
            purchaseField.setDisable(!newValue);
            searchButton.setDisable(!newValue);

            if (!newValue) {
                List<Customer> customers = this.customerService.get(null);
                while (tableView.getItems().size() > 0) {
                    tableView.getItems().remove(0);
                }

                tableView.refresh();
                customerDTOs.clear();

                customers.stream().sorted((c1, c2) -> {
                    return c1.getId() - c2.getId();
                }).forEach(customer -> {
                    CustomerDTO customerDTO = CustomerDTO.convertToCustomerDTO(customer);
                    customerDTOs.add(customerDTO);
                    tableView.getItems().add(customerDTO);
                });
                tableView.refresh();
            }

        });

        // ایجاد یک مستطیل به عنوان کادر
        Rectangle frame = new Rectangle(1000, 100);
        frame.setFill(Color.TRANSPARENT);
        frame.setStroke(Color.GREY);
        frame.setStrokeWidth(2);

        // ایجاد Layout و اضافه کردن اجزا

        //HBox form = new HBox(10, firstNameField, lastNameField, phoneField, purchaseField, addPhotos, saveBtn, deleteBtn, editBtn);

        HBox hBox1 = new HBox(10);
        hBox1.setPadding(new Insets(0, 20, 0, 0));
        hBox1.setAlignment(Pos.CENTER_RIGHT);
        hBox1.getChildren().addAll(checkBox);

        HBox hBox2 = new HBox(10, firstNameField, firstNameLabel, lastNameField, lastNameLabel, phoneField, phoneLabel, purchaseField, purchaseLabel);
        hBox2.setPadding(new Insets(0, 20, 0, 0));
        hBox2.setAlignment(Pos.CENTER_RIGHT);


        HBox hBox3 = new HBox(10, searchButton);
        hBox3.setPadding(new Insets(0, 20, 0, 0));
        hBox3.setAlignment(Pos.CENTER_RIGHT);
        //hBox3.getChildren().addAll(searchButton);


        VBox vBox = new VBox(10, hBox1, hBox2, hBox3);
        vBox.setAlignment(Pos.CENTER_RIGHT);

        // ایجاد یک StackPane برای قرار دادن مستطیل و VBox
        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(frame, vBox);
        return root;

    }

    public static void main(String[] args) {

        System.setProperty("prism.order", "sw");
        System.setProperty("prism.verbose", "true");
        System.setProperty("java.awt.headless", "false");
        Application.launch(MainApp.class, args);
        launch(args);
    }
}