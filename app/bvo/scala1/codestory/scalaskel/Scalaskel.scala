package bvo.scala1.codestory.scalaskel

import scala.collection.mutable.ArrayBuffer
import java.io.StringWriter
import java.io.Writer
import play.api.libs.json.Json
import play.api.Logger
import scala.collection.mutable.Stack
import scala.collection.mutable.Cloneable
import scala.collection.mutable.HashMap

/**
 * Le Scalaskel en mode iterator (RQ: ce n'est pas la version qui a passé code story, mais cela passe mes tests)
 * 
 * TODO : utiliser une librairie JSon au lieu de la concatenation à la mano import com.codahale.jerkson.Json._
 * 
 */

class ScalaskelChangeIterator(val sum: Int, coins: Seq[CoinType]) extends Iterator[Change] {

  /**
   * All the available coins, sorted in ASC order
   */
  private val ascOrderedCoins = coins.sortWith(_.value < _.value)

  /**
   * The smallest coin, which must be of value 1
   */
  private val unitCoin = ascOrderedCoins.head

  {
    assert(unitCoin.value == 1, "Currently the code expects to have one coin with value 1, not " + unitCoin.value)
  }

  //init with the always possible change : N unitCoins
  private var nextChange: Option[Change] = Some(Change(unitCoin -> sum))
  private var nextChangeProvided=false

  override def next: Change = {
    hasNext
    nextChangeProvided = true
    nextChange.get
  }

  private def fetchNext(): Option[Change] = {
    //we start from the previous change, and try to modify it to a new possible change
    var currentChange = nextChange.get
    val coinIterator = ascOrderedCoins.iterator
    
    var remainingSum = 0
    
    while (coinIterator.hasNext) {
      //from the smallest to the biggest coin :
      val coin = coinIterator.next()
      if (remainingSum >= coin.value) {
        //Since we can, lets add a coin of this type to the current change ?  
        val count = currentChange(coin)
        val changeWithOneMoreOfThisCoins = currentChange + coin
        remainingSum -= coin.value
        var newChange = if (remainingSum == 0) changeWithOneMoreOfThisCoins else changeWithOneMoreOfThisCoins + (unitCoin * remainingSum)
        return Some(newChange)
      } else if (currentChange.contains(coin)) { 
        //We can't add anymore of these coins, the try removing them all, and move on the bigger coin
        remainingSum += currentChange(coin) * coin.value
        currentChange = currentChange -* coin
      }
    }
    return None
  }

  override def hasNext: Boolean = {
    if (nextChangeProvided==true) {
      nextChange = fetchNext()
      nextChangeProvided=false
    }
    return nextChange.isDefined
  }

}

trait ScalaskelChanger {
  val orderedCoins = Seq(Baz, Qix, Bar, Foo)

  def findPossibleChanges(sum: Int): Iterator[Change] = {
    return new ScalaskelChangeIterator(sum, orderedCoins)
  }

  def findPossibleChangesAsJSon(sum: Int): String = {
    val writer = new StringWriter();
    var count = 0
    writer.append('[')
    var firstValue = true
    val changeCB =

      findPossibleChanges(sum).foreach({ (m: Change) =>
        if (firstValue) {
          firstValue = false
        } else {
          writer.append(',')
        }
        writeMapAsJSon(writer, m)
        count += 1
      })

    writer.write("]")
    val ret = writer.toString()
    Logger.info("findPossibleChangesAsJSon(" + sum + ") => " + count + " maps (" + ret.length() + " chars)")
    return ret
  }

  /**
   * TODO : utiliser une librairie JSon au lieu de la concatenation à la mano import com.codahale.jerkson.Json._
   */
  private def writeMapAsJSon(writer: Writer, m: Change): Unit = {
    writer.append('{')
    var firstValue = true

    for ((coinType, count) <- m) {
      if (firstValue) {
        firstValue = false
      } else {
        writer.append(',')
      }
      writer.append('\"')
      writer.write(coinType.name)
      writer.write("\":")
      writer.write(Integer.toString(count))
    }
    writer.write("}")
  }
}

object ScalaskelChanger extends ScalaskelChanger {
}