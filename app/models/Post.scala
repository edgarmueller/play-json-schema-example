package models

import java.util.concurrent.atomic.AtomicLong

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Post(id: Long, title: String, body: String = "")

object Post {

  var posts = Set(
    Post(1L, "Paperclips Large",
      "Large Plain Pack of 1000"),
    Post(2L, "Giant Paperclips",
      "Giant Plain 51mm 100 pack"),
    Post(3L, "Paperclip Giant Plain",
      "Giant Plain Pack of 10000"),
    Post(4L, "No Tear Paper Clip",
      "No Tear Extra Large Pack of 1000"),
    Post(5L, "Zebra Paperclips",
      "Zebra Length 28mm Assorted 150 Pack")
  )

  val nextId: AtomicLong = new AtomicLong(posts.size)

  def findAll = this.posts.toList.sortBy(_.id)

  def findById(ean: Long) = this.posts.find(_.id == ean)

  def delete(id: Long) = {
    this.posts = posts.filterNot(_.id == id)
  }

  def save(post: Post) = {
    findById(post.id).fold(
      this.posts += post
    )(oldPost =>
      this.posts = this.posts - oldPost + post
    )
  }

  implicit val reads: Reads[Post] = (
    (__ \ "id").readNullable[Long] and
      (__ \ "title").read[String] and
      (__ \ "body").readNullable[String]
    ).tupled.map(read => Post(read._1.fold(nextId.getAndIncrement)(id => id), read._2, read._3.getOrElse("")))

  implicit object PostWrites extends Writes[Post] {
    def writes(post: Post) = Json.obj(
      "id"    -> post.id,
      "title" -> post.title,
      "body"  -> post.body
    )
  }
}
