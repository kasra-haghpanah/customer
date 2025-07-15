package org.application;

import org.application.database.DbUtil;
import org.application.model.Customer;
import org.application.model.File;
import org.application.repository.CustomerRepository;
import org.application.repository.FileRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CustomerService implements Service<Integer, Customer> {

    private final CustomerRepository customerRepository;
    private final FileRepository fileRepository;

    public CustomerService() {
        Connection connection = DbUtil.getMariaDBConnection();
        this.customerRepository = new CustomerRepository(connection);
        this.fileRepository = new FileRepository(connection);
    }

    @Override
    public Customer add(Customer customer) {
        File file = fileRepository.add(customer.getFile());
        customer.setFile(file);
        Customer model = customerRepository.add(customer);
        try {
            if (model == null) {
                customerRepository.getConnection().rollback();
            } else {
                customerRepository.getConnection().commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return model;
    }

    @Override
    public Customer update(Customer customer) {
        Customer model = customerRepository.update(customer);
        try {
            if (model == null) {
                customerRepository.getConnection().rollback();
            } else {
                customerRepository.getConnection().commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return model;
    }

    @Override
    public boolean deleteByIDs(List<Integer> ids) {
        boolean isDelete = customerRepository.deleteByIds(ids);
        if (isDelete) {
            try {
                if (!isDelete) {
                    customerRepository.getConnection().rollback();
                } else {
                    customerRepository.getConnection().commit();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return isDelete;
    }

    @Override
    public boolean deleteByIDs(Integer... ids) {
        return deleteByIDs(List.of(ids));
    }


    @Override
    public List<Customer> get(Customer model) {
        return customerRepository.get(model);
    }
}
