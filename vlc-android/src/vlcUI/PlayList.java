package vlcUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lau.vlcdemo.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


public class PlayList extends Activity {
	private List<Map<String, Object>> mData;
	private ListView lv;  
	private SocketIO  sio;
	private Button fresh;
	private String dirs;
	private MyAdapter madapter;
	private String ip_addr;
	// private List<String> data = new ArrayList<String>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist_holder);
		lv = (ListView) findViewById(R.id.playlist_view);
		sio=new SocketIO();
		fresh=(Button)findViewById(R.id.dir_fresh);
		Bundle bundle = this.getIntent().getExtras();
		ip_addr = bundle.getString("ip").trim();
		fresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(){
		               @Override
		        public void run()
		               {
		            	   dirs=sio.sendmsg_f();
		            	   if(dirs==null){
		            		  dirs="";  
		            	   }
		            	   Message msg =new Message();
		            	   msg.obj = dirs;
		            	   mHandler.sendMessage(msg); 
		            	   //Toast.makeText(PlayList.this,dirs.length+"",  Toast.LENGTH_SHORT).show();
		               }
		               }.start();
			}
		});
		new Thread(){
		@Override
			public void run(){
			dirs=sio.sendmsg_f();
			 if(dirs==null){
				 dirs="";
      	   }
			 Message msg =new Message();
        	 msg.obj = dirs;
        	 mHandler.sendMessage(msg);  
			//Toast.makeText(PlayList.this,dirs.length+"",  Toast.LENGTH_SHORT).show();
			}
		}.start();
		madapter = new MyAdapter(this);
		//lv.setAdapter(madapter);
	}

	Handler mHandler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
            super.handleMessage(msg);  
            switch (msg.what) {  
            case 0:  
                //完成主界面更新,拿到数据  
                String data = (String)msg.obj;  
                data="直播,"+data;
                String [] dirs=data.split(",");
                mData = getData(dirs);
                madapter.notifyDataSetChanged();
                lv.setAdapter(madapter);
                break;  
            default:  
                break;  
            }  
        }  
    };  
	private List<Map<String, Object>> getData(String[] dirs_t) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(int i=0;i<dirs_t.length;i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", dirs_t[i]);
			list.add(map);
		}
		return list;
	}
	
	public final class ViewHolder{
		public TextView title;
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
				convertView = mInflater.inflate(R.layout.display_list, null);
				holder.title = (TextView)convertView.findViewById(R.id.dir_name);
				holder.viewBtn = (Button)convertView.findViewById(R.id.play_video);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder)convertView.getTag();
			}
			holder.viewBtn.setTag(position+"");  //一定要加，否则为空；
			holder.title.setText((String)mData.get(position).get("title"));
			holder.viewBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int index = Integer.parseInt(v.getTag().toString());
					 String file_name = (String)mData.get(index).get("title");
					 //showInfo(tmp_name);
					 Bundle bundle = new Bundle();
					 bundle.putString("file", file_name);
					 bundle.putString("ip", ip_addr);
					Intent intent = new Intent(PlayList.this, VideoPlayerActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
			return convertView;
		}
	 }
}
