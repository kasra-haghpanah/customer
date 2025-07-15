package org.application;

import org.application.database.DbUtil;
import org.application.model.Customer;
import org.application.model.File;
import org.application.repository.CustomerRepository;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        String path = "C:/Users/98911/Downloads/01.jpg";

        java.io.File fileSystem = new java.io.File(path);

        File file = new File();
        file.setId(5006);
        file.setFilename(fileSystem.getName());
        file.setContent(FileUtil.readFileToByteArray(path));

        FileService service = new FileService();
        //File file2 = service.update(file);
        boolean isDelete = service.deleteByIDs(3002);

        System.out.println("args = " + isDelete);



        /*CustomerService customerService = new CustomerService();

        File file = new File();
        file.setFilename("02.jpeg");
        file.setContent(FileUtil.readFileToByteArray("C:/Users/98911/Desktop/documents/adsl-01.jpg"));

        Customer customer = new Customer();
        customer.setId(1003);
        customer.setFirstName("kamran");
        customer.setLastName("diba");
        customer.setPhoneNumber("09113394965");
        customer.setPurchase(45000000);

        customer.setFile(file);

        List<Customer> list = customerService.get(customer);

        System.out.println(list);*/

//        Customer customer1 = customerService.update(customer);
//        System.out.println("args = " + customer1);

        //Customer customer2 = customerService.add(customer);
        //System.out.println("args = " + customer2);

//        CustomerService service = new CustomerService();
//        service.deleteByIDs(1, 4, 7, 1001);


    }
}