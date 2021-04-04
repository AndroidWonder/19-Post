/*
 * When button is clicked, background thread is started.
 * It sends multiple requests to the main thread handler to run 
 * the foreground task.
 */
package com.course.example.post;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Post extends Activity {
	
	private ProgressBar bar;
	private TextView    lblTopCaption;
	private EditText    txtBox1;
	private Button		btnDoSomething;

	private int 		accum = 0;
	private long 		startingMills = System.currentTimeMillis();	
	private String      CollectMsg = "Some important data is being collected now.";
	
	//Create Handler object to handle tasks placed on queue 
	Handler   handler = new Handler(Looper.getMainLooper());

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
        lblTopCaption = (TextView)findViewById(R.id.lblTopCaption);
        
        bar = (ProgressBar) findViewById(R.id.myBar);
        bar.setMax(100);
        
        txtBox1 = (EditText) findViewById(R.id.txtBox1);
        txtBox1.setHint("Foreground distraction. Enter some data here"); 
        
        //get button handle and set listener
        btnDoSomething = (Button)findViewById(R.id.btn);
        btnDoSomething.setOnClickListener(new OnClickListener() {
        	
			public void onClick(View v) {
				String txt = txtBox1.getText().toString();
				Toast.makeText(Post.this, 
						"You said - " + txt, Toast.LENGTH_SHORT).show();
			}       	
        });      
    }//onCreate
    	
    @Override
	protected void onStart() {
		super.onStart();
    	// create background thread where the busy work will be done
    	Thread t = new Thread(backgroundTask, "background");
    	t.start();    	
    	bar.setProgress(0); //initialize progress bar
    }

    // this is the foreground "Runnable" object 
    // responsible for GUI updates 
    private Runnable foregroundTask = new Runnable() {
	
		public void run() {	
		   	try {
				int progressStep = 5;  
				lblTopCaption.setText(CollectMsg +
						"\nTotal seconds so far: " + 
						(System.currentTimeMillis() 
						 - startingMills) / 1000 );
				//increment progress bar
				bar.incrementProgressBy(progressStep);
				
				accum = accum + progressStep;
				if (accum >= bar.getMax()){
					lblTopCaption.setText("Background work is OVER!");
					bar.setVisibility(View.INVISIBLE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}    	
    }; //foregroundTask
    
	//Runnable object that executes as the background thread
    private Runnable backgroundTask = new Runnable() {
	
		public void run() {
	    	
	    	try {
	    		for (int n=0; n<20; n++) {
	    			//this simulates 1 second of activity
	        		Thread.sleep(1000);
	        		//post foreground task for main thread to execute
	        		handler.post(foregroundTask);
	    		}    		
			} catch (InterruptedException e) {			
				e.printStackTrace();
			}    				
		}//run   	
    };//backgroundTask
    
}//ThreadsPosting