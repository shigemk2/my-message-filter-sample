package com.example

import akka.actor._

case class Order(id: String, orderType: String, orderItems: Map[String, OrderItem]) {
  val grandTotal: Double = orderItems.values.map(orderItem => orderItem.price).sum

  def isType(prefix: String): Boolean = {
    this.orderType.startsWith(prefix)
  }
  override def toString: String = {
    s"Order($id, $orderType, $orderItems, Totaling: $grandTotal)"
  }
}

case class OrderItem(id: String, itemType: String, description: String, price: Double) {
  override def toString: String = {
    s"OrderItem($id, $itemType, '$description', $price)"
  }
}

object MessageFilterDriver extends CompletableApp(4) {
}
