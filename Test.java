package sync;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

class Sleep
{
	
	public static void nap() {
		nap(NAP_TIME);
	}

	
	public static void nap(int duration) {
        	int sleeptime = (int) (NAP_TIME * Math.random() );
        	try { Thread.sleep(sleeptime*1000); }
        	catch (InterruptedException e) {}
	}

	private static final int NAP_TIME = 5;
}

public class Test {
	public static void main(String [] args) {
		ExecutorService executorService = Executors.newCachedThreadPool();
		ReadWriteLock RW = new ReadWriteLock();
		
		
		executorService.execute(new Writer(4,RW));
		executorService.execute(new Writer(3,RW));
		executorService.execute(new Writer(2,RW));
		executorService.execute(new Writer(1,RW));
		
		executorService.execute(new Reader(4,RW));
		executorService.execute(new Reader(3,RW));
		executorService.execute(new Reader(2,RW));
		executorService.execute(new Reader(1,RW));
		
		
	}
}


class ReadWriteLock{
	private Semaphore S=new Semaphore(1);
        private Semaphore S2 = new Semaphore(1);
        private int reader_count=0;
	
	public void readLock(int readerNo) {
            try {
            S.acquire();
            } catch (Exception e) {
                Logger.getLogger(ReadWriteLock.class.getName()).log(Level.SEVERE, null, e);
            }
            ++reader_count;
            if(reader_count == 1){
            try {
                S2.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ReadWriteLock.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
            System.out.println("Reader " + readerNo + " is reading. Reader count = " + reader_count);
            S.release();
		
	}
	public void writeLock(int writerNo) {
                try{
                S2.acquire();
                }
             catch (InterruptedException e) {}
         System.out.println("Writer " + writerNo + " is writing.");
		
	}
	public void readUnLock(int readerNo) {
		try{
         
            S.acquire();
         }
             catch (InterruptedException e) {}
      
         --reader_count;
      
      
         if (reader_count == 0){
            S2.release();
         }
      
         System.out.println("Reader " + readerNo + " is done reading. Reader count = " + reader_count);
      
      
         S.release();
		
		
	}
	public void writeUnLock(int writerNo) {
            System.out.println("Writer " + writerNo + " is done writing.");
            S2.release();
		
		
	}

}




class Writer implements Runnable
{
   private ReadWriteLock RW_lock;
   private int writerNo;
   

    public Writer(int writerNo, ReadWriteLock rw) {
    	this.RW_lock = rw;
        this.writerNo = writerNo;
   }

    public void run() {
      while (true){
          Sleep.nap();
          
          System.out.println("writer " + writerNo + " wants to write.");
    	  RW_lock.writeLock(writerNo);
    	
          Sleep.nap();
          
    	  RW_lock.writeUnLock(writerNo);
       
      }
   }


}



class Reader implements Runnable
{
   private ReadWriteLock RW_lock;
   private int readerNo;

   public Reader(int readerNo, ReadWriteLock rw) {
    	this.readerNo = readerNo;
        this.RW_lock = rw;
   }
    public void run() {
      while (true){
          Sleep.nap();   
          System.out.println("reader " + readerNo + " wants to read." );
          
    	  RW_lock.readLock(readerNo);
    	 
    	  Sleep.nap();
          
    	  RW_lock.readUnLock(readerNo);
       
      }
   }


}