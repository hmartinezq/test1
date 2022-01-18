/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hugo.test.managment;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.transform.ResultTransformer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author aacero
 */

public class DataSourceRequest {
    private int page;
    private int pageSize;
    private int take;
    private int skip;
    private List<SortDescriptor> sort;
    private List<GroupDescriptor> group;
    private List<AggregateDescriptor> aggregate;
    private HashMap<String, Object> data;
    
    private FilterDescriptor filter;
    
    public DataSourceRequest() {
        filter = new FilterDescriptor();
        data = new HashMap<String, Object>();
    }
    
    public HashMap<String, Object> getData() {
        return data;
    }
    
    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        data.put(key, value);
    }
    
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTake() {
        return take;
    }

    public void setTake(int take) {
        this.take = take;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public List<SortDescriptor> getSort() {
        return sort;
    }

    public void setSort(List<SortDescriptor> sort) {
        this.sort = sort;
    }

    public FilterDescriptor getFilter() {
        return filter;
    }

    public void setFilter(FilterDescriptor filter) {
        this.filter = filter;
    }

    private static void restrict(Junction junction, FilterDescriptor filter, Class<?> clazz) {
        String operator = filter.getOperator();
        String field = filter.getField();
        Object value = filter.getValue();
        boolean ignoreCase = filter.isIgnoreCase();
        
        try {
            Class<?> type = new PropertyDescriptor(field, clazz).getPropertyType();
            if (type == double.class || type == Double.class) {
                value = Double.parseDouble(value.toString());
            } else if (type == float.class || type == Float.class) {
                value = Float.parseFloat(value.toString());
            } else if (type == long.class || type == Long.class) {
                value = Long.parseLong(value.toString());
            } else if (type == int.class || type == Integer.class) {
                value = Integer.parseInt(value.toString());
            } else if (type == short.class || type == Short.class) {
                value = Short.parseShort(value.toString());
            } else if (type == boolean.class || type == Boolean.class) {
                value = Boolean.parseBoolean(value.toString());
            } else if(type==char.class || type== Character.class){
            	value= value.toString().charAt(0);
            }
            else if(type== Date.class){
            	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            	try {
                    Date result =  df.parse(value.toString());
                    value= result;
            	} catch (ParseException e) {
                    e.printStackTrace();
                }  
            }
            
        }catch (IntrospectionException e) {
            if(field.contains(".")){
                value = Integer.parseInt(value.toString());
            }
        }catch(NumberFormatException nfe) {
        }
        
        int operadorNum =0;
        if (operator.equals("eq"))
        	operadorNum = 1;
        if (operator.equals("neq"))
        	operadorNum = 2;
        if (operator.equals("gt"))
        	operadorNum = 3;
        if (operator.equals("gte"))
        	operadorNum = 4;
        if (operator.equals("lt"))
        	operadorNum = 5;
        if (operator.equals("lte"))
        	operadorNum = 6;
        if (operator.equals("startswith"))
        	operadorNum = 7;
        if (operator.equals("endswith"))
        	operadorNum = 8;
        if (operator.equals("contains"))
        	operadorNum = 9;
        if (operator.equals("doesnotcontain"))
        	operadorNum = 10;

        switch(operadorNum) {
            case 1:
                if (value instanceof String) {
                    junction.add(Restrictions.ilike(field, value.toString(), MatchMode.EXACT));
                } else {
                    junction.add(Restrictions.eq(field, value));
                }
                break;
            case 2:
                if (value instanceof String) {
                    junction.add(Restrictions.not(Restrictions.ilike(field, value.toString(), MatchMode.EXACT)));
                } else {
                    junction.add(Restrictions.ne(field, value));
                }
                break;
            case 3:
                junction.add(Restrictions.gt(field, value));
                break;
            case 4:
                junction.add(Restrictions.ge(field, value));
                break;
            case 5:
                junction.add(Restrictions.lt(field, value));
                break;
            case 6:
                junction.add(Restrictions.le(field, value));
                break;
            case 7:
                junction.add(getLikeExpression(field, value.toString(), MatchMode.START, ignoreCase));
                break;
            case 8:
                junction.add(getLikeExpression(field, value.toString(), MatchMode.END, ignoreCase));
                break;
            case 9:
                junction.add(getLikeExpression(field, value.toString(), MatchMode.ANYWHERE, ignoreCase));
                break;                
            case 10:
                junction.add(Restrictions.not(Restrictions.ilike(field, value.toString(), MatchMode.ANYWHERE)));
                break;
        }

    }
    
    private static Criterion getLikeExpression(String field, String value, MatchMode mode, boolean ignoreCase) {
        SimpleExpression expression = Restrictions.like(field, value, mode);
        
        if (ignoreCase == true) {
            expression = expression.ignoreCase();
        }
        
        return expression;
    }
    
    private static void filter(Criteria criteria, FilterDescriptor filter, Class<?> clazz) {
        if (filter != null) {
            List<FilterDescriptor> filters = filter.filters;
            
            if (!filters.isEmpty()) {
                Junction junction = Restrictions.conjunction();
                
                if (!filter.getFilters().isEmpty()  && filter.getLogic().toString().equals("or")) {
                    junction = Restrictions.disjunction();
                }
                
                for(FilterDescriptor entry : filters) {
                    if (!entry.getFilters().isEmpty()) {
                        filter(criteria, entry, clazz);
                    } else {
                        restrict(junction, entry, clazz);
                    }
                }
                
                criteria.add(junction);
            }
        }
    }
    
    private static void sort(Criteria criteria, List<SortDescriptor> sort) {
        if (sort != null && !sort.isEmpty()) {
            for (SortDescriptor entry : sort) {
                String field = entry.getField();
                String dir = entry.getDir();
                
                if (dir.equals("asc")) {
                    criteria.addOrder(Order.asc(field));    
                } else if (dir.equals("desc")) {
                    criteria.addOrder(Order.desc(field));
                }
            }
        }
    }   
    
    private List<?> groupBy(List<?> items, List<GroupDescriptor> group, Class<?> clazz, final Session session, List<SimpleExpression> parentRestrictions)  throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    	ArrayList<Map<String, Object>> result = new ArrayList<Map<String,  Object>>();    	
    	                        
        if (!items.isEmpty() && group != null && !group.isEmpty()) {            
            List<List<SimpleExpression>> restrictions = new ArrayList<List<SimpleExpression>>();            
                    
            GroupDescriptor descriptor = group.get(0);
            List<AggregateDescriptor> aggregates = descriptor.getAggregates();
                    
        	final String field = descriptor.getField();
        	
            Method accessor = new PropertyDescriptor(field, clazz).getReadMethod();          		
            
            Object groupValue = accessor.invoke(items.get(0));
                        
            List<Object> groupItems = createGroupItem(group.size() > 1, clazz, session, result, aggregates, field, groupValue, parentRestrictions);            
            
            List<SimpleExpression> groupRestriction = new ArrayList<SimpleExpression>(parentRestrictions);
            groupRestriction.add(Restrictions.eq(field, groupValue));
            restrictions.add(groupRestriction);
            
            for (Object item : items) {            	
            	Object currentValue = accessor.invoke(item);
            	
				if (!groupValue.equals(currentValue)) {
					groupValue = currentValue;					
					groupItems = createGroupItem(group.size() > 1, clazz, session, result, aggregates, field, groupValue, parentRestrictions);
					
					groupRestriction = new ArrayList<SimpleExpression>(parentRestrictions);
		            groupRestriction.add(Restrictions.eq(field, groupValue));
		            restrictions.add(groupRestriction);
				}
				groupItems.add(item);
			}        
            
            if (group.size() > 1) {   
                Integer counter = 0;
            	for (Map<String,Object> g : result) {            	    
            		g.put("items", groupBy((List<?>)g.get("items"), group.subList(1, group.size()), clazz, session, restrictions.get(counter++)));
				}
            }
        }
        
    	return result;
    }

    private List<Object> createGroupItem(Boolean hasSubgroups, Class<?> clazz, final Session session, ArrayList<Map<String, Object>> result,
            List<AggregateDescriptor> aggregates,
            final String field, 
            Object groupValue,
            List<SimpleExpression> aggregateRestrictions) {
        
        Map<String, Object> groupItem = new HashMap<String, Object>();
        List<Object> groupItems = new ArrayList<Object>();
        
        result.add(groupItem);
        
        groupItem.put("value", groupValue);
        groupItem.put("field", field);
        groupItem.put("hasSubgroups", hasSubgroups);
         
        if (aggregates != null && !aggregates.isEmpty()) {           
            Criteria criteria = session.createCriteria(clazz);
            
            filter(criteria, getFilter(), clazz); // filter the set by the selected criteria            
            
            SimpleExpression currentRestriction = Restrictions.eq(field, groupValue);
            
            if (aggregateRestrictions != null && !aggregateRestrictions.isEmpty()) {
                for (SimpleExpression simpleExpression : aggregateRestrictions) {                    
                    criteria.add(simpleExpression);
                }
            }
            criteria.add(currentRestriction);
            
            groupItem.put("aggregates", calculateAggregates(criteria, aggregates));
        } else {
            groupItem.put("aggregates", new HashMap<String, Object>());
        }
        groupItem.put("items", groupItems);
        return groupItems;
    }

    @SuppressWarnings({ "serial", "unchecked" })
    private static Map<String, Object> calculateAggregates(Criteria criteria, List<AggregateDescriptor> aggregates) {
        return (Map<String, Object>)criteria                    
                .setProjection(createAggregatesProjection(aggregates))                    
                .setResultTransformer(new ResultTransformer() {                                    
                    @Override
                    public Object transformTuple(Object[] value, String[] aliases) {                            
                        Map<String, Object> result = new HashMap<String, Object>();
                        
                        for (int i = 0; i < aliases.length; i++) {                                
                            String alias = aliases[i];
                            Map<String, Object> aggregate;
                            
                            String name = alias.split("_")[0];
                            if (result.containsKey(name)) {
                                ((Map<String, Object>)result.get(name)).put(alias.split("_")[1], value[i]);
                            } else {
                                aggregate = new HashMap<String, Object>();                                    
                                aggregate.put(alias.split("_")[1], value[i]);        
                                result.put(name, aggregate);
                            }
                        } 
                        
                        return result;
                    }
                    
                    @SuppressWarnings("rawtypes")
                    @Override
                    public List transformList(List collection) {
                        return collection;
                    }
                })
                .list()
                .get(0);
    }    
    
    private static ProjectionList createAggregatesProjection(List<AggregateDescriptor> aggregates) {
        ProjectionList projections = Projections.projectionList();
        for (AggregateDescriptor aggregate : aggregates) {
            String alias = aggregate.getField() + "_" + aggregate.getAggregate();
            if (aggregate.getAggregate().equals("count")) {                
                projections.add(Projections.count(aggregate.getField()), alias);                
            } else if (aggregate.getAggregate().equals("sum")) {
                projections.add(Projections.sum(aggregate.getField()), alias);                
            } else if (aggregate.getAggregate().equals("average")) {
                projections.add(Projections.avg(aggregate.getField()), alias);                
            } else if (aggregate.getAggregate().equals("min")) {
                projections.add(Projections.min(aggregate.getField()), alias);                
            } else if (aggregate.getAggregate().equals("max")) {
                projections.add(Projections.max(aggregate.getField()), alias);                
            }
        }
        return projections;
    }
    
    private List<?>  group(final Criteria criteria, final Session session, final Class<?> clazz) {
    	List<?> result = new ArrayList<Object>();
    	List<GroupDescriptor> group = getGroup();
    	
        if (group != null && !group.isEmpty()) {
            try {
				result = groupBy(criteria.list(), group, clazz, session, new ArrayList<SimpleExpression>());
			} catch (IllegalAccessException e){
				e.printStackTrace();
			}  catch (IllegalArgumentException e){
				e.printStackTrace();	
			} catch (InvocationTargetException e){
				e.printStackTrace();
			} catch (HibernateException e){
				e.printStackTrace();
			} catch (IntrospectionException e){
				e.printStackTrace();
			}
			
		}
        
        return result;
    }

    private static long total(Criteria criteria) {
        long total = (Long)criteria.setProjection(Projections.rowCount()).uniqueResult();
        
        criteria.setProjection(null);
        criteria.setResultTransformer(Criteria.ROOT_ENTITY);
        
        return total;
    }
    
    private static void page(Criteria criteria, int take, int skip) {
        criteria.setMaxResults(take);
        criteria.setFirstResult(skip);
    }
    
    public DataSourceResult toDataSourceResult(Session session, Class<?> clazz) {
        Criteria criteria = session.createCriteria(clazz);
        
        filter(criteria, getFilter(), clazz);
        
        long total = total(criteria);       
                
        sort(criteria, sortDescriptors());
    
        page(criteria, getTake(), getSkip());        
        
        DataSourceResult result = new DataSourceResult();
        
        result.setTotal(total);
        
        List<GroupDescriptor> groups = getGroup();
        
        if (groups != null && !groups.isEmpty()) {        	
			result.setData(group(criteria, session, clazz));									
        } else {
        	result.setData(criteria.list());	
        }
        
        List<AggregateDescriptor> aggregates = getAggregate();
        if (aggregates != null && !aggregates.isEmpty()) {
            result.setAggregates(aggregate(aggregates, getFilter(), session, clazz));
        }
        
        return result;
    }    
    
     public DataSourceResult toDataSourceResult(Session session, Class<?> clazz, Criteria criteria) {

        
        filter(criteria, getFilter(), clazz);
        
        long total = total(criteria);       
                
        sort(criteria, sortDescriptors());
    
        //TODO: if take =0 and total>0 take=250
        if(getTake()==0 && total>0)
            setTake(250);
        //END TODO 
        page(criteria, getTake(), getSkip());        
        
        DataSourceResult result = new DataSourceResult();
        
        result.setTotal(total);
        
        List<GroupDescriptor> groups = getGroup();
        
        if (groups != null && !groups.isEmpty()) {        	
			result.setData(group(criteria, session, clazz));									
        } else {
        	result.setData(criteria.list());	
        }
        
        List<AggregateDescriptor> aggregates = getAggregate();
        if (aggregates != null && !aggregates.isEmpty()) {
            result.setAggregates(aggregate(aggregates, getFilter(), session, clazz));
        }
        
        return result;
    }    
    private static Map<String, Object> aggregate(List<AggregateDescriptor> aggregates, FilterDescriptor filters, Session session, Class<?> clazz) {
        Criteria criteria = session.createCriteria(clazz);
        
        filter(criteria, filters, clazz);
        
        return calculateAggregates(criteria, aggregates);                
    }    
    
    private List<SortDescriptor> sortDescriptors() {
        List<SortDescriptor> sort = new ArrayList<SortDescriptor>();
        
        List<GroupDescriptor> groups = getGroup();
        List<SortDescriptor> sorts = getSort();
        
        if (groups != null) {
            sort.addAll(groups);
        }        
        
        if (sorts != null) {
            sort.addAll(sorts);
        }
        return sort;        
    }
    
    public List<GroupDescriptor> getGroup() {
        return group;
    }

    public void setGroup(List<GroupDescriptor> group) {
        this.group = group;
    }
    
    public List<AggregateDescriptor> getAggregate() {
        return aggregate;
    }

    public void setAggregate(List<AggregateDescriptor> aggregate) {
        this.aggregate = aggregate;
    }

    public static class SortDescriptor {
        private String field;
        private String dir;
        
        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }
    }
    
    public static class GroupDescriptor extends SortDescriptor {        
        private List<AggregateDescriptor> aggregates;

        public GroupDescriptor() {
            aggregates = new ArrayList<AggregateDescriptor>();
        }        
        
        public List<AggregateDescriptor> getAggregates() {
            return aggregates;
        }
    }
    
    public static class AggregateDescriptor {
        private String field;
        private String aggregate;
        
        public String getField() {
            return field;
        }
        public void setField(String field) {
            this.field = field;
        }
        
        public String getAggregate() {
            return aggregate;
        }
        
        public void setAggregate(String aggregate) {
            this.aggregate = aggregate;
        }
    }
    
    public static class FilterDescriptor {
        private String logic;
        private List<FilterDescriptor> filters;        
        private String field;
        private Object value;        
        private String operator;
        private boolean ignoreCase = true;
        
        public FilterDescriptor() {
            filters = new ArrayList<FilterDescriptor>();
        }
        
        public String getField() {
            return field;
        }
        public void setField(String field) {
            this.field = field;
        }
        public Object getValue() {
            return value;
        }
        public void setValue(Object value) {
            this.value = value;
        }
        public String getOperator() {
            return operator;
        }
        public void setOperator(String operator) {
            this.operator = operator;
        }
        
        public String getLogic() {
            return logic;
        }
        
        public void setLogic(String logic) {
            this.logic = logic;
        }

        public boolean isIgnoreCase() {
            return ignoreCase;
        }

        public void setIgnoreCase(boolean ignoreCase) {
            this.ignoreCase = ignoreCase;
        }
        
        public List<FilterDescriptor> getFilters() {
            return filters;
        }
    }
}
