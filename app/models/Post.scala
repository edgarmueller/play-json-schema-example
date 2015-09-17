package models

import java.util.concurrent.atomic.AtomicLong

import play.api.libs.json._

case class Post(id: Option[Long], title: String)

object Post {

  var posts = Set(
    Post(Some(0L), "Paperclips Large"),
    Post(Some(1L), "Giant Paperclips"),
    Post(Some(2L), "Paperclip Giant Plain"),
    Post(Some(3L), "No Tear Paper Clip"),
    Post(Some(4L), "Zebra Paperclips")
  )

  val nextId: AtomicLong = new AtomicLong(posts.size)

  def findAll = this.posts.toList.sortBy(_.id)

  def findById(id: Long): Option[Post] = this.posts.collectFirst { case post@Post(Some(i), _) if i == id => post }

  def delete(id: Long) = {
    findById(id).foreach { post => this.posts -= post }
  }

  def save(post: Post) = {
    val id = post.id.getOrElse(nextId.getAndIncrement())
    findById(id).fold {
      // new post
      val postWithId = post.copy(id = Some(id))
      this.posts += postWithId
      postWithId
    }(oldPost => {
      // update
      this.posts = posts.filterNot(_ == oldPost)
      this.posts += post
      post
    })
  }

  implicit val reads = Json.reads[Post]
  implicit val writes = Json.writes[Post]

}
