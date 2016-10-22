/**
 * 
 */
package vlcUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.videolan.vlc.LibVLC;
import org.videolan.vlc.LibVlcException;
import com.lau.vlcdemo.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * @author allin
 * 
 */
public class Main extends Activity {
	private List<Map<String, Object>> mData;
	private ListView lv;
	private Button dev_add_btn;
	private DEV dev;
	private SqliteDAO sdao;
    public final static String TAG = "VLC/VideoPlayerActivity";
	private LibVLC mLibVLC = null;
	private SocketIO  sio;
	private String ip_addr;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sticky);
		lv = (ListView) findViewById(R.id.lv);
		sio=new SocketIO();
		dev=new DEV();
		try {
        	LibVLC.useIOMX(getApplicationContext());
			mLibVLC = LibVLC.getInstance();
		} catch (LibVlcException e) {
			e.printStackTrace();
		}
		sdao=new SqliteDAO(this.getBaseContext());
		mData = getData();
		MyAdapter adapter = new MyAdapter(this);
		lv.setAdapter(adapter);
		dev_add_btn=(Button) findViewById(R.id.add_item);
		dev_add_btn.setOnClickListener(dev_add_BtnListener);
		//setListAdapter(adapter);
	}
	
	private View.OnClickListener dev_add_BtnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.v("MyListView", "myListViewBtnListener");
			// Intent intent = new Intent("android.intent.action.mylistview");
			Intent intent = new Intent(Main.this, DevList.class);
			Bundle bundle = new Bundle();
		    bundle.putString("tag", "add");
		    intent.putExtras(bundle);
			startActivity(intent);
		}

	};
	private List<Map<String, Object>> getData() {
		List<DEV> devs = sdao.findAll(); 
		//Toast.makeText(MyListView4.this,devs.get(1).getIP()+"",  Toast.LENGTH_SHORT).show();  
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (DEV de : devs) {  
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", de.getName());
			map.put("info", de.getIP());
			map.put("status", de.getStatus());
			map.put("img", R.drawable.camera);
			list.add(map);
        }
		return list;
	}
	// ListView ‰∏≠ÊüêÈ°πË¢´ÈÄâ‰∏≠ÂêéÁöÑÈÄªËæë
	/**
	 * listview‰∏≠ÁÇπÂáªÊåâÈîÆÂºπÂá∫ÂØπËØùÊ°Ü
	 */
	public void showInfo(String name){
		new AlertDialog.Builder(this)
		.setTitle("√‹‘ølistview")
		.setMessage(name)
		.setPositiveButton("»∑∂®", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		.show();
	}
	public final class ViewHolder{
		public ImageView img;
		public TextView title;
		public TextView info;
		public Button setBtn;
		public Button viewBtn;
	}
	public class MyAdapter extends BaseAdapter{
		private LayoutInflater mInflater;
		public MyAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder=new ViewHolder();  
				convertView = mInflater.inflate(R.layout.dev_list, null);
				holder.img = (ImageView)convertView.findViewById(R.id.img);
				holder.title = (TextView)convertView.findViewById(R.id.title);
				holder.info = (TextView)convertView.findViewById(R.id.info);
				holder.setBtn = (Button)convertView.findViewById(R.id.option_set);
				holder.viewBtn = (Button)convertView.findViewById(R.id.view_btn);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			holder.viewBtn.setTag(position+"");  //“ª∂®“™º”£¨∑Ò‘ÚŒ™ø’£ª
			holder.setBtn.setTag(position+""); 
			holder.img.setBackgroundResource((Integer)mData.get(position).get("img"));
			holder.title.setText((String)mData.get(position).get("title"));
			holder.info.setText((String)mData.get(position).get("info"));
			ip_addr=(String)mData.get(position).get("info");
			holder.viewBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int index = Integer.parseInt(v.getTag().toString());
					 String ip = (String)mData.get(index).get("info");
					 //showInfo(tmp_name);
					 Bundle bundle = new Bundle();
					 bundle.putString("ip", ip);
					Intent intent = new Intent(Main.this, PlayList.class);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
			
			holder.setBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Main.this, DevList.class);
					Bundle bundle = new Bundle();
				    bundle.putString("tag", "set");
				    int index = Integer.parseInt(v.getTag().toString());
					String tmp_name = (String)mData.get(index).get("info");
					//Toast.makeText(MyListView4.this,tmp_name+"",  Toast.LENGTH_SHORT).show();  
				    bundle.putString("ip", tmp_name);
				    intent.putExtras(bundle);
					startActivity(intent);
				}
			});
			final int cur_index=position;
			Toast.makeText(Main.this, mData.get(cur_index).get("status")+"----",  Toast.LENGTH_SHORT).show();
			new Thread(){
	               @Override
	               public void run()
	               {
	            	   if(mData.get(cur_index).get("status").equals("on")){
	            		   String status=sio.sendmsg_camer(ip_addr,"open");
	            	   }else{
	            		   String status=sio.sendmsg_camer(ip_addr,"close");
	            	   }
	            	   //Toast.makeText(PlayList.this,dirs.length+"",  Toast.LENGTH_SHORT).show();
	               }
	               }.start();
			return convertView;
		}
	}
}
