package com.horizen.utils

import java.util

import scorex.core.NodeViewModifier
import scorex.core.serialization.{BytesSerializable, ScorexSerializer}
import scorex.util.serialization.{Reader, Writer}


case class BoxMerklePathInfo(boxId: Array[Byte], merklePath: MerklePath) extends BytesSerializable {
  require(boxId.length == NodeViewModifier.ModifierIdSize, s"BoxId length is wrong. ${NodeViewModifier.ModifierIdSize} bytes expected.")

  override type M = BoxMerklePathInfo

  override def serializer: ScorexSerializer[BoxMerklePathInfo] = BoxMerklePathInfoSerializer

  override def hashCode: Int = util.Arrays.hashCode(boxId)

  override def equals(obj: Any): Boolean = {
    obj match {
      case info: BoxMerklePathInfo =>
        boxId.sameElements(info.boxId) && merklePath.bytes().sameElements(info.merklePath.bytes())
      case _ => false
    }
  }
}

object BoxMerklePathInfoSerializer extends ScorexSerializer[BoxMerklePathInfo] {
  override def serialize(obj: BoxMerklePathInfo, w: Writer): Unit = {
    w.putBytes(obj.boxId)
    val merklePathBytes = obj.merklePath.bytes()
    w.putInt(merklePathBytes.length)
    w.putBytes(merklePathBytes)
  }

  override def parse(r: Reader): BoxMerklePathInfo = {
    val boxId = r.getBytes(NodeViewModifier.ModifierIdSize)
    val merklePathBytesLength = r.getInt()
    val merklePath = MerklePath.parseBytes(r.getBytes(merklePathBytesLength))
    BoxMerklePathInfo(boxId, merklePath)
  }
}