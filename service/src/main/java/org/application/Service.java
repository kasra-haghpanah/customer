package org.application;

import org.application.model.Model;

import java.util.List;

public interface Service<ID, M extends Model> {

    public M add(M model);

    public M update(M model);

    public boolean deleteByIDs(List<ID> ids);

    public boolean deleteByIDs(ID... ids);

    public List<M> get(M model);

}
