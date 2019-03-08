package io.github.serivesmejia.EDLB.Exception;

public class LangSaveException
  extends Exception
{
  public LangSaveException() {}
  
  public LangSaveException(String message)
  {
    super(message);
  }
  
  public LangSaveException(Throwable caused)
  {
    super(caused);
  }
  
  public LangSaveException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
