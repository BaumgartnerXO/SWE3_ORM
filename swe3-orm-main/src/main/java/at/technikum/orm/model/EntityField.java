package at.technikum.orm.model;

import at.technikum.orm.annotations.Column;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import static org.apache.commons.lang3.StringUtils.isNotBlank;


/**
 * Describes the contents of an @{@link Entity}-field
 */
@Getter
@Setter
public class EntityField {

    private String columnName;

    private Class<?> type;

    private Class<?> rawType;

    private Field field;

    private boolean isPK;

    private boolean isFK;

    private boolean isCollection;

    public EntityField(Field field, Column columnAnnotation) {
        this.field = field;
        if (columnAnnotation != null && isNotBlank(columnAnnotation.value())) {
            columnName = columnAnnotation.value();
        } else {
            columnName = field.getName();
        }
        if (field.getGenericType() instanceof ParameterizedType parameterizedType) {
            if (Collection.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
                isCollection = true;
            }
            type = field.getType();
            rawType = (Class<?>) parameterizedType.getActualTypeArguments()[0]; // extracts eg. String from List<String>
        } else {
            this.type = field.getType();
        }
    }

    public Object fromDbObject(Object value) {
        if (value == null) {
            return null;
        }
        if (type.isEnum()) {
            Class<? extends Enum> en = (Class<? extends Enum>) type;
            return Enum.valueOf(en, (String) value);
        }
        if (type.equals(LocalDate.class)) {
            var dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
            return LocalDate.parse((String) value, dateTimeFormatter);
        }
        return value;
    }

    public Object toDbObject(Object value) {
        if (value == null) {
            return null;
        }
        if (type.isEnum()) {
            return value.toString();
        }
        if (isFK) {
            var fkType = Entity.ofClass(type).getPrimaryKey();
            return fkType.toDbObject(value);
        }

        if (type.equals(LocalDate.class)) {
            var dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
            return dateTimeFormatter.format((LocalDate) value);
        }
        return value;
    }

    public Object getValue(Object o) {
        field.setAccessible(true);
        try {
            return field.get(o);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setValue(Object o, Object value) {
        field.setAccessible(true);
        try {
            if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
                field.setBoolean(o, value.toString().equals("1"));
            } else {
                field.set(o, value);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error with reflective access to field", e);
        }
    }
}
