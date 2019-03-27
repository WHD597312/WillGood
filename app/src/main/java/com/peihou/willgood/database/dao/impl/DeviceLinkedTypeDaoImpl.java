package com.peihou.willgood.database.dao.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.peihou.willgood.database.DBManager;
import com.peihou.willgood.database.dao.DaoMaster;
import com.peihou.willgood.database.dao.DaoSession;
import com.peihou.willgood.database.dao.LinkedTypeDao;
import com.peihou.willgood.pojo.LinkedType;

import java.util.List;

public class DeviceLinkedTypeDaoImpl {
    private Context context;
    private SQLiteDatabase db;
    private DaoMaster master;
    private LinkedTypeDao linkedTypeDao;
    private DaoSession session;
    public DeviceLinkedTypeDaoImpl(Context context) {
        this.context = context;
        db= DBManager.getInstance(context).getWritableDasebase();
        master=new DaoMaster(db);
        session=master.newSession();
        linkedTypeDao=session.getLinkedTypeDao();
    }

    /**
     * 批量插入设备联动类型
     * @param linkedTypes
     */
    public void insertLinkedTypes(List<LinkedType> linkedTypes){
        linkedTypeDao.insertInTx(linkedTypes);
    }

    /**
     * 更新设备联动类型
     * @param linkedType
     */
    public void update(LinkedType linkedType){
        linkedTypeDao.update(linkedType);
    }

    /**
     * 批量更新联动类型
     * @param linkedTypes
     */
    public void updateLinkedTypes(List<LinkedType> linkedTypes){
        linkedTypeDao.updateInTx(linkedTypes);
    }
    /**
     * 批量删除设备的联动
     * @param deviceMac
     */
    public void deleteLinkedTypes(String deviceMac){
        List<LinkedType> list=linkedTypeDao.queryBuilder().where(LinkedTypeDao.Properties.MacAddress.eq(deviceMac)).list();
        linkedTypeDao.deleteInTx(list);
    }

    /**
     * 查询设备的所有联动
     * @param deviceMac
     * @return
     */
    public List<LinkedType> findLinkdType(String deviceMac){
        return linkedTypeDao.queryBuilder().where(LinkedTypeDao.Properties.MacAddress.eq(deviceMac)).orderAsc(LinkedTypeDao.Properties.Type).list();
    }


    /**
     * 删除所有的联动类型
     */
    public void deleteAll(){
        linkedTypeDao.deleteAll();
    }
}
