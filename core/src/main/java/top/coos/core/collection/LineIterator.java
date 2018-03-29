package top.coos.core.collection;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.NoSuchElementException;

import top.coos.core.io.IORuntimeException;
import top.coos.core.io.IoUtil;
import top.coos.core.lang.Assert;

/**
 * 将Reader包装为一个按照行读取的Iterator<br>
 * 此对象遍历结束后，应关闭之，推荐使用方式:
 * 
 * <pre>
 * LineIterator it = null;
 * try {
 * 	it = new LineIterator(reader);
 * 	while (it.hasNext()) {
 * 		String line = it.nextLine();
 * 		// do something with line
 * 	}
 * } finally {
 * 	it.close();
 * }
 * </pre>
 * 
 * 此类来自于Apache Commons io
 *
 */
public class LineIterator implements Iterator<String>, Closeable {

	/** The reader that is being read. */
	private final BufferedReader bufferedReader;
	/** The current line. */
	private String cachedLine;
	/** A flag indicating if the iterator has been fully read. */
	private boolean finished = false;

	/**
	 * 构造
	 *
	 * @param in
	 *            {@link InputStream}
	 * @param charset
	 *            编码
	 * @throws IllegalArgumentException
	 *             reader为null抛出此异常
	 */
	public LineIterator(InputStream in, Charset charset) throws IllegalArgumentException {

		this(IoUtil.getReader(in, charset));
	}

	/**
	 * 构造
	 *
	 * @param reader
	 *            {@link Reader}对象，不能为null
	 * @throws IllegalArgumentException
	 *             reader为null抛出此异常
	 */
	public LineIterator(Reader reader) throws IllegalArgumentException {

		Assert.notNull(reader, "Reader must not be null");
		this.bufferedReader = IoUtil.getReader(reader);
	}

	// -----------------------------------------------------------------------
	/**
	 * 判断{@link Reader}是否可以存在下一行。 If there is an <code>IOException</code> then
	 * {@link #close()} will be called on this instance.
	 *
	 * @return {@code true} 表示有更多行
	 * @throws IORuntimeException
	 *             IO异常
	 */
	@Override
	public boolean hasNext() throws IORuntimeException {

		if (cachedLine != null) {
			return true;
		} else if (finished) {
			return false;
		} else {
			try {
				while (true) {
					String line = bufferedReader.readLine();
					if (line == null) {
						finished = true;
						return false;
					} else if (isValidLine(line)) {
						cachedLine = line;
						return true;
					}
				}
			} catch (IOException ioe) {
				close();
				throw new IORuntimeException(ioe);
			}
		}
	}

	/**
	 * 返回下一行内容
	 *
	 * @return 下一行内容
	 * @throws NoSuchElementException
	 *             没有新行
	 */
	@Override
	public String next() throws NoSuchElementException {

		return nextLine();
	}

	/**
	 * 返回下一行
	 *
	 * @return 下一行
	 * @throws NoSuchElementException
	 *             没有更多行
	 */
	public String nextLine() throws NoSuchElementException {

		if (!hasNext()) {
			throw new NoSuchElementException("No more lines");
		}
		String currentLine = cachedLine;
		cachedLine = null;
		return currentLine;
	}

	/**
	 * 关闭Reader
	 */
	@Override
	public void close() {

		finished = true;
		IoUtil.close(bufferedReader);
		cachedLine = null;
	}

	/**
	 * Unsupported.
	 *
	 * @throws UnsupportedOperationException
	 *             always
	 */
	@Override
	public void remove() {

		throw new UnsupportedOperationException("Remove unsupported on LineIterator");
	}

	/**
	 * Overridable method to validate each line that is returned. This
	 * implementation always returns true.
	 * 
	 * @param line
	 *            the line that is to be validated
	 * @return true if valid, false to remove from the iterator
	 */
	protected boolean isValidLine(String line) {

		return true;
	}
}
