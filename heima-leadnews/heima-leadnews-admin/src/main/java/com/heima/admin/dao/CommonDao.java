package com.heima.admin.dao;

import org.apache.ibatis.annotations.*;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface CommonDao {

    @Select("select * from ${tableName} limit #{start},#{size}")
    @ResultType(HashMap.class)
    List<HashMap> list(@Param("tableName") String tableName,@Param("start") int start,@Param("size") int size);

    @Select("select count(*) from ${tableName}")
    @ResultType(Integer.class)
    int listCount(@Param("tableName") String tableName);

    @Select("select * from ${tableName} where 1=1 ${where} limit #{start},#{size} ")//where ==> and name = 11  and password = ddd
    @ResultType(HashMap.class)
    List<HashMap> listForWhere(@Param("tableName") String tableName,@Param("where") String where,@Param("start") int start,@Param("size") int size);

    @Select("select * from ${tableName} where 1=1 ${where} ")//where ==> and name = 11  and password = ddd
    @ResultType(Integer.class)
    int listCountForWhere(@Param("tableName") String tableName,@Param("where") String where);

    @Update("update ${tableName} set ${sets} where 1=1 ${where}")//sets==>  name=xxx,password=xsdfd
    @ResultType(Integer.class)
    int update(@Param("tableName") String tableName,@Param("where") String where,@Param("sets") String sets);

    @Insert("insert into ${tableName} (${fileds}) values (${values})")
    @ResultType(Integer.class)
    int insert(@Param("tableName") String tableName,@Param("fileds") String fileds,@Param("values")String values);

    @Delete("delete from ${tableName} where 1 = 1 ${where} limit 1")
    @ResultType(Integer.class)
    int delete(@Param("tableName") String tableName,@Param("where") String where);
}
