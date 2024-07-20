package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import java.io.Serializable;
import java.util.List;

public interface BasicRepository<Entity, Key extends Serializable> {

    Entity create(Entity entity);

    List<Entity> create(List<Entity> entities);

    Entity update(Entity entity);

    List<Entity> update(List<Entity> entities);

    Entity findById(Key id);

    List<Entity> findAll();

    List<Entity> findByIds(List<Key> ids);

    int delete(Key id);

    int delete(List<Key> keys);

}
