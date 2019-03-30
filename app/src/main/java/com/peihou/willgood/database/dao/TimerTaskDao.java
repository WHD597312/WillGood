package com.peihou.willgood.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.peihou.willgood.pojo.TimerTask;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TIMER_TASK".
*/
public class TimerTaskDao extends AbstractDao<TimerTask, Long> {

    public static final String TABLENAME = "TIMER_TASK";

    /**
     * Properties of entity TimerTask.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property DeviceId = new Property(1, long.class, "deviceId", false, "DEVICE_ID");
        public final static Property Open = new Property(2, boolean.class, "open", false, "OPEN");
        public final static Property DeviceMac = new Property(3, String.class, "deviceMac", false, "DEVICE_MAC");
        public final static Property Name = new Property(4, String.class, "name", false, "NAME");
        public final static Property Timer = new Property(5, String.class, "timer", false, "TIMER");
        public final static Property McuVersion = new Property(6, int.class, "mcuVersion", false, "MCU_VERSION");
        public final static Property Choice = new Property(7, int.class, "choice", false, "CHOICE");
        public final static Property Year = new Property(8, int.class, "year", false, "YEAR");
        public final static Property Month = new Property(9, int.class, "month", false, "MONTH");
        public final static Property Day = new Property(10, int.class, "day", false, "DAY");
        public final static Property Week = new Property(11, int.class, "week", false, "WEEK");
        public final static Property Hour = new Property(12, int.class, "hour", false, "HOUR");
        public final static Property Min = new Property(13, int.class, "min", false, "MIN");
        public final static Property Prelines = new Property(14, int.class, "prelines", false, "PRELINES");
        public final static Property Lastlines = new Property(15, int.class, "lastlines", false, "LASTLINES");
        public final static Property ControlState = new Property(16, int.class, "controlState", false, "CONTROL_STATE");
        public final static Property State = new Property(17, int.class, "state", false, "STATE");
        public final static Property Timers = new Property(18, int.class, "timers", false, "TIMERS");
        public final static Property Seconds = new Property(19, long.class, "seconds", false, "SECONDS");
        public final static Property Visitity = new Property(20, int.class, "visitity", false, "VISITITY");
    }


    public TimerTaskDao(DaoConfig config) {
        super(config);
    }
    
    public TimerTaskDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TIMER_TASK\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"DEVICE_ID\" INTEGER NOT NULL ," + // 1: deviceId
                "\"OPEN\" INTEGER NOT NULL ," + // 2: open
                "\"DEVICE_MAC\" TEXT," + // 3: deviceMac
                "\"NAME\" TEXT," + // 4: name
                "\"TIMER\" TEXT," + // 5: timer
                "\"MCU_VERSION\" INTEGER NOT NULL ," + // 6: mcuVersion
                "\"CHOICE\" INTEGER NOT NULL ," + // 7: choice
                "\"YEAR\" INTEGER NOT NULL ," + // 8: year
                "\"MONTH\" INTEGER NOT NULL ," + // 9: month
                "\"DAY\" INTEGER NOT NULL ," + // 10: day
                "\"WEEK\" INTEGER NOT NULL ," + // 11: week
                "\"HOUR\" INTEGER NOT NULL ," + // 12: hour
                "\"MIN\" INTEGER NOT NULL ," + // 13: min
                "\"PRELINES\" INTEGER NOT NULL ," + // 14: prelines
                "\"LASTLINES\" INTEGER NOT NULL ," + // 15: lastlines
                "\"CONTROL_STATE\" INTEGER NOT NULL ," + // 16: controlState
                "\"STATE\" INTEGER NOT NULL ," + // 17: state
                "\"TIMERS\" INTEGER NOT NULL ," + // 18: timers
                "\"SECONDS\" INTEGER NOT NULL ," + // 19: seconds
                "\"VISITITY\" INTEGER NOT NULL );"); // 20: visitity
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TIMER_TASK\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TimerTask entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getDeviceId());
        stmt.bindLong(3, entity.getOpen() ? 1L: 0L);
 
        String deviceMac = entity.getDeviceMac();
        if (deviceMac != null) {
            stmt.bindString(4, deviceMac);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
 
        String timer = entity.getTimer();
        if (timer != null) {
            stmt.bindString(6, timer);
        }
        stmt.bindLong(7, entity.getMcuVersion());
        stmt.bindLong(8, entity.getChoice());
        stmt.bindLong(9, entity.getYear());
        stmt.bindLong(10, entity.getMonth());
        stmt.bindLong(11, entity.getDay());
        stmt.bindLong(12, entity.getWeek());
        stmt.bindLong(13, entity.getHour());
        stmt.bindLong(14, entity.getMin());
        stmt.bindLong(15, entity.getPrelines());
        stmt.bindLong(16, entity.getLastlines());
        stmt.bindLong(17, entity.getControlState());
        stmt.bindLong(18, entity.getState());
        stmt.bindLong(19, entity.getTimers());
        stmt.bindLong(20, entity.getSeconds());
        stmt.bindLong(21, entity.getVisitity());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TimerTask entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getDeviceId());
        stmt.bindLong(3, entity.getOpen() ? 1L: 0L);
 
        String deviceMac = entity.getDeviceMac();
        if (deviceMac != null) {
            stmt.bindString(4, deviceMac);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
 
        String timer = entity.getTimer();
        if (timer != null) {
            stmt.bindString(6, timer);
        }
        stmt.bindLong(7, entity.getMcuVersion());
        stmt.bindLong(8, entity.getChoice());
        stmt.bindLong(9, entity.getYear());
        stmt.bindLong(10, entity.getMonth());
        stmt.bindLong(11, entity.getDay());
        stmt.bindLong(12, entity.getWeek());
        stmt.bindLong(13, entity.getHour());
        stmt.bindLong(14, entity.getMin());
        stmt.bindLong(15, entity.getPrelines());
        stmt.bindLong(16, entity.getLastlines());
        stmt.bindLong(17, entity.getControlState());
        stmt.bindLong(18, entity.getState());
        stmt.bindLong(19, entity.getTimers());
        stmt.bindLong(20, entity.getSeconds());
        stmt.bindLong(21, entity.getVisitity());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public TimerTask readEntity(Cursor cursor, int offset) {
        TimerTask entity = new TimerTask( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // deviceId
            cursor.getShort(offset + 2) != 0, // open
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // deviceMac
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // name
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // timer
            cursor.getInt(offset + 6), // mcuVersion
            cursor.getInt(offset + 7), // choice
            cursor.getInt(offset + 8), // year
            cursor.getInt(offset + 9), // month
            cursor.getInt(offset + 10), // day
            cursor.getInt(offset + 11), // week
            cursor.getInt(offset + 12), // hour
            cursor.getInt(offset + 13), // min
            cursor.getInt(offset + 14), // prelines
            cursor.getInt(offset + 15), // lastlines
            cursor.getInt(offset + 16), // controlState
            cursor.getInt(offset + 17), // state
            cursor.getInt(offset + 18), // timers
            cursor.getLong(offset + 19), // seconds
            cursor.getInt(offset + 20) // visitity
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TimerTask entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDeviceId(cursor.getLong(offset + 1));
        entity.setOpen(cursor.getShort(offset + 2) != 0);
        entity.setDeviceMac(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setTimer(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setMcuVersion(cursor.getInt(offset + 6));
        entity.setChoice(cursor.getInt(offset + 7));
        entity.setYear(cursor.getInt(offset + 8));
        entity.setMonth(cursor.getInt(offset + 9));
        entity.setDay(cursor.getInt(offset + 10));
        entity.setWeek(cursor.getInt(offset + 11));
        entity.setHour(cursor.getInt(offset + 12));
        entity.setMin(cursor.getInt(offset + 13));
        entity.setPrelines(cursor.getInt(offset + 14));
        entity.setLastlines(cursor.getInt(offset + 15));
        entity.setControlState(cursor.getInt(offset + 16));
        entity.setState(cursor.getInt(offset + 17));
        entity.setTimers(cursor.getInt(offset + 18));
        entity.setSeconds(cursor.getLong(offset + 19));
        entity.setVisitity(cursor.getInt(offset + 20));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TimerTask entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TimerTask entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TimerTask entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
