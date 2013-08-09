package net.sevenscales.editor.api.event;

import com.google.gwt.event.shared.GwtEvent;

public class OperationQueueRequestEvent extends GwtEvent<OperationQueueRequestEventHandler> {
  public static Type<OperationQueueRequestEventHandler> TYPE = new Type<OperationQueueRequestEventHandler>();

  public enum QueueRequest {
  	FREE_SENDING,
  	BLOCK_SENDING
  }

  private QueueRequest queueRequest;

  public OperationQueueRequestEvent(QueueRequest queueRequest) {
  	this.queueRequest = queueRequest;
  }

  public QueueRequest getQueueRequest() {
  	return queueRequest;
  }

  @Override
  protected void dispatch(OperationQueueRequestEventHandler handler) {
      handler.on(this);
  }

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<OperationQueueRequestEventHandler> getAssociatedType() {
		return TYPE;
	}
}
