package ru.itis.kpfu.domain.criteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Criteria {

    private StringBuilder expression;
    ArrayList<Object> values;

    Criteria init(){
        expression = new StringBuilder();
        expression.append(" WHERE");
        values = new ArrayList();
        return this;
    }

    boolean add(String columnName,String condition,Object value){
        if(!isValid(columnName, condition, value)) return false;
        expression
                .append(" ")
                .append(columnName)
                .append(" ")
                .append(condition)
                .append(" ")
                .append("?");
        values.add(value);
        return true;
    }

    boolean add(String operator){
        expression
                .append(" ")
                .append(operator);

        return true;
    }

    private boolean isValid(String columnName, String condition,Object value){
        return columnName != null && value != null && !columnName.isEmpty();
    }

    @Override
    public String toString() {
        return expression.toString();
    }
}
