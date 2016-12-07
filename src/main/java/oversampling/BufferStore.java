package oversampling;
/*
 * ==========================================================================
 * Core class for building dictionary based map                              | 
 * read every word from dictionary file for each of the language             |
 * Build the Map and then serialize it for future use                        |
 * ==========================================================================                           
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;


public class BufferStore {
	private static String tmpFolder = "";
	////////////////////////////////////////////////////////
	
	// Serialize the object in hard disk
	// to speed up the processing on next call of the program
	public int SerializeMx(String mapPath, Map<String, Double> map) {
		int ret = 1;
		try
        {
           FileOutputStream fos =
              new FileOutputStream( getTempFolder() + mapPath);
           ObjectOutputStream oos = new ObjectOutputStream(fos);
           oos.writeObject(map);
           oos.close();
           fos.close();
        //   System.out.printf("Serialized HashMap is saved at " + mapPath);
        }catch(IOException ioe)
        {
           ioe.printStackTrace();
           ret = -1;
        }		
		return ret;
	}
	
	// pull out the serialized object 
	// optimized way of running program
	@SuppressWarnings("unchecked")
	public Map<String, Double> DeserializeMx(String mapPath) {
		
		Map<String, Double> tmpMap = new HashMap<String, Double>();
		try
	      {
	         FileInputStream fis = new FileInputStream( getTempFolder() + mapPath);
	         ObjectInputStream ois = new ObjectInputStream(fis);
	         tmpMap = (Map<String, Double>) ois.readObject();
	         ois.close();
	         fis.close();
	      }catch(IOException ioe)
	      {
	         ioe.printStackTrace();
	         System.out.println("map: " + mapPath + " not found");
	         return null;
	      }catch(ClassNotFoundException c)
	      {	         
	         c.printStackTrace();
	         return null;
	      }
	      //System.out.println("Deserialized HashMap..");
		return tmpMap;	
	}
	
	//////////////////////////////////////////////////////////////////////////
	public void purgeDirectory(String dirName) {
		File dir = new File(dirName);
    	
		for (File file: dir.listFiles()) {
			if (file.isDirectory()) purgeDirectory(dirName);
				file.delete();
			}
	}

	//////////////////////////////////////////////////////////////////////////
	// Constructor to load the properties file and essential path	
	public BufferStore() {
     	//   nothing		
	}	
		
	////////////////////////////////////////////////////////
	public void setTempFolder(String val) {
		tmpFolder = val;
	}
	////////////////////////////////////////////////////////
	public String getTempFolder() {
		return tmpFolder;
	}
	
	////////////////////////////////////////////////////////
	
	// Serialize the object in hard disk
	// to speed up the processing on next call of the program
	public int SerializeOv(String mapPath, Map<String, String> map) {
		int ret = 1;
		try
        {
           FileOutputStream fos =
              new FileOutputStream( getTempFolder() + mapPath);
           ObjectOutputStream oos = new ObjectOutputStream(fos);
           oos.writeObject(map);
           oos.close();
           fos.close();
        //   System.out.printf("Serialized HashMap is saved at " + mapPath);
        }catch(IOException ioe)
        {
           ioe.printStackTrace();
           ret = -1;
        }		
		return ret;
	}
	
	// pull out the serialized object 
	// optimized way of running program
	@SuppressWarnings("unchecked")
	public Map<String, String> DeserializeOv(String mapPath) {
		
		Map<String, String> tmpMap = new HashMap<String, String>();
		try
	      {
	         FileInputStream fis = new FileInputStream( getTempFolder() + mapPath);
	         ObjectInputStream ois = new ObjectInputStream(fis);
	         tmpMap = (Map<String, String>) ois.readObject();
	         ois.close();
	         fis.close();
	      }catch(IOException ioe)
	      {
	         ioe.printStackTrace();
	         System.out.println("map: " + mapPath + " not found");
	         return null;
	      }catch(ClassNotFoundException c)
	      {	         
	         c.printStackTrace();
	         return null;
	      }
	      //System.out.println("Deserialized HashMap..");
		return tmpMap;	
	}
	
	//////////////////////////////////////////////////////////////////////////
	// 
	// Serialize the object in hard disk
	// to speed up the processing on next call of the program
	public int SerializeVectorWithMinMax(String mapPath, Map<Long, String> map) {
		int ret = 1;
		try
        {
           FileOutputStream fos =
              new FileOutputStream( getTempFolder() + mapPath);
           ObjectOutputStream oos = new ObjectOutputStream(fos);
           oos.writeObject(map);
           oos.close();
           fos.close();
        //   System.out.printf("Serialized HashMap is saved at " + mapPath);
        }catch(IOException ioe)
        {
           ioe.printStackTrace();
           ret = -1;
        }		
		return ret;
	}
	
	// pull out the serialized object 
	// optimized way of running program
	@SuppressWarnings("unchecked")
	public Map<Long, String> DeserializeVectorWithMinMax(String mapPath) {
		
		Map<Long, String> tmpMap = new HashMap<Long, String>();
		try
	      {
	         FileInputStream fis = new FileInputStream( getTempFolder() + mapPath);
	         ObjectInputStream ois = new ObjectInputStream(fis);
	         tmpMap = (Map<Long, String>) ois.readObject();
	         ois.close();
	         fis.close();
	      }catch(IOException ioe)
	      {
	         ioe.printStackTrace();
	         System.out.println("map: " + mapPath + " not found");
	         return null;
	      }catch(ClassNotFoundException c)
	      {	         
	         c.printStackTrace();
	         return null;
	      }
	      //System.out.println("Deserialized HashMap..");
		return tmpMap;	
	}
	
	//////////////////////////////////////////////////////////////////////////
	public File createTempDirectory() {
		    File temp = null ;
		    
		    try {
				temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

				if(!(temp.delete()))
			    {
			        throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
			    }

			    if(!(temp.mkdir()))
			    {
			        throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
			    }
		    
		    } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		    return (temp);
		}
	
	
} // end of class 

