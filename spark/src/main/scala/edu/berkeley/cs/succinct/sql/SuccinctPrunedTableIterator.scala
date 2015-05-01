package edu.berkeley.cs.succinct.sql

import edu.berkeley.cs.succinct.SuccinctIndexedBuffer
import org.apache.spark.sql.Row

/**
 * Iterator for a SuccinctPrunedTableRDD partition.
 *
 * @constructor Create a new SuccinctTableIterator with a SuccinctIndexedBuffer and a list of separators.
 * @param sBuf The underlying SuccinctIndexedBuffer.
 * @param succinctSerializer The serializer/deserializer for Succinct's representation of records.
 * @param reqColsCheck The list of required columns.
 */
class SuccinctPrunedTableIterator private[succinct](
    sBuf: SuccinctIndexedBuffer,
    succinctSerializer: SuccinctSerializer,
    reqColsCheck: Map[String, Boolean])
  extends Iterator[Row] {

  var curPos: Int = 0

  /**
   * Returns true if there are more [[Row]]s to iterate over.
   *
   * @return true if there are more [[Row]]s to iterate over;
   *         false otherwise.
   */
  override def hasNext: Boolean = (curPos < sBuf.getOriginalSize() - 2)

  /**
   * Returns the next [[Row]].
   *
   * @return The next [[Row]].
   */
  override def next(): Row = {
    val data = sBuf.extractUntil(curPos, SuccinctIndexedBuffer.getRecordDelim)
    curPos = curPos + data.length + 1
    val row = succinctSerializer.deserializeRow(data, reqColsCheck)
    row
  }

}