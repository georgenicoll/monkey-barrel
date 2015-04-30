package org.monkeynuthead.riak

object AlbumRepositoryProtocol {
  case class StoreAlbum(album: Album) //response on success is the album
  case class FetchAlbumByTitle(title: String) //reponse is an Option[Album]
}
