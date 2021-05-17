package ru.itis.kpfu.domain.criteria;

public class CriteriaBuilder {

    private Criteria criteria;

    public CriteriaBuilder(){
        criteria = new Criteria().init();
    }

    public CriteriaBuilder equals(String columnName,Object value){
        criteria.add(columnName,"=",value);
        return this;
    }

    public CriteriaBuilder greaterThan(String columnName,Object value){
        criteria.add(columnName,">",value);
        return this;
    }

    public CriteriaBuilder lessThan(String columnName,Object value){
        criteria.add(columnName,"<",value);
        return this;
    }

    public CriteriaBuilder or(){
        criteria.add("OR");
        return this;
    }

    public CriteriaBuilder and(){
        criteria.add("AND");
        return this;
    }

    public Criteria build(){
        return this.criteria;
    }

}
