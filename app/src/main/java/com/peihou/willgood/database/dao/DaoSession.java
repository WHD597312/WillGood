package com.peihou.willgood.database.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.peihou.willgood.pojo.Alerm;
import com.peihou.willgood.pojo.Device;
import com.peihou.willgood.pojo.Line2;
import com.peihou.willgood.pojo.Linked;
import com.peihou.willgood.pojo.LinkedType;
import com.peihou.willgood.pojo.TimerTask;

import com.peihou.willgood.database.dao.AlermDao;
import com.peihou.willgood.database.dao.DeviceDao;
import com.peihou.willgood.database.dao.Line2Dao;
import com.peihou.willgood.database.dao.LinkedDao;
import com.peihou.willgood.database.dao.LinkedTypeDao;
import com.peihou.willgood.database.dao.TimerTaskDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig alermDaoConfig;
    private final DaoConfig deviceDaoConfig;
    private final DaoConfig line2DaoConfig;
    private final DaoConfig linkedDaoConfig;
    private final DaoConfig linkedTypeDaoConfig;
    private final DaoConfig timerTaskDaoConfig;

    private final AlermDao alermDao;
    private final DeviceDao deviceDao;
    private final Line2Dao line2Dao;
    private final LinkedDao linkedDao;
    private final LinkedTypeDao linkedTypeDao;
    private final TimerTaskDao timerTaskDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        alermDaoConfig = daoConfigMap.get(AlermDao.class).clone();
        alermDaoConfig.initIdentityScope(type);

        deviceDaoConfig = daoConfigMap.get(DeviceDao.class).clone();
        deviceDaoConfig.initIdentityScope(type);

        line2DaoConfig = daoConfigMap.get(Line2Dao.class).clone();
        line2DaoConfig.initIdentityScope(type);

        linkedDaoConfig = daoConfigMap.get(LinkedDao.class).clone();
        linkedDaoConfig.initIdentityScope(type);

        linkedTypeDaoConfig = daoConfigMap.get(LinkedTypeDao.class).clone();
        linkedTypeDaoConfig.initIdentityScope(type);

        timerTaskDaoConfig = daoConfigMap.get(TimerTaskDao.class).clone();
        timerTaskDaoConfig.initIdentityScope(type);

        alermDao = new AlermDao(alermDaoConfig, this);
        deviceDao = new DeviceDao(deviceDaoConfig, this);
        line2Dao = new Line2Dao(line2DaoConfig, this);
        linkedDao = new LinkedDao(linkedDaoConfig, this);
        linkedTypeDao = new LinkedTypeDao(linkedTypeDaoConfig, this);
        timerTaskDao = new TimerTaskDao(timerTaskDaoConfig, this);

        registerDao(Alerm.class, alermDao);
        registerDao(Device.class, deviceDao);
        registerDao(Line2.class, line2Dao);
        registerDao(Linked.class, linkedDao);
        registerDao(LinkedType.class, linkedTypeDao);
        registerDao(TimerTask.class, timerTaskDao);
    }
    
    public void clear() {
        alermDaoConfig.clearIdentityScope();
        deviceDaoConfig.clearIdentityScope();
        line2DaoConfig.clearIdentityScope();
        linkedDaoConfig.clearIdentityScope();
        linkedTypeDaoConfig.clearIdentityScope();
        timerTaskDaoConfig.clearIdentityScope();
    }

    public AlermDao getAlermDao() {
        return alermDao;
    }

    public DeviceDao getDeviceDao() {
        return deviceDao;
    }

    public Line2Dao getLine2Dao() {
        return line2Dao;
    }

    public LinkedDao getLinkedDao() {
        return linkedDao;
    }

    public LinkedTypeDao getLinkedTypeDao() {
        return linkedTypeDao;
    }

    public TimerTaskDao getTimerTaskDao() {
        return timerTaskDao;
    }

}
