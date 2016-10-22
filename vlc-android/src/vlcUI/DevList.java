package vlcUI;

import com.lau.vlcdemo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class DevList extends Activity {
	private Button del;
	private Button ok;
	private Switch status;
	private String tag;
	private EditText e_ip;
	private EditText e_name;
	private String ip;
	private DEV dev=null;
	private SqliteDAO sdao;
	private SocketIO  sio;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.form);
        dev=new DEV();
        sio=new SocketIO();
        sdao=new SqliteDAO(this.getBaseContext());
		del=(Button) findViewById(R.id.dev_del);
		ok=(Button) findViewById(R.id.option_ok);
		status=(Switch)findViewById(R.id.dev_status);
		e_ip=(EditText)findViewById(R.id.dev_ip);
		e_name=(EditText)findViewById(R.id.dev_name);
		Bundle bundle = this.getIntent().getExtras();
        tag = bundle.getString("tag").trim();
        //Toast.makeText(MyForm.this, tag+ "",  Toast.LENGTH_SHORT).show(); 
        //Toast.makeText(MyForm.this, (tag.equals("set"))+ "",  Toast.LENGTH_SHORT).show();  
        if(tag.equals("set")){
        	ip=bundle.getString("ip");
        	//Toast.makeText(MyForm.this, ip,  Toast.LENGTH_SHORT).show();
        	dev=sdao.find(ip);
        	Toast.makeText(DevList.this, "getid"+dev.getId() + "",  Toast.LENGTH_SHORT).show();
        	e_name.setText(dev.getName());
        	//e_name.setText("set");
        	e_ip.setText(dev.getIP());
        	//e_ip.setText("set");
        	if(dev.getStatus().toString().equals("on")){
        		status.setChecked(true);
        		//status.setText("¿ªÆô");
        	}else{
        		status.setChecked(false);
        		//status.setText("¹Ø±Õ");
        	}
        }
        setListeners();
	}
   private void setListeners(){
	   del.setOnClickListener(delBtnListener);
	   ok.setOnClickListener(okBtnListener);
	   //status.setOnClickListener(statusBtnListener);
	   status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {  
           @Override  
           public void onCheckedChanged(CompoundButton buttonView,  
                   boolean isChecked) {  
               Toast.makeText(DevList.this, isChecked + "",  Toast.LENGTH_SHORT).show();  
               if(isChecked){
            	   dev.setStatus("on"); 
            	   new Thread(){
		               @Override
		               public void run()
		               {
		            	   String status=sio.sendmsg_camer(ip,"open");
		            	   //Toast.makeText(PlayList.this,dirs.length+"",  Toast.LENGTH_SHORT).show();
		               }
		               }.start();
               }else{
            	   dev.setStatus("off");
            	   new Thread(){
		               @Override
		               public void run()
		               {
		            	   String status=sio.sendmsg_camer(ip,"close");
		            	   //Toast.makeText(PlayList.this,dirs.length+"",  Toast.LENGTH_SHORT).show();
		               }
		               }.start();
               }
           }  
       });
   }
   
	private View.OnClickListener delBtnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.v("MyListView4", "myAdapter");
			sdao.delete(dev.getIP());
			//sdao.delete_all();
			Intent intent = new Intent(DevList.this, Main.class);
			startActivity(intent);
		}
	};
	private View.OnClickListener okBtnListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.v("MyListView4", "myAdapter");
			if(tag.equals("set")){
				dev.setIP(e_ip.getText().toString());
				dev.setName(e_name.getText().toString());
				sdao.update(dev);
			}else{
				dev.setIP(e_ip.getText().toString());
				Toast.makeText(DevList.this, e_ip.getText().toString(),  Toast.LENGTH_SHORT).show();  
				dev.setName(e_name.getText().toString());
				sdao.save(dev);
			}
			Intent intent = new Intent(DevList.this, Main.class);
			startActivity(intent);
		}
	};
}
