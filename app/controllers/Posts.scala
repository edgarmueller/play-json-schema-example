package controllers

import com.eclipsesource.schema._
import models.Post
import play.api.data.mapping.{VA, Path}
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc.{Action, Controller}

class Posts extends Controller {

  def schema = Json.fromJson[SchemaType](Json.parse(
    """{
      |"properties": {
      |  "id":    { "type": "integer" },
      |  "title": { "type": "string", "minLength": 3, "pattern": "^[A-Z].*" },
      |  "body":  { "type": "string" }
      |}
    }""".stripMargin)).get

  def list = Action {
    val posts = Post.findAll
    Ok(Json.toJson(posts))
  }

  def delete(id: Long) = Action {
    Post.delete(id)
    Ok("Deleted")
  }
  
  def save = Action(parse.json) { implicit request =>
    val json: JsValue = request.body
    val result: VA[Post] = SchemaValidator.validate(schema, json, Post.format)
    result.fold(
      invalid = { errors: Seq[(Path, Seq[ValidationError])] =>
        BadRequest(JsError.toJson(errors.toJsError))
      },
      valid = { post =>
        Post.save(post)
        Ok(Json.toJson(post))
      }
    )
  }
}