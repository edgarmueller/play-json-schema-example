package controllers

import com.eclipsesource.schema.{SchemaType, Validator, _}
import models.Post
import play.api.data.mapping.Path
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc.{Action, Controller}

class Posts extends Controller {

  //  (id: Long, title: String, body: String = "")

  def schema = Json.fromJson[SchemaType](Json.parse(
    """{
      |"properties": {
      |  "id":    { "type": "integer" },
      |  "title": { "type": "string" },
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
    val result = Validator.validate(schema, json, Post.format)
    result.fold(
      valid = { post =>
        Post.save(post)
        Ok(Json.toJson(post))
      },
      invalid = { errors: Seq[(Path, Seq[ValidationError])] =>
        BadRequest(JsError.toJson(errors.toJsError))
      }
    )
  }

}
