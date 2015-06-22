/**
 * 
 */
package com.fccfc.framework.web.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.FrameworkException;
import com.fccfc.framework.common.GlobalConstants;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.UtilException;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.web.bean.operator.AccountPojo;
import com.fccfc.framework.web.bean.operator.OperatorPojo;
import com.fccfc.framework.web.dao.operator.AccountDao;
import com.fccfc.framework.web.dao.operator.OperatorDao;
import com.fccfc.framework.web.service.OperatorService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月30日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.service.impl <br>
 */
@Service
public class OperatorServiceImple implements OperatorService {

    @Resource
    private OperatorDao operatorDao;

    @Resource
    private AccountDao accountDao;

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.api.operator.OperatorService#getOperator(java.lang.Integer, java.lang.String)
     */
    @Override
    public OperatorPojo getOperator(Integer id, Integer code) throws ServiceException {
        OperatorPojo operator = null;
        try {
            if (id != null) {
                operator = (OperatorPojo) CacheHelper.getCache().getValue(CacheConstant.OPERATOR,
                    id + GlobalConstants.BLANK);
                if (operator != null) {
                    return operator;
                }
            }

            operator = operatorDao.getOperator(id, code);
            if (operator != null) {
                CacheHelper.getCache().putValue(CacheConstant.OPERATOR,
                    operator.getOperatorId() + GlobalConstants.BLANK, operator);
            }
        }
        catch (FrameworkException e) {
            throw new ServiceException(e);
        }

        return operator;
    }

    /**
     * @see com.fccfc.framework.api.operator.OperatorService#addOperator(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    @Override
    public OperatorPojo addOperator(String username, String password, String accountType, String operatorType,
        String registIp) throws ServiceException {
        try {
            OperatorPojo operator = operatorDao.getOperatorByAccount(username, accountType);
            if (operator != null) {
                throw new ServiceException(ErrorCodeDef.ACCOUNT_EXSIST_20004, "账号已经存在");
            }

            operator = new OperatorPojo();
            Date currentDate = new Date();
            operator.setCreateDate(currentDate);
            operator.setIsLocked("N");
            operator.setLoginFail(0);
            operator.setOperatorType(operatorType);
            operator.setRegistIp(registIp);
            operator.setState("A");
            operator.setStateDate(currentDate);
            if (",A,B,C,M,".indexOf("," + accountType + ",") != -1) {
                operator.setUserName(username);
                if (CommonUtil.isNotEmpty(password)) {
                    operator.setPassword(CommonUtil.md5(password));
                }
            }
            operatorDao.save(operator);

            AccountPojo account = new AccountPojo();
            account.setAccountType(accountType);
            account.setAccountValue(username);
            account.setCreateTime(currentDate);
            account.setOperatorId(operator.getOperatorId());
            account.setState("A");
            account.setStateTime(currentDate);
            accountDao.save(account);

            return operator;
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
        catch (UtilException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * @see com.fccfc.framework.api.operator.OperatorService#updateOperatorCode(int, int)
     */
    @Override
    public int updateOperatorCode(int id, int code) throws ServiceException {
        try {
            return operatorDao.updateOperatorCode(id, code);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * @see com.fccfc.framework.api.operator.OperatorService#login(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public OperatorPojo login(String type, String username, String password, String ip) throws ServiceException {
        OperatorPojo operator = checkOperator(type, username, password);

        try {
            operatorDao.insertOperatorHistory(operator.getOperatorId(), operator.getOperatorId());
            operator.setLastIp(ip);
            operator.setLastLoginDate(new Date());
            operatorDao.update(operator);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
        return operator;
    }

    /**
     * @see com.fccfc.framework.api.operator.OperatorService#getOperatorByAccount(java.lang.String, java.lang.String)
     */
    @Override
    public OperatorPojo getOperatorByAccount(String type, String username) throws ServiceException {
        try {
            return operatorDao.getOperatorByAccount(username, type);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * @see com.fccfc.framework.api.operator.OperatorService#checkOperator(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    @Override
    public OperatorPojo checkOperator(String type, String username, String password) throws ServiceException {
        OperatorPojo operator = getOperatorByAccount(type, username);
        if (operator == null) {
            throw new ServiceException(ErrorCodeDef.OPERATOR_NOT_EXSIST_20007, "用户名或密码错误");
        }
        // 自有类型账号需要校验密码
        try {
            if (CommonUtil.isNotEmpty(operator.getPassword()) && ",A,B,C,M,".indexOf("," + type + ",") != -1
                && !StringUtils.equals(CommonUtil.md5(password), operator.getPassword())) {
                throw new ServiceException(ErrorCodeDef.USER_NAME_OR_PASSWORD_ERROR_20002, "用户名或密码错误");
            }
        }
        catch (UtilException e) {
            throw new ServiceException(e);
        }

        if ("Y".equals(operator.getIsLocked())) {
            throw new ServiceException(ErrorCodeDef.ACCOUNT_IS_LOCK_20008, "账号被锁定了");
        }

        if (!OperatorPojo.STATE_AVALIABLE.equals(operator.getState())) {
            throw new ServiceException(ErrorCodeDef.STATE_ERROR_20009, "账号不可用");
        }

        return operator;
    }

    /**
     * @see com.fccfc.framework.api.operator.OperatorService#updatePassword(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void updatePassword(OperatorPojo operator, String password) throws ServiceException {

        try {
            operatorDao.insertOperatorHistory(operator.getOperatorId(), operator.getOperatorId());
            operator.setPassword(CommonUtil.md5(password));
            operatorDao.updateOperatorPassword(operator.getOperatorId(), operator.getPassword());
        }
        catch (UtilException e) {
            throw new ServiceException(e);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

}
