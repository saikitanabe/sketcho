package net.sevenscales.editor.api.ot;

public interface OperationTransaction {
	void beginTransaction();
	void commitTransaction();
}