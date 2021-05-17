package ru.itis.kpfu.domain.converter;

import ru.itis.kpfu.domain.data.IDatabase;
import ru.itis.kpfu.domain.exceptions.UnfamiliarTypeException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Field2StringConverter {

    public final String separatorBetweenFields;
    public final String separatorBetweenTypeAndValue;
    public final IDatabase dataBase;

    public Field2StringConverter(IDatabase dataBase) {
        this.separatorBetweenFields = dataBase.getSeparatorBetweenValues();
        this.separatorBetweenTypeAndValue = dataBase.getSeparatorBetweenValueAndType();
        this.dataBase = dataBase;
    }

    public String addValuesInSqlStatement(boolean hasType, Field[] fields) {
        try {
            Method method = getMethod(hasType);
            return getStringFields(method, fields);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    public Method getMethod(boolean hasType) throws NoSuchMethodException {
        if (hasType) {
            return this.getClass().getMethod("addFieldWithType", StringBuilder.class, Field.class);
        }
        return this.getClass().getMethod("addFieldName", StringBuilder.class, Field.class);
    }

    public String getStringFields(Method method, Field[] fields) throws InvocationTargetException, IllegalAccessException {
        StringBuilder sqlStringBuilder = new StringBuilder();
        for (int i = 0; i < fields.length - 1; i++) {
            method.invoke(this, sqlStringBuilder, fields[i]);
            sqlStringBuilder.append(separatorBetweenFields);
        }
        method.invoke(this, sqlStringBuilder, fields[fields.length - 1]);
        return sqlStringBuilder.toString();
    }

    public String getStringObjectValues(Field[] fields, Object object) throws IllegalAccessException {
        StringBuilder valuesString = new StringBuilder();
        for (int i = 0; i < fields.length - 1; i++) {
            addValue(valuesString, object, fields[i]);
            valuesString.append(separatorBetweenFields);
        }
        addValue(valuesString, object, fields[fields.length - 1]);
        return valuesString.toString();
    }

    public void addFieldWithType(StringBuilder builder, Field field) throws UnfamiliarTypeException {
        addFieldName(builder, field);
        builder.append(separatorBetweenTypeAndValue);
        addFieldType(builder, field);
    }

    public void addFieldName(StringBuilder builder, Field field) {
        builder.append(field.getName());
    }

    public void addFieldType(StringBuilder builder, Field field) throws UnfamiliarTypeException {
        builder.append(dataBase.addFieldType(field));
    }

    public void addValue(StringBuilder builder, Object object, Field field) throws IllegalAccessException {
        field.setAccessible(true);
        builder.append("'").append(field.get(object)).append("'");
    }
}
