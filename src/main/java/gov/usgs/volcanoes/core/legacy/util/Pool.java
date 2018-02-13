package gov.usgs.volcanoes.core.legacy.util;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * A class for a simple object pool.
 *
 * $Log: not supported by cvs2svn $
 * @author Dan Cervelli
 */
public class Pool<E>
{
  protected LinkedBlockingQueue<E> queue;

  /**
   * Default constructor
   */
  public Pool()
  {
    queue = new LinkedBlockingQueue<E>();
  }

  /**
   * Place object into pool
   * @param o object to place
   * @return true if operation is success
   */
  public boolean checkin(E o)
  {
    try
    {
      queue.put(o);
      return true;
    }
    catch (InterruptedException e)
    {}  // queue is unbounded -- should never block
    return false;
  }

  /**
   *Removes from pool first entered object and returns it
   *
   */
  public E checkout()
  {
    try
    {
      E o = queue.take();
      return o;
    }
    catch (InterruptedException e)
    {
      return null;
    }
  }

  /**
   * Getter for pool size
   * @return Current pool size
   */
  public int size()
  {
    return queue.size();
  }
}
