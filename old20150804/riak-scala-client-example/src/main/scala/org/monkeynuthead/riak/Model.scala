package org.monkeynuthead.riak

import spray.json.DefaultJsonProtocol._

case class Track(number: Int, title: String)
object Track {
  implicit val jsonFormat = jsonFormat2(Track.apply)
}

case class Album(title: String, artist: String, releasedIn: Int, tracks: List[Track])
object Album {
  implicit val jsonFormat = jsonFormat4(Album.apply)
}