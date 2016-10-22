package vlcUI;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SqliteDAO {
	private SqliteHandler SqliteHandler;  
    public SqliteDAO(Context context) {  
        this.SqliteHandler = new SqliteHandler(context, "db4dev.db", null, 1);  
    }  
  
    public void save(DEV dev) {// 插入记录  
        SQLiteDatabase db = SqliteHandler.getWritableDatabase();// 取得数据库操作
        db.execSQL("CREATE TABLE  IF NOT EXISTS t_dev (id integer primary key autoincrement, name varchar(60), ip varchar(60),status varchar(60))");
        db.execSQL("insert into t_dev (name,ip,status) values(?,?,?)", new Object[] { dev.getName(), dev.getIP(),dev.getStatus() });  
        db.close();// 记得关闭数据库操作  
    }  
  
    public void delete(String ip) {// 删除纪录  
        SQLiteDatabase db = SqliteHandler.getWritableDatabase();  
        db.execSQL("delete from t_dev where ip=?", new Object[] { ip.toString() });  
        db.close();  
    }  
  
    public void update(DEV DEV) {// 修改纪录  
        SQLiteDatabase db = SqliteHandler.getWritableDatabase();  
        db.execSQL("update t_dev set name=?,status=?,ip=?  where" + " id=?", new Object[] { DEV.getName(), DEV.getStatus(), DEV.getIP(), DEV.getId()});  
        db.close();  
    }  
  
    public DEV find(String ip) {// 根据ID查找纪录  
        DEV dev = null;  
        SQLiteDatabase db = SqliteHandler.getReadableDatabase();  
        db.execSQL("CREATE TABLE  IF NOT EXISTS t_dev (id integer primary key autoincrement, name varchar(60), ip varchar(60),status varchar(60))");
        // 用游标Cursor接收从数据库检索到的数据  
        Cursor cursor = db.rawQuery("select * from t_dev where ip=?", new String[] { ip.toString() });  
        if (cursor.moveToFirst()) {// 依次取出数据  
            dev = new DEV();  
            dev.setId(cursor.getInt(cursor.getColumnIndex("id")));
            dev.setIP(cursor.getString(cursor.getColumnIndex("ip")));  
            dev.setName(cursor.getString(cursor.getColumnIndex("name")));  
            dev.setStatus(cursor.getString(cursor.getColumnIndex("status")));  
        }  
        db.close();  
        return dev;  
    }  
  
    public List<DEV> findAll() {// 查询所有记录  
        List<DEV> lists = new ArrayList<DEV>();  
        DEV dev = null;  
        SQLiteDatabase db = SqliteHandler.getReadableDatabase();  
        db.execSQL("CREATE TABLE  IF NOT EXISTS t_dev (id integer primary key autoincrement, name varchar(60), ip varchar(60),status varchar(60))");
        // Cursor cursor=db.rawQuery("select * from t_dev limit ?,?", new  
        // String[]{offset.toString(),maxLength.toString()});  
        // //这里支持类型MYSQL的limit分页操作  
  
        Cursor cursor = db.rawQuery("select * from t_dev ", null);  
        while (cursor.moveToNext()) {  
            dev = new DEV();  
            dev.setIP(cursor.getString(cursor.getColumnIndex("ip")));  
            dev.setName(cursor.getString(cursor.getColumnIndex("name")));  
            dev.setStatus(cursor.getString(cursor.getColumnIndex("status")));  
            lists.add(dev);  
        }  
        db.close();  
        return lists;  
    }  
  
    public long getCount() {//统计所有记录数  
        SQLiteDatabase db = SqliteHandler.getReadableDatabase();  
        Cursor cursor = db.rawQuery("select count(*) from t_dev ", null);  
        cursor.moveToFirst();  
        db.close();  
        return cursor.getLong(0);  
    }  
    
    public void delete_all() {//删除表
    	SQLiteDatabase db = SqliteHandler.getWritableDatabase();  
    	db.execSQL("DROP TABLE IF EXISTS t_dev");
        db.close();  
    }  
}
