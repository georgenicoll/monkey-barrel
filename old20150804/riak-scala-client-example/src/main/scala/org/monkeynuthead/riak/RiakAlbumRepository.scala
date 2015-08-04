package org.monkeynuthead.riak

import akka.actor.{ActorRef, ActorLogging, Actor}
import com.scalapenos.riak.RiakClient

class RiakAlbumRepository extends Actor with ActorLogging {

  import AlbumRepositoryProtocol._
  import context.dispatcher

  private val albums = RiakClient(context.system, "localhost", 8098).bucket("albums")

  def receive = {
    case StoreAlbum(album) => storeAlbum(album, sender)
    case FetchAlbumByTitle(title) => fetchAlbumByTitle(title, sender)
  }

  private def storeAlbum(album: Album, actor: ActorRef): Unit =
    albums.storeAndFetch(album.title, album)
      .map(_.as[Album])
      .onSuccess {
        case storedAlbum => actor ! storedAlbum
      }

  private def fetchAlbumByTitle(s: String, actor: ActorRef): Unit = ???

}
