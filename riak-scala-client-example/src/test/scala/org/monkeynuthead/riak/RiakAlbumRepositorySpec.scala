package org.monkeynuthead.riak

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, ImplicitSender, TestActorRef, TestKitBase}
import com.scalapenos.riak.{RiakClient, RiakValue}
import org.junit.runner.RunWith
import org.monkeynuthead.riak.AlbumRepositoryProtocol.{FetchAlbumByTitle, StoreAlbum}
import org.scalatest.junit.JUnitRunner
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

@RunWith(classOf[JUnitRunner])
class RiakAlbumRepositorySpec extends TestKit(ActorSystem()) with ImplicitSender
with WordSpecLike with MustMatchers with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    system.shutdown()
    system.awaitTermination()
    super.afterAll()
  }

  val timeout = 2 seconds
  val album1 = Album(
    title = "Employment",
    artist = "Kaiser Chiefs",
    releasedIn = 2005,
    tracks = List(
      Track(1, "Everyday I Love You Less and Less"),
      Track(2, "I Predict a Riot"),
      Track(3, "Modern Way"),
      Track(4, "Na Na Na Na Naa"),
      Track(5, "You Can Have It All"),
      Track(6, "Oh My God"),
      Track(7, "Born to Be a Dancer"),
      Track(8, "Saturday Night"),
      Track(9, "What Did I Ever Give You?"),
      Track(10, "Time Honoured Tradition"),
      Track(11, "Caroline, Yes"),
      Track(12, "Team Mate")
    )
  )

  "when receiving a StoreAlbum message, the RiakAlbumRepository" should {

    "store the album in the database" in {
      val albumRepository = TestActorRef(Props[RiakAlbumRepository])

      verifyAlbumDoesNotExistInDatabase(album1.title)

      within(timeout) {
        albumRepository ! StoreAlbum(album1)

        val albumFromDb = expectMsgType[Album]

        albumFromDb must equal(album1)

        verifyAlbumExistsInDatabase(album1)
      }

      removeAlbumFromDatabase(album1)
    }

    "allow the album to be fetched from the database after it is stored" in {
      val albumRepository = TestActorRef(Props[RiakAlbumRepository])

      verifyAlbumDoesNotExistInDatabase(album1.title)

      albumRepository ! StoreAlbum(album1)

      within(timeout) {
        albumRepository ! FetchAlbumByTitle(album1.title)

        val fetched = expectMsgType[Album]

        fetched must equal(Some(album1))
      }

      removeAlbumFromDatabase(album1)
    }

  }

  private def bucket(implicit system: ActorSystem) = RiakClient(system).bucket("albums")

  private def removeAlbumFromDatabase(album: Album)(implicit system: ActorSystem): Unit = {
    Await.ready(bucket.delete(album.title), timeout)

    verifyAlbumDoesNotExistInDatabase(album.title)
  }

  private def verifyAlbumExistsInDatabase(album: Album)(implicit system: ActorSystem): Unit = {
    val dbValue = Await.result(bucket.fetch(album.title), timeout).getOrElse(fail(s"No Album with title ${album.title}"))

    val albumInDb = dbValue.as[Album]

    albumInDb must equal(album)
  }

  private def verifyAlbumDoesNotExistInDatabase(title: String)(implicit system: ActorSystem): Unit = {
    Await.result(bucket.fetch(title), timeout) must equal(None)
  }

}
