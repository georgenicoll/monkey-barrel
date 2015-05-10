package org.monkeynuthead.akka_stream.twitter

import java.security.Timestamp

object Model {

  final case class Author(handle: String)

  final case class HashTag(name: String)

  final case class Tweet(author: Author, timestamp: Long, body: String) {
    def hashtags: Set[HashTag] =
      body.split(" ").collect { case t if t.startsWith("#") => HashTag(t) }.toSet
  }

  val AkkaTeam = Author("akkateam")
  val Akka = HashTag("#akka")


}
