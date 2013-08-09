package net.sevenscales.editor.uicomponents;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.MouseDiagramHandler;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;

public class DoubleClickState implements MouseDiagramHandler {
	private State state;
	private final State startState = new StartState();
	private final State oneClick = new OneClick();
	private final State t1 = new T1();
	private final State twoClick = new TwoClick();
	private final State acceptState = new AcceptState();
	private final State failState = new FailState();
	private long currentTime = 0;
	private long timeLimit = 350;
	private boolean moved = false;
	
	public DoubleClickState() {
		state = startState;
	}
	
	interface State {
		boolean downClick();
	}
	class StartState implements State {
		public boolean downClick() {
			state = oneClick;
			return state.downClick();
		}
	}
	class OneClick implements State {
		public boolean downClick() {
			state = twoClick;
      currentTime = System.currentTimeMillis();
//      System.out.println("one click:");
			return false;
		}		
	}	
	class TwoClick implements State {
		public boolean downClick() {
			state = startState;
			boolean result = true;
			long diff = System.currentTimeMillis() - currentTime;
//      System.out.println("two click:" + diff);
			if (diff > timeLimit) {
				// {time has been passed && if second click is coming after dragging
				// it is not counted as double click} 
				// => go to one click state
//				result = false;
				state = oneClick;
				result = state.downClick();
			}
			return result;
		}		
	}
	class FailState implements State {
		public boolean downClick() {
//			System.out.println"failed");
			state = startState;
			return false;
		}
	}
	class T1 implements State {
		public boolean downClick() {
			state = twoClick;
			return false;
		}
	}
	class AcceptState implements State {
		public boolean downClick() {
			state = startState;
			return true;
		}		
	}

	public boolean downClick() {
		return state.downClick();
	}

	public void onDoubleClick(Diagram sender, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	public boolean onMouseDown(Diagram sender, MatrixPointJS point, int keys) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onMouseEnter(Diagram sender, MatrixPointJS point) {
		// TODO Auto-generated method stub
		
	}

	public void onMouseLeave(Diagram sender, MatrixPointJS point) {
		// TODO Auto-generated method stub
		
	}

	public void onMouseMove(Diagram sender, MatrixPointJS point) {
		moved = true;
	}

	public void onMouseUp(Diagram sender, MatrixPointJS point) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onTouchStart(Diagram sender, MatrixPointJS point) {
	}
  @Override
  public void onTouchMove(Diagram sender, MatrixPointJS point) {
  }
  @Override
  public void onTouchEnd(Diagram sender, MatrixPointJS point) {
  }

}
