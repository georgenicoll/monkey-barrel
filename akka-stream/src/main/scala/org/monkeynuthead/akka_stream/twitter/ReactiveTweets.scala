package org.monkeynuthead.akka_stream.twitter

import akka.actor.ActorSystem
import akka.stream.{OverflowStrategy, ActorFlowMaterializerSettings, ActorFlowMaterializer}
import akka.stream.scaladsl._

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._

import scala.language.postfixOps

object ReactiveTweets {

  import Model._

  def filter(tweets:Source[Tweet, _])(implicit system: ActorSystem, materializer: ActorFlowMaterializer): Unit = {
    //Basic filter and map to authors
    val authors: Source[Author, _] =
      tweets
        .filter(_.hashtags.contains(Akka))
        .map(_.author)
    authors.runWith(Sink.foreach(a => println(s"author: ${a}")))
  }

  def flatten(tweets:Source[Tweet, _])(implicit system: ActorSystem, materializer: ActorFlowMaterializer): Unit = {
    //Basic flattening, hashtags flattened to a stream of hashtags
    val hashtags: Source[HashTag, _] = tweets.mapConcat(_.hashtags.toList)
    hashtags.runForeach(ht => println(s"hashtag: ${ht}"))
  }

  def fold(tweets:Source[Tweet, _])(implicit system: ActorSystem, materializer: ActorFlowMaterializer): Unit = {
    implicit val ec = system.dispatcher
    //Folding
    val sum = tweets.runFold(0)((acc, tweet) => acc + tweet.value)
    sum.foreach(i => println(s"Sum: ${i}"))
  }

  def graph(tweets:Source[Tweet, _])(implicit system: ActorSystem, materializer: ActorFlowMaterializer): Unit = {
    //Graphs - Broadcast
    val writeAuthors: Sink[Author, _] = Sink.foreach(println)
    val writeHashtags: Sink[HashTag, _] = Sink.foreach(println)

    val g = FlowGraph.closed() { implicit b =>
      import FlowGraph.Implicits._

      val bcast = b.add(Broadcast[Tweet](2))
      tweets ~> bcast.in // ~> operator reads as 'edge', 'via' or 'to'
      bcast.out(0) ~> Flow[Tweet].map(_.author) ~> writeAuthors
      bcast.out(1) ~> Flow[Tweet].mapConcat(_.hashtags.toList) ~> writeHashtags
    }
    g.run()
  }

  def buffer(tweets:Source[Tweet, _])(implicit system: ActorSystem, materializer: ActorFlowMaterializer): Unit = {
    //Buffering - what to do when the publisher is faster than the subscriber
    val g = FlowGraph.closed() { implicit b =>
      import FlowGraph.Implicits._
      val slowFlow = Flow[Tweet].buffer(2, OverflowStrategy.dropHead).map{ t => Thread.sleep(1000); t }.map(_.author)
      val writeAuthors: Sink[Author, _] = Sink.foreach(a => println(s"Buffer: ${a}"))

      tweets ~> slowFlow ~> writeAuthors
    }
    g.run()
  }

  def materializedValues(tweets:Source[Tweet, _])(implicit system: ActorSystem, materializer: ActorFlowMaterializer): Unit = {
    implicit val ec = system.dispatcher

    val sumSink: Sink[Int, Future[Int]] = Sink.fold[Int,Int](0)(_ + _)
    val counter: RunnableFlow[Future[Int]] = tweets.map(t => 1).toMat(sumSink)(Keep.right)
    val sum: Future[Int] = counter.run()
    sum.foreach(c => println(s"Materialized: ${c}"))
  }

  def main(args: Array[String]): Unit = {
    val tweets:Source[Tweet, _] = Source.apply(List(
      Tweet(1, AkkaTeam, System.currentTimeMillis(), "#akka #reactive"),
      Tweet(2, Author("George"), System.currentTimeMillis(), "#akka"),
      Tweet(3, Author("Bob"), System.currentTimeMillis(), "#reactive #streams"),
      Tweet(4, Author("Phil"), System.currentTimeMillis(), "#akka #reactive")
    ))

    implicit val system = ActorSystem("reactive-tweets")
    implicit val materializer = ActorFlowMaterializer()
    try {
      filter(tweets)
      flatten(tweets)
      fold(tweets)
      graph(tweets)
      buffer(tweets)
      materializedValues(tweets)

      Thread.sleep(2000)
    } finally {
      system.shutdown()
    }
  }

}
