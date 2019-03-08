package io.github.serivesmejia.EDLB.Exception;

public class DataSaveException
  extends Exception
{
  public DataSaveException() {}
  
  public DataSaveException(String message)
  {
    super(message);
  }
  
  public DataSaveException(Throwable caused)
  {
    super(caused);
  }
  
  public DataSaveException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
