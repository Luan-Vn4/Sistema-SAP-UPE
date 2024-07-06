package br.upe.sap.sistemasapupe.data.jdbcutils;

import java.util.ArrayList;
import java.util.List;

public class ReflectionsUtils {

    public static List<Class<?>> getAllAscendants(Class<?> clazz) {
        List<Class<?>> classes = new ArrayList<>();

        while (clazz != Object.class) {
            classes.add(clazz);
            clazz = clazz.getSuperclass();
        }

        return classes;
    }

}
