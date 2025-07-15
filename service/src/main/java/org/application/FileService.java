package org.application;

import org.application.database.DbUtil;
import org.application.model.File;
import org.application.repository.FileRepository;

import java.sql.SQLException;
import java.util.List;

public class FileService implements Service<Integer, File> {

    private final FileRepository fileRepository;

    public FileService() {
        this.fileRepository = new FileRepository(DbUtil.getMariaDBConnection());
    }

    @Override
    public File add(File file) {

        File model = fileRepository.add(file);
        try {
            if (model == null) {
                fileRepository.getConnection().rollback();
            } else {
                fileRepository.getConnection().commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return model;
    }

    @Override
    public File update(File file) {

        File model = fileRepository.update(file);
        try {
            if (model == null) {
                fileRepository.getConnection().rollback();
            } else {
                fileRepository.getConnection().commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return model;


    }

    @Override
    public boolean deleteByIDs(List<Integer> ids) {

        boolean isDelete = fileRepository.deleteByIds(ids);


        try {
            if (!isDelete) {
                fileRepository.getConnection().rollback();
            } else {
                fileRepository.getConnection().commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return isDelete;
    }

    @Override
    public boolean deleteByIDs(Integer... ids) {
        return deleteByIDs(List.of(ids));
    }

    @Override
    public List<File> get(File model) {
        return fileRepository.get(model);
    }
}
