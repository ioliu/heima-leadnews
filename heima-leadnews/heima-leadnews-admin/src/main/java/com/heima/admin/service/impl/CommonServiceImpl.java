package com.heima.admin.service.impl;

import com.heima.admin.dao.CommonDao;
import com.heima.admin.service.CommonService;
import com.heima.admin.service.impl.commfilter.BaseCommonFilter;
import com.heima.model.admin.dtos.CommonDto;
import com.heima.model.admin.pojos.AdUser;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.threadlocal.AdminThreadLocalUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@SuppressWarnings("all")
public class CommonServiceImpl implements CommonService {

    @Autowired
    private CommonDao commonDao;

    @Autowired
    private ApplicationContext context;

    /**
     * 通用列表加载方法
     * @param dto
     * @return
     */
    @Override
    public ResponseResult list(CommonDto dto) {
        if(!dto.getName().isList()){
            return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        }
        String where = getWhere(dto);
        String tableName = dto.getName().name().toLowerCase();
        int start = (dto.getPage()-1)*dto.getSize();
        if(start<-1){
            start = 0;
        }
        List<?> list = null;
        int total = 0;
        if(StringUtils.isEmpty(where)){
            list = commonDao.list(tableName, start, dto.getSize());
            total = commonDao.listCount(tableName);
        }else{
            list = commonDao.listForWhere(tableName,where,start,dto.getSize());
            total = commonDao.listCountForWhere(tableName,where);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("list",list);
        map.put("total",total);

        //后处理的bean
        doFilter(dto,"list");

        return ResponseResult.okResult(map);
    }

    private void doFilter(CommonDto dto, String name) {
        BaseCommonFilter baseCommonFilter = findFilter(dto);
        if(baseCommonFilter!=null ){
            AdUser adUser = AdminThreadLocalUtils.getUser();
            if("insert".equals(name)){
                baseCommonFilter.doInsertAfter(adUser,dto);
            }
            if("update".equals(name)){
                baseCommonFilter.doUpdateAfter(adUser,dto);
            }
            if("list".equals(name)){
                baseCommonFilter.doListAfter(adUser,dto);
            }
            if("delete".equals(name)){
                baseCommonFilter.doDeleteAfter(adUser,dto);
            }
        }
    }

    private BaseCommonFilter findFilter(CommonDto dto) {
        String name = dto.getName().name();
        if(context.containsBean(name)){
            return context.getBean(name,BaseCommonFilter.class);
        }
        return null;
    }

    /**
     * 拼接查询条件
     * @param dto
     * @return
     */
    private String getWhere(CommonDto dto) {
        StringBuffer where = new StringBuffer();
        if(dto.getWhere()!=null){
            dto.getWhere().stream().forEach(w->{
                if(StringUtils.isNotEmpty(w.getFiled())&&StringUtils.isNotEmpty(w.getValue())&&!w.getFiled().equalsIgnoreCase(w.getValue())){
                    String tempF = parseValue(w.getFiled());
                    String tempV = parseValue(w.getValue());
                    if(!tempF.matches("\\d*")&&!tempF.equalsIgnoreCase(tempV)){
                        if("eq".equalsIgnoreCase(w.getType())){
                            where.append(" and ").append(tempF).append("=\'").append(tempV).append("\'");
                        }
                        if("like".equals(w.getType())){
                            where.append(" and ").append(tempF).append("like \'%").append(tempV).append("%\'");
                        }
                        if("between".equals(w.getType())){
                            String[] temp = tempV.split(",");
                            where.append(" and ").append(tempF).append(temp[0]).append(" and ").append(temp[1]);
                        }
                    }
                }
            });
        }
        return where.toString();
    }

    /**
     * 为了防止sql注入，替换一些敏感的符号
     * @param filed
     * @return
     */
    private String parseValue(String filed) {
        if(StringUtils.isNotEmpty(filed)){
            return filed.replaceAll(".*([';#%]+|(--)+).*","");
        }
        return filed;
    }

    /**
     * 新增和修改的通用封装方法
     * @param dto
     * @return
     */
    @Override
    public ResponseResult update(CommonDto dto) {
        String model = dto.getModel();
        String where = getWhere(dto);
        String tableName = dto.getName().name().toLowerCase();
        if("add".equals(model)){
            if(StringUtils.isNotEmpty(where)){
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"新增数据不能设置条件");
            }else{
                return addData(dto,tableName);
            }
        }else{
            if(StringUtils.isEmpty(where)){
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"修改条件不能为空");
            }else{
                return updateData(dto,tableName,where);
            }
        }
    }

    /**
     * 更新一条数据
     * @param dto
     * @param tableName
     * @param where
     * @return
     */
    private ResponseResult updateData(CommonDto dto, String tableName, String where) {
        String sets = getSets(dto);
        if(!dto.getName().isUpdate()){
            return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        }
        if(StringUtils.isEmpty(sets)){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"修改的参数值不能为空");
        }
        int temp = commonDao.update(tableName, where, sets);
        if(temp>0){
            doFilter(dto,"update");
        }
        return ResponseResult.okResult(temp);
    }

    /**
     * 拼接update语句
     * @param dto
     * @return
     */
    private String getSets(CommonDto dto) {
        StringBuffer sets = new StringBuffer();
        AtomicInteger count = new AtomicInteger();
        if(dto.getSets()!=null){
            dto.getSets().stream().forEach(w->{
                if(StringUtils.isEmpty(w.getValue())){
                    count.incrementAndGet();
                }else{
                    String tempF = parseValue(w.getFiled());
                    String tempV = parseValue(w.getValue());
                    if(!tempF.matches("\\d*")&&!tempF.equalsIgnoreCase(tempV)){
                        if(sets.length()>0){
                            sets.append(",");
                        }
                        sets.append(tempF).append("=\'").append(tempV).append("\'");
                    }
                }

            });
        }
        if(count.get()>0){
            return null;
        }
        return sets.toString();
    }

    /**
     * 插入一条数据的方法
     * @param dto
     * @param tableName
     * @return
     */
    private ResponseResult addData(CommonDto dto, String tableName) {
        String [] sql = getInsertSql(dto);
        if(!dto.getName().isAdd()){
            return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        }
        if(sql==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"传入的参数值不能为空");
        }
        int temp = commonDao.insert(tableName, sql[0], sql[1]);
        if(temp > 0){
            doFilter(dto,"add");
        }
        return ResponseResult.okResult(temp);
    }

    /**
     * 拼接插入的字符串
     * @param dto
     * @return
     */
    private String[] getInsertSql(CommonDto dto) {
        StringBuffer fileds = new StringBuffer();
        StringBuffer values = new StringBuffer();
        AtomicInteger count = new AtomicInteger();
        if(dto.getSets()!=null){
            dto.getSets().stream().forEach(w->{
                if(StringUtils.isEmpty(w.getValue())){
                    count.incrementAndGet();
                }else{
                    String tempF = parseValue(w.getFiled());
                    String tempV = parseValue(w.getValue());
                    if(!tempF.matches("\\d*")&&!tempF.equalsIgnoreCase(tempV)){
                        if(fileds.length()>0){
                            fileds.append(",");
                            values.append(",");
                        }
                        fileds.append(tempF);
                        values.append("\'").append(tempV).append("\'");
                    }
                }
            });
        }
        if(count.get()>0){
            return null;
        }
        return new String[]{fileds.toString(),values.toString()};
    }

    /**
     * 通用的删除方法
     * @param dto
     * @return
     */
    @Override
    public ResponseResult delete(CommonDto dto) {
        String where = getWhere(dto);
        String tableName = dto.getName().name().toLowerCase();
        if(!dto.getName().isDelete()){
            return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        }
        if(StringUtils.isEmpty(where)){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"删除条件不合法");
        }
        int temp = commonDao.delete(tableName, where);
        if(temp>0){
            doFilter(dto,"delete");
        }
        return ResponseResult.okResult(temp);
    }
}
