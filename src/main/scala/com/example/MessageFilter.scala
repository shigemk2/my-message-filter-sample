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

case class OrderPlaced(order: Order)

object MessageFilterDriver extends CompletableApp(4) {
}

class InventorySystemA extends Actor {
  def receive = {
    case OrderPlaced(order) if (order.isType("TypeABC")) =>
      println(s"InventorySystemA: handling $order")
      MessageFilterDriver.completedStep()
    case incompatibleOrder =>
      println(s"InventorySystemA: filtering out: $incompatibleOrder")
      MessageFilterDriver.completedStep()
  }
}

class InventorySystemX extends Actor {
  def receive = {
    case OrderPlaced(order) =>
      println(s"InventorySystemX: handling $order")
      MessageFilterDriver.completedStep()
    case _ =>
      println(s"InventorySystemX: received unexpected message")
      MessageFilterDriver.completedStep()
  }
}

class InventorySystemXMessageFilter(actualInventorySystemX: ActorRef) extends Actor {
  def receive = {
    case orderPlaced: OrderPlaced if (orderPlaced.order.isType("TypeXYZ")) =>
      actualInventorySystemX forward orderPlaced
      MessageFilterDriver.completedStep()
    case incompatibleOrder =>
      println(s"InventorySystemXMessageFilter: filtering out: $incompatibleOrder")
      MessageFilterDriver.completedStep()
  }
}
