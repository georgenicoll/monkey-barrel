package org.monkeynuthead.akka_stream.twitter

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl.{Sink, Source}

object ReactiveTweets {

  import Model._

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("reactive-tweets")
    implicit val mat = ActorFlowMaterializer()

    val tweets = Source.apply(List(
      Tweet(AkkaTeam, System.currentTimeMillis(), "#akka #reactive"),
      Tweet(Author("George"), System.currentTimeMillis(), "#akka"),
      Tweet(Author("Bob"), System.currentTimeMillis(), "#reactive"),
      Tweet(Author("Phil"), System.currentTimeMillis(), "#akka #reactive")
    ))

    val authors =
      tweets
        .filter(_.hashtags.contains(Akka))
        .map(_.author)

    authors.runWith(Sink.foreach(println(_)))

  }

}
