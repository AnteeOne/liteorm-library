package ru.itis.kpfu.domain.data;

import ru.itis.kpfu.domain.exceptions.UnfamiliarTypeException;

import java.lang.reflect.Field;

public class PostgreSQLDatabase implements IDatabase {

    public String addFieldType(Field field) throws UnfamiliarTypeException {
        String type;
        if (Long.class.equals(field.getType())) {
            type = "bigint";
        } else if (Integer.class.equals(field.getType())) {
            type = "int";
        } else if (String.class.equals(field.getType()) || Character.class.equals(field.getType())) {
            type = "varchar";
        } else if (Boolean.class.equals(field.getType())) {
            type = "boolean";
        }
        else {
            throw new UnfamiliarTypeException();
        }
        return type;
    }

    @Override
    public String getSeparatorBetweenValueAndType() {
        return " ";
    }

    @Override
    public String getSeparatorBetweenValues() {
        return ", ";
    }
}
