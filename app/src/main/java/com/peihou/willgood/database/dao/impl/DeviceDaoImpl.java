package com.peihou.willgood.database.dao.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.peihou.willgood.database.DBManager;
import com.peihou.willgood.database.dao.DaoMaster;
import com.peihou.willgood.database.dao.DaoSession;
import com.peihou.willgood.database.dao.DeviceDao;
import com.peihou.willgood.pojo.Device;

import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class DeviceDaoImpl {
    int userId;
    private DeviceDao deviceDao;

    public DeviceDaoImpl(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userInfo2", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);
        DBManager dbManager=DBManager.getInstance(context);//获取数据库管理者单例对象
        DaoSession session=dbManager.getDaoSession();//获取数据库会话对象
        deviceDao=session.getDeviceDao();
    }

    /**
     * 添加设备
     *
     * @param device
     * @return
     */
    public boolean insert(Device device) {
        deviceDao.insert(device);
        return true;
    }

    /**
     * 删除设备
     *
     * @param device
     */
    public void delete(Device device) {
        deviceDao.delete(device);
    }

    /**
     * 批量删除设备
     *
     * @param devices
     */
    public void deleteDevices(List<Device> devices) {
        deviceDao.deleteInTx(devices);
    }

    /**
     * 删除所有设备
     */
    public void deleteAll() {
        List<Device> devices=findAllDevice();
        if (!devices.isEmpty()){
            deviceDao.deleteInTx(devices);
        }
    }

    /**
     * 修改设备
     *
     * @param device
     */
    public void update(Device device) {
        deviceDao.update(device);
    }

    /**
     * 根据设备id,查询设备
     *
     * @param deviceId
     * @return
     */
    public Device findDeviceById(long deviceId) {
        return deviceDao.queryBuilder().where(DeviceDao.Properties.DeviceId.eq(deviceId)).unique();
    }

    public Device findDeviceByType(int type){
        WhereCondition whereCondition=deviceDao.queryBuilder().and(DeviceDao.Properties.System.eq(type),DeviceDao.Properties.UserId.eq(userId));
        return deviceDao.queryBuilder().where(whereCondition).unique();
    }
    /**
     * 查询同一个macd的所有设备
     *
     * @param deviceMac
     * @return
     */
    public List<Device> findDevicesByMac(String deviceMac) {
        WhereCondition whereCondition=deviceDao.queryBuilder().and(DeviceDao.Properties.UserId.eq(userId), DeviceDao.Properties.DeviceOnlyMac.eq(deviceMac));
        return deviceDao.queryBuilder().where(whereCondition).list();
    }

    public Device findDeviceByMac(String deviceMac) {
        WhereCondition whereCondition=deviceDao.queryBuilder().and(DeviceDao.Properties.DeviceOnlyMac.eq(deviceMac),DeviceDao.Properties.UserId.eq(userId));
        return deviceDao.queryBuilder().where(whereCondition).unique();
    }

    /**
     * 查询设备类型
     * @param type
     * @return
     */
    public List<Device> findDeviceDeviceType(int type){
        WhereCondition whereCondition=deviceDao.queryBuilder().and(DeviceDao.Properties.UserId.eq(userId),DeviceDao.Properties.System.eq(type));
        return deviceDao.queryBuilder().where(whereCondition).list();
    }

    /**
     * 查询设备
     *
     * @return
     */
    public List<Device> findAllDevice() {
        return deviceDao.queryBuilder().where(DeviceDao.Properties.UserId.eq(userId)).list();
    }
}
