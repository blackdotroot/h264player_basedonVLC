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
  
    public void save(DEV dev) {// �����¼  
        SQLiteDatabase db = SqliteHandler.getWritableDatabase();// ȡ�����ݿ����
        db.execSQL("CREATE TABLE  IF NOT EXISTS t_dev (id integer primary key autoincrement, name varchar(60), ip varchar(60),status varchar(60))");
        db.execSQL("insert into t_dev (name,ip,status) values(?,?,?)", new Object[] { dev.getName(), dev.getIP(),dev.getStatus() });  
        db.close();// �ǵùر����ݿ����  
    }  
  
    public void delete(String ip) {// ɾ����¼  
        SQLiteDatabase db = SqliteHandler.getWritableDatabase();  
        db.execSQL("delete from t_dev where ip=?", new Object[] { ip.toString() });  
        db.close();  
    }  
  
    public void update(DEV DEV) {// �޸ļ�¼  
        SQLiteDatabase db = SqliteHandler.getWritableDatabase();  
        db.execSQL("update t_dev set name=?,status=?,ip=?  where" + " id=?", new Object[] { DEV.getName(), DEV.getStatus(), DEV.getIP(), DEV.getId()});  
        db.close();  
    }  
  
    public DEV find(String ip) {// ����ID���Ҽ�¼  
        DEV dev = null;  
        SQLiteDatabase db = SqliteHandler.getReadableDatabase();  
        db.execSQL("CREATE TABLE  IF NOT EXISTS t_dev (id integer primary key autoincrement, name varchar(60), ip varchar(60),status varchar(60))");
        // ���α�Cursor���մ����ݿ������������  
        Cursor cursor = db.rawQuery("select * from t_dev where ip=?", new String[] { ip.toString() });  
        if (cursor.moveToFirst()) {// ����ȡ������  
            dev = new DEV();  
            dev.setId(cursor.getInt(cursor.getColumnIndex("id")));
            dev.setIP(cursor.getString(cursor.getColumnIndex("ip")));  
            dev.setName(cursor.getString(cursor.getColumnIndex("name")));  
            dev.setStatus(cursor.getString(cursor.getColumnIndex("status")));  
        }  
        db.close();  
        return dev;  
    }  
  
    public List<DEV> findAll() {// ��ѯ���м�¼  
        List<DEV> lists = new ArrayList<DEV>();  
        DEV dev = null;  
        SQLiteDatabase db = SqliteHandler.getReadableDatabase();  
        db.execSQL("CREATE TABLE  IF NOT EXISTS t_dev (id integer primary key autoincrement, name varchar(60), ip varchar(60),status varchar(60))");
        // Cursor cursor=db.rawQuery("select * from t_dev limit ?,?", new  
        // String[]{offset.toString(),maxLength.toString()});  
        // //����֧������MYSQL��limit��ҳ����  
  
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
  
    public long getCount() {//ͳ�����м�¼��  
        SQLiteDatabase db = SqliteHandler.getReadableDatabase();  
        Cursor cursor = db.rawQuery("select count(*) from t_dev ", null);  
        cursor.moveToFirst();  
        db.close();  
        return cursor.getLong(0);  
    }  
    
    public void delete_all() {//ɾ����
    	SQLiteDatabase db = SqliteHandler.getWritableDatabase();  
    	db.execSQL("DROP TABLE IF EXISTS t_dev");
        db.close();  
    }  
}
