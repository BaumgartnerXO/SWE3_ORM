package at.technikum.orm;

import at.technikum.orm.annotations.Id;
import at.technikum.orm.cache.Cache;
import at.technikum.orm.model.Entity;
import at.technikum.orm.model.EntityField;
import lombok.extern.slf4j.Slf4j;
import org.stringtemplate.v4.ST;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

@Slf4j
public class Orm {

    private final static String SELECT_TEMPLATE = "SELECT <columNames> FROM <tableName> WHERE <pkColumnName> = ?";

    private final static String INSERT_TEMPLATE = "INSERT INTO <tableName> (<columNames>) VALUES (<valuePlaceholder>)";

    private final static String UPDATE_TEMPLATE = "UPDATE <tableName> SET <columnsWithoutPKWithPlaceholder> WHERE <pkColumnName> = ?";

    private final static String DELETE_TEMPLATE = "DELETE FROM <tableName> WHERE <pkColumnName> = ?";


    private final ConnectionFactory connectionFactory;

    private final static Cache cache = new Cache();

    /**
     * Creates a orm object
     *
     * @param connectionFactory to use
     * @throws SQLException when no connection could be established
     */
    public Orm(ConnectionFactory connectionFactory) throws SQLException {
        this.connectionFactory = connectionFactory;
    }


    /**
     * Fetch entity by Id
     *
     * @param clazz entity-class
     * @param id    the id
     *
     * @return the object, returns null if not found.
     */
    public <T> T get(Class<T> clazz, Object id) {

        T cachedObject = cache.get(clazz, id);
        if(cachedObject != null){
            log.debug("returning cached object");
            return cachedObject;
        }

        var entity = Entity.ofClass(clazz);

        var entityFields = entity.getEntityFields();
        var columnNames = entityFields.stream()
                .map(EntityField::getColumnName).collect(Collectors.joining(", "));
        var columnTypes = entityFields.stream()
                .map(EntityField::getType)
                .toList();

        ST select = new ST(SELECT_TEMPLATE);
        select.add("tableName", entity.getTableName());
        select.add("columNames", columnNames);
        select.add("pkColumnName", entity.getPrimaryKey().getColumnName());

        String selectStatement = select.render();

        log.info(selectStatement);
        Object o = null;
        try {
            Connection connection = connectionFactory.get();
            log.info("Executing SQL-Statement: {}", selectStatement);
            PreparedStatement preparedStatement = connection.prepareStatement(selectStatement);
            preparedStatement.setObject(1, id);
            var resultSet = preparedStatement.executeQuery();
            List<Object> values = new ArrayList<>(columnTypes.size());
            if (resultSet.next()) {
                for (int i = 0; i < entityFields.size(); i++) {
                    var entityField = entityFields.get(i);
                    var value = entityField.fromDbObject(resultSet.getObject(i + 1));
                    values.add(value);
                }
            } else {
                return null;
            }
            Constructor<?> constructor = getConstructor(entity);
            constructor.setAccessible(true);
            T retval = (T) constructor.newInstance();
            for (int i = 0; i < values.size(); i++) {
                EntityField entityField = entityFields.get(i);
                Object value = values.get(i);
                if (entityField.isFK()) {
                    if (value != null) {
                        Object foreignEntity = get(entityField.getType(), value);
                        entityField.setValue(retval, foreignEntity);
                    }
                } else {
                    entityField.setValue(retval, value);
                }
            }
            return retval;


        } catch (Exception e) {
            log.error("unexpected Exception{}", e);
            throw new RuntimeException(e);
        }

    }

    /**
     * Use ORM Mapper to store your entities to the database.
     * Supports INSERT and UPDATE
     *
     * @param o object to persist
     * @return o the peristed object
     */

    public Object persist(Object o) {

        Entity entity = Entity.ofClass(o.getClass());

        List<String> columnNames = new ArrayList<>();
        List<String> columnNamesWithoutPK = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        List<Object> valuesWithoutPK = new ArrayList<>();
        final Object[] pk = new Object[1];

        for (EntityField entityField : entity.getEntityFields()) {
            if (entityField.isPK()) {
                pk[0] = entityField.toDbObject(entityField.getValue(o));
                continue;
            }
            if (entityField.isFK()) {
                var fkToStore = entityField.getValue(o);
                if (fkToStore == null) {
                    continue;
                }
                columnNames.add(entityField.getColumnName());
                columnNamesWithoutPK.add(entityField.getColumnName());
                Object value = Entity.ofClass(entityField.getType()).getPrimaryKey().getValue(fkToStore);
                valuesWithoutPK.add(value);
                values.add(value);
                continue;
            }
            columnNames.add(entityField.getColumnName());
            Object object = entityField.toDbObject(entityField.getValue(o));
            if (!entityField.isPK()) {
                columnNamesWithoutPK.add(entityField.getColumnName());
                valuesWithoutPK.add(object);
            }
            values.add(object);
        }


        if (entity.getPrimaryKey().getValue(o) == null) {
            return doInsert(o, entity, columnNames, columnNamesWithoutPK, values, valuesWithoutPK);

        } else {
            return doUpdate(o, entity, columnNamesWithoutPK, values, pk[0]);
        }

    }

    /**
     * truncate table
     *
     * @param clazz entity-class
     */
    public void truncateTable(Class<?> clazz) {
        String tableName = Entity.ofClass(clazz).getTableName();

        var truncate = "TRUNCATE TABLE " + tableName;
        try {
            Connection connection = connectionFactory.get();
            log.info("Executing SQL-Statement: {}", truncate);

            PreparedStatement preparedStatement = connection.prepareStatement(truncate);
            preparedStatement.execute();
        } catch (Exception e) {
            throw new RuntimeException("Unexpected Exception during truncate table", e);
        }
    }


    private Object doInsert(Object o, Entity entity, List<String> columnNames, List<String> columnNamesWithoutPK, List<Object> values, List<Object> valuesWithoutPK) {
        values.addAll(valuesWithoutPK);
        ST insert = new ST(INSERT_TEMPLATE);
        insert.add("tableName", entity.getTableName());
        insert.add("pkColumnName", entity.getPrimaryKey().getColumnName());
        insert.add("columNames", String.join(", ", columnNames));
        insert.add("valuePlaceholder", createPlaceholders(columnNames));
        insert.add("columnsWithoutPKWithPlaceholder", createColumnNamesWithPlaceholders(columnNamesWithoutPK));
        String statement = insert.render();
        log.info(statement);
        try {
            Connection connection = connectionFactory.get();
            log.info("Executing SQL-Statement: {}", statement);
            PreparedStatement ps = connection.prepareStatement(statement, RETURN_GENERATED_KEYS);
            for (int i = 0; i < values.size(); i++) {
                ps.setObject(i + 1, values.get(i));
            }
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                updateIdField(o, rs.getInt(1));
            }
        } catch (SQLException | IllegalAccessException e) {
            throw new RuntimeException("unexpected Exception", e);
        }
        cache.put(o);
        return o;
    }

    private Object doUpdate(Object o, Entity entity, List<String> columnNamesWithoutPK, List<Object> values, Object x) {
        ST insert = new ST(UPDATE_TEMPLATE);
        insert.add("tableName", entity.getTableName());
        insert.add("pkColumnName", entity.getPrimaryKey().getColumnName());
        insert.add("columnsWithoutPKWithPlaceholder", createColumnNamesWithPlaceholders(columnNamesWithoutPK));
        String statement = insert.render();
        log.info(statement);

        try {
            Connection connection = connectionFactory.get();
            connection.setAutoCommit(true);
            log.info("Executing SQL-Statement: {}", statement);
            PreparedStatement ps = connection.prepareStatement(statement);
            for (int i = 0; i < values.size(); i++) {
                ps.setObject(i + 1, values.get(i));
            }
            ps.setObject(values.size() + 1, x);

            ps.executeUpdate();
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException("unexpected Exception", e);
        }
        cache.put(o);
        return o;
    }

    /**
     * EXTRA FEATURE: Delete Entities
     *
     * @param o entity to delete
     */
    public void delete(Object o) {
        Entity entity = Entity.ofClass(o.getClass());

        final Object[] pk = new Object[1];
        for (EntityField entityField : entity.getEntityFields()) {
            if (entityField.isPK()) {
                pk[0] = entityField.toDbObject(entityField.getValue(o));
                continue;
            }
        }

        //"DELETE FROM <tableName> WHERE <pkColumnName> = ?"
        ST delete = new ST(DELETE_TEMPLATE);
        delete.add("tableName", entity.getTableName());
        delete.add("pkColumnName", entity.getPrimaryKey().getColumnName());
        String statement = delete.render();
        log.info(statement);

        try {
            Connection connection = connectionFactory.get();
            connection.setAutoCommit(true);
            log.info("Executing SQL-Statement: {}", statement);
            PreparedStatement ps = connection.prepareStatement(statement);
            ps.setObject(1, pk[0]);

            ps.execute();
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException("unexpected Exception", e);
        }
        cache.remove(o);
    }


    private <T> Constructor<T> getConstructor(Entity entity) {
        try {
            return (Constructor<T>) entity.getType().getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unexpected RuntimeException");
        }
    }


    private String createColumnNamesWithPlaceholders(List<String> columnNamesWithoutPK) {
        return String.join(" = ?, ", columnNamesWithoutPK) + " = ?";
    }

    private String createPlaceholders(List<String> columnNames) {
        return String.join(", ", Collections.nCopies(columnNames.size(), "?"));
    }


    private void updateIdField(Object o, Integer newId) throws IllegalAccessException {
        Optional<Field> idField = Arrays.stream(o.getClass().getDeclaredFields()).filter(field -> field.getAnnotation(Id.class) != null
        ).findFirst();
        if (idField.isEmpty()) {
            idField = Arrays.stream(o.getClass().getSuperclass().getDeclaredFields()).filter(field -> field.getAnnotation(Id.class) != null
            ).findFirst();
        }

        Field field = idField.get();
        field.setAccessible(true);
        field.set(o, newId);
    }
}
