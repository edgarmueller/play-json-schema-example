package controllers

import com.eclipsesource.schema._
import models.Post
import play.api.data.mapping.{VA, Path}
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc.{Result, Action, Controller}


// example based on 'Play for Scala' (http://www.manning.com/hilton/)
class Posts extends Controller {

  def schema = Json.fromJson[SchemaType](Json.parse(
    """{
      |"properties": {
      |  "id":    { "type": "integer" },
      |  "title": { "type": "string", "minLength": 3, "pattern": "^[A-Z].*" }
      |}
    |}""".stripMargin)).get

  def list = Action {
    val posts = Post.findAll
    Ok(Json.toJson(posts))
  }

  def post(id: Long) = Action {
    Post.findById(id).fold(
      NotFound("Post not found")
    )(post => Ok(Json.toJson(post)))
  }

  def delete(id: Long) = Action {
    Post.findById(id).fold(
      NotFound("Post not found")
    )(post =>  {
      Post.delete(id)
      Ok(s"Deleted Post $id")
    })
  }
  
  def save = Action(parse.json) { implicit request =>
    val json: JsValue = request.body
    validate(json)
  }

  def update(id: Long) = Action(parse.json) { request =>
    val json: JsValue = request.body
    Post.findById(id).fold(
      NotFound("Post not found")
    )(post => validate(json))
  }

  private def validate(json: JsValue): Result = {
    val result: VA[Post] = SchemaValidator.validate(schema, json, Post.reads)
    result.fold(
      invalid = { errors =>  BadRequest(errors.toJson) },
      valid = { post =>
        Post.save(post)
        Ok(Json.toJson(post))
      }
    )
  }
}