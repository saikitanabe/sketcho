package net.sevenscales.appFrame.api.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class IncrementalAsyncProcessor<T> {
  private List<IIncrementalExecutionObserver> observers = new ArrayList<IIncrementalExecutionObserver>();
  private List<IncrementalAsyncCommand<T>> commands = new ArrayList<IncrementalAsyncCommand<T>>();
  
  public abstract class IncrementalAsyncCommand<T> implements AsyncCallback<T> {
    
    public final void onFailure(Throwable caught) {
      onStepFailure(caught);
      IncrementalAsyncProcessor.this.execute();
    }

    public final void onSuccess(T result) {
      onStepSuccess(result);
      IncrementalAsyncProcessor.this.execute();
    }
    
    public abstract void onStepFailure(Throwable caught);
    public abstract void onStepSuccess(T result);
    public abstract boolean execute(); 
  }

  /////////////
  
  public static interface IIncrementalExecutionObserver {
    public void finished();
  }

  /////////////

  public void addCommand(IncrementalAsyncCommand<T> command) {
    commands.add(command);
  }
  
  public void addExecutionObserver(IIncrementalExecutionObserver observer) {
    this.observers.add(observer);
  }
  
  public void execute() {
    try {
      IncrementalAsyncCommand<T> c = commands.remove(0);
      c.execute();
    } catch (IndexOutOfBoundsException e) {
      for (IIncrementalExecutionObserver o : observers) {
        o.finished();
      }
    }
    
  }
}
