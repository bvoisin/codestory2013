package bvo.scala1.codestory.scalaskel

case class Change(coinMap: Map[CoinType, Int]) extends Iterable[(CoinType,Int)] {
  def +(coinType: CoinType): Change = new Change(coinMap + (coinType -> (coinMap.getOrElse(coinType, 0) + 1)))
  
  def +(other: Change): Change = new Change(mergeMaps(List(coinMap,other.coinMap)) { (acc, m) => acc + m})
 
  /**
   * copy pasted from http://stackoverflow.com/questions/1262741/scala-how-to-merge-a-collection-of-maps
   */
  private def mergeMaps[A, B](ms: List[Map[A, B]])(f: (B, B) => B): Map[A, B] =
  (Map[A, B]() /: (for (m <- ms; kv <- m) yield kv)) { (a, kv) =>
    a + (if (a.contains(kv._1)) kv._1 -> f(a(kv._1), kv._2) else kv)
  }
  /**
   * remove all coins of given type
   */
  def -*(coinType: CoinType): Change = if (coinMap.contains(coinType)) new Change(coinMap - coinType) else this
 
  /**
   * removes one coin of given type
   * throws exception if does not contain coin
   */
  def -(coinType: CoinType): Change = {
    new Change(coinMap + (coinType -> (coinMap(coinType) - 1)))
  }
  
  
  def sum:Int = coinMap.foldLeft(0) ((acc, e) => acc + e._1.value*e._2)
  
  def contains(c:CoinType) = coinMap.contains(c)
  
  def apply(c:CoinType) = coinMap.getOrElse(c, 0)
  
  def iterator = coinMap.iterator
}

object Change {
  def apply(elems: (CoinType, Int)*) = new Change(Map(elems:_*))
  def apply() = Map[CoinType, Int]()
}