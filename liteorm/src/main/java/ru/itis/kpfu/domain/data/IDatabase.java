package ru.itis.kpfu.domain.data;

import ru.itis.kpfu.domain.exceptions.UnfamiliarTypeException;

import java.lang.reflect.Field;

public interface IDatabase {

    String addFieldType(Field field) throws UnfamiliarTypeException;

    String getSeparatorBetweenValueAndType();

    String getSeparatorBetweenValues();
}
