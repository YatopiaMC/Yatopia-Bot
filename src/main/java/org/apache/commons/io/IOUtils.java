/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.apache.commons.io.output.StringBuilderWriter;

/**
 * General IO stream manipulation utilities.
 *
 * <p>This class provides static utility methods for input/output operations.
 *
 * <ul>
 *   <li><b>[Deprecated]</b> closeQuietly - these methods close a stream ignoring nulls and
 *       exceptions
 *   <li>toXxx/read - these methods read data from a stream
 *   <li>write - these methods write data to a stream
 *   <li>copy - these methods copy all the data from one stream to another
 *   <li>contentEquals - these methods compare the content of two streams
 * </ul>
 *
 * <p>The byte-to-char methods and char-to-byte methods involve a conversion step. Two methods are
 * provided in each case, one that uses the platform default encoding and the other which allows you
 * to specify an encoding. You are encouraged to always specify an encoding because relying on the
 * platform default can lead to unexpected results, for example when moving from development to
 * production.
 *
 * <p>All the methods in this class that read a stream are buffered internally. This means that
 * there is no cause to use a <code>BufferedInputStream</code> or <code>BufferedReader</code>. The
 * default buffer size of 4K has been shown to be efficient in tests.
 *
 * <p>The various copy methods all delegate the actual copying to one of the following methods:
 *
 * <ul>
 *   <li>{@link #copyLarge(InputStream, OutputStream, byte[])}
 * </ul>
 *
 * For example, {@link #copy(InputStream, OutputStream)} calls {@link #copyLarge(InputStream,
 * OutputStream)} which calls {@link #copy(InputStream, OutputStream, int)} which creates the buffer
 * and calls {@link #copyLarge(InputStream, OutputStream, byte[])}.
 *
 * <p>Applications can re-use buffers by using the underlying methods directly. This may improve
 * performance for applications that need to do a lot of copying.
 *
 * <p>Wherever possible, the methods in this class do <em>not</em> flush or close the stream. This
 * is to avoid making non-portable assumptions about the streams' origin and further use. Thus the
 * caller is still responsible for closing streams after use.
 *
 * <p>Origin of code: Excalibur.
 */
public class IOUtils {
  // NOTE: This class is focused on InputStream, OutputStream, Reader and
  // Writer. Each method should take at least one of these as a parameter,
  // or return one of them.

  /** The default buffer size ({@value}) to use in copy methods. */
  public static final int DEFAULT_BUFFER_SIZE = 8192;

  /**
   * Represents the end-of-file (or stream).
   *
   * @since 2.5 (made public)
   */
  public static final int EOF = -1;

  /** The system line separator string. */
  public static final String LINE_SEPARATOR;

  static {
    // avoid security issues
    try (final StringBuilderWriter buf = new StringBuilderWriter(4);
        final PrintWriter out = new PrintWriter(buf)) {
      out.println();
      LINE_SEPARATOR = buf.toString();
    }
  }

  /**
   * Copies bytes from an <code>InputStream</code> to an <code>OutputStream</code>.
   *
   * <p>This method buffers the input internally, so there is no need to use a <code>
   * BufferedInputStream</code>.
   *
   * <p>Large streams (over 2GB) will return a bytes copied value of <code>-1</code> after the copy
   * has completed since the correct number of bytes cannot be returned as an int. For large streams
   * use the <code>copyLarge(InputStream, OutputStream)</code> method.
   *
   * @param input the <code>InputStream</code> to read from
   * @param output the <code>OutputStream</code> to write to
   * @return the number of bytes copied, or -1 if &gt; Integer.MAX_VALUE
   * @throws NullPointerException if the input or output is null
   * @throws IOException if an I/O error occurs
   * @since 1.1
   */
  public static int copy(final InputStream input, final OutputStream output) throws IOException {
    final long count = copyLarge(input, output);
    if (count > Integer.MAX_VALUE) {
      return -1;
    }
    return (int) count;
  }

  /**
   * Copies bytes from an <code>InputStream</code> to an <code>OutputStream</code> using an internal
   * buffer of the given size.
   *
   * <p>This method buffers the input internally, so there is no need to use a <code>
   * BufferedInputStream</code>.
   *
   * <p>
   *
   * @param input the <code>InputStream</code> to read from
   * @param output the <code>OutputStream</code> to write to
   * @param bufferSize the bufferSize used to copy from the input to the output
   * @return the number of bytes copied
   * @throws NullPointerException if the input or output is null
   * @throws IOException if an I/O error occurs
   * @since 2.5
   */
  public static long copy(final InputStream input, final OutputStream output, final int bufferSize)
      throws IOException {
    return copyLarge(input, output, new byte[bufferSize]);
  }

  /**
   * Copies bytes from a large (over 2GB) <code>InputStream</code> to an <code>OutputStream</code>.
   *
   * <p>This method buffers the input internally, so there is no need to use a <code>
   * BufferedInputStream</code>.
   *
   * <p>The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
   *
   * @param input the <code>InputStream</code> to read from
   * @param output the <code>OutputStream</code> to write to
   * @return the number of bytes copied
   * @throws NullPointerException if the input or output is null
   * @throws IOException if an I/O error occurs
   * @since 1.3
   */
  public static long copyLarge(final InputStream input, final OutputStream output)
      throws IOException {
    return copy(input, output, DEFAULT_BUFFER_SIZE);
  }

  /**
   * Copies bytes from a large (over 2GB) <code>InputStream</code> to an <code>OutputStream</code>.
   *
   * <p>This method uses the provided buffer, so there is no need to use a <code>BufferedInputStream
   * </code>.
   *
   * <p>
   *
   * @param input the <code>InputStream</code> to read from
   * @param output the <code>OutputStream</code> to write to
   * @param buffer the buffer to use for the copy
   * @return the number of bytes copied
   * @throws NullPointerException if the input or output is null
   * @throws IOException if an I/O error occurs
   * @since 2.2
   */
  public static long copyLarge(
      final InputStream input, final OutputStream output, final byte[] buffer) throws IOException {
    long count = 0;
    int n;
    while (EOF != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
      count += n;
    }
    return count;
  }

  /** Instances should NOT be constructed in standard programming. */
  public IOUtils() {
    super();
  }
}
