package net.sevenscales.editor.content;

import net.sevenscales.domain.api.IContent;
import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.editor.api.ISurfaceHandler;

public interface ContentSaveListener {

	void save(IContent content);
  void close(IContent content);
	void cancel(IContent content);
	void delete(IContent content);
  void share(IContent content);
  void generateImage(IDiagramContent content, ISurfaceHandler surfaceHandler);

}
