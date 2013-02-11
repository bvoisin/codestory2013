package bvo.scala1.codestory.scalaskel

abstract sealed class CoinType(val value: Int) {
  val name = this.getClass().getSimpleName().toLowerCase().replace("$", "")
  def * (nbCoins:Int) = Change((this -> nbCoins))
}
case object Foo extends CoinType(1)
case object Bar extends CoinType(7)
case object Qix extends CoinType(11)
case object Baz extends CoinType(21)


